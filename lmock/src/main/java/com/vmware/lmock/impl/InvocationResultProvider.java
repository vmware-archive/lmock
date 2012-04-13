/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.impl;

/**
 * Objects producing the result of an invocation of a stub or expectation.
 *
 * <p>
 * This interface allows to trap the invocation of methods and apply a specific
 * behavior. When doing this, users must pay a particular attention at:
 * </p>
 * <ul>
 * <li>Applying simple procedures, in particular not implying any invocation of any mock.</li>
 * <li>Return (or throw) something that is coherent with the prototype of the
 * handled method.</li>
 * </ul>
 *
 * <p>
 * If those precautions are not respected, the behavior of Lmock is unexpected.
 * </p>
 */
public interface InvocationResultProvider {
    /**
     * Handles the invocation of a mock to apply a result.
     *
     * <p>
     * This result can be:
     * </p>
     * <ul>
     * <li>Throwing an exception</li>
     * <li>Returning of a value (should be <code>null</code> in case of void methods)</li>
     * </ul>
     *
     * @return The invocation result.
     * @throws Throwable
     *             The resulting exception, if any.
     */
    public Object apply() throws Throwable;
}
