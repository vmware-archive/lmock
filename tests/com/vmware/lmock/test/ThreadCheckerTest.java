/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.test;

import com.vmware.lmock.checker.ThreadChecker;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Validates the thread checkers.
 */
public class ThreadCheckerTest {
    /** A dummy interface used to validate implementations. */
    private interface TestInterface {
        public void ping();
    }

    /** A class overloading <code>Thread</code>. */
    private class TestThread extends Thread implements TestInterface {
        public void ping() {
        }
    };

    /** Verifies that a thread checker claims to handle the proper class. */
    @Test
    public void testGetRelatedClass() {
        assertEquals(Thread.class, ThreadChecker.anyThread.getRelatedClass());
    }

    /**
     * Verifies that any thread actually accepts threads (at least the current
     * one).
     */
    @Test
    public void testAnyThread() {
        ThreadChecker instance = ThreadChecker.anyThread;
        assertTrue(instance.valueIsCompatibleWith(Thread.currentThread()));
    }

    /**
     * Validates the "equalTo" thread checker.
     */
    @Test
    public void testEqualTo() {
        Thread thread = new Thread();
        ThreadChecker instance = ThreadChecker.equalTo(thread);
        assertFalse(instance.valueIsCompatibleWith(Thread.currentThread()));
        assertTrue(instance.valueIsCompatibleWith(thread));
    }

    /**
     * Validates the checker referencing names.
     */
    @Test
    public void testThreadsCalled() {
        Thread thread = new Thread("thrx");
        ThreadChecker instance = ThreadChecker.threadsCalled("thrx");
        assertFalse(instance.valueIsCompatibleWith(Thread.currentThread()));
        assertTrue(instance.valueIsCompatibleWith(thread));
    }

    /**
     * Validates the checker referencing instances.
     */
    @Test
    public void testInstancesOf() {
        TestThread thread1 = new TestThread(), thread2 = new TestThread();
        ThreadChecker instance = ThreadChecker.instancesOf(TestThread.class);
        assertFalse(instance.valueIsCompatibleWith(Thread.currentThread()));
        assertTrue(instance.valueIsCompatibleWith(thread1));
        assertTrue(instance.valueIsCompatibleWith(thread2));
    }

    /**
     * Validates the checker referencing instances of interfaces.
     */
    @Test
    public void testInstancesOfInterface() {
        TestThread thread1 = new TestThread();
        ThreadChecker instance = ThreadChecker.instancesOf(TestInterface.class);
        assertTrue(instance.valueIsCompatibleWith(thread1));
    }
}
