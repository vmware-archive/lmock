/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.test;

import static com.vmware.lmock.checker.Occurrences.exactly;
import static com.vmware.lmock.impl.InvocationResult.returnValue;
import static com.vmware.lmock.test.Dalton.jack;
import static com.vmware.lmock.test.Dalton.joe;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vmware.lmock.impl.Scenario;
import com.vmware.lmock.impl.Story;
import com.vmware.lmock.masquerade.Schemer;

/**
 * Validation of the willReturn clause in stories.
 *
 * <p>
 * This series of tests verify that the return values specified in expectations
 * are what stories actually see.
 * </p>
 */
public class WillReturnTest {
    /**
     * Validation of the default values returned by the methods of a mock.
     */
    @Test
    public void testWillReturnWithDefaultValues() {
        Story story = Story.create(new Scenario() {
            {
                expect(joe).getBoolean();
                occurs(exactly(1));
                expect(joe).getBoolean_();
                occurs(exactly(1));
                expect(joe).getChar();
                occurs(exactly(1));
                expect(joe).getChar_();
                occurs(exactly(1));
                expect(joe).getByte();
                occurs(exactly(1));
                expect(joe).getByte_();
                occurs(exactly(1));
                expect(joe).getShort();
                occurs(exactly(1));
                expect(joe).getShort_();
                occurs(exactly(1));
                expect(joe).getInt();
                occurs(exactly(1));
                expect(joe).getInt_();
                occurs(exactly(1));
                expect(joe).getLong();
                occurs(exactly(1));
                expect(joe).getLong_();
                occurs(exactly(1));
                expect(joe).getFloat();
                occurs(exactly(1));
                expect(joe).getFloat_();
                occurs(exactly(1));
                expect(joe).getDouble();
                occurs(exactly(1));
                expect(joe).getDouble_();
                occurs(exactly(1));
            }
        });

        story.begin();
        assertFalse(joe.getBoolean());
        assertFalse(joe.getBoolean_());
        assertEquals(' ', joe.getChar());
        assertEquals(Character.valueOf(' '), joe.getChar_());
        assertEquals(Byte.MIN_VALUE, joe.getByte());
        assertEquals(Byte.valueOf(Byte.MIN_VALUE), joe.getByte_());
        assertEquals(Short.MIN_VALUE, joe.getShort());
        assertEquals(Short.valueOf(Short.MIN_VALUE), joe.getShort_());
        assertEquals(Integer.MIN_VALUE, joe.getInt());
        assertEquals(Integer.valueOf(Integer.MIN_VALUE), joe.getInt_());
        assertEquals(Long.MIN_VALUE, joe.getLong());
        assertEquals(Long.valueOf(Long.MIN_VALUE), joe.getLong_());
        assertEquals(Float.MIN_VALUE, joe.getFloat(), 0);
        assertEquals(Float.valueOf(Float.MIN_VALUE), joe.getFloat_());
        assertEquals(Double.MIN_VALUE, joe.getDouble(), 0);
        assertEquals(Double.valueOf(Double.MIN_VALUE), joe.getDouble_());
        story.end();
    }

    /**
     * Validation of the default values returned by the methods of a mock.
     */
    @Test
    public void testWillReturnWithDefaultValuesMA() {
        Schemer.begin();
        Schemer.willInvoke(1).of(joe).getBoolean();
        Schemer.willInvoke(1).of(joe).getBoolean_();
        Schemer.willInvoke(1).of(joe).getChar();
        Schemer.willInvoke(1).of(joe).getChar_();
        Schemer.willInvoke(1).of(joe).getByte();
        Schemer.willInvoke(1).of(joe).getByte_();
        Schemer.willInvoke(1).of(joe).getShort();
        Schemer.willInvoke(1).of(joe).getShort_();
        Schemer.willInvoke(1).of(joe).getInt();
        Schemer.willInvoke(1).of(joe).getInt_();
        Schemer.willInvoke(1).of(joe).getLong();
        Schemer.willInvoke(1).of(joe).getLong_();
        Schemer.willInvoke(1).of(joe).getFloat();
        Schemer.willInvoke(1).of(joe).getFloat_();
        Schemer.willInvoke(1).of(joe).getDouble();
        Schemer.willInvoke(1).of(joe).getDouble_();

        assertFalse(joe.getBoolean());
        assertFalse(joe.getBoolean_());
        assertEquals(' ', joe.getChar());
        assertEquals(Character.valueOf(' '), joe.getChar_());
        assertEquals(Byte.MIN_VALUE, joe.getByte());
        assertEquals(Byte.valueOf(Byte.MIN_VALUE), joe.getByte_());
        assertEquals(Short.MIN_VALUE, joe.getShort());
        assertEquals(Short.valueOf(Short.MIN_VALUE), joe.getShort_());
        assertEquals(Integer.MIN_VALUE, joe.getInt());
        assertEquals(Integer.valueOf(Integer.MIN_VALUE), joe.getInt_());
        assertEquals(Long.MIN_VALUE, joe.getLong());
        assertEquals(Long.valueOf(Long.MIN_VALUE), joe.getLong_());
        assertEquals(Float.MIN_VALUE, joe.getFloat(), 0);
        assertEquals(Float.valueOf(Float.MIN_VALUE), joe.getFloat_());
        assertEquals(Double.MIN_VALUE, joe.getDouble(), 0);
        assertEquals(Double.valueOf(Double.MIN_VALUE), joe.getDouble_());

        Schemer.end();
    }

