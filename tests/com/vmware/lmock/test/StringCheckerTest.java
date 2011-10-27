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

import org.junit.Test;

import com.vmware.lmock.checker.StringChecker;

/**
 * Tests of the string checkers.
 */
public class StringCheckerTest {
    /**
     * Verifies the value returned by <code>getRelatedClass</code>.
     */
    @Test
    public void testGetRelatedClass() {
        assertEquals(String.class, StringChecker.valuesEqual(null).getRelatedClass());
        assertEquals(String.class, StringChecker.valuesContain(null).getRelatedClass());
        assertEquals(String.class, StringChecker.valuesMatch(null).getRelatedClass());
    }

    /**
     * Verifies that <code>valuesEqual</code> can use a null reference.
     */
    @Test
    public void testValuesEqualWithNullReference() {
        StringChecker instance = StringChecker.valuesEqual(null);
        assertTrue(instance.valueIsCompatibleWith(null));
        assertFalse(instance.valueIsCompatibleWith("a string"));
    }

    /**
     * Verifies that <code>valuesContain</code> can use a null reference.
     */
    @Test
    public void testValuesContainWithNullReference() {
        StringChecker instance = StringChecker.valuesContain(null);
        assertTrue(instance.valueIsCompatibleWith(null));
        assertFalse(instance.valueIsCompatibleWith("a string"));
    }

    /**
     * Verifies that <code>valuesMatch</code> can use a null reference.
     */
    @Test
    public void testValuesMatchWithNullReference() {
        StringChecker instance = StringChecker.valuesMatch(null);
        assertTrue(instance.valueIsCompatibleWith(null));
        assertFalse(instance.valueIsCompatibleWith("a string"));
    }

    /**
     * Verifies that <code>valuesEqual</code> do not match null string if the
     * reference is not null.
     */
    @Test
    public void testValuesEqualWithNullValue() {
        StringChecker instance = StringChecker.valuesEqual("a string");
        assertFalse(instance.valueIsCompatibleWith(null));
    }

    /**
     * Verifies that <code>valuesContain</code> do not match null string if the
     * reference is not null.
     */
    @Test
    public void testValuesContainWithNullValue() {
        StringChecker instance = StringChecker.valuesContain("a string");
        assertFalse(instance.valueIsCompatibleWith(null));
    }

    /**
     * Verifies that <code>valuesMatch</code> do not match null string if the
     * reference is not null.
     */
    @Test
    public void testValuesMatchWithNullValue() {
        StringChecker instance = StringChecker.valuesMatch("a string");
        assertFalse(instance.valueIsCompatibleWith(null));
    }

    /**
     * Validates <code>valuesEqual</code> when case sensitive and insensitive.
     */
    @Test
    public void testValuesEqual() {
        StringChecker instance = StringChecker.valuesEqual("A string");
        assertFalse(instance.valueIsCompatibleWith("a string"));
        assertTrue(instance.valueIsCompatibleWith("A string"));
        assertFalse(instance.valueIsCompatibleWith("A string longer"));
        assertFalse(instance.valueIsCompatibleWith("with A string longer"));
        instance.caseInsensitive();
        assertTrue(instance.valueIsCompatibleWith("a string"));
        assertTrue(instance.valueIsCompatibleWith("A string"));
        assertFalse(instance.valueIsCompatibleWith("a string longer"));
        assertFalse(instance.valueIsCompatibleWith("with a string longer"));
    }

    /**
     * Validates <code>valuesContain</code> when case sensitive and insensitive.
     */
    @Test
    public void testValuesContain() {
        StringChecker instance = StringChecker.valuesContain("A string");
        assertFalse(instance.valueIsCompatibleWith("a string"));
        assertTrue(instance.valueIsCompatibleWith("A string"));
        assertTrue(instance.valueIsCompatibleWith("A string longer"));
        instance.caseInsensitive();
        assertTrue(instance.valueIsCompatibleWith("a string"));
        assertTrue(instance.valueIsCompatibleWith("A string"));
        assertTrue(instance.valueIsCompatibleWith("a strIng longer"));
    }

    /**
     * Validates <code>valuesMatch</code> when case sensitive and insensitive.
     */
    @Test
    public void testValuesMatch() {
        // Could not test every regular expression, so let's focus on a
        // simple one.
        StringChecker instance = StringChecker.valuesMatch(".*A string$");
        assertFalse(instance.valueIsCompatibleWith("a string"));
        assertTrue(instance.valueIsCompatibleWith("A string"));
        assertFalse(instance.valueIsCompatibleWith("A string longer"));
        assertTrue(instance.valueIsCompatibleWith("with A string"));
        assertFalse(instance.valueIsCompatibleWith("with A string longer"));
        instance.caseInsensitive();
        assertTrue(instance.valueIsCompatibleWith("a string"));
        assertTrue(instance.valueIsCompatibleWith("A StrIng"));
        assertFalse(instance.valueIsCompatibleWith("a string longer"));
        assertTrue(instance.valueIsCompatibleWith("with A StriNg"));
        assertFalse(instance.valueIsCompatibleWith("with a string longer"));
    }

    /**
     * Validates the string describing matches.
     */
    @Test
    public void testStringMatcherToString() {
        StringChecker instance = StringChecker.valuesEqual("hello world");
        assertEquals("equals(hello world)", instance.toString());
        instance = StringChecker.valuesContain("hello world");
        assertEquals("contains(hello world)", instance.toString());
        instance = StringChecker.valuesMatch(".*hello world$");
        assertEquals("matches(.*hello world$)", instance.toString());
    }
}
