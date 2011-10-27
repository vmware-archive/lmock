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
 * Tracks the ongoing expectations of a story, reported by a story processor.
 *
 * <p>
 * A story tracker creates expectation reports upon request, thanks to
 * <code>addExpectationReport</code>. This report can then be retrieved for
 * an update, using <code>getCurrentExpectationReport</code>. That list of
 * expectations can then be retrieved by iterating on this tracker.
 * </p>
 */
class StoryTracker implements Iterable<ExpectationReport> {
    /** The list of expectation reports. */
    private final List<ExpectationReport> reports =
      new ArrayList<ExpectationReport>();

    /**
     * Records an expectation by creating a new report and adding it.
     *
     * @param expectation
     *            the new expectation
     */
    void addExpectationReport(Expectation expectation) {
        reports.add(new ExpectationReport(expectation));
    }

    /**
     * Provides the last expectation report added to the list.
     *
     * <p>
     * We assume that the user knows that such a report is present. Otherwise,
     * the method throws an exception.
     * </p>
     *
     * @return The last expectation report added to this.
     */
    ExpectationReport getCurrentExpectationReport() {
        return reports.get(reports.size() - 1);
    }

    /** @return The number of expectations reported up to now. */
    int size() {
        return reports.size();
    }

    public Iterator<ExpectationReport> iterator() {
        return reports.iterator();
    }

    /** Clears the list. */
    void clear() {
        reports.clear();
    }

    @Override
    public String toString() {
        if (reports.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder(256);
        builder.append("what happened up to now:\n");
        for (ExpectationReport report : reports) {
            builder.append(report);
            builder.append("\n");
        }

        return builder.toString();
    }
}
