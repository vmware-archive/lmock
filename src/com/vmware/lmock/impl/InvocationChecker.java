/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.impl;

import static com.vmware.lmock.impl.ClassChecker.anyArgumentOf;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.vmware.lmock.checker.Checker;
import com.vmware.lmock.exception.MockReferenceException;

/**
 * Registers an invocation and compares subsequent invocations with this one.
 *
 * <p>
 * An invocation checker is defined by an expected object (a mock), a method and
 * expected arguments (represented by checkers, registered by the
 * <code>with</code> method).
 * </p>
 *
 * <p>
 * It provides the capability to compare an invocation with the registered
 * profile, via <code>isSatisfiedBy</code>.
 * </p>
 */
class InvocationChecker implements Checker<Invocation> {
    /** Proxy enclosing the mock object. */
    private final Mock proxy;
    /** Expected method name. */
    private final Method method;
    /** List of expected arguments to the method. */
    private final List<Checker<Object>> expectedArguments = new ArrayList<Checker<Object>>();

    /**
     * Sets the basic configuration of a stub.
     *
     * <p>
     * Resets every argument to an "any" value.
     * </p>
     */
    private void setupBasicConfiguration() {
        for (Class<?> type : method.getParameterTypes()) {
            expectedArguments.add(anyArgumentOf(type));
        }
    }

    /**
     * Creates a new invocation checker for a mock and a method.
     *
     * @param mock
     *            the mock to which this stub applies
     * @param method
     *            the method
     * @throws MockReferenceException
     *             The specified object is not a mock.
     */
    protected InvocationChecker(Object mock, Method method) {
        proxy = Mock.getProxyOrThrow(mock);
        this.method = method;
        setupBasicConfiguration();
    }

    /**
     * Registers the list of expected arguments when invoking the method.
     *
     * <p>
     * The arguments are represented by their associated checkers.
     * </p>
     *
     * <p>
     * This method must be invoked only once, otherwise it resets the argument
     * list. Moreover, the supplied argument list must be coherent with the
     * prototype of the invoked method. In particular, the list must have
     * exactly the same number of arguments as defined by the prototype of the
     * method.
     * </p>
     *
     * @param checkerList
     *            the list of checkers to the arguments. Can be null.
     */
    @SuppressWarnings("unchecked")
    protected <T> void with(Checker<?>... checkerList) {
        // The argument list may be null to express that there is no argument.
        if (checkerList != null) {
            // Populate the list of checkers, associating an argument checker
            // to the supplied checker.
            for (int index = 0; index < checkerList.length; index++) {
                // We can safely cast to a Checker<Object> here: primitive types
                // have been auto-boxed.
                expectedArguments.set(index,
                  (Checker<Object>) checkerList[index]);
            }
        }
    }

    /** @return The proxy object associated to this checker. */
    protected final Mock getProxy() {
        return proxy;
    }

    /** @return The method associated to this checker. */
    protected final Method getMethod() {
        return method;
    }

    /**
     * Validates the invocation regarding the arguments passed to the method.
     */
    @Override
    public boolean valueIsCompatibleWith(Invocation invocation) {
        if (invocation.getMock().getUid() != proxy.getUid()) {
            return false;
        }

        if (!invocation.getMethod().getName().equals(method.getName())) {
            return false;
        }

        // Be careful! args can be null (case of void methods).
        if (invocation.getArgs() == null) {
            return expectedArguments.isEmpty();
        } else if (expectedArguments.size() != invocation.getArgs().length) {
            return false;
        } else {
            Object[] arguments = invocation.getArgs();
            for (int index = 0; index < arguments.length; index++) {
                Checker<Object> currentChecker = expectedArguments.get(index);
                if (!currentChecker.valueIsCompatibleWith(arguments[index])) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public String toString() {
        // Builds a string with the actual definition of the expectation,
        // including the method name, the arguments...
        StringBuilder builder = new StringBuilder(64);

        // Display the qualified method name
        builder.append(proxy.getMockedClass());
        builder.append('.');
        builder.append(method.getName());

        // Display the expected arguments
        builder.append('(');
        for (int idx = 0; idx < expectedArguments.size(); idx++) {
            builder.append(expectedArguments.get(idx).toString());
            if (idx < expectedArguments.size() - 1) {
                builder.append(',');
            }
        }
        builder.append(')');

        return builder.toString();
    }

    @Override
    public Class<?> getRelatedClass() {
        return Invocation.class;
    }
}
