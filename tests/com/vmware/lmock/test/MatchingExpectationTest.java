/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.test;

import com.vmware.lmock.exception.ExpectationError;
import static com.vmware.lmock.checker.Occurrences.atLeast;
import static com.vmware.lmock.checker.Occurrences.exactly;
import static com.vmware.lmock.test.Dalton.averell;
import static com.vmware.lmock.test.Dalton.jack;
import static com.vmware.lmock.test.Dalton.joe;
import static com.vmware.lmock.test.Dalton.william;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import org.junit.Test;

import com.vmware.lmock.exception.UnexpectedInvocationError;
import com.vmware.lmock.exception.UnsatisfiedOccurrenceError;
import com.vmware.lmock.impl.Scenario;
import com.vmware.lmock.impl.Story;
import com.vmware.lmock.masquerade.Schemer;
import static com.vmware.lmock.test.LMAsserts.*;

/**
 * Tests of stories using scenarios based on exact (matching) invocations.
 */
public class MatchingExpectationTest {
    /**
     * Verifies that there's no confusion between the prototypes of different
     * methods having the same name.
     */
    @Test
    public void testSeveralInvocationsWithDifferentMethodsWithSameNames() {
        ExpectationError lastError = null;

        // We will run different stories referencing the same scenario.
        Story story = Story.create(new Scenario() {
            {
                // Define a specific return value for each invocation, so that
                // we verify that we passed through the correct one.
                expect(joe).ping();
                occurs(exactly(1)).willReturn(0);
                expect(joe).ping(jack);
                occurs(exactly(1)).willReturn(1);
                expect(joe).ping(jack, "hello brother!");
                occurs(exactly(1)).willReturn(2);
                expect(joe).ping(jack, "hello", "brother!");
                occurs(exactly(1)).willReturn(3);
            }
        });

        story.begin();
        try {
            joe.ping(jack);
            fail("wrong invocation");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);

        story.begin();
        try {
            joe.ping(jack, "hello brother!");
            fail("wrong invocation");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);

        story.begin();
        try {
            joe.ping(jack, "hello", "brother!");
            fail("wrong invocation");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);

        story.begin();
        assertEquals(0, joe.ping());
        try {
            joe.ping();
            fail("wrong invocation");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);

        story.begin();
        assertEquals(0, joe.ping());
        try {
            joe.ping(jack, "hello brother!");
            fail("wrong invocation");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);

        story.begin();
        assertEquals(0, joe.ping());
        try {
            joe.ping(jack, "hello", "brother!");
            fail("wrong invocation");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);

        story.begin();
        assertEquals(0, joe.ping());
        assertEquals(1, joe.ping(jack));
        try {
            joe.ping();
            fail("wrong invocation");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);

        story.begin();
        assertEquals(0, joe.ping());
        assertEquals(1, joe.ping(jack));
        try {
            joe.ping(jack, "hello", "brother!");
            fail("wrong invocation");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);

        story.begin();
        assertEquals(0, joe.ping());
        assertEquals(1, joe.ping(jack));
        assertEquals(2, joe.ping(jack, "hello brother!"));
        try {
            joe.ping();
            fail("wrong invocation");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);

        story.begin();
        assertEquals(0, joe.ping());
        assertEquals(1, joe.ping(jack));
        assertEquals(2, joe.ping(jack, "hello brother!"));
        try {
            joe.ping(jack);
            fail("wrong invocation");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);

        story.begin();
        assertEquals(0, joe.ping());
        assertEquals(1, joe.ping(jack));
        assertEquals(2, joe.ping(jack, "hello brother!"));
        try {
            joe.ping(jack, "hello brother!");
            fail("wrong invocation");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);
    }

