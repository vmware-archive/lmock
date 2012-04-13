/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.test;

import com.vmware.lmock.exception.ExpectationError;
import static com.vmware.lmock.test.Dalton.joe;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.vmware.lmock.checker.IntegerChecker;
import com.vmware.lmock.checker.Occurrences;
import com.vmware.lmock.exception.UnexpectedInvocationError;
import com.vmware.lmock.impl.Scenario;
import com.vmware.lmock.impl.Story;
import com.vmware.lmock.masquerade.Schemer;
import static com.vmware.lmock.test.LMAsserts.*;

/**
 * Verifies that the with clauses using checkers are properly handled.
 */
public class WithCheckerClauseTest {
    /**
     * Verifies that checkers supplied to a with clause are actually invoked.
     */
    @Test
    public void testWithChecker() {
        ExpectationError lastError = null;

        Story story = Story.create(new Scenario() {
            {
                expect(joe).setInt(with(IntegerChecker.valuesBetween(-3, 3)));
            }
        });

        story.begin();
        joe.setInt(3);
        try {
            joe.setInt(-5);
            fail("invoked with a value that should not satisfy the checker");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);
    }

    /**
     * Verifies that checkers supplied to a with clause are actually invoked.
     */
    @Test
    public void testWithCheckerMA() {
        ExpectationError lastError = null;

        Schemer.begin();
        Schemer.willInvoke(Occurrences.any()).of(joe).setInt(Schemer.with(IntegerChecker.valuesBetween(-3, 3)));
        joe.setInt(3);
        try {
            joe.setInt(-5);
            fail("invoked with a value that should not satisfy the checker");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);
    }
}
