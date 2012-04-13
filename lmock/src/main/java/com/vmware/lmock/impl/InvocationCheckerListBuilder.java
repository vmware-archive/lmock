/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.impl;

import static com.vmware.lmock.impl.ClassChecker.anyArgumentOf;
import static com.vmware.lmock.impl.ClassChecker.anyNonNullArgumentOf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.vmware.lmock.checker.Checker;
import com.vmware.lmock.exception.IllegalClassDefinitionException;
import com.vmware.lmock.exception.IllegalClauseException;
import com.vmware.lmock.exception.MockReferenceException;

/**
 * A common interface to create a list of invocation checkers.
 *
 * <p>
 * This root class handles the common part of a checker specification,
 * providing:
 * </p>
 * <ul>
 * <li>A list of invocation checker builders, constructed on the fly</li>
 * <li>Base methods used by programmers to construct their list</li>
 * </ul>
 *
 * <p>
 * Notice that this method is abstract: it requires the implementer to provide a
 * factory method that actually creates the builder (<code>createBuilder</code>
 * ).
 * </p>
 *
 * <p>
 * A list builder behaves as a three stages process:
 * </p>
 * <ul>
 * <li>Create the checker list: each time a new mock is invoked to create a
 * checker, call <code>registerCheckerBuilder</code> to create and register that
 * checker into the list</li>
 * <li>Add clauses to the current checker: used to add arguments to the checked
 * invocation. These are the standard clauses <code>anyOf</code>,
 * <code>aNonNullOf</code> and <code>with</code>. Also put specific clause by
 * retrieving the checker, using <code>getCurrentChecker</code>.</li>
 * <li>Retrieve the checkers: when the construction is complete, use this
 * iterable property of this class to get the resulting list of checkers</li>
 * </ul>
 */