    /**
     * Verifies that there's no confusion between the prototypes of different
     * methods having the same name.
     */
    @Test
    public void testSeveralInvocationsWithDifferentMethodsWithSameNamesMA() {
        ExpectationError lastError = null;

        // We will run different stories referencing the same scenario.
        Scenario scenario = new Scenario() {
            {
                // Define a specific return value for each invocation, so that
                // we verify that we passed through the correct one.
                expect(joe).ping();
                occurs(exactly(1)).willReturn(0);
                expect(joe).ping(jack);
                occurs(exactly(1)).willReturn(1);
                expect(joe).ping(jack, "hello brother!");
                occurs(exactly(1)).willReturn(2);
                expect(joe).ping(jack, "hello", "brother!");
                occurs(exactly(1)).willReturn(3);
            }
        };

        Schemer.begin();
        Schemer.append(scenario);
        try {
            joe.ping(jack);
            fail("wrong invocation");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);

        Schemer.begin();
        Schemer.append(scenario);
        try {
            joe.ping(jack, "hello brother!");
            fail("wrong invocation");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);

        Schemer.begin();
        Schemer.append(scenario);
        try {
            joe.ping(jack, "hello", "brother!");
            fail("wrong invocation");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);

        Schemer.begin();
        Schemer.append(scenario);
        assertEquals(0, joe.ping());
        try {
            joe.ping();
            fail("wrong invocation");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);

        Schemer.begin();
        Schemer.append(scenario);
        assertEquals(0, joe.ping());
        try {
            joe.ping(jack, "hello brother!");
            fail("wrong invocation");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);

        Schemer.begin();
        Schemer.append(scenario);
        assertEquals(0, joe.ping());
        try {
            joe.ping(jack, "hello", "brother!");
            fail("wrong invocation");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);

        Schemer.begin();
        Schemer.append(scenario);
        assertEquals(0, joe.ping());
        assertEquals(1, joe.ping(jack));
        try {
            joe.ping();
            fail("wrong invocation");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);

        Schemer.begin();
        Schemer.append(scenario);
        assertEquals(0, joe.ping());
        assertEquals(1, joe.ping(jack));
        try {
            joe.ping(jack, "hello", "brother!");
            fail("wrong invocation");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);

        Schemer.begin();
        Schemer.append(scenario);
        assertEquals(0, joe.ping());
        assertEquals(1, joe.ping(jack));
        assertEquals(2, joe.ping(jack, "hello brother!"));
        try {
            joe.ping();
            fail("wrong invocation");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);

        Schemer.begin();
        Schemer.append(scenario);
        assertEquals(0, joe.ping());
        assertEquals(1, joe.ping(jack));
        assertEquals(2, joe.ping(jack, "hello brother!"));
        try {
            joe.ping(jack);
            fail("wrong invocation");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);

        Schemer.begin();
        Schemer.append(scenario);
        assertEquals(0, joe.ping());
        assertEquals(1, joe.ping(jack));
        assertEquals(2, joe.ping(jack, "hello brother!"));
        try {
            joe.ping(jack, "hello brother!");
            fail("wrong invocation");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);
    }

    /**
     * Validates expectations with primitive arguments.
     */
    @Test
    public void testArgsWithPrimitiveTypes() {
        Story story = Story.create(new Scenario() {
            {
                expect(joe).setBoolean(true);
                occurs(1);
                expect(joe).setChar('Z');
                occurs(1);
                expect(joe).setByte((byte) 8);
                occurs(1);
                expect(joe).setShort((short) 123);
                occurs(1);
                expect(joe).setInt(0xdead);
                occurs(1);
                expect(joe).setLong(0xbeefL);
                occurs(1);
                expect(joe).setFloat((float) 1337.165);
                occurs(1);
                expect(joe).setDouble(1337.164);
                occurs(1);
            }
        });
        story.begin();
        joe.setBoolean(true);
        joe.setChar('Z');
        joe.setByte((byte) 8);
        joe.setShort((short) 123);
        joe.setInt(0xdead);
        joe.setLong(0xbeefL);
        joe.setFloat((float) 1337.165);
        joe.setDouble(1337.164);
        story.end();
    }

    /**
     * Validates expectations with primitive arguments.
     */
    @Test
    public void testArgsWithPrimitiveTypesMA() {
        Schemer.begin();
        Schemer.willInvoke(1).of(joe).setBoolean(true);
        joe.setBoolean(true);
        Schemer.willInvoke(1).of(joe).setChar('Z');
        joe.setChar('Z');
        Schemer.willInvoke(1).of(joe).setByte((byte) 8);
        joe.setByte((byte) 8);
        Schemer.willInvoke(1).of(joe).setShort((short) 123);
        joe.setShort((short) 123);
        Schemer.willInvoke(1).of(joe).setInt(0xdead);
        joe.setInt(0xdead);
        Schemer.willInvoke(1).of(joe).setLong(0xbeefL);
        joe.setLong(0xbeefL);
        Schemer.willInvoke(1).of(joe).setFloat((float) 1337.165);
        joe.setFloat((float) 1337.165);
        Schemer.willInvoke(1).of(joe).setDouble(1337.164);
        joe.setDouble(1337.164);
        Schemer.end();
    }

