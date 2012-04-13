/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.test;

import static com.vmware.lmock.impl.InvocationResult.returnValue;
import static com.vmware.lmock.test.Dalton.jack;
import static com.vmware.lmock.test.Dalton.joe;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Test;

import com.vmware.lmock.exception.IncompatibleReturnValueException;
import com.vmware.lmock.impl.Scenario;
import com.vmware.lmock.impl.Stubs;

/**
 * Tests of the specification of a willReturn clause.
 *
 * These tests focus on:
 * <ul>
 * <li>Invalid specifications: an incoherent clause issues an error.</li>
 * <li>The correctness of the clause: verify that the information is correctly
 * recorded.</li>
 * </ul>
 *
 * <p>
 * Validates the clause in the context of scenarios and stubs.
 * </p>
 */
public class WillReturnSpecificationTest {
    /**
     * Validates the default behavior of a method (no willThrow or willReturn
     * clause specified yet).
     */
    @Test
    public void testDefaultInvocationResult() {
        new Scenario() {
            {
                expect(joe).ping();
                String instance = expect().toString();
                assertTrue(instance.contains(":void"));
            }
        };

        new Stubs() {
            {
                stub(joe).ping();
                String instance = stub().toString();
                assertTrue(instance.contains(":void"));
            }
        };
    }

    /**
     * Verifies that a 'willReturn' clause fails with void methods.
     */
    @Test
    public void testWillReturnOnVoidMethod() {
        new Scenario() {
            {
                try {
                    expect(joe).doNothing();
                    willReturn("abc");
                    fail("specified a returned value for a void method");
                } catch (IncompatibleReturnValueException e) {
                }
            }
        };

        new Stubs() {
            {
                try {
                    stub(joe).doNothing();
                    willReturn("abc");
                    fail("specified a returned value for a void method");
                } catch (IncompatibleReturnValueException e) {
                }
            }
        };
    }

