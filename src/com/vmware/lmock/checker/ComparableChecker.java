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
 * Base class representing a checker for comparable objects.
 * 
 * <p>
 * The use of comparable allows to define generic methods, such as lower, equal
 * or greater than and range checking.
 * </p>
 *
 * @param <T>
 *            the comparable class to which the checker is dedicated
 */
public class ComparableChecker<T extends Comparable<T>> implements Checker<T> {
    /** Related comparable class. */
    private final Class<T> clazz;
    /** Lower bound of the allowed range. */
    private final T min;
    /** Upper bound of the allowed range. */
    private final T max;

    /**
     * Checks a value against the lower bound of the allowed range.
     * 
     * @param value
     *            the checked value
     * @return <code>true</code> if the min value is null or the value is
     *         greater than the min.
     */
    private boolean valueIsGreaterOrEqualToMin(T value) {
        return min == null || value.compareTo(min) >= 0;
    }

    /**
     * Checks a value against the upper bound of the allowed range.
     * 
     * @param value
     *            the checked value
     * @return <code>true</code> if the min value is null or the value is
     *         greater than the min.
     */
    private boolean valueIsLowerOrEqualToMax(T value) {
        return max == null || value.compareTo(max) <= 0;
    }

    /**
     * Creates a new checker to verify that values are within a given range of
     * values.
     * 
     * <p>
     * The constructor comes with a range specification <code>[min,max]</code>.
     * </p>
     * 
     * @param clazz
     *            the comparable class handled by this checker
     * @param min
     *            the lower bound of the range (null if none)
     * @param max
     *            the upper bound of the range (null if none)
     * @throws CheckerCreationException
     *             The min is greater than the max.
     */
    public ComparableChecker(Class<T> clazz, T min, T max) {
        this.clazz = clazz;
        this.min = min;
        this.max = max;
        if (max != null && !valueIsGreaterOrEqualToMin(max)) {
            throw new CheckerCreationException("'" + min
                    + "' is greater than '" + max + "'");
        }
    }

    /**
     * Checks the supplied value regarding the defined range.
     * 
     * <p>
     * The method returns <code>false</code> if the value is <code>null</code> or if it's out of the range
     * <code>[min,max]</code>.
     * </p>
     */
    @Override
    public boolean valueIsCompatibleWith(T value) {
        return value != null && valueIsGreaterOrEqualToMin(value)
                && valueIsLowerOrEqualToMax(value);
    }

    @Override
    public Class<T> getRelatedClass() {
        return clazz;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(getRelatedClass().getSimpleName());
        builder.append('=');
        builder.append('[');
        if (min != null) {
            builder.append(min);
        }
        builder.append(',');
        if (max != null) {
            builder.append(max);
        }
        builder.append(']');
        return builder.toString();
    }
}