    /**
     * Validates an expectation with a null argument.
     */
    @Test
    public void testArgsWithNullArgument() {
        ExpectationError lastError = null;

        Story story = Story.create(new Scenario() {
            {
                expect(joe).ping(null);
                expect().occurs(exactly(2)).willReturn(1);
            }
        });

        story.begin();
        assertEquals(1, joe.ping(null));
        try {
            joe.ping(jack);
            fail("expected null, invoked with non-null");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);
    }

    /**
     * Validates an expectation with a null argument.
     */
    @Test
    public void testArgsWithNullArgumentMA() {
        ExpectationError lastError = null;

        Schemer.begin();
        Schemer.willInvoke(2).willReturn(1).when(joe).ping(null);

        assertEquals(1, joe.ping(null));
        try {
            joe.ping(jack);
            fail("expected null, invoked with non-null");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);
    }

    /**
     * Validates an expectation with a non-null argument.
     */
    @Test
    public void testArgsWithNonNullArgument() {
        ExpectationError lastError = null;

        Story story = Story.create(new Scenario() {
            {
                expect(joe).ping(jack);
                expect().occurs(exactly(2)).willReturn(1);
            }
        });

        story.begin();
        assertEquals(1, joe.ping(jack));
        try {
            joe.ping(null);
            fail("expected non-null, invoked with null");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);
    }

    /**
     * Validates an expectation with a non-null argument.
     */
    @Test
    public void testArgsWithNonNullArgumentMA() {
        ExpectationError lastError = null;

        Schemer.begin();
        Schemer.willInvoke(2).willReturn(1).when(joe).ping(jack);
        assertEquals(1, joe.ping(jack));
        try {
            joe.ping(null);
            fail("expected non-null, invoked with null");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);
    }

    /**
     * Validates an expectation with a simple object.
     *
     * <p>
     * The comparison should use the <code>equals</code> method.
     * </p>
     */
    @Test
    public void testArgsWithObject() {
        ExpectationError lastError = null;

        Story story = Story.create(new Scenario() {
            {
                expect(joe).setObject("hurray!");
                expect().occurs(exactly(2));
            }
        });

        story.begin();
        joe.setObject("hurray!");
        try {
            joe.setObject("kurrai!");
            fail("called a method with an invalid argument");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);
    }

    /**
     * Validates an expectation with a simple object.
     *
     * <p>
     * The comparison should use the <code>equals</code> method.
     * </p>
     */
    @Test
    public void testArgsWithObjectMA() {
        ExpectationError lastError = null;

        Schemer.begin();
        Schemer.willInvoke(2).of(joe).setObject("hurray!");
        joe.setObject("hurray!");
        try {
            joe.setObject("kurrai!");
            fail("called a method with an invalid argument");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);
    }

    /**
     * Validates an expectation with a mock as argument.
     */
    @Test
    public void testArgsWithMock() {
        ExpectationError lastError = null;

        Story story = Story.create(new Scenario() {
            {
                expect(joe).ping(jack);
                expect().occurs(exactly(2)).willReturn(1);
            }
        });

        story.begin();
        assertEquals(1, joe.ping(jack));
        try {
            joe.ping(william);
            fail("called a method with an invalid argument");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);
    }

    /**
     * Validates an expectation with a mock as argument.
     */
    @Test
    public void testArgsWithMockMA() {
        ExpectationError lastError = null;

        Schemer.begin();
        Schemer.willInvoke(2).willReturn(1).when(joe).ping(jack);
        assertEquals(1, joe.ping(jack));
        try {
            joe.ping(william);
            fail("called a method with an invalid argument");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);
    }

    /**
     * Verifies that expectation with multiple parameters are correctly checked.
     */
    @Test
    public void testArgsWithMultipleParameters() {
        ExpectationError lastError = null;

        Story story = Story.create(new Scenario() {
            {
                expect(joe).ping(averell, "c'mon!");
                expect().occurs(exactly(2)).willReturn(154);
            }
        });

        story.begin();
        assertEquals(154, joe.ping(averell, "c'mon!"));
        try {
            joe.ping(averell, "c''mon!");
            fail("called a method with an invalid argument");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);
    }

