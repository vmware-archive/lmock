/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.test;

import static com.vmware.lmock.test.Dalton.jack;
import static com.vmware.lmock.test.Dalton.joe;
import static com.vmware.lmock.test.Dalton.william;
import static com.vmware.lmock.test.LMAsserts.*;

import org.junit.Test;
import static org.junit.Assert.*;

import com.vmware.lmock.checker.Occurrences;
import com.vmware.lmock.exception.ExpectationError;
import com.vmware.lmock.exception.UnexpectedInvocationError;
import com.vmware.lmock.exception.UnsatisfiedOccurrenceError;
import com.vmware.lmock.impl.Scenario;
import com.vmware.lmock.impl.Story;
import com.vmware.lmock.masquerade.Schemer;

/**
 * Tests of stories using scenarios based on expectations with 'with', 'anyOf'
 * and 'aNonNullOf' clauses.
 */
public class AnyClauseTest {
    /**
     * Validates expectations with anyOf clause using primitive arguments.
     */
    @Test
    public void testAnyOfWithPrimitiveTypes() {
        Story story = Story.create(new Scenario() {
            {
                expect(joe).setBoolean(anyOf(boolean.class));
                expect(joe).setBoolean_(anyOf(boolean.class));

                expect(joe).setChar(anyOf(char.class));
                expect(joe).setChar_(anyOf(char.class));

                expect(joe).setByte(anyOf(byte.class));
                expect(joe).setByte_(anyOf(byte.class));

                expect(joe).setShort(anyOf(short.class));
                expect(joe).setShort_(anyOf(short.class));

                expect(joe).setInt(anyOf(int.class));
                expect(joe).setInt_(anyOf(int.class));

                expect(joe).setLong(anyOf(long.class));
                expect(joe).setLong_(anyOf(long.class));

                expect(joe).setFloat(anyOf(float.class));
                expect(joe).setFloat_(anyOf(float.class));

                expect(joe).setDouble(anyOf(double.class));
                expect(joe).setDouble_(anyOf(double.class));
            }
        });
        story.begin();
        joe.setBoolean(true);
        joe.setBoolean(false);
        joe.setBoolean_(true);
        joe.setBoolean_(false);
        joe.setBoolean_(null);

        joe.setChar('@');
        joe.setChar_('_');
        joe.setChar_(null);

        joe.setByte((byte) 11);
        joe.setByte_((byte) 49);
        joe.setByte_(null);

        joe.setShort((short) 124);
        joe.setShort_((short) 233);
        joe.setShort_(null);

        joe.setInt(6553);
        joe.setInt_(1234);
        joe.setInt_(null);

        joe.setLong(999L);
        joe.setLong_(-167L);
        joe.setLong_(null);

        joe.setFloat((float) 3.141592);
        joe.setFloat_((float) 154.155);
        joe.setFloat_(null);
        story.end();
    }

    /**
     * Validates expectations with anyOf clause using primitive arguments.
     */
    @Test
    public void testAnyOfWithPrimitiveTypesMA() {
        Schemer.begin();
        Schemer.willInvoke(1).of(joe).setBoolean(true);
        Schemer.willInvoke(1).of(joe).setBoolean(false);
        Schemer.willInvoke(1).of(joe).setBoolean_(true);
        Schemer.willInvoke(1).of(joe).setBoolean_(false);
        Schemer.willInvoke(1).of(joe).setBoolean_(null);
        joe.setBoolean(true);
        joe.setBoolean(false);
        joe.setBoolean_(true);
        joe.setBoolean_(false);
        joe.setBoolean_(null);

        Schemer.willInvoke(1).of(joe).setChar('@');
        Schemer.willInvoke(1).of(joe).setChar_('_');
        Schemer.willInvoke(1).of(joe).setChar_(null);
        joe.setChar('@');
        joe.setChar_('_');
        joe.setChar_(null);

        Schemer.willInvoke(1).of(joe).setByte((byte) 11);
        Schemer.willInvoke(1).of(joe).setByte_((byte) 49);
        Schemer.willInvoke(1).of(joe).setByte_(null);
        joe.setByte((byte) 11);
        joe.setByte_((byte) 49);
        joe.setByte_(null);

        Schemer.willInvoke(1).of(joe).setShort((short) 124);
        Schemer.willInvoke(1).of(joe).setShort_((short) 233);
        Schemer.willInvoke(1).of(joe).setShort_(null);
        joe.setShort((short) 124);
        joe.setShort_((short) 233);
        joe.setShort_(null);

        Schemer.willInvoke(1).of(joe).setInt(6553);
        Schemer.willInvoke(1).of(joe).setInt_(1234);
        Schemer.willInvoke(1).of(joe).setInt_(null);
        joe.setInt(6553);
        joe.setInt_(1234);
        joe.setInt_(null);

        Schemer.willInvoke(1).of(joe).setLong(999L);
        Schemer.willInvoke(1).of(joe).setLong_(-167L);
        Schemer.willInvoke(1).of(joe).setLong_(null);
        joe.setLong(999L);
        joe.setLong_(-167L);
        joe.setLong_(null);

        Schemer.willInvoke(1).of(joe).setFloat((float) 3.141592);
        Schemer.willInvoke(1).of(joe).setFloat_((float) 154.155);
        Schemer.willInvoke(1).of(joe).setFloat_(null);
        joe.setFloat((float) 3.141592);
        joe.setFloat_((float) 154.155);
        joe.setFloat_(null);
        Schemer.end();
    }

