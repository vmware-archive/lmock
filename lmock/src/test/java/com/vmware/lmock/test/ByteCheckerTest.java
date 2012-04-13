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

import com.vmware.lmock.checker.ByteChecker;
import com.vmware.lmock.exception.CheckerCreationException;

/**
 * Validation of the byte checker class.
 */
public class ByteCheckerTest {
    /**
     * Verifies that an incoherent range is not allowed.
     */
    @Test
    public void testIncoherentRangeSpecification() {
        try {
            ByteChecker.valuesBetween((byte) 0, (byte) -4);
            fail("created a checker with range [0,-4]");
        } catch (CheckerCreationException e) {
        }
    }

    /**
     * Verifies that integer checkers are associated to the proper class.
     */
    @Test
    public void testByteCheckerRelatedClass() {
        assertEquals(Byte.class, ByteChecker.positiveValues.getRelatedClass());
    }

    /**
     * Creates and validates a checker for positive values.
     */
    @Test
    public void testPositiveValues() {
        assertFalse(ByteChecker.positiveValues.valueIsCompatibleWith((byte) -1));
        assertTrue(ByteChecker.positiveValues.valueIsCompatibleWith((byte) 0));
        assertTrue(ByteChecker.positiveValues.valueIsCompatibleWith((byte) 1));
    }

    /**
     * Creates and validates a checker for negative values.
     */
    @Test
    public void testNegativeValues() {
        assertTrue(ByteChecker.negativeValues.valueIsCompatibleWith((byte) -1));
        assertTrue(ByteChecker.negativeValues.valueIsCompatibleWith((byte) 0));
        assertFalse(ByteChecker.negativeValues.valueIsCompatibleWith((byte) 1));
    }

    /**
     * Creates and validates a checker for values greater than a lower bound.
     */
    @Test
    public void testValuesGreaterThan() {
        final byte min = -66;
        ByteChecker instance = ByteChecker.valuesGreaterOrEqualTo(min);
        assertTrue(instance.valueIsCompatibleWith(min));
        assertTrue(instance.valueIsCompatibleWith((byte) (min + 1)));
        assertFalse(instance.valueIsCompatibleWith((byte) (min - 1)));
    }

    /**
     * Creates and validates a checker for values lower than an upper bound.
     */
    @Test
    public void testValuesLowerThan() {
        final byte max = 32;
        ByteChecker instance = ByteChecker.valuesLowerOrEqualTo(max);
        assertTrue(instance.valueIsCompatibleWith(max));
        assertTrue(instance.valueIsCompatibleWith((byte) (max - 1)));
        assertFalse(instance.valueIsCompatibleWith((byte) (max + 1)));
    }

    /**
     * Creates and validates a checker for values within a range.
     */
    @Test
    public void testValuesBetween() {
        final byte min = -2;
        final byte max = 4;
        ByteChecker instance = ByteChecker.valuesBetween(min, max);
        for (int value = min; value <= max; value++) {
            assertTrue(instance.valueIsCompatibleWith((byte) value));
        }
        assertFalse(instance.valueIsCompatibleWith((byte) (min - 1)));
        assertFalse(instance.valueIsCompatibleWith((byte) (max + 1)));
    }
}