    /**
     * Verifies that expectation with multiple parameters are correctly checked.
     */
    @Test
    public void testArgsWithMultipleParametersMA() {
        ExpectationError lastError = null;

        Schemer.begin();
        Schemer.willInvoke(2).willReturn(154).when(joe).ping(averell, "c'mon!");
        assertEquals(154, joe.ping(averell, "c'mon!"));
        try {
            joe.ping(averell, "c''mon!");
            fail("called a method with an invalid argument");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);
    }

    /**
     * Verifies that a valid invocation implying an array is correctly handled.
     *
     * <p>
     * The checker should validate the whole contents of the array.
     * </p>
     */
    @Test
    public void testArgsWithAnArray() {
        Story story = Story.create(new Scenario() {
            {
                expect(joe).fillPocketWithAPackOf(
                  new Object[]{null, 1, jack, "boo!"});
                expect().occurs(exactly(1));
            }
        });

        story.begin();
        joe.fillPocketWithAPackOf(new Object[]{null, 1, jack, "boo!"});
        story.end();
    }

    /**
     * Verifies that a valid invocation implying an array is correctly handled.
     *
     * <p>
     * The checker should validate the whole contents of the array.
     * </p>
     */
    @Test
    public void testArgsWithAnArrayMA() {
        Schemer.begin();
        Schemer.willInvoke(1).of(joe).fillPocketWithAPackOf(new Object[]{null, 1,
              jack, "boo!"});
        joe.fillPocketWithAPackOf(new Object[]{null, 1, jack, "boo!"});
        Schemer.end();
    }

    /**
     * Verifies that the length of arrays is tested when having such
     * expectations.
     */
    @Test
    public void testArgsWithArrayInvokedWithAShorterArray() {
        ExpectationError lastError = null;

        Story story = Story.create(new Scenario() {
            {
                expect(joe).fillPocketWithAPackOf(
                  new Object[]{null, 1, jack, "boo!"});
                expect().occurs(exactly(1));
            }
        });

        story.begin();
        try {
            joe.fillPocketWithAPackOf(new Object[]{null, 1, jack});
            fail("invoked a method with a different array");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);
    }

    /**
     * Verifies that the length of arrays is tested when having such
     * expectations.
     */
    @Test
    public void testArgsWithArrayInvokedWithAShorterArrayMA() {
        ExpectationError lastError = null;

        Schemer.begin();
        Schemer.willInvoke(1).of(joe).fillPocketWithAPackOf(new Object[]{null, 1,
              jack, "boo!"});
        try {
            joe.fillPocketWithAPackOf(new Object[]{null, 1, jack});
            fail("invoked a method with a different array");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);
    }

    /**
     * Verifies that the length of arrays is tested when having such
     * expectations.
     */
    @Test
    public void testArgsWithArrayInvokedWithALongerArray() {
        ExpectationError lastError = null;

        Story story = Story.create(new Scenario() {
            {
                expect(joe).fillPocketWithAPackOf(
                  new Object[]{null, 1, jack, "boo!"});
                expect().occurs(exactly(1));
            }
        });

        story.begin();
        try {
            joe.fillPocketWithAPackOf(new Object[]{null, 1, jack, "boo!",
                  "yop!"});
            fail("invoked a method with a different array");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);
    }

    /**
     * Verifies that the length of arrays is tested when having such
     * expectations.
     */
    @Test
    public void testArgsWithArrayInvokedWithALongerArrayMA() {
        ExpectationError lastError = null;

        Schemer.begin();
        Schemer.willInvoke(1).of(joe).fillPocketWithAPackOf(new Object[]{null, 1,
              jack, "boo!"});
        try {
            joe.fillPocketWithAPackOf(new Object[]{null, 1, jack, "boo!",
                  "yop!"});
            fail("invoked a method with a different array");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);
    }

    /**
     * Verifies that array contents are actually checked during invocations.
     */
    @Test
    public void testArgsWithArrayInvokedWithADifferentArray() {
        ExpectationError lastError = null;

        Story story = Story.create(new Scenario() {
            {
                expect(joe).fillPocketWithAPackOf(
                  new Object[]{null, 1, jack, "boo!"});
                expect().occurs(exactly(1));
            }
        });

        story.begin();
        try {
            joe.fillPocketWithAPackOf(new Object[]{null, 1, william, "boo!"});
            fail("invoked a method with a different array");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);
    }

