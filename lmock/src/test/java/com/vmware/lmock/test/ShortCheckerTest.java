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

import com.vmware.lmock.checker.ShortChecker;
import com.vmware.lmock.exception.CheckerCreationException;

/**
 * Validation of the checker for short values.
 */
public class ShortCheckerTest {
    /**
     * Verifies that an incoherent range is not allowed.
     */
    @Test
    public void testIncoherentRangeSpecification() {
        try {
            ShortChecker.valuesBetween((short) 1, (short) 0);
            fail("created a checker with range [1,0]");
        } catch (CheckerCreationException e) {
        }
    }

    /**
     * Verifies that short checkers are associated to the proper class.
     */
    @Test
    public void testShortCheckerRelatedClass() {
        assertEquals(Short.class, ShortChecker.positiveValues.getRelatedClass());
    }

    /**
     * Creates and validates a checker for positive values.
     */
    @Test
    public void testPositiveValues() {
        assertFalse(ShortChecker.positiveValues.valueIsCompatibleWith((short) -1));
        assertTrue(ShortChecker.positiveValues.valueIsCompatibleWith((short) 0));
        assertTrue(ShortChecker.positiveValues.valueIsCompatibleWith((short) 1));
    }

    /**
     * Creates and validates a checker for negative values.
     */
    @Test
    public void testNegativeValues() {
        assertTrue(ShortChecker.negativeValues.valueIsCompatibleWith((short) -1));
        assertTrue(ShortChecker.negativeValues.valueIsCompatibleWith((short) 0));
        assertFalse(ShortChecker.negativeValues.valueIsCompatibleWith((short) 1));
    }

    /**
     * Creates and validates a checker for values greater than a lower bound.
     */
    @Test
    public void testValuesGreaterThan() {
        final short min = -66;
        ShortChecker instance = ShortChecker.valuesGreaterOrEqualTo(min);
        assertTrue(instance.valueIsCompatibleWith(min));
        assertTrue(instance.valueIsCompatibleWith((short) (min + 1)));
        assertFalse(instance.valueIsCompatibleWith((short) (min - 1)));
    }

    /**
     * Creates and validates a checker for values lower than an upper bound.
     */
    @Test
    public void testValuesLowerThan() {
        final short max = 32;
        ShortChecker instance = ShortChecker.valuesLowerOrEqualTo(max);
        assertTrue(instance.valueIsCompatibleWith(max));
        assertTrue(instance.valueIsCompatibleWith((short) (max - 1)));
        assertFalse(instance.valueIsCompatibleWith((short) (max + 1)));
    }

    /**
     * Creates and validates a checker for values within a range.
     */
    @Test
    public void testValuesBetween() {
        final short min = -2;
        final short max = 4;
        ShortChecker instance = ShortChecker.valuesBetween(min, max);
        for (int value = min; value <= max; value++) {
            assertTrue(instance.valueIsCompatibleWith((short) value));
        }
        assertFalse(instance.valueIsCompatibleWith((short) (min - 1)));
        assertFalse(instance.valueIsCompatibleWith((short) (max + 1)));
    }

    /**
     * Verifies that we don't get a null pointer exception with null values.
     */
    @Test
    public void testNullArguments() {
        ShortChecker instance = ShortChecker.valuesBetween((short) -2,
          (short) 4);
        assertFalse(instance.valueIsCompatibleWith(null));
    }
}