    /**
     * Validates expectations with aNonNullOf clause using primitive arguments.
     */
    @Test
    public void testANonNullOfWithPrimitiveTypes() {
        Story story = Story.create(new Scenario() {
            {
                expect(joe).setBoolean(aNonNullOf(boolean.class));
                expect(joe).setBoolean_(aNonNullOf(boolean.class));

                expect(joe).setChar(aNonNullOf(char.class));
                expect(joe).setChar_(aNonNullOf(char.class));

                expect(joe).setByte(aNonNullOf(byte.class));
                expect(joe).setByte_(aNonNullOf(byte.class));

                expect(joe).setShort(aNonNullOf(short.class));
                expect(joe).setShort_(aNonNullOf(short.class));

                expect(joe).setInt(aNonNullOf(int.class));
                expect(joe).setInt_(aNonNullOf(int.class));

                expect(joe).setLong(aNonNullOf(long.class));
                expect(joe).setLong_(aNonNullOf(long.class));

                expect(joe).setFloat(aNonNullOf(float.class));
                expect(joe).setFloat_(aNonNullOf(float.class));

                expect(joe).setDouble(aNonNullOf(double.class));
                expect(joe).setDouble_(aNonNullOf(double.class));
            }
        });
        story.begin();
        joe.setBoolean(true);
        joe.setBoolean(false);
        joe.setBoolean_(true);
        joe.setBoolean_(false);

        joe.setChar('@');
        joe.setChar_('_');

        joe.setByte((byte) 11);
        joe.setByte_((byte) 49);

        joe.setShort((short) 124);
        joe.setShort_((short) 233);

        joe.setInt(6553);
        joe.setInt_(1234);

        joe.setLong(999L);
        joe.setLong_(-167L);

        joe.setFloat((float) 3.141592);
        joe.setFloat_((float) 154.155);
        story.end();
    }

    /**
     * Validates expectations with aNonNullOf clause using primitive arguments.
     */
    @Test
    public void testANonNullOfWithPrimitiveTypesMA() {
        Schemer.begin();
        Schemer.willInvoke(1).of(joe).setBoolean(Schemer.aNonNullOf(
          boolean.class));
        Schemer.willInvoke(1).of(joe).setBoolean(Schemer.aNonNullOf(
          boolean.class));
        Schemer.willInvoke(1).of(joe).setBoolean_(Schemer.aNonNullOf(
          boolean.class));
        Schemer.willInvoke(1).of(joe).setBoolean_(Schemer.aNonNullOf(
          boolean.class));
        joe.setBoolean(true);
        joe.setBoolean(false);
        joe.setBoolean_(true);
        joe.setBoolean_(false);

        Schemer.willInvoke(1).of(joe).setChar(Schemer.aNonNullOf(char.class));
        Schemer.willInvoke(1).of(joe).setChar_(Schemer.aNonNullOf(char.class));
        joe.setChar('@');
        joe.setChar_('_');

        Schemer.willInvoke(1).of(joe).setByte(Schemer.aNonNullOf(byte.class));
        Schemer.willInvoke(1).of(joe).setByte_(Schemer.aNonNullOf(byte.class));
        joe.setByte((byte) 11);
        joe.setByte_((byte) 49);

        Schemer.willInvoke(1).of(joe).setShort(Schemer.aNonNullOf(short.class));
        Schemer.willInvoke(1).of(joe).setShort_(Schemer.aNonNullOf(short.class));
        joe.setShort((short) 124);
        joe.setShort_((short) 233);

        Schemer.willInvoke(1).of(joe).setInt(Schemer.aNonNullOf(int.class));
        Schemer.willInvoke(1).of(joe).setInt_(Schemer.aNonNullOf(int.class));
        joe.setInt(6553);
        joe.setInt_(1234);

        Schemer.willInvoke(1).of(joe).setLong(Schemer.aNonNullOf(long.class));
        Schemer.willInvoke(1).of(joe).setLong_(Schemer.aNonNullOf(long.class));
        joe.setLong(999L);
        joe.setLong_(-167L);

        Schemer.willInvoke(1).of(joe).setFloat(Schemer.aNonNullOf(float.class));
        Schemer.willInvoke(1).of(joe).setFloat_(Schemer.aNonNullOf(float.class));
        joe.setFloat((float) 3.141592);
        joe.setFloat_((float) 154.155);
        Schemer.end();
    }

