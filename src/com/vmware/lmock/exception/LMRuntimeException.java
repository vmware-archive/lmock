/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.exception;

import com.vmware.lmock.impl.Cleaner;

/**
 * Base runtime exceptions issued by the mock engine.
 *
 * <p>
 * Connects to the cleaner, so that all the resources are removed if any
 * exception occurs.
 * </p>
 */
public class LMRuntimeException extends RuntimeException {
    /** Class version, for serialization. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new exception, along with an error message.
     *
     * @param msg
     *            the error message
     */
    public LMRuntimeException(String msg) {
        super(msg);
        Cleaner.cleanup();
    }

    /**
     * Creates a new exception, caused by an initial error.
     *
     * @param msg
     *            the error message
     * @param cause
     *            the initial error cause
     */
    public LMRuntimeException(String msg, Throwable cause) {
        super(msg, cause);
        Cleaner.cleanup();
    }
}
