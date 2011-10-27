/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.impl;

/**
 * Handles the closure of an invocation handler specification.
 *
 * <p>
 * The closure of such specification is the moment at which the invocation is
 * completely defined (this means that the mock was associated to the
 * specification and the expected/stubbed method was invoked once to build the
 * checker). At this point, clauses are not necessarily defined, but the user
 * knows the mock, the method and the arguments.
 * </p>
 */
public interface InvocationCheckerClosureHandler {
    /**
     * Handles the closure of the specification.
     */
    public void onClosure();
}
