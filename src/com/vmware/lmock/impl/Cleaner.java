/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A central point for cleaning the invocation handlers put during a test.
 *
 * <p>
 * Mocks are associated to invocation handlers when constructing directives and
 * running scenarios. Although we can't manage all the use cases, this cleaner
 * allows to make a fast cleanup of the associations:
 * </p>
 * <ul>
 * <li>When a story ends.</li>
 * <li>When an exception is thrown</li>
 * </ul>
 *
 * <p>
 * The cleaner enriches a list of registered mock (<code>register</code>) and
 * calls all the registered mock when the method <code>cleanup</code> is
 * invoked.
 * </p>
 *
 * <p>
 * <b>This module is available for internal purpose only. Don't use when writing tests.</b>
 * </p>
 */
public class Cleaner implements Iterable<Mock> {
    /** List of registered mocks. */
    private final List<Mock> mockList = new ArrayList<Mock>();
    /** The cleaner singleton. */
    private static final Cleaner cleaner = new Cleaner();

    /**
     * Checks if a given mock is already known by the cleaner.
     *
     * @param mock
     *            the requested mock
     * @return <code>true</code> if the specified mock is recognized.
     */
    private boolean isMockRegistered(Mock mock) {
        return mockList.contains(mock);
    }

    /**
     * Registers a new mock so that it will be cleaned up.
     *
     * @param mock
     *            the registered mock
     */
    private void registerMock(Mock mock) {
        mockList.add(mock);
    }

    @Override
    public Iterator<Mock> iterator() {
        return mockList.iterator();
    }

    /**
     * Registers a mock so that it will be cleaned up.
     *
     * @param mock
     *            the registered mock
     */
    protected static void register(Mock mock) {
        if (!cleaner.isMockRegistered(mock)) {
            cleaner.registerMock(mock);
        }
    }

    /**
     * Cleans up all the registered mocks.
     */
    public static void cleanup() {
        for (Mock mock : cleaner) {
            mock.cleanupInvocationHandlers();
        }
    }
}
