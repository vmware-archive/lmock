/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.impl;

/**
 * Different types of invocation handlers that can be associated to a mock.
 */
enum MockInvocationHandlerType {
    /**
     * Used to construct directives (expectations, stubs).
     *
     * <p>
     * There must be one and only one of such handler at a time.
     * </p>
     */
    CONSTRUCTOR,
    /**
     * Used to validate the invocation.
     *
     * <p>
     * The application may try to associate the mock to several of such handler,
     * but only one is operating (which means that we overwrite this type of
     * handler).
     * </p>
     */
    CHECKER;
}
