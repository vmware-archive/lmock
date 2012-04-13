/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.impl;

/**
 * Hooks the methods called from mocks.
 *
 * <p>
 * Such a class of objects validates the call to a method, along with its
 * arguments (<code>invoke</code>), and provides the resulting behavior.
 * </p>
 */
interface MockInvocationHandler {
    /**
     * Handles the call to a method.
     *
     * <p>
     * Throws an expectation error if the handler fails to process the invocation.
     * </p>
     *
     * @param invocation
     *            the invocation
     * @return The result to apply to complete the invocation, never null.
     */
    public InvocationResultProvider invoke(Invocation invocation);
}
