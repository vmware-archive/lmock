/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.impl;

import java.lang.reflect.Method;

import com.vmware.lmock.clauses.HasInvocationResultSpecificationClauses;
import com.vmware.lmock.exception.MockReferenceException;

/**
 * Representation of stubs.
 *
 * <p>
 * A stub simulates the execution of a given method (with specific arguments) of a mock.
 * </p>
 *
 * <p>
 * The main advantage of this kind of stub compared to common java stubs is the
 * capability to simulate only specific methods of a class instead of the
 * complete class.
 * </p>
 */
public final class Stub extends InvocationChecker implements
  HasInvocationResultSpecificationClauses<Stub> {

    /** Logs the activity. */
    private static final Logger logger = Logger.get(Stub.class);
    /** Result applied by the expectation when the method is invoked. */
    private InvocationResultProvider invocationResult;

    /**
     * Creates a new stub.
     *
     * @param mock
     *            the mock to which this stub applies
     * @param method
     *            the method
     * @throws MockReferenceException
     *             The specified object is not a mock.
     */
    protected Stub(Object mock, Method method) {
        super(mock, method);
        logger.trace("Stub", "method=", method);
        invocationResult = InvocationResult.getDefaultResultForClass(method.getReturnType());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(super.toString());

        // Display the result of an invocation
        builder.append(':');
        builder.append(invocationResult);

        return builder.toString();
    }

    @Override
    public <T> Stub willReturn(T result) {
        invocationResult = new InvocationResultChecker(getMethod()).willReturn(result);
        return this;
    }

    @Override
    public <T extends Throwable> Stub willThrow(T excpt) {
        invocationResult = new InvocationResultChecker(getMethod()).willThrow(excpt);
        return this;
    }

    @Override
    public Stub will(InvocationResultProvider result) {
        if (result instanceof InvocationResult) {
            invocationResult = new InvocationResultChecker(getMethod()).will((InvocationResult) result);
            return this;
        } else {
            return willDelegateTo(result);
        }
    }

    @Override
    public Stub willDelegateTo(InvocationResultProvider provider) {
        invocationResult = provider;
        return this;
    }

    /** @return The result of an invocation to this stub. */
    InvocationResultProvider getInvocationResult() {
        return invocationResult;
    }
}