    /**
     * Verifies that array contents are actually checked during invocations.
     */
    @Test
    public void testArgsWithArrayInvokedWithADifferentArrayMA() {
        ExpectationError lastError = null;

        Schemer.begin();
        Schemer.willInvoke(1).of(joe).fillPocketWithAPackOf(new Object[]{null, 1,
              jack, "boo!"});
        try {
            joe.fillPocketWithAPackOf(new Object[]{null, 1, william, "boo!"});
            fail("invoked a method with a different array");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);
    }

    /**
     * Validates the case of null arrays in expectations.
     */
    @Test
    public void testArgsWithNullArrayExpected() {
        ExpectationError lastError = null;

        Story story = Story.create(new Scenario() {
            {
                expect(joe).fillPocketWithAPackOf(null);
                expect().occurs(exactly(2));
            }
        });

        story.begin();
        joe.fillPocketWithAPackOf(null);
        try {
            joe.fillPocketWithAPackOf(new Object[]{"gold", "silver"});
            fail("invoked a method with an unexpected non-null array");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);
    }

    /**
     * Validates the case of null arrays in expectations.
     */
    @Test
    public void testArgsWithNullArrayExpectedMA() {
        ExpectationError lastError = null;

        Schemer.begin();
        Schemer.willInvoke(2).of(joe).fillPocketWithAPackOf(null);
        joe.fillPocketWithAPackOf(null);
        try {
            joe.fillPocketWithAPackOf(new Object[]{"gold", "silver"});
            fail("invoked a method with an unexpected non-null array");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);
    }

    /**
     * Validates the case of null arrays in invocations.
     */
    @Test
    public void testArgsWithNullArrayInvoked() {
        ExpectationError lastError = null;

        Story story = Story.create(new Scenario() {
            {
                expect(joe).fillPocketWithAPackOf(new Object[]{1, 2, 3});
                expect().occurs(exactly(1));
            }
        });

        story.begin();
        try {
            joe.fillPocketWithAPackOf(null);
            fail("invoked a method with an unexpected null array");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);
    }

    /**
     * Validates the case of null arrays in invocations.
     */
    @Test
    public void testArgsWithNullArrayInvokedMA() {
        ExpectationError lastError = null;

        Schemer.begin();
        Schemer.willInvoke(1).of(joe).fillPocket(new Object[]{1, 2, 3});
        try {
            joe.fillPocketWithAPackOf(null);
            fail("invoked a method with an unexpected null array");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);
    }

    /**
     * Verifies that there's no corner cases with null arrays.
     */
    @Test
    public void testArgsWithANullArray() {
        Story story = Story.create(new Scenario() {
            {
                expect(joe).fillPocketWithAPackOf(null);
                expect().occurs(exactly(1));
            }
        });

        story.begin();
        joe.fillPocketWithAPackOf(null);
        story.end();
    }

    /**
     * Verifies that there's no corner cases with null arrays.
     */
    @Test
    public void testArgsWithANullArrayMA() {
        Schemer.begin();
        Schemer.willInvoke(1).of(joe).fillPocketWithAPackOf(null);
        joe.fillPocketWithAPackOf(null);
        Schemer.end();
    }

    /**
     * Plays with array to verify that the invocation checker actually verifies
     * the data each time.
     */
    @Test
    public void testExpectationsWithAnArray() {
        final Object[] stuff = {"nothing", "nada", "rien"};
        Story story = Story.create(new Scenario() {
            {
                expect(joe).fillPocketWithAPackOf(stuff);
            }
        });
        story.begin();
        // Before checking, change the stuff contents. Since we basically
        // reference the object table, this modification should not impact
        // the expectation (in other words, we basically reference the array
        // object, not its contents).
        stuff[0] = "things";
        stuff[1] = "cosas";
        stuff[2] = "choses";
        joe.fillPocketWithAPackOf(stuff);
        story.end();
    }

