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
 * A checker to validate floats within ranges of values.
 * 
 * <p>
 * Such a checker, created by a factory method, validates the floats comprised
 * within a certain range.
 * </p>
 */
public class FloatChecker extends ComparableChecker<Float> {
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
    private FloatChecker(Float min, Float max) {
        super(Float.class, min, max);
    }

    /**
     * @param min
     *            the minimum value allowed
     * @return A checker to test values greater or equal to the min.
     */
    public static FloatChecker valuesGreaterOrEqualTo(float min) {
        return new FloatChecker(min, null);
    }

    /**
     * A checker to test values greater or equal to 0.
     */
    public static final FloatChecker positiveValues = valuesGreaterOrEqualTo(0);

    /**
     * @param max
     *            the maximum value allowed by the checker
     * @return A checker to test values lower or equal to the max.
     */
    public static FloatChecker valuesLowerOrEqualTo(float max) {
        return new FloatChecker(null, max);
    }

    /**
     * A checker to test values lower or equal to 0.
     */
    public static final FloatChecker negativeValues = valuesLowerOrEqualTo(0);

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
    public static FloatChecker valuesBetween(float min, float max) {
        return new FloatChecker(min, max);
    }
}
