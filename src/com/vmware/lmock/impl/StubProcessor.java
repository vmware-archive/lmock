/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.impl;

import java.util.List;

/**
 * Internal invocation handler trying to stub an invocation.
 *
 * <p>
 * The processor maintains an internal map of known stubs that it tries to fetch
 * with each invocation it receives. If such a stub is found, it returns the
 * stub result, otherwise, it defers the invocation to another handler.
 * </p>
 *
 * <p>
 * Notice that if several known stubs match a given invocation, the most
 * recently declared one prevails.
 * </p>
 */
class StubProcessor {
    /** Logs the stub processor activity. */
    private static final Logger logger = Logger.get(StubProcessor.class);
    /** Map of known stubs. */
    private final StubMap stubMap;

    /**
     * Creates a new processor handling a given map.
     *
     * @param stubMap
     *            the map of known stubs
     */
    protected StubProcessor(StubMap stubMap) {
        logger.trace("StubProcessor", "stubMap=", stubMap);
        this.stubMap = stubMap;
    }

    /**
     * Adds a stub to the map of known stubs.
     *
     * @param stub
     *            the new stub
     */
    protected void addStub(Stub stub) {
        logger.trace("addStub", "stub=", stub);
        stubMap.register(stub);
    }

    /**
     * Parses a list of stubs to fetch a given invocation.
     *
     * @param stubs
     *            the list of stubs to parse
     * @param invocation
     *            the checked invocation
     * @return The fetched stub, if found, null otherwise.
     */
    private Stub searchStubForInvocation(List<Stub> stubs, Invocation invocation) {
        logger.trace("searchStubForInvocation", "stubs=", stubs, "invocation=", invocation);
        // Give the priority to the younger stubs.
        for (int index = stubs.size() - 1; index >= 0; index--) {
            Stub stub = stubs.get(index);
            logger.trace("searchStubForInvocation", "checking stub", stub, "against invocation", invocation);
            if (stub.valueIsCompatibleWith(invocation)) {
                logger.trace("searchStubForInvocation", "stub matches");
                return stub;
            }
        }

        return null;
    }

    /**
     * Tries to stub the invocation of a mock.
     *
     * <p>
     * The method searches for a known stub that could satisfy the specified
     * invocation and returns the specified invocation result if found.
     * </p>
     *
     * @param invocation
     *            the invocation
     * @return The invocation result provider if a stub is found,
     *   <code>null</code> otherwise.
     */
    public InvocationResultProvider invoke(Invocation invocation) {
        logger.trace("invoke", "invocation=", invocation);
        // Important: at this level we are sure that the stub map knows about
        // the invoked mock. In fact, either the mock is simply attached to
        // a scenario, in which case we will not enter this piece of code, or
        // it is related to a stub, in which case the stub is already
        // registered.
        List<Stub> stubs = stubMap.search(invocation);
        if (stubs != null) {
            logger.trace("invoke", "found stubs", stubs);
            Stub stub = searchStubForInvocation(stubs, invocation);
            if (stub != null) {
                return stub.getInvocationResult();
            } else {
                logger.trace("invoke", "no stub found for", invocation, "in the stubs list");
                return null;
            }
        } else {
            logger.trace("invoke", "no stubs found for", invocation);
            return null;
        }
    }
}
