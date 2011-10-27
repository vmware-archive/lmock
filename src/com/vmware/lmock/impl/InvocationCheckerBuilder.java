/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.impl;

import static com.vmware.lmock.impl.MockInvocationHandlerType.CONSTRUCTOR;

import java.lang.reflect.Method;

import com.vmware.lmock.checker.Checker;
import com.vmware.lmock.exception.IllegalClauseException;
import com.vmware.lmock.exception.MissingInvocationException;
import com.vmware.lmock.exception.MockReferenceException;
import com.vmware.lmock.exception.UnexpectedInvocationError;

/**
 * Class building an invocation checker on the fly.
 *
 * <p>
 * A builder is temporarily trapping an invocation to a mock object, specified
 * to its constructor, to build a checker. The result can then be read with
 * <code>getChecker</code>. In other words, when the user specifies a checker by
 * invoking the method of a mock, this class traps that invocation and build the
 * corresponding expectation.
 * </p>
 *
 * <p>
 * The builder also provides an argument list which can be accessed by 'with'
 * clauses to build an off-line list of arguments. This list can be populated
 * using <code>registerArgument</code>.
 * </p>
 *
 * <p>
 * Finally, users must not forget to call the <code>complete</code> method when
 * the building of this checker is finished in order to unregister this builder
 * from the associated mock.
 * </p>
 *
 * <p>
 * Notice that this class is abstract. It invokes the invocation checker factory
 * method <code>createInvocationChecker</code> when the checker actually needs
 * to be built.
 * </p>
 */
abstract class InvocationCheckerBuilder<T extends InvocationChecker> implements MockInvocationHandler {
    /** Logs the checker builder activity. */
    private static final Logger logger = Logger.get(InvocationCheckerBuilder.class);
    /** The checker for which we create this builder, null if not yet created. */
    private T builtChecker;
    private final CheckerList argumentCheckerList = new CheckerList();
    /** The invoked object. */
    private final Object invokedObject;
    /** The related mock. */
    private final Mock mock;
    /** Handles the closure of the specification. */
    private final InvocationCheckerClosureHandler closureHandler;
    /** Asserted when the associated method was invoked. */
    private boolean invocationFound;

    /**
     * Generates the invocation checker.
     *
     * @param object
     *            the invoked object
     * @param method
     *            the invoked method
     * @return The new invocation checker.
     */
    protected abstract T createInvocationChecker(Object object, Method method);

    /**
     * @return The current checker under construction, null if not yet created.
     */
    protected T getChecker() {
        return builtChecker;
    }

    /**
     * Creates an builder that will be specified an invocation.
     *
     * <p>
     * This builder registers as the current invocation checker for the
     * specified mock, so that the forthcoming invocation will be trapped and
     * allow to build the argument list.
     * </p>
     *
     * @param object
     *            the object that will be checked
     * @param closureHandler
     *            handles the closure of the specification if not null
     * @throws MockReferenceException
     *             The specified object is not a mock.
     */
    protected InvocationCheckerBuilder(Object object,
      InvocationCheckerClosureHandler closureHandler) {
        mock = Mock.getProxyOrThrow(object);
        invokedObject = object;
        mock.setInvocationHandler(CONSTRUCTOR, this);
        this.closureHandler = closureHandler;
        logger.trace("InvocationCheckerBuilder", "mock=", mock, "invokedObject=", invokedObject, "closureHandler=",
          closureHandler);
    }

    /**
     * Prevents from multiple invocations when constructing an invocation
     * checker.
     *
     * <p>
     * By essence, an checker is for one and only one invocation.
     * </p>
     *
     * @return false if the invocation checker is already built.
     */
    private boolean isInvocationLegal() {
        return builtChecker == null;
    }

    /**
     * Prevents from adding arguments to an checker when the invocation has
     * already been done.
     *
     * @return false if the argument list cannot be enriched.
     */
    private boolean canRegisterArgument() {
        return isInvocationLegal();
    }

    /**
     * Registers a new argument for the checked invocation.
     *
     * <p>
     * This procedure must be used when using 'with' clauses. It adds the
     * specified argument to the off-line list of arguments for this
     * expectation.
     * </p>
     *
     * @param arg
     *            the new argument
     * @throws IllegalClauseException
     *             The expectation is already registered, cannot add more
     *             arguments.
     */
    protected void registerArgument(Object arg) {
        logger.trace("registerArgument", "arg", arg);
        if (canRegisterArgument()) {
            argumentCheckerList.add(ObjectCheckerFactory.getExistingOrNewChecker(arg));
        } else {
            // Note: keep the word "expectation" here... Would be confusing
            // otherwise.
            throw new IllegalClauseException(
              "cannot register argument: expectation already specified");
        }
    }

    /**
     * Picks a list of arguments and creates the corresponding list of checkers.
     *
     * <p>
     * Every argument that is expressed as a raw value is provided a value
     * checker.
     * </p>
     *
     * @param arguments
     *            the argument list
     * @return The list of checkers.
     */
    private Checker<?>[] createCheckers(Object[] arguments) {
        logger.trace("createCheckers", arguments.length, "arguments");
        Checker<?>[] result = new Checker<?>[arguments.length];
        for (int index = 0; index < arguments.length; index++) {
            // Note: if the current argument is a checker, then the invoked
            // method expects a true checker as argument. Which means that
            // we actually want to validate the checker value, not use it.
            result[index] = ObjectCheckerFactory.getNewChecker(arguments[index]);
        }

        return result;
    }

    /**
     * Terminates the invocation of the method to close the specification.
     */
    private void closeSpecification() {
        invocationFound = true;
        if (closureHandler != null) {
            logger.trace("closeSpecification", "invoking closure handler");
            closureHandler.onClosure();
        }
    }

    @Override
    public InvocationResult invoke(Invocation invocation) {
        logger.trace("invoke", "invocation", invocation);
        if (!isInvocationLegal()) {
            throw new UnexpectedInvocationError(invocation.toString());
        }

        builtChecker = createInvocationChecker(invokedObject,
          invocation.getMethod());

        if (argumentCheckerList.size() != 0) {
            builtChecker.with(argumentCheckerList.buildArray(invocation));
        } else {
            if (invocation.getArgs() != null) {
                builtChecker.with(createCheckers(invocation.getArgs()));
            }
        }

        // The checker is built, this mock should not be available for building
        invocation.getMock().unsetInvocationHandler(CONSTRUCTOR);
        closeSpecification();
        // Generate an acceptable returned value for the invocation, in order
        // not to break the overlying proxy.
        Class<?> returnedType = invocation.getMethod().getReturnType();
        return InvocationResult.getDefaultResultForClass(returnedType);
    }

    /**
     * Verifies that the builder has reached its closure point.
     *
     * <p>
     * The builder is not closed when the user created the builder (e.g.
     * <code>expect(mock)</code>) but did not invoke any method.
     * </p>
     *
     * @throws MissingInvocationException
     *             No invocation found.
     */
    protected void throwIfNotClosed() {
        if (!invocationFound) {
            throw new MissingInvocationException();
        }
    }
}
