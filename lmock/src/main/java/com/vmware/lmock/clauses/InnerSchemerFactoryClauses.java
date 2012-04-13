/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.clauses;

import com.vmware.lmock.checker.OccurrenceChecker;
import com.vmware.lmock.exception.IncompatibleReturnValueException;
import com.vmware.lmock.exception.IncompatibleThrowableException;
import com.vmware.lmock.impl.InvocationResultProvider;

/**
 * A set of interfaces that allow to build the grammar of the schemer factory.
 */
public interface InnerSchemerFactoryClauses {
    /**
     * An object that allows to associate a mock to an expectation.
     */
    interface HasOfClause {
        /**
         * Prepares a mock to build an expectation.
         *
         * @param <T>
         *            type of the invoked object
         * @param mock
         *            the mock object for which we build an expectation
         * @return The mock object.
         */
        public <T> T of(T mock);
    }

    /**
     * An object that allows to associate a mock to a stub.
     */
    interface HasWhenClause {
        /**
         * Prepares a mock to build a stub (or indirectly an invocation).
         *
         * @param <T>
         *            type of the invoked object
         * @param mock
         *            the mock object for which we build an expectation
         * @return The mock object.
         */
        public <T> T when(T mock);
    }

    /**
     * An object that allows to specify the result of an invocation.
     *
     * <p>
     * Stubs are implicitly created by such clauses (but they may become
     * expectations during the specification process).
     * </p>
     */
    interface HasResultClauses {
        /**
         * Specifies a return value for an invocation.
         *
         * @param <T>
         *            type of the returned value
         * @param result
         *            the returned value
         * @return The object that will allow to specify the invocation.
         * @throws IncompatibleReturnValueException
         *             The specified result is incompatible with the stubbed
         *             method.
         */
        public <T> HasWhenClause willReturn(T result);

        /**
         * Specifies an exception thrown during an invocation.
         *
         * @param <T>
         *            type of the thrown exception
         * @param excpt
         *            the raised exception
         * @return The object that will allow to specify the invocation.
         * @throws IncompatibleThrowableException
         *             The specified exception is incompatible with the stubbed
         *             method.
         */
        public <T extends Throwable> HasWhenClause willThrow(T excpt);

        /**
         * Specifies the result of an invocation.
         *
         * <p>
         * The argument should be one of the pre-defined invocation results.
         * </p>
         *
         * @param result
         *            the invocation result
         * @return The object that will allow to specify the invocation.
         * @throws IncompatibleReturnValueException
         *             The specified result is incompatible with the stubbed
         *             method.
         * @throws IncompatibleThrowableException
         *             The specified exception is incompatible with the stubbed
         *             method.
         */
        public HasWhenClause will(InvocationResultProvider result);

        /**
         * Clause specifying a user supplied result.
         *
         * <p>
         * This clause gives an opportunity to apply test specific operations
         * upon an invocation. A particular attention shall be payed at not
         * implementing procedures that would lead to unexpected behaviors (such
         * as invoking mocks).
         * </p>
         *
         * @param provider
         *            the object providing the invocation result
         * @return The object that will allow to specify the invocation.
         */
        public HasWhenClause willDelegateTo(InvocationResultProvider provider);
    }

    /**
     * An object that allows to specify clauses to <code>expect</code>.
     */
    interface HasExpectationClauses extends HasOfClause, HasResultClauses {
    }

    /**
     * An object that allows to create an expectation.
     */
    interface HasExpectationFactoryClauses {
        /**
         * Specifies that we are going to create an expectation.
         *
         * @param occurrences
         *            the occurrence checker validating the expectation
         * @return The object that will allow to specify the clauses of the
         *         expectation.
         */
        HasExpectationClauses willInvoke(OccurrenceChecker occurrences);

        /**
         * Expects exactly a specific number of invocations.
         *
         * @param n
         *            the expected number of invocations
         * @return The object that will allow to specify the clauses of the
         *         expectation.
         */
        HasExpectationClauses willInvoke(int n);
    }
}
