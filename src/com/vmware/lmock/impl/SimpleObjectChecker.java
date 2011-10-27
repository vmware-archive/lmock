/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.impl;

import com.vmware.lmock.checker.Checker;

/**
 * Checks the coherence of objects <b>different from arrays</b>.
 *
 * <p>
 * This checker is using the <code>equals</code> to check the equality of a
 * reference object (provided to the constructor) with an argument value.
 * </p>
 *
 * <p>
 * For the particular case of mocks, the checker directly compares the mocks
 * rather than invoking the <code>equals</code> method of the object, in order
 * to avoid recursive invocations of the mock.
 * </p>
 */
class SimpleObjectChecker implements Checker<Object> {
    /** Reference object, or mock. */
    private final Object referenceObject;

    /**
     * Creates a new checker with a reference value.
     *
     * @param object
     *            the reference object
     */
    protected SimpleObjectChecker(Object object) {
        referenceObject = Mock.getObjectOrMock(object);
    }

    /**
     * Compares two values.
     *
     * @param value1
     *            the first argument to check
     * @param value2
     *            the second argument to check
     * @return true if the two arguments are equal.
     */
    private static boolean valuesAreEqual(Object value1, Object value2) {
        if (value1 == null) {
            return value2 == null;
        } else if (value2 == null) {
            // Of course, value1 is not null (we have just tested it).
            return false;
        } else {
            return value1.equals(value2);
        }
    }

    /**
     * Verifies that an object equals the reference object.
     *
     * <p>
     * The test checks:
     * </p>
     * <ul>
     * <li>That two mocks are equals (i.e. they are the same mock).</li>
     * <li>That other types of objects are equals, using their
     * <code>equals</code> method.</li>
     * </ul>
     */
    @Override
    public boolean valueIsCompatibleWith(Object value) {
        // Be sure that we're on the same line regarding mock objects.
        Object actualObject = Mock.getObjectOrMock(value);
        return valuesAreEqual(referenceObject, actualObject);
    }

    @Override
    public Class<?> getRelatedClass() {
        if (referenceObject == null) {
            return Object.class;
        } else {
            return referenceObject.getClass();
        }
    }

    /** Displays the reference object value. */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(24);
        builder.append(getRelatedClass().getSimpleName());
        builder.append('=');
        if (referenceObject == null) {
            builder.append("null");
        } else {
            builder.append(referenceObject.toString());
        }
        return builder.toString();
    }
}
