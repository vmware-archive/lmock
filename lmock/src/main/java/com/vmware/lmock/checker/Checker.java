/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.checker;

/**
 * Generic interface defining a validation item for an argument.
 *
 * <p>
 * Checkers are called when running stories to verify that an argument provided
 * during an invocation is compatible with an expected value.
 * </p>
 *
 * @param <T>
 *            the class to which this checker is dedicated
 */
public interface Checker<T> {
    /**
     * Tells whether an argument is compatible with the expectations of this checker.
     *
     * @param value
     *            the argument value
     * @return <code>true</code> if the argument is compatible.
     */
    public boolean valueIsCompatibleWith(T value);

    /**
     * Returns the class of values handled by this checker.
     *
     * <p>
     * Since this class is specialized to a given class of objects (<code>T</code>), this method returns
     * <code>T.class</code>.
     * </p>
     *
     * @return The class handled by this checker.
     */
    public Class<?> getRelatedClass();
}
