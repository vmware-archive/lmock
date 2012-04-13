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

import com.vmware.lmock.checker.DoubleChecker;
import com.vmware.lmock.exception.CheckerCreationException;

/**
 * Validation of the checkers for double values.
 */
public class DoubleCheckerTest {
    /**
     * Verifies that an incoherent range is not allowed.
     */
    @Test
    public void testIncoherentRangeSpecification() {
        try {
            DoubleChecker.valuesBetween(6.56, -5.45);
            fail("created a checker with range [6.56,-5.45]");
        } catch (CheckerCreationException e) {
        }
    }

    /**
     * Verifies that double checkers are associated to the proper class.
     */
    @Test
    public void testDoubleCheckerRelatedClass() {
        assertEquals(Double.class,
          DoubleChecker.positiveValues.getRelatedClass());
    }

    /**
     * Creates and validates a checker for positive values.
     */
    @Test
    public void testPositiveValues() {
        assertFalse(DoubleChecker.positiveValues.valueIsCompatibleWith(-0.001));
        assertTrue(DoubleChecker.positiveValues.valueIsCompatibleWith(0.0));
        assertTrue(DoubleChecker.positiveValues.valueIsCompatibleWith(0.001));
    }

    /**
     * Creates and validates a checker for negative values.
     */
    @Test
    public void testNegativeValues() {
        assertTrue(DoubleChecker.negativeValues.valueIsCompatibleWith(-0.001));
        assertTrue(DoubleChecker.negativeValues.valueIsCompatibleWith(0.0));
        assertFalse(DoubleChecker.negativeValues.valueIsCompatibleWith(0.001));
    }

    /**
     * Creates and validates a checker for values greater than a lower bound.
     */
    @Test
    public void testValuesGreaterThan() {
        final double min = 9.89;
        DoubleChecker instance = DoubleChecker.valuesGreaterOrEqualTo(min);
        assertTrue(instance.valueIsCompatibleWith(min));
        assertTrue(instance.valueIsCompatibleWith(min + 1));
        assertFalse(instance.valueIsCompatibleWith(min - 1));
    }

    /**
     * Creates and validates a checker for values lower than an upper bound.
     */
    @Test
    public void testValuesLowerThan() {
        final double max = -6.987;
        DoubleChecker instance = DoubleChecker.valuesLowerOrEqualTo(max);
        assertTrue(instance.valueIsCompatibleWith(max));
        assertTrue(instance.valueIsCompatibleWith(max - 1));
        assertFalse(instance.valueIsCompatibleWith(max + 1));
    }

    /**
     * Creates and validates a checker for values within a range.
     */
    @Test
    public void testValuesBetween() {
        DoubleChecker instance = DoubleChecker.valuesBetween(-1.21, 2.32);
        assertTrue(instance.valueIsCompatibleWith(0.0));
        assertFalse(instance.valueIsCompatibleWith(-2.22));
        assertFalse(instance.valueIsCompatibleWith(2.33));
    }

    /**
     * Verifies that we don't get a null pointer exception with null values.
     */
    @Test
    public void testNullArguments() {
        DoubleChecker instance = DoubleChecker.valuesBetween(-2.0, 4.0);
        assertFalse(instance.valueIsCompatibleWith(null));
    }
}