    /**
     * Verifies that primitive types are correctly registered.
     */
    @Test
    public void testWillReturnWithPrimitiveTypes() {
        Story story = Story.create(new Scenario() {
            {
                expect(joe).getBoolean();
                expect().willReturn(true).occurs(exactly(1));
                expect(joe).getBoolean_();
                expect().willReturn(true).occurs(exactly(1));

                expect(joe).getChar();
                expect().willReturn('z').occurs(exactly(1));
                expect(joe).getChar_();
                expect().willReturn('z').occurs(exactly(1));

                expect(joe).getByte();
                expect().willReturn((byte) 51).occurs(exactly(1));
                expect(joe).getByte_();
                expect().willReturn((byte) 51).occurs(exactly(1));

                expect(joe).getShort();
                expect().willReturn((short) 223).occurs(exactly(1));
                expect(joe).getShort_();
                expect().willReturn((short) 223).occurs(exactly(1));

                expect(joe).getInt();
                expect().willReturn(93).occurs(exactly(1));
                expect(joe).getInt_();
                expect().willReturn(93).occurs(exactly(1));

                expect(joe).getLong();
                expect().willReturn((long) 68).occurs(exactly(1));
                expect(joe).getLong_();
                expect().willReturn((long) 68).occurs(exactly(1));

                expect(joe).getFloat();
                expect().willReturn((float) 1337).occurs(exactly(1));
                expect(joe).getFloat_();
                expect().willReturn((float) 1337).occurs(exactly(1));

                expect(joe).getDouble();
                expect().willReturn((double) 1492).occurs(exactly(1));
                expect(joe).getDouble_();
                expect().willReturn((double) 1492).occurs(exactly(1));
            }
        });
        story.begin();
        assertTrue(joe.getBoolean());
        assertTrue(joe.getBoolean_());

        assertEquals('z', joe.getChar());
        assertEquals(Character.valueOf('z'), joe.getChar_());

        assertEquals((byte) 51, joe.getByte());
        assertEquals(Byte.valueOf((byte) 51), joe.getByte_());

        assertEquals((short) 223, joe.getShort());
        assertEquals(Short.valueOf((short) 223), joe.getShort_());

        assertEquals(93, joe.getInt());
        assertEquals(Integer.valueOf(93), joe.getInt_());

        assertEquals(68, joe.getLong());
        assertEquals(Long.valueOf(68), joe.getLong_());

        assertEquals(Float.MIN_VALUE, 1337, joe.getFloat());
        assertEquals(Float.MIN_VALUE, Float.valueOf(1337), joe.getFloat_());

        assertEquals(Double.MIN_VALUE, 1492.0, joe.getDouble());
        assertEquals(Double.MIN_VALUE, Double.valueOf(1492), joe.getDouble_());

        story.end();
    }

