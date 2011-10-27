/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.impl;

/**
 * Description of the action to be taken when the invocation is valid.
 */
public final class InvocationResult implements InvocationResultProvider {
    /** Exception thrown by the invocation, if any. */
    private final Throwable resultingException;
    /** Value returned by the invocation, if any. */
    private final Object returnedValue;
    /** True for a non-void result. */
    private final boolean hasReturnedValue;

    /**
     * Creates an invocation result.
     *
     * @param resultingException
     *            will raise the specified exception, null if none
     * @param returnedValue
     *            will return the specified value if no exception
     */
    private InvocationResult(Throwable resultingException, Object returnedValue) {
        this.resultingException = resultingException;
        if (resultingException == null) {
            hasReturnedValue = true;
            this.returnedValue = returnedValue;
        } else {
            hasReturnedValue = false;
            this.returnedValue = null;
        }
    }

    /**
     * Creates a new invocation result to throw an exception.
     *
     * @param resultingException
     *            the exception to throw
     * @return The built invocation result object.
     */
    public static InvocationResult throwException(Throwable resultingException) {
        return new InvocationResult(resultingException, null);
    }

    /**
     * Creates a new invocation result to return a value.
     *
     * @param returnedValue
     *            the returned value.
     * @return The built invocation result object.
     */
    public static InvocationResult returnValue(Object returnedValue) {
        return new InvocationResult(null, returnedValue);
    }

    /**
     * Generates a default value for a given class of objects.
     *
     * <p>
     * Basically this is a null value, except for primitive types.
     * </p>
     *
     * @param clazz
     *            the returned type
     * @return The default value.
     */
    static Object getDefaultValueForClass(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            if (clazz.equals(Boolean.TYPE)) {
                return Boolean.FALSE;
            } else if (clazz.equals(Character.TYPE)) {
                return ' ';
            } else if (clazz.equals(Byte.TYPE)) {
                return Byte.MIN_VALUE;
            } else if (clazz.equals(Short.TYPE)) {
                return Short.MIN_VALUE;
            } else if (clazz.equals(Integer.TYPE)) {
                return Integer.MIN_VALUE;
            } else if (clazz.equals(Long.TYPE)) {
                return Long.MIN_VALUE;
            } else if (clazz.equals(Float.TYPE)) {
                return Float.MIN_VALUE;
            } else if (clazz.equals(Double.TYPE)) {
                return Double.MIN_VALUE;
            } else {
                // Case of void methods.
                return null;
            }
        } else if (clazz.equals(Boolean.class)) {
            return Boolean.FALSE;
        } else if (clazz.equals(Character.class)) {
            return ' ';
        } else if (clazz.equals(Byte.class)) {
            return Byte.MIN_VALUE;
        } else if (clazz.equals(Short.class)) {
            return Short.MIN_VALUE;
        } else if (clazz.equals(Integer.class)) {
            return Integer.MIN_VALUE;
        } else if (clazz.equals(Long.class)) {
            return Long.MIN_VALUE;
        } else if (clazz.equals(Float.class)) {
            return Float.MIN_VALUE;
        } else if (clazz.equals(Double.class)) {
            return Double.MIN_VALUE;
        } else {
            return null;
        }
    }

    /**
     * Creates a new invocation result to return the default value of a given
     * type.
     *
     * @param clazz
     *            the returned type
     */
    private InvocationResult(Class<?> clazz) {
        resultingException = null;
        hasReturnedValue = false;
        returnedValue = getDefaultValueForClass(clazz);
    }

    /**
     * Factory creating an invocation result returning a default value of a
     * given type.
     *
     * <p>
     * Basically this is a null value, except for primitive types.
     * </p>
     *
     * @param clazz
     *            the returned type
     * @return The built invocation result
     */
    protected static final InvocationResult getDefaultResultForClass(
      Class<?> clazz) {
        return new InvocationResult(clazz);
    }

    @Override
    public String toString() {
        if (resultingException != null) {
            return "throws(" + resultingException + ")";
        } else if (hasReturnedValue) {
            // Avoid to provoke a new invocation if the returned value is a
            // mock. On the one hand we must be able to return mocks, on the
            // other hand we must not overflow the stack...
            return "returns(" + Mock.getObjectOrMock(returnedValue) + ")";
        } else {
            return "void";
        }
    }

    /**
     * Applies the result described by this invocation result.
     *
     * <p>
     * Notice that in case of void methods, the returned value is void.
     * </p>
     */
    @Override
    public Object apply() throws Throwable {
        if (resultingException != null) {
            throw resultingException;
        } else {
            // In practice there's always something to return... Can be
            // a default value.
            return returnedValue;
        }
    }

    /** @return <code>true</code> if the result is an exception. */
    boolean isAnException() {
        return resultingException != null;
    }

    /** @return The raised exception. */
    Throwable getException() {
        return resultingException;
    }

    /** @return <code>true</code> if the result is a value. */
    boolean isAReturnedValue() {
        return hasReturnedValue;
    }

    /** @return The returned value. */
    Object getReturnedValue() {
        return returnedValue;
    }
};
