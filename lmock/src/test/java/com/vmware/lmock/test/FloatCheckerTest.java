/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.vmware.lmock.checker.FloatChecker;
import com.vmware.lmock.exception.CheckerCreationException;

/**
 * Validation of the checkers for float values.
 */
public class FloatCheckerTest {
    /**
     * Verifies that an incoherent range is not allowed.
     */
    @Test
    public void testIncoherentRangeSpecification() {
        try {
            FloatChecker.valuesBetween((float) 6.53, (float) -15.2);
            fail("created a checker with range [6.53,-15.2]");
        } catch (CheckerCreationException e) {
        }
    }

    /**
     * Verifies that float checkers are associated to the proper class.
     */
    @Test
    public void testFloatCheckerRelatedClass() {
        assertEquals(Float.class, FloatChecker.positiveValues.getRelatedClass());
    }

    /**
     * Creates and validates a checker for positive values.
     */
    @Test
    public void testPositiveValues() {
        assertFalse(FloatChecker.positiveValues.valueIsCompatibleWith((float) -3.33));
        assertTrue(FloatChecker.positiveValues.valueIsCompatibleWith((float) 0));
        assertTrue(FloatChecker.positiveValues.valueIsCompatibleWith((float) 4.54));
    }

    /**
     * Creates and validates a checker for negative values.
     */
    @Test
    public void testNegativeValues() {
        assertTrue(FloatChecker.negativeValues.valueIsCompatibleWith((float) -1.21));
        assertTrue(FloatChecker.negativeValues.valueIsCompatibleWith((float) 0));
        assertFalse(FloatChecker.negativeValues.valueIsCompatibleWith((float) 1.21));
    }

    /**
     * Creates and validates a checker for values greater than a lower bound.
     */
    @Test
    public void testValuesGreaterThan() {
        final float min = (float) -6.67;
        FloatChecker instance = FloatChecker.valuesGreaterOrEqualTo(min);
        assertTrue(instance.valueIsCompatibleWith(min));
        assertTrue(instance.valueIsCompatibleWith(min + 1));
        assertFalse(instance.valueIsCompatibleWith(min - 1));
    }

    /**
     * Creates and validates a checker for values lower than an upper bound.
     */
    @Test
    public void testValuesLowerThan() {
        final float max = 32;
        FloatChecker instance = FloatChecker.valuesLowerOrEqualTo(max);
        assertTrue(instance.valueIsCompatibleWith(max));
        assertTrue(instance.valueIsCompatibleWith(max - 1));
        assertFalse(instance.valueIsCompatibleWith(max + 1));
    }

    /**
     * Creates and validates a checker for values within a range.
     */
    @Test
    public void testValuesBetween() {
        final float min = -2;
        final float max = 4;
        FloatChecker instance = FloatChecker.valuesBetween(min, max);
        assertTrue(instance.valueIsCompatibleWith((float) -1.21));
        assertFalse(instance.valueIsCompatibleWith(min - 1));
        assertFalse(instance.valueIsCompatibleWith(max + 1));
    }

    /**
     * Verifies that we don't get a null pointer exception with null values.
     */
    @Test
    public void testNullArguments() {
        FloatChecker instance = FloatChecker.valuesBetween((float) -2.2,
          (float) 4.4);
        assertFalse(instance.valueIsCompatibleWith(null));
    }
}
