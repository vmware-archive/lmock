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
 * Checks the coherence of classes and object with a reference class.
 *
 * <p>
 * Notice that class checkers may consider null as an element of the reference
 * class or not, depending on the initial configuration of this checker.
 * </p>
 */
class ClassChecker implements Checker<Object> {
    /** The reference class. */
    private final Class<?> referenceClass;
    /** Tells whether null is an element of the reference class or not. */
    private final boolean nullIsMemberOfReferenceClass;

    /**
     * An expected argument belonging to a class, but with no specific value.
     *
     * @param clazz
     *            class of the argument
     */
    protected static ClassChecker anyArgumentOf(Class<?> clazz) {
        return new ClassChecker(clazz, true);
    }

    /**
     * An expected argument belonging to a class, with any value but null.
     *
     * @param clazz
     *            class of the argument
     */
    protected static ClassChecker anyNonNullArgumentOf(Class<?> clazz) {
        return new ClassChecker(clazz, false);
    }

    /**
     * Translates a primitive type to a class, as autoboxing does.
     *
     * @param clazz
     *            the initial class
     * @return The translation of this class.
     */
    private static Class<?> getActualClass(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            if (clazz.equals(Boolean.TYPE)) {
                return Boolean.class;
            } else if (clazz.equals(Character.TYPE)) {
                return Character.class;
            } else if (clazz.equals(Byte.TYPE)) {
                return Byte.class;
            } else if (clazz.equals(Short.TYPE)) {
                return Short.class;
            } else if (clazz.equals(Integer.TYPE)) {
                return Integer.class;
            } else if (clazz.equals(Long.TYPE)) {
                return Long.class;
            } else if (clazz.equals(Float.TYPE)) {
                return Float.class;
            } else if (clazz.equals(Double.TYPE)) {
                return Double.class;
            } else {
                // Case of void.
                return clazz;
            }
        } else {
            return clazz;
        }
    }

    /**
     * Creates a new checker, referencing a given class.
     *
     * @param clazz
     *            the reference class against which the tests will be done
     * @param nullIsMemberOfReferenceClass
     *            <code>false</code> if null is not allowed by the checker
     */
    private ClassChecker(Class<?> clazz, boolean nullIsMemberOfReferenceClass) {
        referenceClass = getActualClass(clazz);
        this.nullIsMemberOfReferenceClass = nullIsMemberOfReferenceClass;
    }

    /**
     * Verifies the compatibility of a class with the reference class of this
     * checker.
     *
     * <p>
     * The method basically checks that the supplied class is equals or inherits
     * from or implements the reference class of this checker.
     * </p>
     *
     * @param checkedClass
     *            the checked class, not an array
     * @return false if the two classes don't match
     */
    private boolean classIsCompatibleWithSimpleClass(Class<?> checkedClass) {
        // The checked class is not an array.
        if (referenceClass.isArray()) {
            return false;
        } else {
            // Be careful of potential issues with autoboxing.
            Class<?> checking = getActualClass(checkedClass);
            return referenceClass.isAssignableFrom(checking);
        }
    }

    /**
     * Verifies the compatibility of an array with the reference class by
     * checking the class of each item.
     *
     * @param array
     *            the checked array
     * @return false if the object cannot be an instance of the reference class.
     */
    private boolean arrayIsCompatibleWith(Object array) {
        // Use a dedicated class checker for the reference component type.
        ClassChecker componentChecker = anyArgumentOf(referenceClass.getComponentType());
        for (int itemIndex = 0; itemIndex < Array.getLength(array); itemIndex++) {
            Object item = Array.get(array, itemIndex);
            if (!componentChecker.valueIsCompatibleWith(item)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Verifies the compatibility of an object with the reference class of this
     * checker.
     *
     * <p>
     * This test includes the fact the null always matches the class.
     * </p>
     * <p>
     * If the object is a mock, the method validates the compatibility using the
     * class of the mocked interface.
     * </p>
     */
    @Override
    public boolean valueIsCompatibleWith(Object value) {
        if (value == null) {
            return nullIsMemberOfReferenceClass;
        } else {
            // There's a special case with arrays due to varargs. When the
            // virtual machine invokes the method with varargs it logically
            // only passes a table of objects. In this case, we cannot rely
            // on the component type of the array to check the classes, since
            // the reference may expect something more "specialized". Which
            // implies to check all the items in the array one by one.
            if (referenceClass.isArray() && value.getClass().isArray()) {
                return arrayIsCompatibleWith(value);
            } else {
                return classIsCompatibleWithSimpleClass(value.getClass());
            }
        }
    }

    /**
     * @return The reference class of this checker.
     */
    @Override
    public Class<?> getRelatedClass() {
        return referenceClass;
    }

    /** Displays the name of the reference class. */
    @Override
    public String toString() {
        if (nullIsMemberOfReferenceClass) {
            return getRelatedClass().getSimpleName() + "=<ANY>";
        } else {
            return getRelatedClass().getSimpleName() + "!=null";
        }
    }
}
