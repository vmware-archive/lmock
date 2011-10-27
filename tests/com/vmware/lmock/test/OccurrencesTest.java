/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.test;

import com.vmware.lmock.exception.ExpectationError;
import static com.vmware.lmock.checker.Occurrences.any;
import static com.vmware.lmock.checker.Occurrences.atLeast;
import static com.vmware.lmock.checker.Occurrences.atMost;
import static com.vmware.lmock.checker.Occurrences.between;
import static com.vmware.lmock.checker.Occurrences.exactly;
import static com.vmware.lmock.checker.Occurrences.never;
import static com.vmware.lmock.test.Dalton.averell;
import static com.vmware.lmock.test.Dalton.jack;
import static com.vmware.lmock.test.Dalton.joe;
import static com.vmware.lmock.test.Dalton.william;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.vmware.lmock.exception.UnexpectedInvocationError;
import com.vmware.lmock.exception.UnsatisfiedOccurrenceError;
import com.vmware.lmock.impl.Scenario;
import com.vmware.lmock.impl.Story;
import com.vmware.lmock.masquerade.Schemer;
import static com.vmware.lmock.test.LMAsserts.*;

/**
 * Validation of occurrences in stories.
 *
 * <p>
 * This series of tests verify that the occurrences are correctly checked during
 * the execution of stories.
 * </p>
 */
public class OccurrencesTest {
    /**
     * Validates the checking of exactly(1).
     */
    @Test
    public void testExactlyWithOneMock() {
        Story story = Story.create(new Scenario() {
            {
                expect(joe).ping();
                occurs(exactly(1));
            }
        });
        story.begin();
        joe.ping();
        story.end();
    }

    /**
     * Validates the checking of exactly(1).
     */
    @Test
    public void testExactlyWithOneMockMA() {
        Schemer.begin();
        Schemer.willInvoke(1).of(joe).ping();
        joe.ping();
        Schemer.end();
    }

    /**
     * Validates the checking of exactly(1), using several mocks.
     */
    @Test
    public void testExactlyWithSeveralMocks() {
        Story story = Story.create(new Scenario() {
            {
                expect(joe).ping();
                occurs(exactly(1));
                expect(jack).ping();
                occurs(exactly(1));
                expect(william).ping();
                occurs(exactly(1));
                expect(averell).ping();
                occurs(exactly(1));
            }
        });
        story.begin();
        joe.ping();
        jack.ping();
        william.ping();
        averell.ping();
        story.end();
    }

    /**
     * Validates the checking of exactly(1), using several mocks.
     */
    @Test
    public void testExactlyWithSeveralMocksMA() {
        Schemer.begin();
        Schemer.willInvoke(1).of(joe).ping();
        Schemer.willInvoke(1).of(jack).ping();
        Schemer.willInvoke(1).of(william).ping();
        Schemer.willInvoke(1).of(averell).ping();
        joe.ping();
        jack.ping();
        william.ping();
        averell.ping();
        Schemer.end();
    }

    /**
     * Verifies that an exactly occurrence that never happens raises an error.
     */
    @Test
    public void testExactlyNeverHappens() {
        Story story = Story.create(new Scenario() {
            {
                expect(joe).ping();
                occurs(exactly(1));
            }
        });

        story.begin();
        try {
            story.end();
            fail("ended a story with an unsatisfied expectation");
        } catch (UnsatisfiedOccurrenceError e) {
        }
    }

    /**
     * Verifies that an exactly occurrence that never happens raises an error.
     */
    @Test
    public void testExactlyNeverHappensMA() {
        Schemer.begin();
        Schemer.willInvoke(1).of(joe).ping();
        try {
            Schemer.end();
            fail("ended a story with an unsatisfied expectation");
        } catch (UnsatisfiedOccurrenceError e) {
        }
    }

