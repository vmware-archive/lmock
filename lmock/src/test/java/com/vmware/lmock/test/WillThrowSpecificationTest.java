/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.test;

import static com.vmware.lmock.impl.InvocationResult.throwException;
import static com.vmware.lmock.test.Dalton.joe;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import com.vmware.lmock.exception.IncompatibleThrowableException;
import com.vmware.lmock.impl.Scenario;
import com.vmware.lmock.impl.Stubs;
import com.vmware.lmock.impl.SystemSpecifics;
import com.vmware.lmock.test.Dalton.SpecialDaltonException;

/**
 * Tests of the specification of willThrow clauses.
 *
 * These tests focus on:
 * <ul>
 * <li>Invalid specifications: an incoherent clause issues an error.</li>
 * <li>The correctness of the clause: verify that the information is correctly
 * recorded.</li>
 * </ul>
 *
 * <p>
 * Validates the clauses in the context of scenarios and stubs.
 * </p>
 */
public class WillThrowSpecificationTest {
    /** A special exception for inheritance tests. */
    private class SuperSpecialDaltonException extends Dalton.SpecialDaltonException {
        /** Class version, for serialization. */
        private static final long serialVersionUID = 1L;

        /**
         * Creates the special sauce...
         *
         * @param msg
         *            a frightening message
         */
        public SuperSpecialDaltonException(String msg) {
            super(msg);
        }
    };

    /**
     * Verifies that a willThrow clause is always valid with runtime exceptions.
     *
     * <p>
     * The invoked method is not supposed to throw any exception.
     * </p>
     */
    @Test
    public void testWillThrowWithRuntimeException() {
        new Scenario() {
            {
                expect(joe).doNothing();
                willThrow(new IllegalArgumentException("hey!"));
                String instance = expect().toString();
                assertTrue(instance.contains(":throws(java.lang.IllegalArgumentException: hey!)"));
            }
        };

        new Stubs() {
            {
                stub(joe).doNothing();
                willThrow(new IllegalArgumentException("hey!"));
                String instance = stub().toString();
                assertTrue(instance.contains(":throws(java.lang.IllegalArgumentException: hey!)"));
            }
        };
    }

    /**
     * Verifies that a willThrow clause is always valid with runtime exceptions.
     *
     * <p>
     * The invoked method is declared to throw other types of exceptions.
     * </p>
     *
     * @throws SpecialDaltonException
     */
    @Test
    public void testWillThrowWithRuntimeExceptionWithThrowableMethod()
      throws SpecialDaltonException {
        new Scenario() {
            {
                expect(joe).bother();
                willThrow(new IllegalArgumentException("hey!"));
                String instance = expect().toString();
                assertTrue(instance.contains(":throws(java.lang.IllegalArgumentException: hey!)"));
            }
        };

        new Stubs() {
            {
                stub(joe).bother();
                willThrow(new IllegalArgumentException("hey!"));
                String instance = stub().toString();
                assertTrue(instance.contains(":throws(java.lang.IllegalArgumentException: hey!)"));
            }
        };
    }

    /**
     * Test of a valid willThrow clause using a declared exception.
     *
     * @throws SpecialDaltonException
     */
    @Test
    public void testWillThrowWithException() throws SpecialDaltonException {
        new Scenario() {
            {
                expect(joe).bother();
                willThrow(new Dalton.SpecialDaltonException());
                String instance = expect().toString();
                assertTrue(instance.contains(":throws(com.vmware.lmock.test.Dalton$SpecialDaltonException)"));
            }
        };

        new Stubs() {
            {
                stub(joe).bother();
                willThrow(new Dalton.SpecialDaltonException());
                String instance = stub().toString();
                assertTrue(instance.contains(":throws(com.vmware.lmock.test.Dalton$SpecialDaltonException)"));
            }
        };
    }

    /**
     * Verifies that a willThrow clause works with inherited exceptions.
     *
     * @throws SpecialDaltonException
     */
    @Test
    public void testWillThrowWithInheritedException()
      throws SpecialDaltonException {
        new Scenario() {
            {
                expect(joe).bother();
                willThrow(new SuperSpecialDaltonException("hands up!"));
                String instance = expect().toString();
                assertTrue(instance.contains("SuperSpecialDaltonException: hands up!)"));
            }
        };

        new Stubs() {
            {
                stub(joe).bother();
                willThrow(new SuperSpecialDaltonException("hands up!"));
                String instance = stub().toString();
                assertTrue(instance.contains("SuperSpecialDaltonException: hands up!)"));
            }
        };
    }

    /**
     * Verifies that willThrow doesn't work with incoherent exceptions.
     *
     * @throws SpecialDaltonException
     */
    @Test
    public void testWillThrowWithInvalidException()
      throws SpecialDaltonException {
        if (!SystemSpecifics.willCheckExceptionIntegrity()) {
            return;
        }

        new Scenario() {
            {
                try {
                    expect(joe).bother();
                    willThrow(new IOException());
                    fail("willThrow an incompatible exception");
                } catch (IncompatibleThrowableException e) {
                }
            }
        };

        new Stubs() {
            {
                try {
                    stub(joe).bother();
                    willThrow(new IOException());
                    fail("willThrow an incompatible exception");
                } catch (IncompatibleThrowableException e) {
                }
            }
        };
    }

    /**
     * Verifies that the willThrow clause does not confuse inheritance.
     *
     * <p>
     * This is pretty important to verify that willThrow correctly performs its
     * instance of.
     * </p>
     *
     * @throws SpecialDaltonException
     */
    @Test
    public void testWillThrowWithASubclassOfException()
      throws SpecialDaltonException {
        if (!SystemSpecifics.willCheckExceptionIntegrity()) {
            return;
        }

        new Scenario() {
            {
                try {
                    expect(joe).bother();
                    willThrow(new Exception());
                    fail("willThrow an incompatible exception");
                } catch (IncompatibleThrowableException e) {
                }
            }
        };

        new Stubs() {
            {
                try {
                    stub(joe).bother();
                    willThrow(new Exception());
                    fail("willThrow an incompatible exception");
                } catch (IncompatibleThrowableException e) {
                }
            }
        };
    }

    /**
     * Verifies that 'willThrow' is an equivalent of 'will(throwException)'.
     *
     * @throws SpecialDaltonException
     */
    @Test
    public void testWillThrowEqualsWillThrowException()
      throws SpecialDaltonException {
        new Scenario() {
            {
                expect(joe).bother();
                will(throwException(new SpecialDaltonException()));
                String instance = expect().toString();
                assertTrue(instance.contains(":throws(com.vmware.lmock.test.Dalton$SpecialDaltonException)"));
            }
        };

        new Stubs() {
            {
                stub(joe).bother();
                will(throwException(new SpecialDaltonException()));
                String instance = stub().toString();
                assertTrue(instance.contains(":throws(com.vmware.lmock.test.Dalton$SpecialDaltonException)"));
            }
        };
    }
}
