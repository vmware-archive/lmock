/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.exception;

/**
 * The invocation of a method shouldn't happen.
 */
public final class UnexpectedInvocationError extends ExpectationError {
    /** Class version, for serialization. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new exception.
     *
     * <p>
     * The constructor adds the current story track in its message.
     * </p>
     *
     * @param invocationString
     *            the unexpected invocation
     */
    public UnexpectedInvocationError(String invocationString) {
        super("unexpected invocation of '" + invocationString + "'");
    }
}