    /**
     * Verifies that aNonNullOf rejects null primitive values.
     */
    @Test
    public void testANonNullOfWithNullPrimitiveTypes() {
        // The last error reported
        ExpectationError lastError = null;

        Story story = Story.create(new Scenario() {
            {
                expect(joe).setBoolean_(aNonNullOf(boolean.class));
                expect(joe).setChar_(aNonNullOf(char.class));
                expect(joe).setByte_(aNonNullOf(byte.class));
                expect(joe).setShort_(aNonNullOf(short.class));
                expect(joe).setInt_(aNonNullOf(int.class));
                expect(joe).setLong_(aNonNullOf(long.class));
                expect(joe).setFloat_(aNonNullOf(float.class));
                expect(joe).setDouble_(aNonNullOf(double.class));
            }
        });

        story.begin();
        try {
            joe.setBoolean_(null);
            fail("set null to aNonNullOf clause");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);

        story.begin();
        joe.setBoolean_(true);
        try {
            joe.setChar_(null);
            fail("set null to aNonNullOf clause");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);

        story.begin();
        joe.setBoolean_(true);
        joe.setChar_('W');
        try {
            joe.setByte_(null);
            fail("set null to aNonNullOf clause");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);

        story.begin();
        joe.setBoolean_(true);
        joe.setChar_('W');
        joe.setByte_((byte) 154);
        try {
            joe.setShort_(null);
            fail("set null to aNonNullOf clause");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);

        story.begin();
        joe.setBoolean_(true);
        joe.setChar_('W');
        joe.setByte_((byte) 154);
        joe.setShort_((short) 154);
        try {
            joe.setInt_(null);
            fail("set null to aNonNullOf clause");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);

        story.begin();
        joe.setBoolean_(true);
        joe.setChar_('W');
        joe.setByte_((byte) 154);
        joe.setShort_((short) 154);
        joe.setInt_(154);
        try {
            joe.setLong_(null);
            fail("set null to aNonNullOf clause");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);

        story.begin();
        joe.setBoolean_(true);
        joe.setChar_('W');
        joe.setByte_((byte) 154);
        joe.setShort_((short) 154);
        joe.setInt_(154);
        joe.setLong_((long) 154);
        try {
            joe.setFloat_(null);
            fail("set null to aNonNullOf clause");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);

        story.begin();
        joe.setBoolean_(true);
        joe.setChar_('W');
        joe.setByte_((byte) 154);
        joe.setShort_((short) 154);
        joe.setInt_(154);
        joe.setLong_((long) 154);
        joe.setFloat_((float) 154.0);
        try {
            joe.setDouble_(null);
            fail("set null to aNonNullOf clause");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);
    }