    /**
     * Validation of a regular willReturn clause referencing a primitive type.
     */
    @Test
    public void testWilReturnWithValidPrimitiveValue() {
        new Scenario() {
            {
                expect(joe).getBoolean();
                willReturn(true);
                String instance = expect().toString();
                assertTrue(
                  instance.contains("Dalton.getBoolean():returns(true)"));

                expect(joe).getBoolean_();
                willReturn(Boolean.TRUE);
                instance = expect().toString();
                assertTrue(instance.contains(
                  "Dalton.getBoolean_():returns(true)"));

                expect(joe).getChar();
                willReturn('z');
                instance = expect().toString();
                assertTrue(instance.contains("Dalton.getChar():returns(z)"));

                expect(joe).getChar_();
                willReturn(Character.valueOf('z'));
                instance = expect().toString();
                assertTrue(instance.contains("Dalton.getChar_():returns(z)"));

                expect(joe).getByte();
                willReturn((byte) 0xdf);
                instance = expect().toString();
                assertTrue(instance.contains("Dalton.getByte():returns(-33)"));

                expect(joe).getByte_();
                willReturn(Byte.valueOf((byte) 0xdf));
                instance = expect().toString();
                assertTrue(instance.contains("Dalton.getByte_():returns(-33)"));

                expect(joe).getShort();
                willReturn((short) 1234);
                instance = expect().toString();
                assertTrue(instance.contains("Dalton.getShort():returns(1234)"));

                expect(joe).getShort_();
                willReturn(Short.valueOf((short) 1234));
                instance = expect().toString();
                assertTrue(instance.contains("Dalton.getShort_():returns(1234)"));

                expect(joe).getInt();
                willReturn(69);
                instance = expect().toString();
                assertTrue(instance.contains("Dalton.getInt():returns(69)"));

                expect(joe).getInt_();
                willReturn(Integer.valueOf(69));
                instance = expect().toString();
                assertTrue(instance.contains("Dalton.getInt_():returns(69)"));

                expect(joe).getLong();
                willReturn(56743L);
                instance = expect().toString();
                assertTrue(instance.contains("Dalton.getLong():returns(56743)"));

                expect(joe).getLong_();
                willReturn(Long.valueOf(56743L));
                instance = expect().toString();
                assertTrue(instance.contains("Dalton.getLong_():returns(56743)"));

                expect(joe).getFloat();
                willReturn((float) 19.71);
                instance = expect().toString();
                assertTrue(instance.contains("Dalton.getFloat():returns(19.71)"));

                expect(joe).getFloat_();
                willReturn(Float.valueOf((float) 19.71));
                instance = expect().toString();
                assertTrue(
                  instance.contains("Dalton.getFloat_():returns(19.71)"));

                expect(joe).getDouble();
                willReturn(19.7110);
                instance = expect().toString();
                assertTrue(instance.contains(
                  "Dalton.getDouble():returns(19.711)"));

                expect(joe).getDouble_();
                willReturn(Double.valueOf(19.7110));
                instance = expect().toString();
                assertTrue(instance.contains(
                  "Dalton.getDouble_():returns(19.711)"));
            }
        };

        new Stubs() {
            {
                stub(joe).getBoolean();
                willReturn(true);
                String instance = stub().toString();
                assertTrue(
                  instance.contains("Dalton.getBoolean():returns(true)"));

                stub(joe).getBoolean_();
                willReturn(Boolean.TRUE);
                instance = stub().toString();
                assertTrue(instance.contains(
                  "Dalton.getBoolean_():returns(true)"));

                stub(joe).getChar();
                willReturn('z');
                instance = stub().toString();
                assertTrue(instance.contains("Dalton.getChar():returns(z)"));

                stub(joe).getChar_();
                willReturn(Character.valueOf('z'));
                instance = stub().toString();
                assertTrue(instance.contains("Dalton.getChar_():returns(z)"));

                stub(joe).getByte();
                willReturn((byte) 0xdf);
                instance = stub().toString();
                assertTrue(instance.contains("Dalton.getByte():returns(-33)"));

                stub(joe).getByte_();
                willReturn(Byte.valueOf((byte) 0xdf));
                instance = stub().toString();
                assertTrue(instance.contains("Dalton.getByte_():returns(-33)"));

                stub(joe).getShort();
                willReturn((short) 1234);
                instance = stub().toString();
                assertTrue(instance.contains("Dalton.getShort():returns(1234)"));

                stub(joe).getShort_();
                willReturn(Short.valueOf((short) 1234));
                instance = stub().toString();
                assertTrue(instance.contains("Dalton.getShort_():returns(1234)"));

                stub(joe).getInt();
                willReturn(69);
                instance = stub().toString();
                assertTrue(instance.contains("Dalton.getInt():returns(69)"));

                stub(joe).getInt_();
                willReturn(Integer.valueOf(69));
                instance = stub().toString();
                assertTrue(instance.contains("Dalton.getInt_():returns(69)"));

                stub(joe).getLong();
                willReturn(56743L);
                instance = stub().toString();
                assertTrue(instance.contains("Dalton.getLong():returns(56743)"));

                stub(joe).getLong_();
                willReturn(Long.valueOf(56743L));
                instance = stub().toString();
                assertTrue(instance.contains("Dalton.getLong_():returns(56743)"));

                stub(joe).getFloat();
                willReturn((float) 19.71);
                instance = stub().toString();
                assertTrue(instance.contains("Dalton.getFloat():returns(19.71)"));

                stub(joe).getFloat_();
                willReturn(Float.valueOf((float) 19.71));
                instance = stub().toString();
                assertTrue(
                  instance.contains("Dalton.getFloat_():returns(19.71)"));

                stub(joe).getDouble();
                willReturn(19.7110);
                instance = stub().toString();
                assertTrue(instance.contains(
                  "Dalton.getDouble():returns(19.711)"));

                stub(joe).getDouble_();
                willReturn(Double.valueOf(19.7110));
                instance = stub().toString();
                assertTrue(instance.contains(
                  "Dalton.getDouble_():returns(19.711)"));
            }
        };
    }

