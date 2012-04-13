/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.impl;

import java.lang.reflect.Method;

/**
 * Internal representation of an invocation issued by the user.
 */
final class Invocation {
    private final Mock mock;
    private final Method method;
    private final Object[] args;

    /**
     * Creates a new invocation.
     *
     * @param mock
     *            the mock catching the invocation
     * @param object
     *            the invoked object
     * @param method
     *            the invoked method
     * @param args
     *            the arguments passed to the invoked method
     */
    protected Invocation(Mock mock, Object object, Method method, Object[] args) {
        this.mock = mock;
        this.method = method;
        this.args = args;
    }

    /** @return The mock handling the invocation. */
    Mock getMock() {
        return mock;
    }

    /** @return The invoked method. */
    Method getMethod() {
        return method;
    }

    /** @return The arguments passed to the invoked method. */
    Object[] getArgs() {
        return args;
    }

    /**
     * Creates a string for a list of arguments.
     *
     * @param args
     *            the argument list
     * @return The argument list as a string.
     */
    private static String makeArgsString(Object[] args) {
        if (args == null) {
            return "";
        } else {
            StringBuilder builder = new StringBuilder(32);
            String prefix = "";
            for (Object arg : args) {
                builder.append(prefix);
                builder.append(Mock.getObjectOrMock(arg));
                prefix = ",";
            }

            return builder.toString();
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(64);

        builder.append(mock);
        builder.append('.');
        builder.append(method.getName());
        builder.append('(');
        builder.append(makeArgsString(args));
        builder.append(')');

        return builder.toString();
    }
}