    /**
     * Verifies that aNonNullOf rejects null primitive values.
     */
    @Test
    public void testANonNullOfWithNullPrimitiveTypesMA() {
        // The last error reported by the mock system.
        ExpectationError lastError = null;

        Schemer.begin();
        Schemer.willInvoke(1).of(joe).setBoolean(Schemer.aNonNullOf(
          boolean.class));
        try {
            joe.setBoolean_(null);
            fail("set null to aNonNullOf clause");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);

        Schemer.begin();
        Schemer.willInvoke(1).of(joe).setChar_(Schemer.aNonNullOf(char.class));
        try {
            joe.setChar_(null);
            fail("set null to aNonNullOf clause");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);

        Schemer.begin();
        Schemer.willInvoke(1).of(joe).setByte_(Schemer.aNonNullOf(byte.class));
        try {
            joe.setByte_(null);
            fail("set null to aNonNullOf clause");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);

        Schemer.begin();
        Schemer.willInvoke(1).of(joe).setShort_(Schemer.aNonNullOf(short.class));
        try {
            joe.setShort_(null);
            fail("set null to aNonNullOf clause");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);

        Schemer.begin();
        Schemer.willInvoke(1).of(joe).setInt_(Schemer.aNonNullOf(int.class));
        try {
            joe.setInt_(null);
            fail("set null to aNonNullOf clause");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);

        Schemer.begin();
        Schemer.willInvoke(1).of(joe).setLong_(Schemer.aNonNullOf(long.class));
        try {
            joe.setLong_(null);
            fail("set null to aNonNullOf clause");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);

        Schemer.begin();
        Schemer.willInvoke(1).of(joe).setFloat_(Schemer.aNonNullOf(float.class));
        try {
            joe.setFloat_(null);
            fail("set null to aNonNullOf clause");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);

        Schemer.begin();
        Schemer.willInvoke(1).of(joe).setDouble_(
          Schemer.aNonNullOf(double.class));
        try {
            joe.setDouble_(null);
            fail("set null to aNonNullOf clause");
        } catch (UnsatisfiedOccurrenceError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);
    }

    /**
     * Validates the behavior of anyOf with a "simple" class.
     */
    @Test
    public void testAnyOfWithSimpleClass() {
        Story story = Story.create(new Scenario() {
            {
                expect(joe).setObject(anyOf(Object.class));
            }
        });
        story.begin();
        joe.setObject("hello world");
        story.end();
    }

    /**
     * Validates the behavior of anyOf with a "simple" class.
     */
    @Test
    public void testAnyOfWithSimpleClassMA() {
        Schemer.begin();
        Schemer.willInvoke(1).of(joe).setObject(Schemer.anyOf(Object.class));
        joe.setObject("hello world");
        Schemer.end();
    }

    /**
     * Verifies that aNonNullOf rejects null arguments.
     */
    @Test
    public void testANonNullOfWithAnInheritedClass() {
        // The last error reported by the test
        ExpectationError lastError = null;

        Story story = Story.create(new Scenario() {
            {
                expect(joe).setObject(aNonNullOf(String.class));
            }
        });

        story.begin();
        joe.setObject("boo!");
        try {
            joe.setObject(null);
            fail("passed a null argument with aNonNullOf clause");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);
    }

    /**
     * Verifies that aNonNullOf rejects null arguments.
     */
    @Test
    public void testANonNullOfWithAnInheritedClassMA() {
        // The last error reported by the test.
        ExpectationError lastError = null;

        Schemer.begin();
        Schemer.willInvoke(Occurrences.atLeast(1)).of(joe).setObject(Schemer.aNonNullOf(String.class));
        joe.setObject("boo!");
        try {
            joe.setObject(null);
            fail("passed a null argument with aNonNullOf clause");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);
    }

    /**
     * Verifies that anyOf "specializes" the expected instance of an argument.
     */
    @Test
    public void testAnyOfWithAnInheritedClass() {
        // The last error reported by the test
        ExpectationError lastError = null;

        Story story = Story.create(new Scenario() {
            {
                expect(joe).setObject(anyOf(String.class));
            }
        });
        story.begin();
        joe.setObject("$ilver");
        try {
            joe.setObject(new Object());
            fail("passed an argument of an unexpected class");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);
    }

    /**
     * Verifies that anyOf "specializes" the expected instance of an argument.
     */
    @Test
    public void testAnyOfWithAnInheritedClassMA() {
        // The last error reported by the test.
        ExpectationError lastError = null;

        Schemer.begin();
        Schemer.willInvoke(Occurrences.atLeast(1)).of(joe).setObject(Schemer.anyOf(String.class));
        joe.setObject("$ilver");
        try {
            joe.setObject(new Object());
            fail("passed an argument of an unexpected class");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);
    }

    /**
     * Verifies that anyOf "specializes" the expected instance of an argument.
     */
    @Test
    public void testAnyOfWithAnInterface() {
        // The last error reported by the test.
        ExpectationError lastError = null;

        Story story = Story.create(new Scenario() {
            {
                expect(joe).setObject(anyOf(Dalton.class));
            }
        });
        story.begin();
        joe.setObject(jack);
        try {
            joe.setObject("Jack");
            fail("passed an argument of an unexpected class");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);
    }