abstract class InvocationCheckerListBuilder<CHECKER extends InvocationChecker, BUILDER extends InvocationCheckerBuilder<CHECKER>>
  implements Iterable<CHECKER> {

    /** Logs the registrations and the generic builder activity. */
    private static final Logger logger = Logger.get(InvocationCheckerListBuilder.class);
    /** Expectation builders constructed during the setup phase. */
    private final List<BUILDER> builders = new ArrayList<BUILDER>();

    /**
     * Factory method providing a new builder for an invocation checker.
     *
     * @param object
     *            the invoked object for which we create the builder
     * @param closureHandler
     *            handles the closure of the specification, or null
     * @return a new invocation checker builder.
     */
    protected abstract BUILDER createBuilder(Object object,
      InvocationCheckerClosureHandler closureHandler);

    /** @return The list of builders. */
    private List<BUILDER> getList() {
        return builders;
    }

    /** @return The current number of checkers built. */
    protected int getListSize() {
        return builders.size();
    }

    /**
     * Completes the last checker builder, if any.
     */
    private void completeLastCheckerBuilder() {
        logger.trace("completeLastCheckerBuilder");
        // If an expectation builder is already here, complete it.
        if (!builders.isEmpty()) {
            builders.get(builders.size() - 1).throwIfNotClosed();
        }
    }

    /**
     * @return The checker builder constructing the current checker.
     * @throws IllegalClauseException
     *             No expectation is currently registered.
     */
    private BUILDER getCurrentCheckerBuilder() {
        logger.trace("getCurrentCheckerBuilder");
        if (!builders.isEmpty()) {
            return builders.get(builders.size() - 1);
        } else {
            throw new IllegalClauseException(
              "clause lacks a preliminary invocation");
        }
    }

    /**
     * Registers a new checker builder in the list.
     *
     * <p>
     * Calls back <code>createBuilder</code> to get an actual builder.
     * </p>
     *
     * @param object
     *            the invoked object for which we create a builder
     * @param closureHandler
     *            handles the closure of the specification, or null
     * @throws MockReferenceException
     *             The specified object is not a mock.
     */
    protected void registerCheckerBuilder(Object object,
      InvocationCheckerClosureHandler closureHandler) {
        logger.trace("registerCheckerBuilder", "closureHandler=", closureHandler);
        completeLastCheckerBuilder();
        builders.add(createBuilder(object, closureHandler));
    }

    /**
     * An iterator on the checkers enclosed by the created list of builders.
     */
    private class CheckerIterator implements Iterator<CHECKER> {

        /** Index on the current builder. */
        private int currentBuilderIndex = 0;

        @Override
        public boolean hasNext() {
            return currentBuilderIndex < builders.size();
        }

        /** Will not return the checker if its builder is not closed. */
        @Override
        public CHECKER next() {
            BUILDER currentBuilder = builders.get(currentBuilderIndex++);
            currentBuilder.throwIfNotClosed();
            return currentBuilder.getChecker();
        }

        @Override
        public void remove() {
            builders.remove(currentBuilderIndex);
        }
    }

    @Override
    public Iterator<CHECKER> iterator() {
        return new CheckerIterator();
    }

    /**
     * Provides a default value for a given class.
     *
     * @param clazz
     *            the requested class
     * @return The created default value.
     */
    @SuppressWarnings("unchecked")
    private static <T> T getDefaultValueFor(Class<?> clazz) {
        return (T) InvocationResult.getDefaultValueForClass(clazz);
    }

    /**
     * Verifies that a user supplied class is allowed to create a clause.
     *
     * <p>
     * Under specific circumstances the user can pass a null class to a clause.
     * </p>
     *
     * @param clazz
     *            the user supplied class
     * @throws IllegalClassDefinitionException
     *             The class is not allowed.
     */
    private static void validateClassArgument(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalClassDefinitionException(null);
        }
    }

    /**
     * Registers any argument of a given class for the expectation under
     * construction.
     *
     * @param clazz
     *            the expected class of arguments
     * @return An object of the requested class.
     * @throws IllegalClauseException
     *             No expectation is currently registered.
     * @throws IllegalClassDefinitionException
     *             The specified class is null.
     */
    @SuppressWarnings("unchecked")
    public final <T> T anyOf(Class<T> clazz) {
        logger.trace("anyOf", "clazz=", clazz);
        validateClassArgument(clazz);
        ClassChecker checker = anyArgumentOf(clazz);
        getCurrentCheckerBuilder().registerArgument(checker);
        return (T) getDefaultValueFor(clazz);
    }

    /**
     * Registers any non-null argument of a given class for the expectation
     * under construction.
     *
     * @param clazz
     *            the expected class of arguments
     * @return An object of the requested class.
     * @throws IllegalClauseException
     *             No expectation is currently registered.
     * @throws IllegalClassDefinitionException
     *             The specified class is null.
     */
    @SuppressWarnings("unchecked")
    public final <T> T aNonNullOf(Class<T> clazz) {
        logger.trace("aNonNullOf", clazz);
        validateClassArgument(clazz);
        ClassChecker checker = anyNonNullArgumentOf(clazz);
        getCurrentCheckerBuilder().registerArgument(checker);
        return (T) getDefaultValueFor(clazz);
    }

    /**
     * Registers an argument for the current expectation under construction.
     *
     * <p>
     * This clause specifies the exact value expected during the invocations.
     * </p>
     *
     * @param object
     *            the registered object
     * @return An object of the requested class.
     * @throws IllegalClauseException
     *             No expectation is currently registered.
     */
    public final <T> T with(T object) {
        logger.trace("with", "object=", object);
        getCurrentCheckerBuilder().registerArgument(object);
        return object;
    }

    /**
     * Registers an explicit checker to an argument for the current expectation
     * under construction.
     *
     * @param checker
     *            the registered checker
     * @return An object of the related class of the checker
     * @throws IllegalClauseException
     *             No expectation is currently registered.
     */
    @SuppressWarnings("unchecked")
    public final <T> T with(Checker<T> checker) {
        logger.trace("with", "checker=", checker);
        // Nasty trick: the argument may be null due to the potential confusion
        // with the 'with' clause taking objects as arguments (this is how the
        // compiler would prioritize the arguments).
        if (checker == null) {
            getCurrentCheckerBuilder().registerArgument(null);
            return null;
        } else {
            getCurrentCheckerBuilder().registerArgument(checker);
            return (T) getDefaultValueFor(checker.getRelatedClass());
        }
    }

    /**
     * @return The current checker under construction, null if none.
     */
    protected CHECKER getCurrentChecker() {
        return getCurrentCheckerBuilder().getChecker();
    }

    /**
     * Appends a list of expectations, provided by a scenario to the current scenario.
     *
     * @param newBuilders
     *            the list of appended builders.
     */
    public void append(
      InvocationCheckerListBuilder<CHECKER, BUILDER> newBuilders) {
        for (BUILDER builder : newBuilders.getList()) {
            builders.add(builder);
        }
    }
}
