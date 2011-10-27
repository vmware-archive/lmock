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
 * A checker to validate shorts within ranges of values.
 *
 * <p>
 * Such a checker, created by a factory method, validates the shorts comprised
 * within a certain range.
 * </p>
 */
public class ShortChecker extends ComparableChecker<Short> {
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
    private ShortChecker(Short min, Short max) {
        super(Short.class, min, max);
    }

    /**
     * Creates a checker to validate a value greater or equal to a short.
     *
     * @param min
     *            the minimum value allowed
     * @return A checker to test values greater or equal to the min.
     */
    public static ShortChecker valuesGreaterOrEqualTo(short min) {
        return new ShortChecker(min, null);
    }
    /**
     * A checker to test values greater or equal to 0.
     */
    public static final ShortChecker positiveValues = valuesGreaterOrEqualTo((short) 0);

    /**
     * Creates a checker to validate a value lower or equal to a short.
     *
     * @param max
     *            the maximum value allowed by the checker
     * @return A checker to test values lower or equal to the max.
     */
    public static ShortChecker valuesLowerOrEqualTo(short max) {
        return new ShortChecker(null, max);
    }
    /**
     * A checker to test values lower or equal to 0.
     */
    public static final ShortChecker negativeValues = valuesLowerOrEqualTo((short) 0);

    /**
     * Creates a checker to validate a value within a range.
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
    public static ShortChecker valuesBetween(short min, short max) {
        return new ShortChecker(min, max);
    }
}
