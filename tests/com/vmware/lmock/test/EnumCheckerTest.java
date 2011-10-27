/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.test;

import static com.vmware.lmock.checker.EnumChecker.oneOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vmware.lmock.checker.EnumChecker;

/**
 * Tests of the enumeration checker.
 */
public class EnumCheckerTest {
    /** An enumeration used for tests. */
    enum TestData {
        VALUE0, VALUE1, VALUE2
    };

    /**
     * Verifies that a checker created with no allowed values rejects any value.
     */
    @Test
    public void testCheckerWithNoAllowedValue() {
        EnumChecker<TestData> checker = oneOf(TestData.class);
        for (TestData data : TestData.values()) {
            assertFalse(checker.valueIsCompatibleWith(data));
        }
    }

    /**
     * Verifies that a checker actually stores allowed values.
     */
    @Test
    public void testCheckerWithAllowedValues() {
        EnumChecker<TestData> checker = oneOf(TestData.class, TestData.VALUE0,
          TestData.VALUE1);
        assertTrue(checker.valueIsCompatibleWith(TestData.VALUE0));
        assertTrue(checker.valueIsCompatibleWith(TestData.VALUE1));
        assertFalse(checker.valueIsCompatibleWith(TestData.VALUE2));
    }

    /**
     * Verifies that <code>getRelatedClass</code> returns the proper value.
     */
    @Test
    public void testGetRelatedClass() {
        assertEquals(TestData.class, oneOf(TestData.class).getRelatedClass());
    }
}
