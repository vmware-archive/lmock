/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.impl;

import java.lang.reflect.Array;

import com.vmware.lmock.checker.Checker;

/**
 * Checks the coherence of arrays.
 *
 * <p>
 * This checker validates the equality of all the items within an array and all
 * the items within a reference array, provided to the constructor.
 * </p>
 * <p>
 * It's important to remember that such a checker does not make an internal copy
 * of the reference array, so that its internal values can change at runtime.
 * </p>
 * <p>
 * Also notice that an array checker is always proceeding with non-null arrays.
 * </p>
 */
class ArrayChecker implements Checker<Object> {
    /** Reference array. */
    private final Object referenceArray;

    /**
     * Creates a new checker for a given reference array.
     *
     * <p>
     * Notice that the constructor does not verify that the supplied object is
     * actually an array.
     * </p>
     *
     * @param object
     *            the non-null reference array
     */
    protected ArrayChecker(Object object) {
        referenceArray = object;
    }

    /**
     * Compares two items of an array.
     *
     * @param item1
     *            the first element to check
     * @param item2
     *            the second element to check
     */
    private static boolean arrayItemsAreEqual(Object item1, Object item2) {
        // Loop back to a checker to validate the classes and the values.
        // This is where the "dynamic" aspect of this checker is: if the
        // contents of the reference array is changed we compare the supplied
        // data with the new values.
        return ObjectCheckerFactory.getExistingOrNewChecker(item1).valueIsCompatibleWith(item2);
    }

    /**
     * Compares the contents of two arrays.
     *
     * @param array1
     *            the first array to check
     * @param array2
     *            the second array to check
     * @return true if the two arrays have the same contents.
     */
    private static boolean arraysAreEqual(Object array1, Object array2) {
        int len1 = Array.getLength(array1);
        int len2 = Array.getLength(array2);

        if (len1 != len2) {
            return false;
        } else {
            // Note: we do not check the classes enclosed by the arrays, because
            // we will do it for each enclosed item.
            for (int index = 0; index < len1; index++) {
                Object item1 = Array.get(array1, index);
                Object item2 = Array.get(array2, index);
                if (!arrayItemsAreEqual(item1, item2)) {
                    return false;
                }
            }

            return true;
        }
    }

    @Override
    public boolean valueIsCompatibleWith(Object value) {
        // Be sure that we're on the same line regarding mock objects.
        Object actualObject = Mock.getObjectOrMock(value);

        // Remember that the reference value cannot be null.
        if (actualObject == null) {
            return false;
        }

        if (actualObject.getClass().isArray()) {
            return arraysAreEqual(referenceArray, actualObject);
        } else {
            return false;
        }
    }

    @Override
    public Class<?> getRelatedClass() {
        // Remember that the reference value cannot be null.
        return referenceArray.getClass();
    }

    @Override
    public String toString() {
        // Build a string with all the arguments.
        StringBuilder builder = new StringBuilder(64);

        builder.append(getRelatedClass().getSimpleName());
        builder.append('=');
        builder.append('{');
        for (int itemIndex = 0; itemIndex < Array.getLength(referenceArray); itemIndex++) {
            if (itemIndex != 0) {
                builder.append(',');
            }
            Object item = Array.get(referenceArray, itemIndex);
            // Will dynamically change the output depending on the current
            // contents of the array.
            builder.append(ObjectCheckerFactory.getExistingOrNewChecker(item).toString());
        }
        builder.append('}');
        return builder.toString();
    }
}