    /**
     * Verifies that the story remains stuck on an exactly clause even if other
     * invocations occur.
     */
    @Test
    public void testExactlyUnsatisfiedWhenInvokingAnotherMethod() {
        ExpectationError lastError = null;

        Story story = Story.create(new Scenario() {
            {
                expect(joe).ping();
                occurs(exactly(2));
                expect(joe).ping(jack);
            }
        });
        story.begin();
        joe.ping();
        try {
            joe.ping(jack);
            fail("continued the story with an unsatisfied expectation");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);
    }

    /**
     * Verifies that the story remains stuck on an exactly clause even if other
     * invocations occur.
     */
    @Test
    public void testExactlyUnsatisfiedWhenInvokingAnotherMethodMA() {
        ExpectationError lastError = null;

        Schemer.begin();
        Schemer.willInvoke(2).of(joe).ping();
        Schemer.willInvoke(any()).of(joe).ping(jack);

        joe.ping();
        try {
            joe.ping(jack);
            fail("continued the story with an unsatisfied expectation");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);
    }

    /**
     * Verifies that a story remains stuck on the first expectation if it's an
     * exactly clause.
     */
    @Test
    public void testExactlyUnsatisfiedWhenEarlyInvokingAnotherMethod() {
        ExpectationError lastError = null;

        Schemer.begin();
        Schemer.willInvoke(1).of(joe).ping(jack);
        Schemer.willInvoke(any()).of(joe).ping();
        try {
            joe.ping();
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);
    }

    /**
     * Verifies that a never clause passes when no invocation occurs.
     */
    @Test
    public void testNeverNotCallingForbiddenInvocation() {
        Story story = Story.create(new Scenario() {
            {
                expect(joe).ping();
                expect(joe).ping(jack);
                occurs(never());
                expect(william).ping();
            }
        });
        story.begin();
        joe.ping();
        joe.ping();
        william.ping();
        story.end();
    }

    /**
     * Verifies that a never clause passes when no invocation occurs.
     */
    @Test
    public void testNeverNotCallingForbiddenInvocationMA() {
        Schemer.begin();
        Schemer.willInvoke(any()).of(joe).ping();
        Schemer.willInvoke(never()).of(joe).ping(jack);
        Schemer.willInvoke(any()).of(william).ping();
        joe.ping();
        joe.ping();
        william.ping();
        Schemer.end();
    }

