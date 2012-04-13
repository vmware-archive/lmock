/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * A map of stubs.
 *
 * <p>
 * Stubs are registered by the <code>register</code> method. It is then able to
 * check whether an invocation may match one or several stubs, using the mock
 * and the method name (<code>search</code>).
 * </p>
 */
class StubMap {
    /** Per-mock map of per-method name map of stubs. */
    private final HashMap<Mock, HashMap<Method, List<Stub>>> map = new HashMap<Mock, HashMap<Method, List<Stub>>>();

    /**
     * Searches for stubs that may be verified by an invocation.
     *
     * <p>
     * The returned list provides all the stubs matching the given mock and
     * method, from the older to the younger in terms of registration.
     * </p>
     *
     * @param invocation
     *            the checked invocation
     * @return The list of fetched stubs, <code>null</code> if not found.
     */
    protected List<Stub> search(Invocation invocation) {
        HashMap<Method, List<Stub>> methodMap = map.get(invocation.getMock());
        if (methodMap != null) {
            return methodMap.get(invocation.getMethod());
        } else {
            return null;
        }
    }

    /**
     * Creates and registers a new map for a given mock.
     *
     * @param mock
     *            the mock
     * @return The created map.
     */
    private HashMap<Method, List<Stub>> createMapForMock(Mock mock) {
        HashMap<Method, List<Stub>> newMap = new HashMap<Method, List<Stub>>();
        map.put(mock, newMap);
        return newMap;
    }

    /**
     * Registers a stub.
     *
     * <p>
     * If other stubs match the same mock and invocation, this new stub is put
     * at the end of the list.
     * </p>
     *
     * @param stub
     *            the registered stub
     */
    protected void register(Stub stub) {
        HashMap<Method, List<Stub>> methodMap = map.get(stub.getProxy());
        if (methodMap == null) {
            methodMap = createMapForMock(stub.getProxy());
        }

        List<Stub> stubList = methodMap.get(stub.getMethod());
        if (stubList == null) {
            stubList = new ArrayList<Stub>();
            methodMap.put(stub.getMethod(), stubList);
        }

        stubList.add(stub);
    }

    /**
     * @return The list of mocks to which a stub was mapped.
     */
    protected Set<Mock> getMockList() {
        return map.keySet();
    }
}
