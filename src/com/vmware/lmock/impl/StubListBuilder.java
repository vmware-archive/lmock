/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.impl;

/**
 * A common interface to create a list of stubs.
 *
 * <p>
 * This root class handles the common part of a stub specification, providing:
 * </p>
 * <ul>
 * <li>A list of stub builders, constructed on the fly</li>
 * <li>Base methods used by programmers to construct their list</li>
 * </ul>
 *
 * <p>
 * The result of the construction process is a list of stubs that can be added
 * to a stub map, using <code>addStubsToMap</code>.
 * </p>
 */
class StubListBuilder extends InvocationCheckerListBuilder<Stub, StubBuilder> {
    @Override
    protected StubBuilder createBuilder(Object object,
      InvocationCheckerClosureHandler closureHandler) {
        return new StubBuilder(object, closureHandler);
    }

    /**
     * Creates and registers a new builder for a stub.
     *
     * @param object
     *            the mock for which the stub applies
     * @param closureHandler
     *            handles the closure of the specification, or null
     */
    protected void createStub(Object object,
      InvocationCheckerClosureHandler closureHandler) {
        registerCheckerBuilder(object, closureHandler);
    }

    /**
     * Registers the stub found in the list into a stub map.
     *
     * @param map
     *            the populated map
     */
    protected void addStubsToMap(StubMap map) {
        for (Stub stub : this) {
            map.register(stub);
        }
    }
}
