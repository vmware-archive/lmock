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
import static org.junit.Assert.fail;

import org.junit.Test;

import com.vmware.lmock.exception.IncoherentArgumentListException;
import com.vmware.lmock.impl.Scenario;
import com.vmware.lmock.impl.Stubs;

/**
 * Validation of the with clause.
 *
 * <p>
 * These tests focus on:
 * </p>
 * <ul>
 * <li>Valid clause definitions: verify that the with clauses are properly
 * recorded</li>
 * <li>invalid specifications: check the coherence of such a clause</li>
 * </ul>
 */
public class WithExpectationSpecificationTest {
    /**
     * Verifies that users cannot mix matching expectations with clauses.
     */
    @Test
    public void testWithClauseMixedWithMatchingExpectations() {
        new Scenario() {
            {
                try {
                    expect(joe).ping(jack, with("hello"));
                    fail("mixed with clauses and matching expectations");
                } catch (IncoherentArgumentListException e) {
                }
            }
        };
    }

    /**
     * Verifies that with clauses work fine with primitive types.
     */
    @Test
    public void testWithClauseWithPrimitiveTypesForExpectations() {
        new Scenario() {
            {
                expect(joe).setBoolean(with(true));
                String instance = expect().toString();
                assertTrue(instance.contains("setBoolean(Boolean=true)"));
                expect(joe).setBoolean(with(Boolean.TRUE));
                instance = expect().toString();
                assertTrue(instance.contains("setBoolean(Boolean=true)"));
                expect(joe).setBoolean_(with(Boolean.TRUE));
                instance = expect().toString();
                assertTrue(instance.contains("setBoolean_(Boolean=true)"));
                expect(joe).setBoolean_(with(true));
                instance = expect().toString();
                assertTrue(instance.contains("setBoolean_(Boolean=true)"));

                expect(joe).setChar(with('d'));
                instance = expect().toString();
                assertTrue(instance.contains("setChar(Character=d)"));
                expect(joe).setChar(with(Character.valueOf('d')));
                instance = expect().toString();
                assertTrue(instance.contains("setChar(Character=d)"));
                expect(joe).setChar_(with(Character.valueOf('d')));
                instance = expect().toString();
                assertTrue(instance.contains("setChar_(Character=d)"));
                expect(joe).setChar_(with('d'));
                instance = expect().toString();
                assertTrue(instance.contains("setChar_(Character=d)"));

                expect(joe).setByte(with((byte) 0xf));
                instance = expect().toString();
                assertTrue(instance.contains("setByte(Byte=15)"));
                expect(joe).setByte(with(Byte.valueOf((byte) 0xf)));
                instance = expect().toString();
                assertTrue(instance.contains("setByte(Byte=15)"));
                expect(joe).setByte_(with(Byte.valueOf((byte) 0xf)));
                instance = expect().toString();
                assertTrue(instance.contains("setByte_(Byte=15)"));
                expect(joe).setByte_(with((byte) 0xf));
                instance = expect().toString();
                assertTrue(instance.contains("setByte_(Byte=15)"));

                expect(joe).setShort(with((short) 123));
                instance = expect().toString();
                assertTrue(instance.contains("setShort(Short=123)"));
                expect(joe).setShort(with(Short.valueOf((short) 123)));
                instance = expect().toString();
                assertTrue(instance.contains("setShort(Short=123)"));
                expect(joe).setShort_(with(Short.valueOf((short) 123)));
                instance = expect().toString();
                assertTrue(instance.contains("setShort_(Short=123)"));
                expect(joe).setShort_(with((short) 123));
                instance = expect().toString();
                assertTrue(instance.contains("setShort_(Short=123)"));

                expect(joe).setInt(with(999));
                instance = expect().toString();
                assertTrue(instance.contains("setInt(Integer=999)"));
                expect(joe).setInt(with(Integer.valueOf(999)));
                instance = expect().toString();
                assertTrue(instance.contains("setInt(Integer=999)"));
                expect(joe).setInt_(with(Integer.valueOf(999)));
                instance = expect().toString();
                assertTrue(instance.contains("setInt_(Integer=999)"));
                expect(joe).setInt_(with(999));
                instance = expect().toString();
                assertTrue(instance.contains("setInt_(Integer=999)"));

                expect(joe).setLong(with(666L));
                instance = expect().toString();
                assertTrue(instance.contains("setLong(Long=666)"));
                expect(joe).setLong(with(Long.valueOf(666L)));
                instance = expect().toString();
                assertTrue(instance.contains("setLong(Long=666)"));
                expect(joe).setLong_(with(Long.valueOf(666L)));
                instance = expect().toString();
                assertTrue(instance.contains("setLong_(Long=666)"));
                expect(joe).setLong_(with(666L));
                instance = expect().toString();
                assertTrue(instance.contains("setLong_(Long=666)"));

                expect(joe).setFloat(with((float) 145.146));
                instance = expect().toString();
                assertTrue(instance.contains("setFloat(Float=145.146)"));
                expect(joe).setFloat(with(Float.valueOf((float) 145.146)));
                instance = expect().toString();
                assertTrue(instance.contains("setFloat(Float=145.146)"));
                expect(joe).setFloat_(with(Float.valueOf((float) 145.146)));
                instance = expect().toString();
                assertTrue(instance.contains("setFloat_(Float=145.146)"));
                expect(joe).setFloat_(with((float) 145.146));
                instance = expect().toString();
                assertTrue(instance.contains("setFloat_(Float=145.146)"));

                expect(joe).setDouble(with(3.141592));
                instance = expect().toString();
                assertTrue(instance.contains("setDouble(Double=3.141592)"));
                expect(joe).setDouble(with(Double.valueOf(3.141592)));
                instance = expect().toString();
                assertTrue(instance.contains("setDouble(Double=3.141592)"));
                expect(joe).setDouble_(with(Double.valueOf(3.141592)));
                instance = expect().toString();
                assertTrue(instance.contains("setDouble_(Double=3.141592)"));
                expect(joe).setDouble_(with(3.141592));
                instance = expect().toString();
                assertTrue(instance.contains("setDouble_(Double=3.141592)"));
            }
        };
    }