    /**
     * Verifies that anyOf "specializes" the expected instance of an argument.
     */
    @Test
    public void testAnyOfWithAnInterfaceMA() {
        // The last error reported by the test.
        ExpectationError lastError = null;

        Schemer.begin();
        Schemer.willInvoke(Occurrences.atLeast(1)).of(joe).setObject(Schemer.anyOf(Dalton.class));
        joe.setObject(jack);
        try {
            joe.setObject("Jack");
            fail("passed an argument of an unexpected class");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);
    }

    /**
     * Validates the anyOf clause with arrays.
     *
     * <p>
     * In particular, verifies that we actually check each item of an array and
     * not simply the overall array definition.
     * </p>
     */
    @Test
    public void testAnyOfWithAnArray() {
        // The last error reported by the test.
        ExpectationError lastError = null;

        Story story = Story.create(new Scenario() {
            {
                expect(joe).fillPocketWithAPackOf(anyOf(String[].class));
            }
        });

        story.begin();
        joe.fillPocketWithAPackOf(new String[]{"eurO$", null, "g0ld"});
        joe.fillPocketWithAPackOf(new Object[]{"d0llar$", null, "diAmonds"});
        try {
            joe.fillPocketWithAPackOf(new Object[]{"ruby", null, jack});
            fail("passed an array argument with incompatible items");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);
    }

    /**
     * Validates the anyOf clause with arrays.
     *
     * <p>
     * In particular, verifies that we actually check each item of an array and
     * not simply the overall array definition.
     * </p>
     */
    @Test
    public void testAnyOfWithAnArrayMA() {
        // The last error reported by the test.
        ExpectationError lastError = null;

        Schemer.begin();
        Schemer.willInvoke(Occurrences.atLeast(1)).of(joe).fillPocketWithAPackOf(Schemer.anyOf(String[].class));

        joe.fillPocketWithAPackOf(new String[]{"eurO$", null, "g0ld"});
        joe.fillPocketWithAPackOf(new Object[]{"d0llar$", null, "diAmonds"});
        try {
            joe.fillPocketWithAPackOf(new Object[]{"ruby", null, jack});
            fail("passed an array argument with incompatible items");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);
    }

    /**
     * Validates the anyOf clause with arrays of arrays.
     */
    @Test
    public void testAnyOfWithAnArrayOfArrays() {
        // The last error reported by the test.
        ExpectationError lastError = null;

        Story story = Story.create(new Scenario() {
            {
                expect(joe).fillPocketWithAPackOf(anyOf(String[][].class));
            }
        });

        story.begin();
        joe.fillPocketWithAPackOf(new String[][]{{"d0llar$"},
              {"euros", null}});
        try {
            joe.fillPocketWithAPackOf(new int[][]{{1, 2}, {3, 4}});
            fail("invoked method with an incompatible array of arrays");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);
    }

    /**
     * Validates the anyOf clause with arrays of arrays.
     */
    @Test
    public void testAnyOfWithAnArrayOfArraysMA() {
        // The last error reported by the test.
        ExpectationError lastError = null;

        Schemer.begin();
        Schemer.willInvoke(Occurrences.atLeast(1)).of(joe).fillPocketWithAPackOf(Schemer.anyOf(String[][].class));
        joe.fillPocketWithAPackOf(new String[][]{{"d0llar$"},
              {"euros", null}});
        try {
            joe.fillPocketWithAPackOf(new int[][]{{1, 2}, {3, 4}});
            fail("invoked method with an incompatible array of arrays");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);
    }

    /**
     * Validates the anyOf clause with varargs.
     */
    @Test
    public void testAnyOfWithVarargs() {
        // The last error reported by the test.
        ExpectationError lastError = null;

        Story story = Story.create(new Scenario() {
            {
                expect(joe).fillPocket(anyOf(Object.class),
                  anyOf(String.class), anyOf(Dalton.class),
                  anyOf(int[].class));
            }
        });

        story.begin();
        joe.fillPocket(new Object(), "Damnit!", jack, new int[]{1, 2, 3});
        story.end();

        story.begin();
        try {
            joe.fillPocket();
            fail("wrong invocation of a method");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);

        story.begin();
        try {
            joe.fillPocket(null, william, jack, new int[3]);
            fail("wrong invocation of a method");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);

        story.begin();
        try {
            joe.fillPocket(null, "Damnit!", "Again!", new int[3]);
            fail("wrong invocation of a method");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);

        story.begin();
        try {
            joe.fillPocket(null, "Damnit!", jack,
              new Object[]{1, 2, "three"});
            fail("wrong invocation of a method");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);

        // A final corner case: verify that we still parse arrays.
        story.begin();
        joe.fillPocket(null, "Damnit!", jack, new Object[]{1, 2, 3});
        joe.fillPocket(null, "Damnit!", jack, null);
        story.end();
    }

