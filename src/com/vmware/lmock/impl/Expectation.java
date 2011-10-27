/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.impl;

import java.lang.reflect.Method;

import com.vmware.lmock.checker.OccurrenceChecker;
import com.vmware.lmock.checker.Occurrences;
import com.vmware.lmock.clauses.HasInvocationResultSpecificationClauses;
import com.vmware.lmock.clauses.HasOccurrencesSpecificationClauses;
import com.vmware.lmock.exception.MockReferenceException;

/**
 * Representation of an expectation from the test.
 *
 * <p>
 * An expectation represents the invocation of an expected method, specified to
 * the constructor. The user can then tune the form of the expectation via the
 * following methods:
 * </p>
 * <ul>
 * <li><code>with</code>: defines the list of expected arguments expected when
 * invoking the method.</li>
 * <li><code>willReturn</code>: by default, returns a default dummy value. This
 * method allows to explicitly specify what to return.</li>
 * <li><code>willThrow</code>: by default, the invoked method does not throw an
 * exception. This method allows to issue an exception each time the method is
 * invoked.</li>
 * <li><code>willDelegateTo</code>: handle the invocation result with a user supplied
 * method.</li>
 * <li><code>occurs</code>: defines the occurrences of the expected invocation.</li>
 * </ul>
 */
public final class Expectation extends InvocationChecker implements
  HasInvocationResultSpecificationClauses<Expectation>,
  HasOccurrencesSpecificationClauses<Expectation> {
    /** Result applied by the expectation when the method is invoked. */
    private InvocationResultProvider invocationResult;
    /** Expected occurrences of this expectation. By default, none specific. */
    private OccurrenceChecker occurrences = Occurrences.any();

    /**
     * Creates a new expectation, assuming that we know the method.
     *
     * @param mock
     *            the mock to which this expectation applies
     * @param method
     *            the method
     * @throws MockReferenceException
     *             The specified object is not a mock.
     */
    protected Expectation(Object mock, Method method) {
        super(mock, method);
        invocationResult = InvocationResult.getDefaultResultForClass(method.getReturnType());
    }

    /** @return The occurrences of this expectation, never null. */
    OccurrenceChecker getOccurrences() {
        return occurrences;
    }

    @Override
    public <T> Expectation willReturn(T result) {
        invocationResult = new InvocationResultChecker(getMethod()).willReturn(result);
        return this;
    }

    @Override
    public <T extends Throwable> Expectation willThrow(T excpt) {
        invocationResult = new InvocationResultChecker(getMethod()).willThrow(excpt);
        return this;
    }

    @Override
    public Expectation will(InvocationResultProvider result) {
        if (result instanceof InvocationResult) {
            invocationResult = new InvocationResultChecker(getMethod()).will((InvocationResult) result);
            return this;
        } else {
            return willDelegateTo(result);
        }
    }

    @Override
    public Expectation willDelegateTo(InvocationResultProvider provider) {
        invocationResult = provider;
        return this;
    }

    @Override
    public Expectation occurs(OccurrenceChecker occurrences) {
        this.occurrences = occurrences;
        return this;
    }

    @Override
    public Expectation occurs(int n) {
        occurrences = Occurrences.exactly(n);
        return this;
    }

    /**
     * Applies the user defined invocation result, simulating a call to the
     * object.
     *
     * <p>
     * Notice that the occurrence counter is incremented by this method.
     * </p>
     *
     * @return The invocation result, never null.
     */
    InvocationResultProvider getResult() {
        occurrences.increment();
        return invocationResult;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(super.toString());

        // Display the result of an invocation
        builder.append(':');
        builder.append(invocationResult);

        builder.append('/');
        builder.append(occurrences);

        return builder.toString();
    }
}
