/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.test;

import static com.vmware.lmock.checker.Occurrences.any;
import static com.vmware.lmock.test.Dalton.averell;
import static com.vmware.lmock.test.Dalton.jack;
import static com.vmware.lmock.test.Dalton.joe;
import static com.vmware.lmock.test.Dalton.william;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.vmware.lmock.exception.IllegalClauseException;
import com.vmware.lmock.exception.UnexpectedInvocationError;
import com.vmware.lmock.impl.Scenario;

/**
 * Validates the basic behavior regarding the definition of a scenario.
 *
 * <p>
 * The main goal is to verify that the user cannot inject incoherent statements
 * when creating scenarios.
 * </p>
 */
public class ScenarioTest {
    /**
     * Verifies that a mock cannot be directly invoked within the construction
     * of a scenario.
     */
    @Test
    public void testMockInvocationWithNoExpect() {
        new Scenario() {
            {
                try {
                    joe.ping(jack);
                    fail("invoked a mock outside of an expect");
                } catch (UnexpectedInvocationError e) {
                }
            }
        };
    }

    /**
     * Verifies that a mock cannot be directly invoked within the construction
     * of a scenario.
     *
     * Corner case: we had a valid expectation before, so we must be sure that
     * the invocation handler was removed.
     */
    @Test
    public void testMockInvocationWithNoExpectNow() {
        new Scenario() {
            {
                expect(joe).ping(jack);
                try {
                    joe.ping(william);
                    fail("invoked a mock outside of an expect");
                } catch (UnexpectedInvocationError e) {
                }
            }
        };
    }

    /**
     * Verifies that an occurs clause cannot be called without an expectation.
     */
    @Test
    public void testOccursClauseWithNoExpectation() {
        new Scenario() {
            {
                try {
                    occurs(any());
                    fail("specified a clause without an expectation");
                } catch (IllegalClauseException e) {
                }
            }
        };
    }

    /**
     * Verifies that a willReturn clause cannot be called without an
     * expectation.
     */
    @Test
    public void testWillReturnClauseWithNoExpectation() {
        new Scenario() {
            {
                try {
                    willReturn(true);
                    fail("specified a clause without an expectation");
                } catch (IllegalClauseException e) {
                }
            }
        };
    }

    /**
     * Verifies that a willThrow clause cannot be called without an expectation.
     */
    @Test
    public void testWillThrowClauseWithNoExpectation() {
        new Scenario() {
            {
                try {
                    willThrow(new RuntimeException());
                    fail("specified a clause without an expectation");
                } catch (IllegalClauseException e) {
                }
            }
        };
    }

    /**
     * Verifies that a with clause cannot be called outside of an expectation.
     */
    @Test
    public void testWithClauseWithNoExpectation() {
        new Scenario() {
            {
                try {
                    with(4);
                    fail("specified a clause without an expectation");
                } catch (IllegalClauseException e) {
                }
            }
        };
    }

    /**
     * Verifies that an anyOf clause cannot be called outside of an expectation.
     */
    @Test
    public void testAnyOfClauseWithNoExpectation() {
        new Scenario() {
            {
                try {
                    anyOf(Object.class);
                    fail("specified a clause without an expectation");
                } catch (IllegalClauseException e) {
                }
            }
        };
    }

    /**
     * Verifies that an aNonNullOf clause cannot be called outside of an
     * expectation.
     */
    @Test
    public void testANonNullOfClauseWithNoExpectation() {
        new Scenario() {
            {
                try {
                    aNonNullOf(Object.class);
                    fail("specified a clause without an expectation");
                } catch (IllegalClauseException e) {
                }
            }
        };
    }

    /**
     * Verifies that a with clause cannot be called outside an invocation.
     */
    @Test
    public void testWithClauseOutsideInvocation() {
        new Scenario() {
            {
                expect(joe).ping(with(jack));
                try {
                    with(averell);
                    fail("used a with clause outside of invocation");
                } catch (IllegalClauseException e) {
                }
            }
        };
    }

    /**
     * Verifies that an anyOf clause cannot be called outside an invocation.
     */
    @Test
    public void testAnyOfClauseOutsideInvocation() {
        new Scenario() {
            {
                expect(joe).ping(with(jack));
                try {
                    anyOf(Dalton.class);
                    fail("used an anyOf clause outside of invocation");
                } catch (IllegalClauseException e) {
                }
            }
        };
    }

    /**
     * Verifies that an aNonNullOf clause cannot be called outside an
     * invocation.
     */
    @Test
    public void testANonNullOfClauseOutsideInvocation() {
        new Scenario() {
            {
                expect(joe).ping(with(jack));
                try {
                    aNonNullOf(Dalton.class);
                    fail("used an aNonNull clause outside an invocation");
                } catch (IllegalClauseException e) {
                }
            }
        };
    }
}
