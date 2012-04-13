/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.checker;

import java.util.ArrayList;
import java.util.List;

/**
 * A checker to validate a set of values belonging to an enumeration.
 *
 * @param <E>
 *            the enumeration handled by this checker
 */
public final class EnumChecker<E extends Enum<E>> implements Checker<E> {
    /** List of allowed values within the enum. */
    private final List<E> allowedValues = new ArrayList<E>();
    /** Class of the enumeration. */
    private final Class<E> clazz;

    /**
     * Creates a new checker validating a value regarding a bunch of allowed
     * ones.
     * 
     * @param clazz
     *            the class of the enumeration
     * @param values
     *            the list of allowed values
     */
    private EnumChecker(Class<E> clazz, E... values) {
        this.clazz = clazz;
        for (E value : values) {
            allowedValues.add(value);
        }
    }

    @Override
    public boolean valueIsCompatibleWith(E value) {
        for (E item : allowedValues) {
            if (item == value) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Class<?> getRelatedClass() {
        return clazz;
    }

    /**
     * Creates a new checker verifying that a value belongs to a list of allowed
     * values.
     * 
     * @param <T>
     *            the checked enumeration
     * @param clazz
     *            the class of the checked enumeration
     * @param values
     *            the list of allowed values
     * @return The created checker.
     */
    public static <T extends Enum<T>> EnumChecker<T> oneOf(
            Class<T> clazz, T... values) {
        return new EnumChecker<T>(clazz, values);
    }
}
