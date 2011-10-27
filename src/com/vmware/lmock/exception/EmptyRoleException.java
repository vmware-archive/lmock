/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.exception;

/**
 * Exceptions thrown when the user wants to perform operations on roles that have no actors.
 */
public class EmptyRoleException extends MasqueradeException {
    /** Class version, for serialization. */
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor, with a default error message.
     */
    public EmptyRoleException() {
        super("could not modify role: no actors associated");
    }
}
