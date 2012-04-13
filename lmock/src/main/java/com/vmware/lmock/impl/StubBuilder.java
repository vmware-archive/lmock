/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.impl;

import java.lang.reflect.Method;

import com.vmware.lmock.exception.MockReferenceException;

/**
 * Class building a stub on the fly.
 *
 * <p>
 * This is basically an invocation checker builder dedicated to stubs.
 * </p>
 */
class StubBuilder extends InvocationCheckerBuilder<Stub> {
    @Override
    protected Stub createInvocationChecker(Object object, Method method) {
        return new Stub(object, method);
    }

    /**
     * Creates a new builder.
     *
     * @param object
     *            the mock for which we create this builder
     * @param closureHandler
     *            handles the closure of the specification, or null
     * @throws MockReferenceException
     *             The specified object is not a mock.
     */
    protected StubBuilder(Object object,
      InvocationCheckerClosureHandler closureHandler) {
        super(object, closureHandler);
    }
}
