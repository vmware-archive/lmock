/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.checker;

import com.vmware.lmock.exception.CheckerCreationException;

/**
 * A checker to validate integers within ranges of values.
 *
 * <p>
 * Such a checker, created by a factory method, validates the integers comprised
 * within a certain range.
 * </p>
 */
public final class IntegerChecker extends ComparableChecker<Integer> {
    /**
     * Basic creator of the checker.
     *
     * @param min
     *            the lower bound of the allowed range (null if none)
     * @param max
     *            the upper bound of the allowed range (null if none)
     * @throws CheckerCreationException
     *             Incoherent range specification.
     */
    private IntegerChecker(Integer min, Integer max) {
        super(Integer.class, min, max);
    }

    /**
     * Creates a checker to validate a value greater or equal to an integer.
     *
     * @param min
     *            the minimum value allowed
     * @return A checker to test values greater or equal to the min.
     */
    public static IntegerChecker valuesGreaterOrEqualTo(int min) {
        return new IntegerChecker(min, null);
    }
    /**
     * A checker to test values greater or equal to 0.
     */
    public static final IntegerChecker positiveValues = valuesGreaterOrEqualTo(0);

    /**
     * Creates a checker to validate a value lower or equal to an integer.
     *
     * @param max
     *            the maximum value allowed by the checker
     * @return A checker to test values lower or equal to the max.
     */
    public static IntegerChecker valuesLowerOrEqualTo(int max) {
        return new IntegerChecker(null, max);
    }
    /**
     * A checker to test values lower or equal to 0.
     */
    public static final IntegerChecker negativeValues = valuesLowerOrEqualTo(0);

    /**
     * Creates an integer checker to validate a value within a range.
     *
     * <p>
     * The checker validates the values within the range <code>[min,max]</code>.
     * </p>
     *
     * @param min
     *            the lower bound
     * @param max
     *            the upper bound
     * @return The created checker.
     * @throws CheckerCreationException
     *             Incoherent range.
     */
    public static IntegerChecker valuesBetween(int min, int max) {
        return new IntegerChecker(min, max);
    }
}
