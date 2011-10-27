/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.clauses;

import com.vmware.lmock.exception.IncompatibleReturnValueException;
import com.vmware.lmock.exception.IncompatibleThrowableException;
import com.vmware.lmock.impl.InvocationResultProvider;

/**
 * Defines clauses that allow to specify the result of an invocation.
 *
 *
 * @param <T>
 *            the class that implements that interface (e.g. expectation, stub)
 */
public interface HasInvocationResultSpecificationClauses<T> {
    /**
     * Clause specifying a result.
     *
     * @param result
     *            the invocation result
     * @return The stub under construction.
     * @throws IncompatibleReturnValueException
     *             The specified result is a returned value, incompatible with
     *             the stubbed method.
     * @throws IncompatibleThrowableException
     *             The specified result is an exception, incompatible with the
     *             stubbed method.
     */
    public T will(InvocationResultProvider result);

    /**
     * Clause specifying a user supplied result.
     *
     * <p>
     * This clause gives an opportunity to apply test specific operations upon
     * an invocation. A particular attention shall be payed at not implementing
     * procedures that would lead to unexpected behaviors (such as invoking
     * mocks).
     * </p>
     *
     * @param provider
     *            the object providing the invocation result
     * @return The stub under construction.
     * @throws IllegalArgumentException
     *            The provider is <code>null</code>.
     */
    public T willDelegateTo(InvocationResultProvider provider);

    /**
     * Clause specifying the value returned by the current stub.
     *
     * @param <C>
     *            type of the returned value
     * @param result
     *            the returned value
     * @return The stub under construction.
     * @throws IncompatibleReturnValueException
     *             The specified result is incompatible with the stubbed method.
     */
    public <C> T willReturn(C result);

    /**
     * Clause specifying the exception thrown by the current stub.
     *
     * @param <C>
     *            type of the thrown exception
     * @param excpt
     *            the thrown exception
     * @return The stub under construction.
     * @throws IncompatibleThrowableException
     *             The specified exception is incompatible with the stubbed
     *             method.
     */
    public <C extends Throwable> T willThrow(C excpt);
}