    /**
     * Validation of a regular willReturn clause referencing a primitive type.
     *
     * <p>
     * In this test, we mix the primitive types with their equivalent object
     * representations in order to verify that auto-boxing is correctly done.
     * </p>
     */
    @Test
    public void testWilReturnWithValidPrimitiveAndAutoboxing() {
        new Scenario() {
            {
                expect(joe).getBoolean();
                willReturn(Boolean.TRUE);
                String instance = expect().toString();
                assertTrue(
                  instance.contains("Dalton.getBoolean():returns(true)"));

                expect(joe).getBoolean_();
                willReturn(true);
                instance = expect().toString();
                assertTrue(instance.contains(
                  "Dalton.getBoolean_():returns(true)"));

                expect(joe).getChar();
                willReturn(Character.valueOf('z'));
                instance = expect().toString();
                assertTrue(instance.contains("Dalton.getChar():returns(z)"));

                expect(joe).getChar_();
                willReturn('z');
                instance = expect().toString();
                assertTrue(instance.contains("Dalton.getChar_():returns(z)"));

                expect(joe).getByte();
                willReturn(Byte.valueOf((byte) 0xdf));
                instance = expect().toString();
                assertTrue(instance.contains("Dalton.getByte():returns(-33)"));

                expect(joe).getByte_();
                willReturn((byte) 0xdf);
                instance = expect().toString();
                assertTrue(instance.contains("Dalton.getByte_():returns(-33)"));

                expect(joe).getShort();
                willReturn(Short.valueOf((short) 1234));
                instance = expect().toString();
                assertTrue(instance.contains("Dalton.getShort():returns(1234)"));

                expect(joe).getShort_();
                willReturn((short) 1234);
                instance = expect().toString();
                assertTrue(instance.contains("Dalton.getShort_():returns(1234)"));

                expect(joe).getInt();
                willReturn(Integer.valueOf(69));
                instance = expect().toString();
                assertTrue(instance.contains("Dalton.getInt():returns(69)"));

                expect(joe).getInt_();
                willReturn(69);
                instance = expect().toString();
                assertTrue(instance.contains("Dalton.getInt_():returns(69)"));

                expect(joe).getLong();
                willReturn(Long.valueOf(56743L));
                instance = expect().toString();
                assertTrue(instance.contains("Dalton.getLong():returns(56743)"));

                expect(joe).getLong_();
                willReturn(56743L);
                instance = expect().toString();
                assertTrue(instance.contains("Dalton.getLong_():returns(56743)"));

                expect(joe).getFloat();
                willReturn(Float.valueOf((float) 19.71));
                instance = expect().toString();
                assertTrue(instance.contains("Dalton.getFloat():returns(19.71)"));

                expect(joe).getFloat_();
                willReturn((float) 19.71);
                instance = expect().toString();
                assertTrue(
                  instance.contains("Dalton.getFloat_():returns(19.71)"));

                expect(joe).getDouble();
                willReturn(Double.valueOf(19.7110));
                instance = expect().toString();
                assertTrue(instance.contains(
                  "Dalton.getDouble():returns(19.711)"));

                expect(joe).getDouble_();
                willReturn(19.7110);
                instance = expect().toString();
                assertTrue(instance.contains(
                  "Dalton.getDouble_():returns(19.711)"));
            }
        };

        new Stubs() {
            {
                stub(joe).getBoolean();
                willReturn(Boolean.TRUE);
                String instance = stub().toString();
                assertTrue(
                  instance.contains("Dalton.getBoolean():returns(true)"));

                stub(joe).getBoolean_();
                willReturn(true);
                instance = stub().toString();
                assertTrue(instance.contains(
                  "Dalton.getBoolean_():returns(true)"));

                stub(joe).getChar();
                willReturn(Character.valueOf('z'));
                instance = stub().toString();
                assertTrue(instance.contains("Dalton.getChar():returns(z)"));

                stub(joe).getChar_();
                willReturn('z');
                instance = stub().toString();
                assertTrue(instance.contains("Dalton.getChar_():returns(z)"));

                stub(joe).getByte();
                willReturn(Byte.valueOf((byte) 0xdf));
                instance = stub().toString();
                assertTrue(instance.contains("Dalton.getByte():returns(-33)"));

                stub(joe).getByte_();
                willReturn((byte) 0xdf);
                instance = stub().toString();
                assertTrue(instance.contains("Dalton.getByte_():returns(-33)"));

                stub(joe).getShort();
                willReturn(Short.valueOf((short) 1234));
                instance = stub().toString();
                assertTrue(instance.contains("Dalton.getShort():returns(1234)"));

                stub(joe).getShort_();
                willReturn((short) 1234);
                instance = stub().toString();
                assertTrue(instance.contains("Dalton.getShort_():returns(1234)"));

                stub(joe).getInt();
                willReturn(Integer.valueOf(69));
                instance = stub().toString();
                assertTrue(instance.contains("Dalton.getInt():returns(69)"));

                stub(joe).getInt_();
                willReturn(69);
                instance = stub().toString();
                assertTrue(instance.contains("Dalton.getInt_():returns(69)"));

                stub(joe).getLong();
                willReturn(Long.valueOf(56743L));
                instance = stub().toString();
                assertTrue(instance.contains("Dalton.getLong():returns(56743)"));

                stub(joe).getLong_();
                willReturn(56743L);
                instance = stub().toString();
                assertTrue(instance.contains("Dalton.getLong_():returns(56743)"));

                stub(joe).getFloat();
                willReturn(Float.valueOf((float) 19.71));
                instance = stub().toString();
                assertTrue(instance.contains("Dalton.getFloat():returns(19.71)"));

                stub(joe).getFloat_();
                willReturn((float) 19.71);
                instance = stub().toString();
                assertTrue(
                  instance.contains("Dalton.getFloat_():returns(19.71)"));

                stub(joe).getDouble();
                willReturn(Double.valueOf(19.7110));
                instance = stub().toString();
                assertTrue(instance.contains(
                  "Dalton.getDouble():returns(19.711)"));

                stub(joe).getDouble_();
                willReturn(19.7110);
                instance = stub().toString();
                assertTrue(instance.contains(
                  "Dalton.getDouble_():returns(19.711)"));
            }
        };
    }

