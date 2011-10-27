/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.exception;

/**
 * Base class of exceptions raised by operations on mock objects.
 */
public class MockException extends LMRuntimeException {
    /** Class version, for serialization. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new exception from an initial cause.
     *
     * @param msg
     *            an error message
     * @param cause
     *            the initial cause
     */
    public MockException(String msg, Throwable cause) {
        super("Mock error: " + msg, cause);
    }
}
