/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * Objects tracking the expectations in order to provide an accurate report of
 * the story in case of error.
 *
 * <p>
 * Such a report includes an expectation and the number of times it was
 * satisfied.
 * </p>
 */
final class ExpectationReport {
    /**
     * Eye-candy: display X time(s).
     *
     * @param count
     *            the displayed count
     */
    private static String aStringForCount(int count) {
        StringBuilder builder = new StringBuilder(8);
        builder.append(count);
        builder.append(' ');
        builder.append("time");
        if (count != 1) {
            builder.append('s');
        }
        return builder.toString();
    }
    /** The recorded expectation. */
    private final Expectation expectation;

    /**
     * Number of invocations of the expectation for a given thread.
     */
    private static final class PerThreadRecord {
        private final Thread thread;
        private int count = 0;

        /**
         * Creates a new record, setting the invocation counter to 1.
         *
         * @param thread
         *            the invoking thread
         */
        PerThreadRecord(Thread thread) {
            this.thread = thread;
            this.count = 1;
        }

        /** Increments the invocation counter. */
        void oneMoreInvocation() {
            this.count++;
        }

        /** @return The recorded thread performing the related invocations. */
        Thread getThread() {
            return thread;
        }

        /** @return The number of invocations by the recorded thread. */
        int getCount() {
            return count;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder(64);
            builder.append(aStringForCount(count));
            builder.append(" from ");
            builder.append(thread);
            return builder.toString();
        }
    }
    /** List of threads that performed an invocation. */
    private List<PerThreadRecord> records = new ArrayList<PerThreadRecord>();

    /**
     * @return <code>true</code> if the list of records is not empty.
     */
    private boolean hasRecords() {
        return !records.isEmpty();
    }

    /**
     * Assuming that the list of records is not empty, provides the top of the
     * stack.
     *
     * @return The very last record in the records list.
     */
    private PerThreadRecord getLastRecord() {
        return records.get(records.size() - 1);
    }

    /**
     * @return The total number of recorded invocations, regardless of the
     * invoking thread.
     */
    private int getTotalNumberOfInvocations() {
        int result = 0;
        for (PerThreadRecord record : records) {
            result += record.getCount();
        }

        return result;
    }

    /**
     * Adds a record specifying that a given thread is performing a tracked
     * invocation.
     *
     * <p>
     * Creates and registers a new per thread record
     * </p>
     *
     * @param thread
     *            the recorded thread
     */
    private void recordNewThreadInvocation(Thread thread) {
        records.add(new PerThreadRecord(thread));
    }

    /**
     * Checks if a recorded thread is already currently recorded and performs
     * the appropriate tracking.
     *
     * <p>
     * The method assumes that the records list is not empty. If the specified
     * thread is currently recorded, increment the record counter. Create a
     * new record otherwise.
     * </p>
     *
     * @param thread
     *            the recorded thread
     */
    private void recordOrIncrementThreadInvocation(Thread thread) {
        PerThreadRecord top = getLastRecord();
        if (top.getThread().equals(thread)) {
            top.oneMoreInvocation();
        } else {
            recordNewThreadInvocation(thread);
        }
    }

    /**
     * Records an invocation from the current thread in the records.
     *
     * <p>
     * If the previously recorded thread is the same, increment the invocation
     * counter, create a new record otherwise.
     * </p>
     */
    private void recordCurrentThreadInvocation() {
        Thread recorded = Thread.currentThread();
        if (hasRecords()) {
            recordOrIncrementThreadInvocation(recorded);
        } else {
            recordNewThreadInvocation(recorded);
        }
    }

    /**
     * Creates a report for an expectation.
     *
     * @param expectation
     *            the reported expectation
     */
    protected ExpectationReport(Expectation expectation) {
        this.expectation = expectation;
    }

    /**
     * Reports the successful validation of the associated expectation.
     */
    void record() {
        recordCurrentThreadInvocation();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(128);

        builder.append("satisfied ");
        builder.append(aStringForCount(getTotalNumberOfInvocations()));
        builder.append(": ");
        builder.append(expectation);

        for (PerThreadRecord record : records) {
            builder.append("\n\t");
            builder.append(record);
        }
        return builder.toString();
    }
}
