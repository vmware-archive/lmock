/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.test;

import static com.vmware.lmock.test.Dalton.averell;
import static com.vmware.lmock.test.Dalton.jack;
import static com.vmware.lmock.test.Dalton.joe;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vmware.lmock.impl.Scenario;

/**
 * Validates the specification of expectations based on exact (matching)
 * invocations.
 *
 * <p>
 * These tests validate the coherence and the proper recording of such
 * expectations.
 * </p>
 */
public class MatchingExpectationSpecificationTest {
    /**
     * Validates a basic registration of an expectation.
     */
    @Test
    public void testArgsWithNoArguments() {
        new Scenario() {
            {
                expect(joe).doNothing();
                String instance = expect().toString();
                assertTrue(instance.contains("Dalton.doNothing()"));
            }
        };
    }

    /**
     * Validates the registration of primitive arguments and auto-boxing.
     */
    @Test
    public void testArgsWithPrimitives() {
        new Scenario() {
            {
                expect(joe).setBoolean(true);
                String instance = expect().toString();
                assertTrue(instance.contains("setBoolean(Boolean=true"));
                expect(joe).setBoolean(Boolean.TRUE);
                instance = expect().toString();
                assertTrue(instance.contains("setBoolean(Boolean=true"));
                expect(joe).setBoolean_(Boolean.TRUE);
                instance = expect().toString();
                assertTrue(instance.contains("setBoolean_(Boolean=true"));
                expect(joe).setBoolean_(true);
                instance = expect().toString();
                assertTrue(instance.contains("setBoolean_(Boolean=true"));

                expect(joe).setChar('M');
                instance = expect().toString();
                assertTrue(instance.contains("setChar(Character=M"));
                expect(joe).setChar(Character.valueOf('M'));
                instance = expect().toString();
                assertTrue(instance.contains("setChar(Character=M"));
                expect(joe).setChar_(Character.valueOf('M'));
                instance = expect().toString();
                assertTrue(instance.contains("setChar_(Character=M"));
                expect(joe).setChar_('M');
                instance = expect().toString();
                assertTrue(instance.contains("setChar_(Character=M"));

                expect(joe).setByte((byte) 54);
                instance = expect().toString();
                assertTrue(instance.contains("setByte(Byte=54"));
                expect(joe).setByte(Byte.valueOf((byte) 54));
                instance = expect().toString();
                assertTrue(instance.contains("setByte(Byte=54"));
                expect(joe).setByte_(Byte.valueOf((byte) 54));
                instance = expect().toString();
                assertTrue(instance.contains("setByte_(Byte=54"));
                expect(joe).setByte_((byte) 54);
                instance = expect().toString();
                assertTrue(instance.contains("setByte_(Byte=54"));

                expect(joe).setShort((short) 154);
                instance = expect().toString();
                assertTrue(instance.contains("setShort(Short=154"));
                expect(joe).setShort(Short.valueOf((short) 154));
                instance = expect().toString();
                assertTrue(instance.contains("setShort(Short=154"));
                expect(joe).setShort_(Short.valueOf((short) 154));
                instance = expect().toString();
                assertTrue(instance.contains("setShort_(Short=154"));
                expect(joe).setShort_((short) 154);
                instance = expect().toString();
                assertTrue(instance.contains("setShort_(Short=154"));

                expect(joe).setInt(193);
                instance = expect().toString();
                assertTrue(instance.contains("setInt(Integer=193"));
                expect(joe).setInt(Integer.valueOf(193));
                instance = expect().toString();
                assertTrue(instance.contains("setInt(Integer=193"));
                expect(joe).setInt_(Integer.valueOf(193));
                instance = expect().toString();
                assertTrue(instance.contains("setInt_(Integer=193"));
                expect(joe).setInt_(193);
                instance = expect().toString();
                assertTrue(instance.contains("setInt_(Integer"));

                expect(joe).setLong(1789L);
                instance = expect().toString();
                assertTrue(instance.contains("setLong(Long=1789"));
                expect(joe).setLong(Long.valueOf(1789L));
                instance = expect().toString();
                assertTrue(instance.contains("setLong(Long=1789"));
                expect(joe).setLong_(Long.valueOf(1789L));
                instance = expect().toString();
                assertTrue(instance.contains("setLong_(Long=1789"));
                expect(joe).setLong_(1789L);
                instance = expect().toString();
                assertTrue(instance.contains("setLong_(Long=1789"));

                expect(joe).setFloat((float) 225.66);
                instance = expect().toString();
                assertTrue(instance.contains("setFloat(Float=225.66"));
                expect(joe).setFloat(Float.valueOf((float) 225.66));
                instance = expect().toString();
                assertTrue(instance.contains("setFloat(Float=225.66"));
                expect(joe).setFloat_(Float.valueOf((float) 225.66));
                instance = expect().toString();
                assertTrue(instance.contains("setFloat_(Float=225.66"));
                expect(joe).setFloat_((float) 225.66);
                instance = expect().toString();
                assertTrue(instance.contains("setFloat_(Float=225.66"));
            }
        };
    }

    /**
     * Validates the registration of a null argument.
     */
    @Test
    public void testArgsWithNull() {
        new Scenario() {
            {
                expect(joe).ping(null);
                String instance = expect().toString();
                assertTrue(instance.contains("ping(Object=null)"));
            }
        };
    }

    /**
     * Validates the registration of simple object as argument to an
     * expectation.
     */
    @Test
    public void testArgsWithObject() {
        new Scenario() {
            {
                expect(joe).setObject("hello world!");
                String instance = expect().toString();
                assertTrue(instance.contains("setObject(String=hello world!)"));
            }
        };
    }

    /**
     * Validates the registration of a mock as argument to an expectation.
     */
    @Test
    public void testArgsWithMock() {
        new Scenario() {
            {
                expect(joe).ping(jack);
                String instance = expect().toString();
                assertTrue(instance.contains("ping(Mock=jack)"));
            }
        };
    }

    /**
     * Verifies that the arguments are correctly registered (good order...).
     */
    @Test
    public void testArgsWithSeveralParameters() {
        new Scenario() {
            {
                expect(joe).ping(jack, "let's go!");
                String instance = expect().toString();
                assertTrue(instance.contains(
                  "ping(Mock=jack,String=let's go!):void"));
            }
        };
    }

    /**
     * Validates the registration of an array as argument to an expectation.
     */
    @Test
    public void testArgsWithAnArray() {
        new Scenario() {
            {
                expect(joe).fillPocketWithAPackOf(
                  new Object[]{"ruby", "diamonds", null, 51});
                String instance = expect().toString();
                assertTrue(
                  instance.contains(
                  "fillPocketWithAPackOf(Object[]={String=ruby,String=diamonds,Object=null,Integer=51})"));
            }
        };
    }

    /**
     * Validates the registration of miscellaneous objects with varargs.
     */
    @Test
    public void testArgsWithVarargs() {
        new Scenario() {
            {
                expect(joe).fillPocket("ruby", "diamonds", null, 51,
                  new String[]{"emeralds", "sapphire"}, averell);
                String instance = expect().toString();
                // In fact, varargs are registered as classical arrays.
                assertTrue(
                  instance.contains(
                  "fillPocket(Object[]={String=ruby,String=diamonds,Object=null,Integer=51,String[]={String=emeralds,String=sapphire},Mock=averell"));
            }
        };
    }
}
