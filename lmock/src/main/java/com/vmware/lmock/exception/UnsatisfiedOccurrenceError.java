/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.exception;

import com.vmware.lmock.impl.Expectation;

/**
 * The minimum number of occurrences of an invocation has not be reached.
 *
 * <p>
 * This happens, for example, when the invocation was not planned by the scenario or if the number of invocations
 * exceeds the maximum allowed by the expectation.
 * </p>
 */
public final class UnsatisfiedOccurrenceError extends ExpectationError {
    /** Class version, for serialization. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new exception.
     *
     * <p>
     * The constructor displays the current story track within its error
     * message.
     * </p>
     *
     * @param expectation
     *            the unsatisfied expectation
     */
    public UnsatisfiedOccurrenceError(Expectation expectation) {
        super("expectation '" + expectation + "' was not fully satisfied");
    }
}