    /**
     * Verifies that primitive types are correctly registered.
     *
     * <p>
     * Take the opportunity to exercise the <code>will</code> clause provided by
     * masquerades.
     * </p>
     */
    @Test
    public void testWillReturnWithPrimitiveTypesMA() {
        Schemer.begin();
        Schemer.willInvoke(1).will(returnValue(true)).when(joe).getBoolean();
        Schemer.willInvoke(1).willReturn(true).when(joe).getBoolean_();
        Schemer.willInvoke(1).will(returnValue('z')).when(joe).getChar();
        Schemer.willInvoke(1).willReturn('z').when(joe).getChar_();
        Schemer.willInvoke(1).will(returnValue((byte) 51)).when(joe).getByte();
        Schemer.willInvoke(1).willReturn((byte) 51).when(joe).getByte_();
        Schemer.willInvoke(1).will(returnValue((short) 223)).when(joe).getShort();
        Schemer.willInvoke(1).willReturn((short) 223).when(joe).getShort_();
        Schemer.willInvoke(1).will(returnValue(93)).when(joe).getInt();
        Schemer.willInvoke(1).willReturn(93).when(joe).getInt_();
        Schemer.willInvoke(1).will(returnValue((long) 68)).when(joe).getLong();
        Schemer.willInvoke(1).willReturn((long) 68).when(joe).getLong_();
        Schemer.willInvoke(1).will(returnValue((float) 1337)).when(joe).getFloat();
        Schemer.willInvoke(1).willReturn((float) 1337).when(joe).getFloat_();
        Schemer.willInvoke(1).will(returnValue((double) 1492)).when(joe).getDouble();
        Schemer.willInvoke(1).willReturn((double) 1492).when(joe).getDouble_();

        assertTrue(joe.getBoolean());
        assertTrue(joe.getBoolean_());

        assertEquals('z', joe.getChar());
        assertEquals(Character.valueOf('z'), joe.getChar_());

        assertEquals((byte) 51, joe.getByte());
        assertEquals(Byte.valueOf((byte) 51), joe.getByte_());

        assertEquals((short) 223, joe.getShort());
        assertEquals(Short.valueOf((short) 223), joe.getShort_());

        assertEquals(93, joe.getInt());
        assertEquals(Integer.valueOf(93), joe.getInt_());

        assertEquals(68, joe.getLong());
        assertEquals(Long.valueOf(68), joe.getLong_());

        assertEquals(Float.MIN_VALUE, 1337, joe.getFloat());
        assertEquals(Float.MIN_VALUE, Float.valueOf(1337), joe.getFloat_());

        assertEquals(Double.MIN_VALUE, 1492.0, joe.getDouble());
        assertEquals(Double.MIN_VALUE, Double.valueOf(1492), joe.getDouble_());

        Schemer.end();
    }

    /**
     * Verifies that we can return an operational mock.
     *
     * <p>
     * Verifies that the returned value is usable by JUnit clauses and other
     * expectations.
     * </p>
     */
    @Test
    public void testWillReturnWithMock() {
        Story story = Story.create(new Scenario() {
            {
                expect(joe).next();
                willReturn(jack);
                expect(jack).ping();
                expect().willReturn(66).occurs(exactly(1));
            }
        });
        story.begin();
        assertEquals(jack, joe.next());
        assertEquals(66, joe.next().ping());
        story.end();
    }

    /**
     * Verifies that we can return an operational mock.
     *
     * <p>
     * Verifies that the returned value is usable by JUnit clauses and other
     * expectations.
     * </p>
     */
    @Test
    public void testWillReturnWithMockMA() {
        Schemer.begin();
        Schemer.willReturn(jack).when(joe).next();
        Schemer.willInvoke(1).willReturn(66).when(jack).ping();
        assertEquals(jack, joe.next());
        assertEquals(66, joe.next().ping());
        Schemer.end();
    }

    /**
     * Validation of a willReturn clause with an array.
     */
    @Test
    public void testWillReturnWithArray() {
        // The array used for tests.
        final String[] pocketStuff = {"gold", "silver", "platinium"};
        Story story = Story.create(new Scenario() {
            {
                expect(joe).emptyPocket();
                willReturn(pocketStuff);
            }
        });
        story.begin();
        String[] instance = (String[]) joe.emptyPocket();
        assertEquals(3, instance.length);
        assertEquals("gold", instance[0]);
        assertEquals("silver", instance[1]);
        assertEquals("platinium", instance[2]);

        // Modify a bit the pocket stuff before doing the test, so that
        // we verify that we actually maintain the array reference.
        pocketStuff[1] = "iron";
        instance = (String[]) joe.emptyPocket();
        assertEquals(3, instance.length);
        assertEquals("gold", instance[0]);
        assertEquals("iron", instance[1]);
        assertEquals("platinium", instance[2]);

        story.end();
    }

    /**
     * Validation of a willReturn clause with an array.
     */
    @Test
    public void testWillReturnWithArrayMA() {
        Schemer.begin();
        // The array used for tests.
        final String[] pocketStuff = {"gold", "silver", "platinium"};
        Schemer.willReturn(pocketStuff).when(joe).emptyPocket();

        String[] instance = (String[]) joe.emptyPocket();
        assertEquals(3, instance.length);
        assertEquals("gold", instance[0]);
        assertEquals("silver", instance[1]);
        assertEquals("platinium", instance[2]);

        // Modify a bit the pocket stuff before doing the test, so that
        // we verify that we actually maintain the array reference.
        pocketStuff[1] = "iron";
        instance = (String[]) joe.emptyPocket();
        assertEquals(3, instance.length);
        assertEquals("gold", instance[0]);
        assertEquals("iron", instance[1]);
        assertEquals("platinium", instance[2]);
        Schemer.end();
    }
}