    /**
     * Verifies that a never clause fails when the forbidden invocation occurs.
     */
    @Test
    public void testNeverCallingForbiddenInvocation() {
        ExpectationError lastError = null;

        Schemer.begin();
        Schemer.willInvoke(any()).of(joe).ping();
        Schemer.willInvoke(never()).of(joe).ping(jack);
        Schemer.willInvoke(any()).of(william).ping();
        joe.ping();
        joe.ping();
        try {
            joe.ping(jack);
            fail("passed an expectation specified with a never clause");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        // Next expectations should be unexpected since the story is cleaning
        // up.
        try {
            william.ping();
            fail("passed an expectation after the story is finished");
        } catch (UnexpectedInvocationError e) {
        }
        assertEndReportsError(lastError);
    }

    /**
     * Verifies that a never clause at the end of a scenario does not stuck the
     * story.
     */
    @Test
    public void testNeverAtTheEndOfAScenario() {
        Story story = Story.create(new Scenario() {
            {
                expect(joe).ping();
                expect(joe).ping(jack);
                occurs(never());
                expect(william).ping();
                occurs(never());
            }
        });
        story.begin();
        joe.ping();
        joe.ping();
        story.end();
    }

    /**
     * Verifies that a never clause at the end of a scenario does not stuck the
     * story.
     */
    @Test
    public void testNeverAtTheEndOfAScenarioMA() {
        Schemer.begin();
        Schemer.willInvoke(any()).of(joe).ping();
        Schemer.willInvoke(never()).of(joe).ping(jack);
        Schemer.willInvoke(never()).of(william).ping();
        joe.ping();
        joe.ping();
        Schemer.end();
    }

    /**
     * Tests a stupid corner case: never is swallowed by the next expectation.
     */
    @Test
    public void testNeverFollowedByExactly() {
        Story story = Story.create(new Scenario() {
            {
                expect(joe).ping();
                occurs(never()); // Will be swallowed by the next expectation
                expect(joe).ping();
                occurs(exactly(1));
                expect(jack).ping();
            }
        });
        story.begin();
        joe.ping();
        jack.ping();
        story.end();
    }

    /**
     * Tests a stupid corner case: never is swallowed by the next expectation.
     */
    @Test
    public void testNeverFollowedByExactlyMA() {
        Schemer.begin();
        Schemer.willInvoke(never()).of(joe).ping();
        Schemer.willInvoke(1).of(joe).ping();
        Schemer.willInvoke(any()).of(jack).ping();
        joe.ping();
        jack.ping();
        Schemer.end();
    }

    /**
     * Verifies that the any occurrences is not a brake for the story.
     *
     * In this case the same expectation specified to occur any times then
     * exactly once is verified.
     */
    @Test
    public void testAnyFirstWithoutInvocations() {
        Story story = Story.create(new Scenario() {
            {
                expect(joe).ping();
                occurs(any());
                expect(jack).ping();
                occurs(exactly(1));
            }
        });
        story.begin();
        jack.ping();
        story.end();
    }

    /**
     * Verifies that the any occurrences is not a brake for the story.
     *
     * In this case the same expectation specified to occur any times then
     * exactly once is verified.
     */
    @Test
    public void testAnyFirstWithoutInvocationsMA() {
        Schemer.begin();
        Schemer.willInvoke(any()).of(joe).ping();
        Schemer.willInvoke(1).of(jack).ping();
        jack.ping();
        Schemer.end();
    }

    /**
     * Verifies that we can stay on an any occurrences as many times as we want.
     */
    @Test
    public void testAnyFirstWithInvocations() {
        Story story = Story.create(new Scenario() {
            {
                expect(joe).ping();
                occurs(any());
                expect(jack).ping();
                occurs(exactly(1));
            }
        });
        story.begin();
        joe.ping();
        joe.ping();
        jack.ping();
        story.end();
    }

    /**
     * Verifies that we can stay on an any occurrences as many times as we want.
     */
    @Test
    public void testAnyFirstWithInvocationsMA() {
        Schemer.begin();
        Schemer.willInvoke(any()).of(joe).ping();
        Schemer.willInvoke(1).of(jack).ping();
        joe.ping();
        joe.ping();
        jack.ping();
        Schemer.end();
    }

    /**
     * Verifies that an any clause at the end of a story does not stuck the
     * story when no invocation occurs.
     */
    @Test
    public void testAnyLastWithoutInvocations() {
        Story story = Story.create(new Scenario() {
            {
                expect(jack).ping();
                occurs(exactly(1));
                expect(joe).ping();
                occurs(any());
            }
        });
        story.begin();
        jack.ping();
        story.end();
    }

    /**
     * Verifies that an any clause at the end of a story does not stuck the
     * story when no invocation occurs.
     */
    @Test
    public void testAnyLastWithoutInvocationsMA() {
        Schemer.begin();
        Schemer.willInvoke(1).of(jack).ping();
        Schemer.willInvoke(any()).of(joe).ping();
        jack.ping();
        Schemer.end();
    }

    /**
     * A trivial test of the any clause at the end of a story.
     */
    @Test
    public void testAnyLastWithInvocations() {
        Story story = Story.create(new Scenario() {
            {
                expect(jack).ping();
                occurs(exactly(1));
                expect(joe).ping();
                occurs(any());
            }
        });
        story.begin();
        jack.ping();
        joe.ping();
        joe.ping();
        story.end();
    }

    /**
     * A trivial test of the any clause at the end of a story.
     */
    @Test
    public void testAnyLastWithInvocationsMA() {
        Schemer.begin();
        Schemer.willInvoke(1).of(jack).ping();
        Schemer.willInvoke(any()).of(joe).ping();
        jack.ping();
        joe.ping();
        joe.ping();
        Schemer.end();
    }

    /**
     * Validation of a satisfied atLeast clause.
     */
    @Test
    public void testAtLeastFirstWithRequiredInvocations() {
        Story story = Story.create(new Scenario() {
            {
                expect(joe).ping();
                occurs(atLeast(3));
                expect(jack).ping();
                occurs(exactly(1));
            }
        });
        story.begin();
        joe.ping();
        joe.ping();
        joe.ping();
        jack.ping();
        story.end();
    }

    /**
     * Validation of a satisfied atLeast clause.
     */
    @Test
    public void testAtLeastFirstWithRequiredInvocationsMA() {
        Schemer.begin();
        Schemer.willInvoke(atLeast(3)).of(joe).ping();
        Schemer.willInvoke(1).of(jack).ping();
        joe.ping();
        joe.ping();
        joe.ping();
        jack.ping();
        Schemer.end();
    }

    /**
     * Verifies that atLeast does not put an upper bound to the occurrences
     * range.
     */
    @Test
    public void testAtLeastFirstWithMoreInvocations() {
        Story story = Story.create(new Scenario() {
            {
                expect(joe).ping();
                occurs(atLeast(3));
                expect(jack).ping();
                occurs(exactly(1));
            }
        });
        story.begin();
        joe.ping();
        joe.ping();
        joe.ping();
        joe.ping();
        jack.ping();
        story.end();
    }

    /**
     * Verifies that atLeast does not put an upper bound to the occurrences
     * range.
     */
    @Test
    public void testAtLeastFirstWithMoreInvocationsMA() {
        Schemer.begin();
        Schemer.willInvoke(atLeast(3)).of(joe).ping();
        Schemer.willInvoke(1).of(jack).ping();
        joe.ping();
        joe.ping();
        joe.ping();
        joe.ping();
        jack.ping();
        Schemer.end();
    }

    /**
     * Verifies that atLeast actually defines a lower bound to occurrences.
     */
    @Test
    public void testAtLeastFirstWithoutRequiredInvocations() {
        ExpectationError lastError = null;

        Story story = Story.create(new Scenario() {
            {
                expect(joe).ping();
                occurs(atLeast(3));
                expect(jack).ping();
                occurs(exactly(1));
            }
        });
        story.begin();
        joe.ping();
        joe.ping();
        try {
            jack.ping();
            fail("story continued with an unsatisfied atLeast clause");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }

        assertEndReportsError(story, lastError);
    }

    /**
     * Verifies that atLeast actually defines a lower bound to occurrences.
     */
    @Test
    public void testAtLeastFirstWithoutRequiredInvocationsMA() {
        ExpectationError lastError = null;

        Schemer.begin();
        Schemer.willInvoke(atLeast(3)).of(joe).ping();
        Schemer.willInvoke(1).of(jack).ping();
        joe.ping();
        joe.ping();
        try {
            jack.ping();
            fail("story continued with an unsatisfied atLeast clause");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);
    }

    /**
     * Verifies the behavior of an atLeast clause at the end of a scenario.
     */
    @Test
    public void testAtLeastLastWithRequiredInvocations() {
        Story story = Story.create(new Scenario() {
            {
                expect(joe).ping();
                occurs(exactly(1));
                expect(jack).ping();
                occurs(atLeast(2));
            }
        });
        story.begin();
        joe.ping();
        jack.ping();
        jack.ping();
        story.end();
    }

    /**
     * Verifies the behavior of an atLeast clause at the end of a scenario.
     */
    @Test
    public void testAtLeastLastWithRequiredInvocationsMA() {
        Schemer.begin();
        Schemer.willInvoke(1).of(joe).ping();
        Schemer.willInvoke(atLeast(2)).of(jack).ping();
        joe.ping();
        jack.ping();
        jack.ping();
        Schemer.end();
    }

    /**
     * Verifies that a story finishing with an atLeast clause can't end if this
     * clause is not satisfied.
     */
    @Test
    public void testAtLeastLastWithoutRequiredInvocations() {
        Story story = Story.create(new Scenario() {
            {
                expect(joe).ping();
                occurs(exactly(1));
                expect(jack).ping();
                occurs(atLeast(2));
            }
        });
        story.begin();
        joe.ping();
        jack.ping();
        try {
            story.end();
            fail("end the story with unverified atLeast clause");
        } catch (UnsatisfiedOccurrenceError e) {
        }
    }

    /**
     * Verifies that a story finishing with an atLeast clause can't end if this
     * clause is not satisfied.
     */
    @Test
    public void testAtLeastLastWithoutRequiredInvocationsMA() {
        Schemer.begin();
        Schemer.willInvoke(1).of(joe).ping();
        Schemer.willInvoke(atLeast(2)).of(jack).ping();
        joe.ping();
        jack.ping();
        try {
            Schemer.end();
            fail("end the story with unverified atLeast clause");
        } catch (UnsatisfiedOccurrenceError e) {
        }
    }

    /**
     * A basic test of atLeast.
     */
    @Test
    public void testAtLeastInterruptedByIllegalInvocation() {
        ExpectationError lastError = null;

        Story story = Story.create(new Scenario() {
            {
                expect(joe).ping();
                occurs(atLeast(3));
            }
        });
        story.begin();
        joe.ping();
        try {
            joe.ping(averell);
            fail("invalid call to a method");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);
    }

    /**
     * A basic test of atLeast.
     */
    @Test
    public void testAtLeastInterruptedByIllegalInvocationMA() {
        ExpectationError lastError = null;

        Schemer.begin();
        Schemer.willInvoke(atLeast(3)).of(joe).ping();
        joe.ping();
        try {
            joe.ping(averell);
            fail("invalid call to a method");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);
    }

    /**
     * Validation of the bound defined by an atMost clause.
     */
    @Test
    public void testAtMostWithProperInvocations() {
        Story story = Story.create(new Scenario() {
            {
                expect(joe).ping();
                occurs(atMost(3));
                expect(jack).ping();
                occurs(exactly(1));
            }
        });
        story.begin();
        joe.ping();
        joe.ping();
        joe.ping();
        jack.ping();
        story.end();
    }

    /**
     * Validation of the bound defined by an atMost clause.
     */
    @Test
    public void testAtMostWithProperInvocationsMA() {
        Schemer.begin();
        Schemer.willInvoke(atMost(3)).of(joe).ping();
        Schemer.willInvoke(1).of(jack).ping();
        joe.ping();
        joe.ping();
        joe.ping();
        jack.ping();
        Schemer.end();
    }

    /**
     * Verifies that an atMost clause does not stuck the story if no invocation
     * occurs.
     */
    @Test
    public void testAtMostWithNoInvocations() {
        Story story = Story.create(new Scenario() {
            {
                expect(joe).ping();
                occurs(atMost(3));
                expect(jack).ping();
                occurs(exactly(1));
            }
        });
        story.begin();
        jack.ping();
        story.end();
    }

    /**
     * Verifies that an atMost clause does not stuck the story if no invocation
     * occurs.
     */
    @Test
    public void testAtMostWithNoInvocationsMA() {
        Schemer.begin();
        Schemer.willInvoke(atMost(3)).of(joe).ping();
        Schemer.willInvoke(1).of(jack).ping();
        jack.ping();
        Schemer.end();
    }

    /**
     * Verifies that the atMost clause defines an upper bound that cannot be
     * exceeded.
     */
    @Test
    public void testAtMostTooManyInvocations() {
        ExpectationError lastError = null;

        Story story = Story.create(new Scenario() {
            {
                expect(joe).ping();
                occurs(atMost(3));
            }
        });
        story.begin();
        joe.ping();
        joe.ping();
        joe.ping();
        try {
            joe.ping();
            fail("invoked a method more times than allowed");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);
    }

    /**
     * Verifies that the atMost clause defines an upper bound that cannot be
     * exceeded.
     */
    @Test
    public void testAtMostTooManyInvocationsMA() {
        ExpectationError lastError = null;

        Schemer.begin();
        Schemer.willInvoke(atMost(3)).of(joe).ping();
        joe.ping();
        joe.ping();
        joe.ping();
        try {
            joe.ping();
            fail("invoked a method more times than allowed");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);
    }

    /**
     * Checks the validity of the lower bound of a between clause.
     */
    @Test
    public void testBetweenLowerBound() {
        Story story = Story.create(new Scenario() {
            {
                expect(joe).ping();
                occurs(between(2, 3));
                expect(jack).ping();
                occurs(exactly(1));
            }
        });
        story.begin();
        joe.ping();
        joe.ping();
        jack.ping();
        story.end();
    }

    /**
     * Checks the validity of the lower bound of a between clause.
     */
    @Test
    public void testBetweenLowerBoundMA() {
        Schemer.begin();
        Schemer.willInvoke(between(2, 3)).of(joe).ping();
        Schemer.willInvoke(1).of(jack).ping();
        joe.ping();
        joe.ping();
        jack.ping();
        Schemer.end();
    }

    /**
     * Checks the validity of the upper bound defined by a between clause.
     */
    @Test
    public void testBetweenUpperBound() {
        Story story = Story.create(new Scenario() {
            {
                expect(joe).ping();
                occurs(between(2, 3));
                expect(jack).ping();
                occurs(exactly(1));
            }
        });
        story.begin();
        joe.ping();
        joe.ping();
        joe.ping();
        jack.ping();
        story.end();
    }

    /**
     * Checks the validity of the upper bound defined by a between clause.
     */
    @Test
    public void testBetweenUpperBoundMA() {
        Schemer.begin();
        Schemer.willInvoke(between(2, 3)).of(joe).ping();
        Schemer.willInvoke(1).of(jack).ping();
        joe.ping();
        joe.ping();
        joe.ping();
        jack.ping();
        Schemer.end();
    }

    /**
     * Verifies that a between clause defines a lower bound below which we can't
     * go.
     */
    @Test
    public void testBetweenNotEnoughInvocations() {
        Story story = Story.create(new Scenario() {
            {
                expect(joe).ping();
                occurs(between(2, 3));
            }
        });
        story.begin();
        joe.ping();
        try {
            story.end();
            fail("ended the story with missing calls");
        } catch (UnsatisfiedOccurrenceError e) {
        }
    }

    /**
     * Verifies that a between clause defines a lower bound below which we can't
     * go.
     */
    @Test
    public void testBetweenNotEnoughInvocationsMA() {
        Schemer.begin();
        Schemer.willInvoke(between(2, 3)).of(joe).ping();
        joe.ping();
        try {
            Schemer.end();
            fail("ended the story with missing calls");
        } catch (UnsatisfiedOccurrenceError e) {
        }
    }

    /**
     * Verifies that a between clause defines an upper bound beyond which we
     * can't go.
     */
    @Test
    public void testBetweenTooManyInvocations() {
        ExpectationError lastError = null;

        Story story = Story.create(new Scenario() {
            {
                expect(joe).ping();
                occurs(between(2, 3));
            }
        });
        story.begin();
        joe.ping();
        joe.ping();
        joe.ping();
        try {
            joe.ping();
            fail("invoked a method more times than allowed");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);
    }

    /**
     * Verifies that a between clause defines an upper bound beyond which we
     * can't go.
     */
    @Test
    public void testBetweenTooManyInvocationsMA() {
        ExpectationError lastError = null;

        Schemer.begin();
        Schemer.willInvoke(between(2, 3)).of(joe).ping();
        joe.ping();
        joe.ping();
        joe.ping();
        try {
            joe.ping();
            fail("invoked a method more times than allowed");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);
    }
}
