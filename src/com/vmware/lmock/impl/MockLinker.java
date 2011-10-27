/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.impl;

import static com.vmware.lmock.impl.MockInvocationHandlerType.CHECKER;

import java.util.ArrayList;
import java.util.List;

/**
 * Maintains a list of mocks and automatically links/unlinks an invocation
 * handler to each of them.
 *
 * <p>
 * The handler is associated as a <code>CHECKER</code> to the mock invocation.
 * </p>
 */
class MockLinker {
    /** The object linked to mocks. */
    private final MockInvocationHandler linkedHandler;
    /** List of known mocks. */
    private final List<Mock> mockList = new ArrayList<Mock>();

    /**
     * Creates a new linker for a given handler.
     *
     * @param linkedHandler
     *            the handler
     */
    protected MockLinker(MockInvocationHandler linkedHandler) {
        this.linkedHandler = linkedHandler;
    }

    /**
     * Adds one mock into the list of known mocks if not already registered.
     *
     * @param mock
     *            the registered mock
     */
    private void registerNewMock(Mock mock) {
        if (!mockList.contains(mock)) {
            mockList.add(mock);
        }
    }

    /**
     * Adds a list of mocks to the mocks known by this linker.
     *
     * @param stubMap
     *            a stub map referencing the added mocks
     */
    void registerNewMocks(StubMap stubMap) {
        for (Mock mock : stubMap.getMockList()) {
            registerNewMock(mock);
        }
    }

    /**
     * Adds a list of mocks to the mocks known by this linker.
     *
     * @param processor
     *            a story processor referencing the added mocks
     */
    void registerNewMocks(StoryProcessor processor) {
        for (Expectation expectation : processor.getExpectationList()) {
            registerNewMock(expectation.getProxy());
        }
    }

    /**
     * Parses the list of known mocks and associates them to the registered
     * invocation handler.
     */
    void linkHandlerToRegisteredMocks() {
        for (Mock mock : mockList) {
            mock.setInvocationHandler(CHECKER, linkedHandler);
        }
    }

    /**
     * Parses the list of known mocks and removes the association to the
     * registered invocation handler.
     */
    void unlinkHandlerFromRegisteredMocks() {
        for (Mock mock : mockList) {
            mock.unsetInvocationHandler(CHECKER);
        }
    }

    /**
     * Registers a new mock given by an invocation checker and links that mock.
     *
     * @param invocationChecker
     *            the reference expectation or stub
     */
    void registerAndLinkNewMocks(InvocationChecker invocationChecker) {
        Mock mock = invocationChecker.getProxy();
        registerNewMock(mock);
        mock.setInvocationHandler(CHECKER, linkedHandler);
    }
}