    /**
     * Validates the anyOf clause with varargs.
     */
    @Test
    public void testAnyOfWithVarargsMA() {
        // The last error reported by the test.
        ExpectationError lastError = null;

        Scenario scenario = new Scenario() {
            {
                expect(joe).fillPocket(anyOf(Object.class),
                  anyOf(String.class), anyOf(Dalton.class),
                  anyOf(int[].class));
            }
        };
        Schemer.begin();
        Schemer.append(scenario);
        joe.fillPocket(new Object(), "Damnit!", jack, new int[]{1, 2, 3});
        Schemer.end();

        Schemer.begin();
        Schemer.append(scenario);
        try {
            joe.fillPocket();
            fail("wrong invocation of a method");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);

        Schemer.begin();
        Schemer.append(scenario);
        try {
            joe.fillPocket(null, william, jack, new int[3]);
            fail("wrong invocation of a method");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);

        Schemer.begin();
        Schemer.append(scenario);
        try {
            joe.fillPocket(null, "Damnit!", "Again!", new int[3]);
            fail("wrong invocation of a method");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);

        Schemer.begin();
        Schemer.append(scenario);
        try {
            joe.fillPocket(null, "Damnit!", jack,
              new Object[]{1, 2, "three"});
            fail("wrong invocation of a method");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);

        // A final corner case: verify that we still parse arrays.
        Schemer.begin();
        Schemer.append(scenario);
        joe.fillPocket(null, "Damnit!", jack, new Object[]{1, 2, 3});
        joe.fillPocket(null, "Damnit!", jack, null);
        Schemer.end();
    }

    /**
     * Verifies that aNonNullOf properly works with varargs.
     */
    @Test
    public void testANonNullOfWithVarargs() {
        // The last error reported by the test
        ExpectationError lastError = null;

        Story story = Story.create(new Scenario() {
            {
                expect(joe).fillPocket(aNonNullOf(Object.class),
                  aNonNullOf(String.class), aNonNullOf(Dalton.class),
                  aNonNullOf(int[].class));
            }
        });
        story.begin();
        joe.fillPocket(new Object(), "Damnit!", jack, new int[4]);
        story.end();

        story.begin();
        try {
            joe.fillPocket(null, "Damnit!", jack, new int[4]);
            fail("aNonNullOf let a null argument pass");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);

        story.begin();
        try {
            joe.fillPocket(new Object(), null, jack, new int[4]);
            fail("aNonNullOf let a null argument pass");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);

        story.begin();
        try {
            joe.fillPocket(new Object(), "Damnit!", null, new int[4]);
            fail("aNonNullOf let a null argument pass");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);

        story.begin();
        try {
            joe.fillPocket(new Object(), "Damnit!", jack, null);
            fail("aNonNullOf let a null argument pass");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);
    }

    /**
     * Verifies that aNonNullOf properly works with varargs.
     */
    @Test
    public void testANonNullOfWithVarargsMA() {
        // The last error reported by the test.
        ExpectationError lastError = null;

        Scenario scenario = new Scenario() {
            {
                expect(joe).fillPocket(aNonNullOf(Object.class),
                  aNonNullOf(String.class), aNonNullOf(Dalton.class),
                  aNonNullOf(int[].class));
            }
        };

        Schemer.begin();
        Schemer.append(scenario);
        joe.fillPocket(new Object(), "Damnit!", jack, new int[4]);
        Schemer.end();

        Schemer.begin();
        Schemer.append(scenario);
        try {
            joe.fillPocket(null, "Damnit!", jack, new int[4]);
            fail("aNonNullOf let a null argument pass");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);

        Schemer.begin();
        Schemer.append(scenario);
        try {
            joe.fillPocket(new Object(), null, jack, new int[4]);
            fail("aNonNullOf let a null argument pass");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);

        Schemer.begin();
        Schemer.append(scenario);
        try {
            joe.fillPocket(new Object(), "Damnit!", null, new int[4]);
            fail("aNonNullOf let a null argument pass");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);

        Schemer.begin();
        Schemer.append(scenario);
        try {
            joe.fillPocket(new Object(), "Damnit!", jack, null);
            fail("aNonNullOf let a null argument pass");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(lastError);
    }
}
