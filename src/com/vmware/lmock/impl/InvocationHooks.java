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
 * Hooks to the most common basic methods of objects.
 *
 * <p>
 * The purpose of a hook is to provide default implementations of the most
 * common methods, in order for a user not to define expectations for the
 * invocations of those methods in his scenarios. The methods are:
 * </p>
 * <ul>
 * <li><code>equals</code> and <code>hashCode</code>: relying on the mock's
 * methods</li>
 * <li><code>toString</code>: displaying the string of the associated mock</li>
 * </ul>
 *
 * <p>
 * The class also provides a way to overwrite each of those methods for specific
 * purpose.
 * </p>
 *
 * <p>
 * For the implementation standpoint, the hooks object must be invoked before
 * actually processing the invocation, using <code>tryInvocation</code>. The
 * method returns the invocation result if the invoked method is handled by the
 * hooks.
 * </p>
 *
 * <p>
 * <b>Note:</b> <code>getClass</code> does not need to be implemented, because
 * the proxies theoretically trap these calls.
 * </p>
 */
final class InvocationHooks {
    /**
     * Representation of a default handler, attached to a hook.
     */
    private interface DefaultHandler {
        /**
         * Applies an invocation to the handled method.
         *
         * @param invocation
         *            the invocation
         * @return The result of the default handler.
         */
        InvocationResult apply(Invocation invocation);
    }

    /**
     * Inner representation of one hook.
     */
    private static final class Hook {
        /** The hooked method. */
        private final Method method;
        private final DefaultHandler defaultHandler;

        /**
         * Replaces <code>Method.equals()</code>.
         *
         * The main reason for this is that we do not really care of the
         * declaring class of a method, and it may be different. At this level
         * of the processing, we know who exactly invoked us and why.
         *
         * @param other
         *            the method to compare to our method
         * @return true if the methods match.
         */
        private boolean methodMatch(Method other) {
            // Note: was checked against the source code of Method.equals.
            if (method.getName().equals(other.getName())
              && method.getReturnType().equals(other.getReturnType())) {
                Class<?>[] methodParams = method.getParameterTypes();
                Class<?>[] otherParams = other.getParameterTypes();
                if (methodParams.length == otherParams.length) {
                    for (int index = 0; index < otherParams.length; index++) {
                        if (methodParams[index] != otherParams[index]) {
                            return false;
                        }
                    }

                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        /**
         * Creates a hook for a given method.
         *
         * @param method
         *            the method
         * @param handler
         *            a default handler for this method
         */
        private Hook(Method method, DefaultHandler handler) {
            this.method = method;
            defaultHandler = handler;
        }

        /**
         * Compares a method with the method enclosed by this hook.
         *
         * @param method
         *            the checked method
         * @return true if the methods match.
         */
        boolean match(Method method) {
            return methodMatch(method);
        }

        /**
         * @return The default handler for the enclosed method, null if not
         *         enabled.
         */
        DefaultHandler getDefaultHandler() {
            return defaultHandler;
        }
    };
    /** The number of common methods managed by the hooks. */
    private static final int NR_COMMON_METHODS = 3;
    /** List of hooks. */
    private final Hook[] hooks = new Hook[NR_COMMON_METHODS];
    /**
     * The default handler for the <code>equals</code> method.
     *
     * <p>
     * The returned value is the result of the comparison of the object argument
     * with the invocation mock.
     * </p>
     */
    private final DefaultHandler equalsDefaultHandler = new DefaultHandler() {
        @Override
        public InvocationResult apply(Invocation invocation) {
            // Be careful to provide the proper reference when comparing with
            // the mock.
            Object other = Mock.getObjectOrMock(invocation.getArgs()[0]);
            return InvocationResult.returnValue(invocation.getMock().equals(
              other));
        }
    };
    /**
     * The default handler for the <code>hashCode</code> method.
     *
     * <p>
     * The returned value is the hash code of the invocation mock (coherent with
     * <code>equals</code>).
     * </p>
     */
    private final DefaultHandler hashCodeDefaultHandler = new DefaultHandler() {
        @Override
        public InvocationResult apply(Invocation invocation) {
            return InvocationResult.returnValue(invocation.getMock().hashCode());
        }
    };
    /**
     * The default handler of the <code>toString</code> method.
     *
     * <p>
     * The returned value is the string representing the invocation mock.
     * </p>
     */
    private final DefaultHandler toStringDefaultHandler = new DefaultHandler() {
        @Override
        public InvocationResult apply(Invocation invocation) {
            return InvocationResult.returnValue(invocation.getMock().toString());
        }
    };

    /**
     * Creates a new set of hooks.
     */
    InvocationHooks() {
        try {
            hooks[0] = new Hook(Object.class.getMethod("equals", Object.class),
              equalsDefaultHandler);
            hooks[1] = new Hook(Object.class.getMethod("hashCode"),
              hashCodeDefaultHandler);
            hooks[2] = new Hook(Object.class.getMethod("toString"),
              toStringDefaultHandler);
        } catch (NoSuchMethodException e) {
            // This block is required, but this will never occur in practice.
            throw new AssertionError("BUG!");
        }
    }

    /**
     * Searches for a hook that matches a method.
     *
     * @param method
     *            the checked method
     * @return The fetched hook, if found, null otherwise.
     */
    private Hook searchForHook(Method method) {
        for (int hookIndex = 0; hookIndex < NR_COMMON_METHODS; hookIndex++) {
            if (hooks[hookIndex].match(method)) {
                return hooks[hookIndex];
            }
        }

        return null;
    }

    /**
     * Checks if an invocation corresponds to a hook method and applies the
     * default invocation if needed.
     *
     * @param invocation
     *            the invocation
     * @return The invocation result if the default call is applied, null
     *         otherwise.
     */
    InvocationResult tryInvocation(Invocation invocation) {
        // Result of this trial.
        InvocationResult result = null;

        Hook hook = searchForHook(invocation.getMethod());
        if (hook != null) {
            DefaultHandler handler = hook.getDefaultHandler();
            if (handler != null) {
                return handler.apply(invocation);
            }
        }

        return result;
    }
}