    /**
     * Test of an invalid 'willReturn' clause.
     *
     * <p>
     * The specified returned value does not match the profile of the method.
     * </p>
     */
    @Test
    public void testWillReturnWithInvalidPrimitiveValue() {
        new Scenario() {
            {
                try {
                    expect(joe).ping();
                    willReturn(69L);
                    fail("specified an incompatible returned value");
                } catch (IncompatibleReturnValueException e) {
                }
            }
        };

        new Stubs() {
            {
                try {
                    stub(joe).ping();
                    willReturn(69L);
                    fail("specified an incompatible returned value");
                } catch (IncompatibleReturnValueException e) {
                }
            }
        };
    }

    /**
     * Verifies that a willReturn clause works fine with mocks.
     */
    @Test
    public void testWillReturnWithValidInterfaceResult() {
        new Scenario() {
            {
                expect(joe).next();
                willReturn(jack);
                String instance = expect().toString();
                assertTrue(instance.contains("returns(jack)"));
            }
        };

        new Stubs() {
            {
                stub(joe).next();
                willReturn(jack);
                String instance = stub().toString();
                assertTrue(instance.contains("returns(jack)"));
            }
        };
    }

    /**
     * Verifies the coherence of a willReturn clause with implementations.
     */
    @Test
    public void testWillReturnWithAnInstanceOfAGenericInterface() {
        new Scenario() {
            {
                ArrayList<String> result = new ArrayList<String>();
                result.add("dollars");
                result.add("euros");

                expect(joe).getPocketContents();
                willReturn(result);
                String instance = expect().toString();
                assertTrue(instance.contains("returns([dollars, euros])"));
            }
        };

        new Stubs() {
            {
                ArrayList<String> result = new ArrayList<String>();
                result.add("dollars");
                result.add("euros");

                stub(joe).getPocketContents();
                willReturn(result);
                String instance = stub().toString();
                assertTrue(instance.contains("returns([dollars, euros])"));
            }
        };
    }

    /**
     * Verifies that willReturn complies with inheritance.
     */
    @Test
    public void testWillReturnWithAnExtendObject() {
        new Scenario() {
            {
                String[] result = {"dollars", "euros"};

                expect(joe).emptyPocket();
                willReturn(result);
                String instance = expect().toString();
                assertTrue(instance.contains("returns([Ljava.lang.String;@"));
            }
        };

        new Stubs() {
            {
                String[] result = {"dollars", "euros"};

                stub(joe).emptyPocket();
                willReturn(result);
                String instance = stub().toString();
                assertTrue(instance.contains("returns([Ljava.lang.String;@"));
            }
        };
    }

    /**
     * Verifies that the willReturn clause does not make confusion in the
     * inheritance hierarchy.
     *
     * <p>
     * This corner case is pretty important to verify that the program does not
     * confuse the order of instances.
     * </p>
     */
    @Test
    public void testWillReturnWithAnIncompatibleExtend() {
        new Scenario() {
            {
                try {
                    expect(joe).emptyPocket();
                    willReturn(new Object());
                    fail("specified an incompatible return value");
                } catch (IncompatibleReturnValueException e) {
                }
            }
        };

        new Stubs() {
            {
                try {
                    stub(joe).emptyPocket();
                    willReturn(new Object());
                    fail("specified an incompatible return value");
                } catch (IncompatibleReturnValueException e) {
                }
            }
        };
    }

    /**
     * Verifies that 'willReturn' is an equivalent of 'will(returnValue)'.
     */
    @Test
    public void testWillReturnEqualsWillReturnValue() {
        new Scenario() {
            {
                expect(joe).getBoolean();
                will(returnValue(true));
                String instance = expect().toString();
                assertTrue(
                  instance.contains("Dalton.getBoolean():returns(true)"));
            }
        };

        new Stubs() {
            {
                stub(joe).getBoolean();
                will(returnValue(true));
                String instance = stub().toString();
                assertTrue(
                  instance.contains("Dalton.getBoolean():returns(true)"));
            }
        };
    }
}
