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
 * List of expectations built by a test to create a scenario.
 *
 * <p>
 * Such an object maintains the list of expectations for the current test and
 * allows to parse this list.
 * </p>
 */
final class ExpectationList implements Iterable<Expectation> {

    /** Logs the construction of the list. */
    private static final Logger logger = Logger.get(ExpectationList.class);
    /** Unique identifier of the list, to help debugging. */
    private long uuid;
    /** A static counter of the lists, to help debugging. */
    private static long uuidCounter;
    /** The registered expectations, in the order they were declared. */
    private final List<Expectation> expectationList = new ArrayList<Expectation>();
    /** Index of the current expectation. */
    private int currentExpectationIndex = 0;

    /**
     * Creates a new list of expectations.
     *
     * This is a shortcut to the constructor followed by
     * <code>addExpectations</code>.
     *
     * @param expectations
     *            the expectation list
     */
    protected ExpectationList(Expectation... expectations) {
        uuid = uuidCounter++;
        logger.trace("ExpectationList", "uuid=", uuid);
        addExpectations(expectations);
    }

    /**
     * Registers a new expectation into the list.
     *
     * The registration order defines how the different expectations are
     * expected to be encountered.
     *
     * @param expectation
     *            the new expectation
     * @return this.
     */
    ExpectationList addExpectation(Expectation expectation) {
        logger.trace("addExpectation", "uuid=", uuid, "expectation=", expectation);
        expectationList.add(expectation);
        return this;
    }

    /**
     * Adds a set of expectations.
     *
     * @param expectations
     *            the expectation list
     * @return this.
     */
    ExpectationList addExpectations(Expectation... expectations) {
        for (Expectation expectation : expectations) {
            addExpectation(expectation);
        }
        return this;
    }

    /**
     * @return The current expectation, null if the end of the story was
     *         reached.
     */
    Expectation getCurrentExpectation() {
        if (currentExpectationIndex < expectationList.size()) {
            return expectationList.get(currentExpectationIndex);
        } else {
            return null;
        }
    }

    /**
     * Moves to the next expectation, if any.
     *
     * @return The next expectation in the list, null if none.
     */
    Expectation nextExpectation() {
        currentExpectationIndex++;
        return getCurrentExpectation();
    }

    /**
     * Gets to the next expectation, if any, without changing the current index.
     *
     * @return The next expectation in the list, null if none.
     */
    Expectation nextExpectationWithoutChangingCurrent() {
        if (currentExpectationIndex + 1 < expectationList.size()) {
            return expectationList.get(currentExpectationIndex + 1);
        } else {
            return null;
        }
    }

    /**
     * Rewinds to the beginning of the list.
     */
    void rewind() {
        currentExpectationIndex = 0;
    }

    /**
     * Unwinds to the end of the list.
     */
    void unwind() {
        currentExpectationIndex = expectationList.size();
    }

    @Override
    public Iterator<Expectation> iterator() {
        return expectationList.iterator();
    }

    @Override
    public String toString() {
        return String.valueOf(uuid);
    }
}
