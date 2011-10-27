/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.impl;

import java.lang.reflect.Method;

import com.vmware.lmock.checker.Checker;
import com.vmware.lmock.exception.IncompatibleReturnValueException;
import com.vmware.lmock.exception.IncompatibleThrowableException;

/**
 * Validate the specification of an invocation result within an invocation
 * checker.
 *
 * <p>
 * In addition to the traditional checker framework, this class implements the
 * <code>willReturn</code> and <code>willThrow</code> clauses that can be
 * directly included by invocation checkers.
 * </p>
 */
class InvocationResultChecker implements Checker<InvocationResult> {
    /** The method for which we check the result. */
    private final Method method;

    /**
     * Creates a new checker for a given method.
     *
     * @param method
     *            the method
     */
    protected InvocationResultChecker(Method method) {
        this.method = method;
    }

    /**
     * @param value
     *            a value that is expected to be returned by the method
     * @return <code>true</code> if a specified value can be returned.
     */
    private boolean returnedValueIsCompatibleWith(Object value) {
        // Check the class compatibility.
        Class<?> expectedClass = method.getReturnType();
        // Use an class checker to verify the result compliance.
        ClassChecker checker = ClassChecker.anyArgumentOf(expectedClass);
        return checker.valueIsCompatibleWith(value);
    }

    /**
     * @param excpt
     *            an exception that is expected to be thrown by the method
     * @return <code>true</code> if a specified exception can be thrown.
     */
    private boolean exceptionIsCompatibleWith(Throwable excpt) {
        // Check only if the system allows us to do it.
        if (!SystemSpecifics.willCheckExceptionIntegrity()) {
            return true;
        }

        Class<?> checkedClass = excpt.getClass();
        if (RuntimeException.class.isAssignableFrom(checkedClass)) {
            return true;
        } else {
            for (Class<?> throwable : method.getExceptionTypes()) {
                // Notice that there's no real need for a class checker here
                // since we deal with exceptions, which are pretty "rough"
                // classes.
                if (throwable.isAssignableFrom(checkedClass)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean valueIsCompatibleWith(InvocationResult value) {
        if (value.isAnException()) {
            return exceptionIsCompatibleWith(value.getException());
        } else {
            return returnedValueIsCompatibleWith(value.getReturnedValue());
        }
    }

    @Override
    public Class<?> getRelatedClass() {
        return InvocationResult.class;
    }

    /**
     * Specifies a value returned by the invoked method.
     *
     * @param result
     *            the returned value.
     * @return The built invocation result.
     * @throws IncompatibleReturnValueException
     *             The returned type does not match the method's returned value.
     */
    public <T> InvocationResult willReturn(T result) {
        InvocationResult invocationResult = InvocationResult.returnValue(result);
        if (!valueIsCompatibleWith(invocationResult)) {
            throw new IncompatibleReturnValueException(method.getReturnType(),
              result);
        } else {
            return invocationResult;
        }
    }

    /**
     * Specifies an exception by the invoked method.
     *
     * @param excpt
     *            the exception.
     * @return The built invocation result.
     * @throws IncompatibleThrowableException
     *             This type of exception cannot be thrown by the method.
     */
    public InvocationResult willThrow(Throwable excpt) {
        InvocationResult invocationResult = InvocationResult.throwException(excpt);
        if (!valueIsCompatibleWith(invocationResult)) {
            throw new IncompatibleThrowableException(excpt.getClass());
        } else {
            return invocationResult;
        }
    }

    public InvocationResult will(InvocationResult result) {
        if (result.isAReturnedValue()) {
            willReturn(result.getReturnedValue());
        } else {
            willThrow(result.getException());
        }

        return result;
    }
}