    /**
     * Plays with array to verify that the invocation checker actually verifies
     * the data each time.
     */
    @Test
    public void testExpectationsWithAnArrayMA() {
        final Object[] stuff = {"nothing", "nada", "rien"};
        Schemer.begin();
        Schemer.willInvoke(1).of(joe).fillPocketWithAPackOf(stuff);
        // Before checking, change the stuff contents. Since we basically
        // reference the object table, this modification should not impact
        // the expectation (in other words, we basically reference the array
        // object, not its contents).
        stuff[0] = "things";
        stuff[1] = "cosas";
        stuff[2] = "choses";
        joe.fillPocketWithAPackOf(stuff);
        Schemer.end();
    }

    /**
     * Validates a story with varargs.
     */
    @Test
    public void testArgsWithVarargs() {
        Story story = Story.create(new Scenario() {
            {
                expect(joe).fillPocket("gold", "silver", 66.77, null, jack);
                expect().occurs(exactly(1));
            }
        });

        story.begin();
        joe.fillPocket("gold", "silver", 66.77, null, jack);
        story.end();
    }

    /**
     * Validates a story with varargs.
     */
    @Test
    public void testArgsWithVarargsMA() {
        Schemer.begin();
        Schemer.willInvoke(1).of(joe).fillPocket("gold", "silver", 66.77, null,
          jack);
        joe.fillPocket("gold", "silver", 66.77, null, jack);
        Schemer.end();
    }

    /**
     * Verifies that an expectation with an empty varargs list is correctly
     * processed.
     */
    @Test
    public void testArgsWithEmptyVarargs() {
        ExpectationError lastError = null;

        Story story = Story.create(new Scenario() {
            {
                expect(joe).fillPocket();
                expect().occurs(exactly(2));
            }
        });

        story.begin();
        joe.fillPocket();
        try {
            joe.fillPocket(154);
            fail("invoked a method with the wrong arguments");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);
    }

    /**
     * Verifies that an expectation with an empty varargs list is correctly
     * processed.
     */
    @Test
    public void testArgsWithEmptyVarargsMA() {
        ExpectationError lastError = null;

        Schemer.begin();
        Schemer.willInvoke(2).of(joe).fillPocket();
        joe.fillPocket();
        try {
            joe.fillPocket(154);
            fail("invoked a method with the wrong arguments");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);
    }

    /**
     * Verifies that an expectation with varargs is not satisfied when the
     * invocation has no arguments.
     */
    @Test
    public void testArgsWithVarargsAndEmptyInvocation() {
        ExpectationError lastError = null;

        Story story = Story.create(new Scenario() {
            {
                expect(joe).fillPocket("dutchy", "skippy");
                expect().occurs(exactly(1));
            }
        });

        story.begin();
        try {
            joe.fillPocket();
            fail("invoked a method with the wrong arguments");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);
    }

    /**
     * Verifies that an expectation with varargs is not satisfied when the
     * invocation has no arguments.
     */
    @Test
    public void testArgsWithVarargsAndEmptyInvocationMA() {
        ExpectationError lastError = null;

        Schemer.begin();
        Schemer.willInvoke(1).of(joe).fillPocket("dutchy", "skippy");
        try {
            joe.fillPocket();
            fail("invoked a method with the wrong arguments");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);
    }

    /**
     * Validates an invalid invocation with varargs: not enough arguments in the
     * invocation.
     */
    @Test
    public void testArgsWithVarargsInvokedWithLessArguments() {
        ExpectationError lastError = null;

        Story story = Story.create(new Scenario() {
            {
                expect(joe).fillPocket("some", "more");
                expect().occurs(exactly(1));
            }
        });

        story.begin();
        try {
            joe.fillPocket("some");
            fail("invoked a method with the wrong arguments");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);
    }

    /**
     * Validates an invalid invocation with varargs: not enough arguments in the
     * invocation.
     */
    @Test
    public void testArgsWithVarargsInvokedWithLessArgumentsMA() {
        ExpectationError lastError = null;

        Schemer.begin();
        Schemer.willInvoke(1).of(joe).fillPocket("some", "more");
        try {
            joe.fillPocket("some");
            fail("invoked a method with the wrong arguments");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);
    }

