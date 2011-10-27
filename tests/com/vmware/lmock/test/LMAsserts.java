/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.test;

import com.vmware.lmock.exception.ExpectationError;
// IMPORT ASSERTS FOR ANDROID
import static org.junit.Assert.*;

import com.vmware.lmock.impl.Story;
import com.vmware.lmock.masquerade.Schemer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A set of useful assertions for tests.
 */
final class LMAsserts {
    /** A singleton for assertion, just to make the things clean. */
    private static final LMAsserts asserts = new LMAsserts();

    /**
     * Validates the fact that an error is propagated by the <code>end</code>
     * method.
     *
     * @param story
     *            the ended story
     * @param error
     *            the expected error
     */
    private void endReportsError(Story story, ExpectationError error) {
        try {
            story.end();
            fail("end does not report previous error");
        } catch (ExpectationError e) {
            assertEquals(error, e);
        }
    }

    /**
     * Validates the fact than an error is propagated by the <code>end</code>
     * method when using the schemer.
     *
     * @param error
     *            the expected error
     */
    private void endReportsError(ExpectationError error) {
        try {
            Schemer.end();
            fail("end does not report previous error");
        } catch (ExpectationError e) {
            assertEquals(error, e);
        }
    }

    /**
     * Validates the fact than an error is propagated by the <code>end</code>
     * method when using the schemer.
     *
     * @param error
     *            the expected error
     */
    static void assertEndReportsError(ExpectationError error) {
        asserts.endReportsError(error);
    }

    /**
     * Validates the fact that an error is propagated by the <code>end</code>
     * method.
     *
     * @param story
     *            the ended story
     * @param error
     *            the expected error
     */
    static void assertEndReportsError(Story story, ExpectationError error) {
        asserts.endReportsError(story, error);
    }

    /**
     * A simple assertion to check if a regular expression matches a string.
     *
     * @param input
     *            the checked string
     * @param regexp
     *            the regular expression to look for
     */
    static void assertStringContains(String input, String regexp) {
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(input);
        assertTrue("'" + input + "' contains '" + regexp + "'", matcher.find());
    }
}
