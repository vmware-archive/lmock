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

import com.vmware.lmock.checker.LongChecker;
import com.vmware.lmock.exception.CheckerCreationException;

/**
 * Validation of the checkers for long values.
 */
public class LongCheckerTest {
    /**
     * Verifies that an incoherent range is not allowed.
     */
    @Test
    public void testIncoherentRangeSpecification() {
        try {
            LongChecker.valuesBetween(1L, 0L);
            fail("created a checker with range [1,0]");
        } catch (CheckerCreationException e) {
        }
    }

    /**
     * Verifies that long checkers are associated to the proper class.
     */
    @Test
    public void testLongCheckerRelatedClass() {
        assertEquals(Long.class, LongChecker.positiveValues.getRelatedClass());
    }

    /**
     * Creates and validates a checker for positive values.
     */
    @Test
    public void testPositiveValues() {
        assertFalse(LongChecker.positiveValues.valueIsCompatibleWith(-1L));
        assertTrue(LongChecker.positiveValues.valueIsCompatibleWith(0L));
        assertTrue(LongChecker.positiveValues.valueIsCompatibleWith(1L));
    }

    /**
     * Creates and validates a checker for negative values.
     */
    @Test
    public void testNegativeValues() {
        assertTrue(LongChecker.negativeValues.valueIsCompatibleWith(-1L));
        assertTrue(LongChecker.negativeValues.valueIsCompatibleWith(0L));
        assertFalse(LongChecker.negativeValues.valueIsCompatibleWith(1L));
    }

    /**
     * Creates and validates a checker for values greater than a lower bound.
     */
    @Test
    public void testValuesGreaterThan() {
        final long min = -66;
        LongChecker instance = LongChecker.valuesGreaterOrEqualTo(min);
        assertTrue(instance.valueIsCompatibleWith(min));
        assertTrue(instance.valueIsCompatibleWith(min + 1));
        assertFalse(instance.valueIsCompatibleWith(min - 1));
    }

    /**
     * Creates and validates a checker for values lower than an upper bound.
     */
    @Test
    public void testValuesLowerThan() {
        final long max = 32;
        LongChecker instance = LongChecker.valuesLowerOrEqualTo(max);
        assertTrue(instance.valueIsCompatibleWith(max));
        assertTrue(instance.valueIsCompatibleWith(max - 1));
        assertFalse(instance.valueIsCompatibleWith(max + 1));
    }

    /**
     * Creates and validates a checker for values within a range.
     */
    @Test
    public void testValuesBetween() {
        final long min = -2;
        final long max = 4;
        LongChecker instance = LongChecker.valuesBetween(min, max);
        for (long value = min; value <= max; value++) {
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
        LongChecker instance = LongChecker.valuesBetween(-2L, 4L);
        assertFalse(instance.valueIsCompatibleWith(null));
    }
}