    /**
     * Validates an invalid invocation with varargs: too many arguments in the
     * invocation.
     */
    @Test
    public void testArgsWithVarargsInvokedWithMoreArguments() {
        ExpectationError lastError = null;

        Story story = Story.create(new Scenario() {
            {
                expect(joe).fillPocket("some", "more");
                expect().occurs(exactly(1));
            }
        });

        story.begin();
        try {
            joe.fillPocket("some", "more", "and few others");
            fail("invoked a method with the wrong arguments");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);
    }

    /**
     * Validates an invalid invocation with varargs: too many arguments in the
     * invocation.
     */
    @Test
    public void testArgsWithVarargsInvokedWithMoreArgumentsMA() {
        ExpectationError lastError = null;

        Schemer.begin();
        Schemer.willInvoke(1).of(joe).fillPocket("some", "more");
        try {
            joe.fillPocket("some", "more", "and few others");
            fail("invoked a method with the wrong arguments");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);
    }

    /**
     * Verifies that wrong invocations implying varargs are correctly detected.
     *
     * <p>
     * We take the opportunity to check that an array within varargs is actually
     * checked.
     * </p>
     */
    @Test
    public void testArgsWithVarargsInvokedWithWrongArguments() {
        ExpectationError lastError = null;

        Story story = Story.create(new Scenario() {
            {
                expect(joe).fillPocket("a bit of",
                  new Integer[]{1, 2, 3, 4}, jack);
            }
        });

        story.begin();
        try {
            joe.fillPocket("a bit of", new Integer[]{1, 2, 3, 5}, jack);
            fail("invoked a method with the wrong arguments");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);
    }

    /**
     * Verifies that wrong invocations implying varargs are correctly detected.
     *
     * <p>
     * We take the opportunity to check that an array within varargs is actually
     * checked.
     * </p>
     */
    @Test
    public void testArgsWithVarargsInvokedWithWrongArgumentsMA() {
        ExpectationError lastError = null;

        Schemer.begin();
        Schemer.willInvoke(1).of(joe).fillPocket("a bit of", new Integer[]{1, 2,
              3, 4}, jack);
        try {
            joe.fillPocket("a bit of", new Integer[]{1, 2, 3, 5}, jack);
            fail("invoked a method with the wrong arguments");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);
    }

    /**
     * Checks an invocation with a set of matching null objects.
     *
     * <p>
     * Also play a little bit with the casting of null objects to verify that we
     * tolerate that.
     * </p>
     */
    @Test
    public void testArgsWithVarargsWithMatchingNullObjects() {
        Story story = Story.create(new Scenario() {
            {
                expect(joe).fillPocket(null, null, (String) null,
                  new Object[]{null, null});
            }
        });

        story.begin();
        joe.fillPocket((String) null, null, null, new Object[]{null, null});
        story.end();
    }

    /**
     * Checks an invocation with a set of matching null objects.
     *
     * <p>
     * Also play a little bit with the casting of null objects to verify that we
     * tolerate that.
     * </p>
     */
    @Test
    public void testArgsWithVarargsWithMatchingNullObjectsMA() {
        Schemer.begin();
        Schemer.willInvoke(1).of(joe).fillPocket(null, null, (String) null,
          new Object[]{null, null});
        joe.fillPocket((String) null, null, null, new Object[]{null, null});
        Schemer.end();
    }

    /**
     * Combines a returned array of object with a method with varargs.
     */
    @Test
    public void testExpectationsWithReturnedListOfObjects() {
        Story story = Story.create(new Scenario() {
            {
                Object listOfStuff = new Object[]{null,
                    new String[]{"hello", "world"}, 1, "again"};
                expect(joe).emptyPocket();
                expect().willReturn(listOfStuff).occurs(atLeast(1));
                // Express the contents explicitly, so that there is no
                // reference at all to this list of stuff...
                expect(jack).fillPocket(null,
                  new String[]{"hello", "world"}, 1, "again");
                expect().occurs(exactly(1));
            }
        });

        story.begin();
        assertNotNull(joe.emptyPocket());
        assertEquals(4, joe.emptyPocket().length);
        jack.fillPocket(joe.emptyPocket());
        story.end();
    }