    /**
     * Verifies that with clauses work fine with primitive types.
     */
    @Test
    public void testWithClauseWithPrimitiveTypesForStubs() {
        new Stubs() {
            {
                stub(joe).setBoolean(with(true));
                String instance = stub().toString();
                assertTrue(instance.contains("setBoolean(Boolean=true)"));
                stub(joe).setBoolean(with(Boolean.TRUE));
                instance = stub().toString();
                assertTrue(instance.contains("setBoolean(Boolean=true)"));
                stub(joe).setBoolean_(with(Boolean.TRUE));
                instance = stub().toString();
                assertTrue(instance.contains("setBoolean_(Boolean=true)"));
                stub(joe).setBoolean_(with(true));
                instance = stub().toString();
                assertTrue(instance.contains("setBoolean_(Boolean=true)"));

                stub(joe).setChar(with('d'));
                instance = stub().toString();
                assertTrue(instance.contains("setChar(Character=d)"));
                stub(joe).setChar(with(Character.valueOf('d')));
                instance = stub().toString();
                assertTrue(instance.contains("setChar(Character=d)"));
                stub(joe).setChar_(with(Character.valueOf('d')));
                instance = stub().toString();
                assertTrue(instance.contains("setChar_(Character=d)"));
                stub(joe).setChar_(with('d'));
                instance = stub().toString();
                assertTrue(instance.contains("setChar_(Character=d)"));

                stub(joe).setByte(with((byte) 0xf));
                instance = stub().toString();
                assertTrue(instance.contains("setByte(Byte=15)"));
                stub(joe).setByte(with(Byte.valueOf((byte) 0xf)));
                instance = stub().toString();
                assertTrue(instance.contains("setByte(Byte=15)"));
                stub(joe).setByte_(with(Byte.valueOf((byte) 0xf)));
                instance = stub().toString();
                assertTrue(instance.contains("setByte_(Byte=15)"));
                stub(joe).setByte_(with((byte) 0xf));
                instance = stub().toString();
                assertTrue(instance.contains("setByte_(Byte=15)"));

                stub(joe).setShort(with((short) 123));
                instance = stub().toString();
                assertTrue(instance.contains("setShort(Short=123)"));
                stub(joe).setShort(with(Short.valueOf((short) 123)));
                instance = stub().toString();
                assertTrue(instance.contains("setShort(Short=123)"));
                stub(joe).setShort_(with(Short.valueOf((short) 123)));
                instance = stub().toString();
                assertTrue(instance.contains("setShort_(Short=123)"));
                stub(joe).setShort_(with((short) 123));
                instance = stub().toString();
                assertTrue(instance.contains("setShort_(Short=123)"));

                stub(joe).setInt(with(999));
                instance = stub().toString();
                assertTrue(instance.contains("setInt(Integer=999)"));
                stub(joe).setInt(with(Integer.valueOf(999)));
                instance = stub().toString();
                assertTrue(instance.contains("setInt(Integer=999)"));
                stub(joe).setInt_(with(Integer.valueOf(999)));
                instance = stub().toString();
                assertTrue(instance.contains("setInt_(Integer=999)"));
                stub(joe).setInt_(with(999));
                instance = stub().toString();
                assertTrue(instance.contains("setInt_(Integer=999)"));

                stub(joe).setLong(with(666L));
                instance = stub().toString();
                assertTrue(instance.contains("setLong(Long=666)"));
                stub(joe).setLong(with(Long.valueOf(666L)));
                instance = stub().toString();
                assertTrue(instance.contains("setLong(Long=666)"));
                stub(joe).setLong_(with(Long.valueOf(666L)));
                instance = stub().toString();
                assertTrue(instance.contains("setLong_(Long=666)"));
                stub(joe).setLong_(with(666L));
                instance = stub().toString();
                assertTrue(instance.contains("setLong_(Long=666)"));

                stub(joe).setFloat(with((float) 145.146));
                instance = stub().toString();
                assertTrue(instance.contains("setFloat(Float=145.146)"));
                stub(joe).setFloat(with(Float.valueOf((float) 145.146)));
                instance = stub().toString();
                assertTrue(instance.contains("setFloat(Float=145.146)"));
                stub(joe).setFloat_(with(Float.valueOf((float) 145.146)));
                instance = stub().toString();
                assertTrue(instance.contains("setFloat_(Float=145.146)"));
                stub(joe).setFloat_(with((float) 145.146));
                instance = stub().toString();
                assertTrue(instance.contains("setFloat_(Float=145.146)"));

                stub(joe).setDouble(with(3.141592));
                instance = stub().toString();
                assertTrue(instance.contains("setDouble(Double=3.141592)"));
                stub(joe).setDouble(with(Double.valueOf(3.141592)));
                instance = stub().toString();
                assertTrue(instance.contains("setDouble(Double=3.141592)"));
                stub(joe).setDouble_(with(Double.valueOf(3.141592)));
                instance = stub().toString();
                assertTrue(instance.contains("setDouble_(Double=3.141592)"));
                stub(joe).setDouble_(with(3.141592));
                instance = stub().toString();
                assertTrue(instance.contains("setDouble_(Double=3.141592)"));
            }
        };
    }

