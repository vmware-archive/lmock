/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.exception;

/**
 * The user defines an expectation that should return a value that cannot be
 * returned.
 */
public final class IncompatibleReturnValueException extends InvocationResultCheckerException {
    /** Class version, for serialization. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new exception.
     *
     * @param expectedClass
     *            the class that should be returned by the method
     * @param result
     *            the declared returned value
     */
    public IncompatibleReturnValueException(Class<?> expectedClass,
      Object result) {
        super("incompatible return value: expecting an element of class '"
          + expectedClass.getSimpleName() + "'" + " but found '"
          + result.getClass().getSimpleName() + "'");
    }
}