    /**
     * Combines a returned array of object with a method with varargs.
     */
    @Test
    public void testExpectationsWithReturnedListOfObjectsMA() {
        Object listOfStuff = new Object[]{null,
            new String[]{"hello", "world"}, 1, "again"};
        Schemer.begin();
        Schemer.willInvoke(atLeast(1)).willReturn(listOfStuff).when(joe).
          emptyPocket();
        assertNotNull(joe.emptyPocket());
        assertEquals(4, joe.emptyPocket().length);

        // Express the contents explicitly, so that there is no
        // reference at all to this list of stuff...
        Schemer.willInvoke(1).of(jack).fillPocket(null, new String[]{"hello",
              "world"}, 1, "again");
        jack.fillPocket(joe.emptyPocket());
        Schemer.end();
    }

    /**
     * Combines a returned array of object with a method with varargs.
     */
    @Test
    public void testExpectationsWithUnmatchingReturnedListOfObjects() {
        ExpectationError lastError = null;

        Story story = Story.create(new Scenario() {
            {
                Object listOfStuff = new Object[]{null, Integer.valueOf(4),
                    1, null, "again"};

                expect(joe).emptyPocket();
                expect().willReturn(listOfStuff).occurs(atLeast(1));

                expect(jack).fillPocket(null,
                  new String[]{"hello", "world"}, 1, null, "again");
                expect().occurs(exactly(1));
            }
        });

        story.begin();
        assertNotNull(joe.emptyPocket());
        assertEquals(5, joe.emptyPocket().length);
        try {
            jack.fillPocket(joe.emptyPocket());
            fail("invoked a method with the wrong arguments");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);
    }

    /**
     * Combines a returned array of object with a method with varargs.
     */
    @Test
    public void testExpectationsWithUnmatchingReturnedListOfObjectsMA() {
        ExpectationError lastError = null;

        Schemer.begin();
        Object listOfStuff = new Object[]{null, Integer.valueOf(4), 1, null,
            "again"};

        Schemer.willInvoke(atLeast(1)).willReturn(listOfStuff).when(joe).
          emptyPocket();
        assertNotNull(joe.emptyPocket());
        assertEquals(5, joe.emptyPocket().length);

        Schemer.willInvoke(1).of(jack).fillPocket(null, new String[]{"hello",
              "world"}, 1, null,
          "again");
        try {
            jack.fillPocket(joe.emptyPocket());
            fail("invoked a method with the wrong arguments");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);
    }

    /**
     * Verifies that there is no confusion between proxies and mocks when
     * testing arguments.
     */
    @Test
    public void testExpectationsWithProxy() {
        final Object nasty = Proxy.newProxyInstance(this.getClass().
          getClassLoader(), new Class[]{List.class},
          new InvocationHandler() {
              @Override
              public Object invoke(Object arg0, Method arg1, Object[] arg2)
                throws Throwable {
                  // The test will imply invocations to equals.
                  if (arg1.getName().equals("equals")) {
                      return true;
                  } else if (arg1.getName().equals("toString")) {
                      return "proxy";
                  } else {
                      fail("don't know how I arrived there, but sure I am - method="
                        + arg1);
                      return null;
                  }
              }
          });
        assertNotNull(nasty);

        Story story = Story.create(new Scenario() {
            {
                expect(jack).emptyPocket();
                willReturn(new Object[]{nasty}).occurs(exactly(1));
                expect(joe).fillPocket(nasty);
                occurs(exactly(1));
            }
        });

        story.begin();
        joe.fillPocket(jack.emptyPocket()[0]);
        story.end();
    }

    /**
     * Verifies that there is no confusion between proxies and mocks when
     * testing arguments.
     */
    @Test
    public void testExpectationsWithProxyMA() {
        final Object nasty = Proxy.newProxyInstance(this.getClass().
          getClassLoader(), new Class[]{List.class},
          new InvocationHandler() {
              @Override
              public Object invoke(Object arg0, Method arg1, Object[] arg2)
                throws Throwable {
                  // The test will imply invocations to equals.
                  if (arg1.getName().equals("equals")) {
                      return true;
                  } else if (arg1.getName().equals("toString")) {
                      return "proxy";
                  } else {
                      fail("don't know how I arrived there, but sure I am - method="
                        + arg1);
                      return null;
                  }
              }
          });
        assertNotNull(nasty);

        Schemer.begin();
        Schemer.willInvoke(1).willReturn(new Object[]{nasty}).when(jack).
          emptyPocket();
        Schemer.willInvoke(1).of(joe).fillPocket(nasty);
        joe.fillPocket(jack.emptyPocket()[0]);
        Schemer.end();
    }
}