    /**
     * Validates a with clause with a null argument.
     */
    @Test
    public void testWithClauseWithNull() {
        new Scenario() {
            {
                expect(joe).setObject(with(null));
                String instance = expect().toString();
                assertTrue(instance.contains("setObject(Object=null)"));
            }
        };
    }

    /**
     * Validates a with clause with a null argument.
     */
    @Test
    public void testWithClauseWithNullForStubs() {
        new Stubs() {
            {
                stub(joe).setObject(with(null));
                String instance = stub().toString();
                assertTrue(instance.contains("setObject(Object=null)"));
            }
        };
    }

    /**
     * Test of the with clause with common objects.
     */
    @Test
    public void testWithClauseWithObject() {
        new Scenario() {
            {
                expect(joe).setObject(with("hello world!"));
                String instance = expect().toString();
                assertTrue(instance.contains("setObject(String=hello world!)"));
            }
        };
    }

    /**
     * Test of the with clause with common objects.
     */
    @Test
    public void testWithClauseWithObjectForStubs() {
        new Stubs() {
            {
                stub(joe).setObject(with("hello world!"));
                String instance = stub().toString();
                assertTrue(instance.contains("setObject(String=hello world!)"));
            }
        };
    }

    /**
     * Validates a with clause referencing a mock.
     */
    @Test
    public void testWithClauseWithMock() {
        new Scenario() {
            {
                expect(joe).ping(with(jack));
                String instance = expect().toString();
                assertTrue(instance.contains("ping(Mock=jack)"));
            }
        };
    }

