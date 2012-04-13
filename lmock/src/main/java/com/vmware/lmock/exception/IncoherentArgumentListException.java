/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.exception;

/**
 * Exceptions thrown when trying to register an argument list that does not
 * match the prototype of the associated method.
 *
 * <p>
 * Typically, this occurs when a with clause is not correctly used (i.e. not
 * specified for every argument).
 * </p>
 */
public class IncoherentArgumentListException extends InvocationCheckerCreationException {
    /** Class version, for serialization. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new exception, along with an error message.
     *
     * @param msg
     *            the error message
     */
    public IncoherentArgumentListException(String msg) {
        super("incoherent arguments: " + msg);
    }
}
