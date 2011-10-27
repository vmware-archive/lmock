/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.test;

import static com.vmware.lmock.checker.CharacterChecker.isDefined;
import static com.vmware.lmock.checker.CharacterChecker.isDigit;
import static com.vmware.lmock.checker.CharacterChecker.isISOControl;
import static com.vmware.lmock.checker.CharacterChecker.isLetter;
import static com.vmware.lmock.checker.CharacterChecker.isLetterOrDigit;
import static com.vmware.lmock.checker.CharacterChecker.isLowerCase;
import static com.vmware.lmock.checker.CharacterChecker.isSpaceChar;
import static com.vmware.lmock.checker.CharacterChecker.isUpperCase;
import static com.vmware.lmock.checker.CharacterChecker.isWhitespace;
import static com.vmware.lmock.checker.CharacterChecker.valuesBetween;
import static com.vmware.lmock.checker.CharacterChecker.valuesGreaterOrEqualTo;
import static com.vmware.lmock.checker.CharacterChecker.valuesLowerOrEqualTo;
import static com.vmware.lmock.checker.CharacterChecker.verifyOneOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.vmware.lmock.checker.CharacterChecker;
import com.vmware.lmock.exception.CheckerCreationException;

/**
 * Validation of the character checker class.
 */
public class CharacterCheckerTest {
    /**
     * Verifies that an incoherent range is not allowed.
     */
    @Test
    public void testIncoherentRangeSpecification() {
        try {
            valuesBetween('z', 'a');
            fail("created a checker with range [z,a]");
        } catch (CheckerCreationException e) {
        }
    }

    /**
     * Verifies that integer checkers are associated to the proper class.
     */
    @Test
    public void testCharacterCheckerRelatedClass() {
        assertEquals(Character.class, valuesBetween('a', 'z').getRelatedClass());
    }

    /**
     * Creates and validates a checker for values greater than a lower bound.
     */
    @Test
    public void testValuesGreaterThan() {
        final char min = 'a';
        CharacterChecker instance = valuesGreaterOrEqualTo(min);
        assertTrue(instance.valueIsCompatibleWith(min));
        assertTrue(instance.valueIsCompatibleWith((char) (min + 1)));
        assertFalse(instance.valueIsCompatibleWith((char) (min - 1)));
    }

    /**
     * Creates and validates a checker for values lower than an upper bound.
     */
    @Test
    public void testValuesLowerThan() {
        final char max = 'z';
        CharacterChecker instance = valuesLowerOrEqualTo(max);
        assertTrue(instance.valueIsCompatibleWith(max));
        assertTrue(instance.valueIsCompatibleWith((char) (max - 1)));
        assertFalse(instance.valueIsCompatibleWith((char) (max + 1)));
    }

    /**
     * Creates and validates a checker for values within a range.
     */
    @Test
    public void testValuesBetween() {
        final char min = 'a';
        final char max = 'z';
        CharacterChecker instance = valuesBetween(min, max);
        for (int value = min; value <= max; value++) {
            assertTrue(instance.valueIsCompatibleWith((char) value));
        }
        assertFalse(instance.valueIsCompatibleWith((char) (min - 1)));
        assertFalse(instance.valueIsCompatibleWith((char) (max + 1)));
    }

    /**
     * Verifies that a checker with an empty property set allows any character.
     */
    @Test
    public void testEmptyPropertySet() {
        CharacterChecker instance = verifyOneOf();
        assertEquals("Character=[,]", instance.toString());
        // The ASCII set should be enough for this test...
        for (int value = 0; value < 128; value++) {
            assertTrue(instance.valueIsCompatibleWith((char) value));
        }
    }

    /**
     * Verifies that a checker specified with several properties behaves as an
     * 'or' on these properties.
     */
    @Test
    public void testMultipleProperties() {
        CharacterChecker instance = verifyOneOf(isWhitespace, isUpperCase);
        assertEquals("Character={ isWhitespace isUpperCase }",
          instance.toString());
        // Let's check the condition with the ASCII set...
        for (int ch = 0; ch < 128; ch++) {
            if (Character.isWhitespace(ch) || Character.isUpperCase(ch)) {
                assertTrue(instance.valueIsCompatibleWith((char) ch));
            } else {
                assertFalse(instance.valueIsCompatibleWith((char) ch));
            }
        }
    }

    /**
     * Verifies the property checkers.
     */
    @Test
    public void testPropertyCheckers() {
        CharacterChecker instance;

        instance = verifyOneOf(isDefined);
        assertEquals("Character={ isDefined }", instance.toString());
        assertTrue(instance.valueIsCompatibleWith('a'));
        assertFalse(instance.valueIsCompatibleWith(Character.MAX_VALUE));

        instance = verifyOneOf(isDigit);
        assertEquals("Character={ isDigit }", instance.toString());
        assertTrue(instance.valueIsCompatibleWith('0'));
        assertFalse(instance.valueIsCompatibleWith('A'));

        instance = verifyOneOf(isISOControl);
        assertEquals("Character={ isISOControl }", instance.toString());
        assertTrue(instance.valueIsCompatibleWith((char) 0));
        assertFalse(instance.valueIsCompatibleWith('x'));

        instance = verifyOneOf(isLetter);
        assertEquals("Character={ isLetter }", instance.toString());
        assertTrue(instance.valueIsCompatibleWith('b'));
        assertFalse(instance.valueIsCompatibleWith('~'));

        instance = verifyOneOf(isLetterOrDigit);
        assertEquals("Character={ isLetterOrDigit }", instance.toString());
        assertTrue(instance.valueIsCompatibleWith('x'));
        assertTrue(instance.valueIsCompatibleWith('4'));
        assertFalse(instance.valueIsCompatibleWith('_'));

        instance = verifyOneOf(isLowerCase);
        assertEquals("Character={ isLowerCase }", instance.toString());
        assertTrue(instance.valueIsCompatibleWith('z'));
        assertFalse(instance.valueIsCompatibleWith('0'));
        assertFalse(instance.valueIsCompatibleWith('Z'));

        instance = verifyOneOf(isSpaceChar);
        assertEquals("Character={ isSpaceChar }", instance.toString());
        assertTrue(instance.valueIsCompatibleWith(' '));
        assertFalse(instance.valueIsCompatibleWith('-'));

        instance = verifyOneOf(isUpperCase);
        assertEquals("Character={ isUpperCase }", instance.toString());
        assertTrue(instance.valueIsCompatibleWith('Z'));
        assertFalse(instance.valueIsCompatibleWith('0'));
        assertFalse(instance.valueIsCompatibleWith('z'));

        instance = verifyOneOf(isWhitespace);
        assertEquals("Character={ isWhitespace }", instance.toString());
        assertTrue(instance.valueIsCompatibleWith(' '));
        assertFalse(instance.valueIsCompatibleWith((char) 127));
    }
}
