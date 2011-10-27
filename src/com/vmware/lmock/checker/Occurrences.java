/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.checker;

import com.vmware.lmock.exception.IllegalOccurrencesDefinitionException;

/**
 * Counts some occurrences of an expectation.
 *
 * <p>
 * Such an object maintains a counter, modified by the
 * <code>increment</code> and <code>reset</code> methods. This counter shall
 * remain in a bounded range, which can be verified with the following methods:
 * </p>
 * <ul>
 * <li><code>canEndNow</code>: reached a minimum number of required occurrences.
 * </li>
 * <li><code>hasReachedLimit</code>: exceeded a maximum number of allowed
 * occurrences.</li>
 * </ul>
 *
 * <p>
 * An occurrences object cannot be created as is, but via a set of static
 * builders that facilitate the definition of the boundaries.
 * </p>
 */
public final class Occurrences implements OccurrenceChecker {
    /** Minimum number of occurrences allowed before this can end. */
    private final int min;
    /** Maximum number of occurrences allowed. */
    private final int max;
    private int count = 0;

    /**
     * Creates an occurrence with initial values.
     *
     * @param min
     *            minimum number of occurrences required, negative if none
     * @param max
     *            maximum number of occurrences allowed, negative if none
     * @throws IllegalOccurrencesDefinitionException
     *             If min and max are not compatible.
     */
    private Occurrences(int min, int max) {
        if (max > 0 && min >= 0 && max < min) {
            throw new IllegalOccurrencesDefinitionException(
              "incoherent occurrence range [" + min + "," + max + "]");
        }
        this.min = min;
        this.max = max;
    }

    @Override
    public void increment() {
        count++;
    }

    @Override
    public void reset() {
        count = 0;
    }

    @Override
    public boolean canEndNow() {
        return count >= min;
    }

    @Override
    public boolean hasReachedLimit() {
        return max >= 0 && count >= max;
    }

    /**
     * Builds an occurrence representing an exact number of times.
     *
     * @param n
     *            the precise number of occurrences expected (0 means 'never')
     * @return The built occurrence.
     * @throws IllegalOccurrencesDefinitionException
     *             If n is negative.
     */
    public static Occurrences exactly(int n) {
        if (n < 0) {
            throw new IllegalOccurrencesDefinitionException("cannot expect '"
              + n + "' occurrences exactly");
        }
        return new Occurrences(n, n);
    }

    /**
     * Creates a new occurrence representing "any times" (0 to infinite).
     *
     * @return The built occurrence.
     */
    public static Occurrences any() {
        return new Occurrences(-1, -1);
    }

    /**
     * Creates a new occurrence representing a minimum required.
     *
     * @param n
     *            the minimum occurrences required, 0 meaning any
     * @return The built occurrence.
     * @throws IllegalOccurrencesDefinitionException
     *             If n is negative.
     */
    public static Occurrences atLeast(int n) {
        if (n < 0) {
            throw new IllegalOccurrencesDefinitionException(
              "cannot expect at least '" + n + "' occurrences");
        } else if (n == 0) {
            return any();
        } else {
            return new Occurrences(n, -1);
        }
    }

    /**
     * Creates an occurrence that cannot happen more than a given number of
     * times.
     *
     * @param n
     *            the maximum occurrences allowed, 0 meaning never
     * @return The built occurrence.
     * @throws IllegalOccurrencesDefinitionException
     *             If n is negative.
     */
    public static Occurrences atMost(int n) {
        if (n < 0) {
            throw new IllegalOccurrencesDefinitionException(
              "cannot expect at most '" + n + "' occurrences");
        } else if (n == 0) {
            return never();
        } else {
            return new Occurrences(-1, n);
        }
    }

    /**
     * Creates an occurrence that never happens.
     *
     * @return The built occurrence.
     */
    public static Occurrences never() {
        return exactly(0);
    }

    /**
     * Creates a new bounded occurrence.
     *
     * @param min
     *            the minimum number of occurrences required
     * @param max
     *            the maximum number of occurrences required
     * @return The built occurrence.
     * @throws IllegalOccurrencesDefinitionException
     *             If min and max are incoherent.
     */
    public static Occurrences between(int min, int max) {
        if (min < 0 || max < 0) {
            throw new IllegalOccurrencesDefinitionException("cannot expect '["
              + min + "," + max + "] occurrences");
        } else if (min == 0 && max == 0) {
            return never();
        } else {
            return new Occurrences(min, max);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(8);

        builder.append("[");
        if (min < 0 && max < 0) {
            builder.append("ANY");
        } else {
            if (min >= 0) {
                builder.append(min);
            }
            builder.append("..");
            if (max >= 0) {
                builder.append(max);
            }
        }
        builder.append("]");

        return builder.toString();
    }
}
