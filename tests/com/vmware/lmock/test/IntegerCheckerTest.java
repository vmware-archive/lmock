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

import com.vmware.lmock.checker.IntegerChecker;
import com.vmware.lmock.exception.CheckerCreationException;

/**
 * Validation of the integer checker class.
 */
public class IntegerCheckerTest {
    /**
     * Verifies that an incoherent range is not allowed.
     */
    @Test
    public void testIncoherentRangeSpecification() {
        try {
            IntegerChecker.valuesBetween(1, 0);
            fail("created a checker with range [1,0]");
        } catch (CheckerCreationException e) {
        }
    }

    /**
     * Verifies that integer checkers are associated to the proper class.
     */
    @Test
    public void testIntegerCheckerRelatedClass() {
        assertEquals(Integer.class,
          IntegerChecker.positiveValues.getRelatedClass());
    }

    /**
     * Creates and validates a checker for positive values.
     */
    @Test
    public void testPositiveValues() {
        assertFalse(IntegerChecker.positiveValues.valueIsCompatibleWith(-1));
        assertTrue(IntegerChecker.positiveValues.valueIsCompatibleWith(0));
        assertTrue(IntegerChecker.positiveValues.valueIsCompatibleWith(1));
    }

    /**
     * Creates and validates a checker for negative values.
     */
    @Test
    public void testNegativeValues() {
        assertTrue(IntegerChecker.negativeValues.valueIsCompatibleWith(-1));
        assertTrue(IntegerChecker.negativeValues.valueIsCompatibleWith(0));
        assertFalse(IntegerChecker.negativeValues.valueIsCompatibleWith(1));
    }

    /**
     * Creates and validates a checker for values greater than a lower bound.
     */
    @Test
    public void testValuesGreaterThan() {
        final int min = -66;
        IntegerChecker instance = IntegerChecker.valuesGreaterOrEqualTo(min);
        assertTrue(instance.valueIsCompatibleWith(min));
        assertTrue(instance.valueIsCompatibleWith(min + 1));
        assertFalse(instance.valueIsCompatibleWith(min - 1));
    }

    /**
     * Creates and validates a checker for values lower than an upper bound.
     */
    @Test
    public void testValuesLowerThan() {
        final int max = 32;
        IntegerChecker instance = IntegerChecker.valuesLowerOrEqualTo(max);
        assertTrue(instance.valueIsCompatibleWith(max));
        assertTrue(instance.valueIsCompatibleWith(max - 1));
        assertFalse(instance.valueIsCompatibleWith(max + 1));
    }

    /**
     * Creates and validates a checker for values within a range.
     */
    @Test
    public void testValuesBetween() {
        final int min = -2;
        final int max = 4;
        IntegerChecker instance = IntegerChecker.valuesBetween(min, max);
        for (int value = min; value <= max; value++) {
            assertTrue(instance.valueIsCompatibleWith(value));
        }
        assertFalse(instance.valueIsCompatibleWith(min - 1));
        assertFalse(instance.valueIsCompatibleWith(max + 1));
    }

    /**
     * Verifies that we don't get a null pointer exception with null values.
     */
    @Test
    public void testNullArguments() {
        final int min = -2;
        final int max = 4;
        IntegerChecker instance = IntegerChecker.valuesBetween(min, max);
        assertFalse(instance.valueIsCompatibleWith(null));
    }
}
