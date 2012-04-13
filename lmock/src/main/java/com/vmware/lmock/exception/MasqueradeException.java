/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.exception;

/**
 * Exceptions thrown by the processing of masquerade.
 */
public class MasqueradeException extends LMRuntimeException {
    /** Class version, for serialization. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception, along with an error message.
     *
     * @param msg
     *            the error message
     */
    public MasqueradeException(String msg) {
        super(msg);
    }
}