    /**
     * Validates a with clause referencing a mock.
     */
    @Test
    public void testWithClauseWithMockForStubs() {
        new Stubs() {
            {
                stub(joe).ping(with(jack));
                String instance = stub().toString();
                assertTrue(instance.contains("ping(Mock=jack)"));
            }
        };
    }

    /**
     * Verifies that the arguments are correctly registered (good order...).
     */
    @Test
    public void testWithClauseWithSeveralParameters() {
        new Scenario() {
            {
                expect(joe).ping(with(jack), with("let's go!"));
                String instance = expect().toString();
                assertTrue(instance.contains(
                  "ping(Mock=jack,String=let's go!):void"));
            }
        };
    }

    /**
     * Verifies that the arguments are correctly registered (good order...).
     */
    @Test
    public void testWithClauseWithSeveralParametersForStubs() {
        new Stubs() {
            {
                stub(joe).ping(with(jack), with("let's go!"));
                String instance = stub().toString();
                assertTrue(instance.contains(
                  "ping(Mock=jack,String=let's go!):void"));
            }
        };
    }

    /**
     * Validates the registration of an array as argument to a with clause.
     */
    @Test
    public void testWithClauseWithAnArray() {
        new Scenario() {
            {
                expect(joe).fillPocketWithAPackOf(
                  new Object[]{"dollars", "yens", null, 3.1415926535});
                String instance = expect().toString();
                assertTrue(
                  instance.contains(
                  "fillPocketWithAPackOf(Object[]={String=dollars,String=yens,Object=null,Double=3.1415926535})"));
            }
        };
    }

    /**
     * Validates the registration of an array as argument to a with clause.
     */
    @Test
    public void testWithClauseWithAnArrayForStubs() {
        new Stubs() {
            {
                stub(joe).fillPocketWithAPackOf(
                  new Object[]{"dollars", "yens", null, 3.1415926535});
                String instance = stub().toString();
                assertTrue(
                  instance.contains(
                  "fillPocketWithAPackOf(Object[]={String=dollars,String=yens,Object=null,Double=3.1415926535})"));
            }
        };
    }

    /**
     * Validates the registration of varargs including with clauses.
     */
    @Test
    public void testWithClauseWithVarargs() {
        new Scenario() {
            {
                expect(joe).fillPocket("dollars", "yens", null, '$',
                  new String[]{"euros", "crowns"}, averell);
                String instance = expect().toString();
                // In fact, varargs are registered as classical arrays.
                assertTrue(
                  instance.contains(
                  "fillPocket(Object[]={String=dollars,String=yens,Object=null,Character=$,String[]={String=euros,String=crowns},Mock=averell"));
            }
        };
    }

    /**
     * Validates the registration of varargs including with clauses.
     */
    @Test
    public void testWithClauseWithVarargsForStubs() {
        new Stubs() {
            {
                stub(joe).fillPocket("dollars", "yens", null, '$',
                  new String[]{"euros", "crowns"}, averell);
                String instance = stub().toString();
                // In fact, varargs are registered as classical arrays.
                assertTrue(
                  instance.contains(
                  "fillPocket(Object[]={String=dollars,String=yens,Object=null,Character=$,String[]={String=euros,String=crowns},Mock=averell"));
            }
        };
    }
}
