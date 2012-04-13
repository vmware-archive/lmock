/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.test;

import com.vmware.lmock.checker.ThreadChecker;
import com.vmware.lmock.exception.ThreadNotFoundException;
import com.vmware.lmock.mt.MTDispatcher;
import com.vmware.lmock.mt.ThreadMatcherNotificationHandler;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Validation of the multithread dispatcher.
 */
public class MTDispatcherTest {
    /** Use loops to stress the model. */
    private static final int TLOOP_LEN = 100000;

    /**
     * Keeps track of the thread matching process.
     */
    private class MatchingHandler implements ThreadMatcherNotificationHandler {
        private boolean matched = false;

        public void onMatchingThread(Thread thread) {
            matched = true;
        }

        /** @return <code>true</code> if the thread was matched. */
        boolean wasMatched() {
            return matched;
        }
    }

    /** An interface used to synchronized the different thread results. */
    private abstract class ThreadTest implements Runnable {
        /** The test result. */
        private boolean result;
        /** Some additional data if something goes wrong. */
        private String infoData = "";

        /** @return A test result. */
        boolean getResult() {
            return result;
        }

        /** @return Additional error data, if any, an empty string otherwise. */
        String getInfoData() {
            return infoData;
        }

        /**
         * Sends a notification to acknowledge the end of the test.
         *
         * @param result
         *            the test result
         */
        synchronized void endTest(boolean result) {
            this.result = result;
            notify();
        }

        /**
         * Sends a notification to acknowledge the end of the test.
         *
         * @param result
         *            the test result
         * @param infoData
         *            the additional data
         */
        synchronized void endTest(boolean result, String infoData) {
            this.infoData = infoData;
            endTest(result);
        }
    }

    /**
     * Waits for a thread to complete and verify that the result is ok.
     *
     * <p>
     * Uses a timeout to prevent from unexpected errors.
     * </p>
     *
     * @param test
     *            the thread test to synchronize on
     */
    private void assertThreadTestIsOk(ThreadTest test) {
        synchronized (test) {
            try {
                test.wait(5000);
            } catch (InterruptedException e) {
                fail(e.toString());
            }
        }
        assertTrue("thread '" + test + "' ok - data='"
          + test.getInfoData() + "'", test.getResult());
    }

    /**
     * Verifies that we get an exception if no thread checker was registered.
     */
    @Test
    public void testDispatcherWithNoThreadChecker() {
        MTDispatcher<String> instance = new MTDispatcher<String>();
        try {
            instance.getCurrentData();
            fail("found data with an empty matcher");
        } catch (ThreadNotFoundException e) {
        }
    }

    /**
     * Verifies that we get an exception if some checkers were registered but
     * none of them recognizes the current thread.
     */
    @Test
    public void testDispatcherNoCheckerMatching() {
        MTDispatcher<String> instance = new MTDispatcher<String>();
        instance.register(new ThreadChecker() {
            public boolean valueIsCompatibleWith(Thread value) {
                return false;
            }
        }, "ABC", null);

        try {
            instance.getCurrentData();
            fail("found data with no matching checker");
        } catch (ThreadNotFoundException e) {
        }
    }

    /**
     * Verifies that a thread is actually fetched with a simple case.
     */
    @Test
    public void testDispatcherMatchingOk() {
        MTDispatcher<String> instance = new MTDispatcher<String>();
        instance.register(ThreadChecker.anyThread, "a thread", null);
        assertEquals("a thread", instance.getCurrentData());
        // We should now get the same data, since the thread is registered.
        assertEquals("a thread", instance.getCurrentData());
    }

    /**
     * Verifies that once a checker is "consumed" it is not used anymore.
     */
    @Test
    public void testCheckerIsUsedOnlyOnce() {
        final MTDispatcher<String> instance = new MTDispatcher<String>();

        ThreadTest otherTest = new ThreadTest() {
            public void run() {
                boolean result = true;
                for (int i = 0; i < TLOOP_LEN && result; i++) {
                    result = "the other".equals(instance.getCurrentData());
                }
                endTest(result);
            }
        };

        Thread other = new Thread(otherTest);
        // Use the same checker twice, so that we are sure that they are
        // actually consumed.
        MatchingHandler[] handlers = {
            new MatchingHandler(), new MatchingHandler()
        };
        instance.register(ThreadChecker.anyThread, "me", handlers[0]);
        instance.register(ThreadChecker.anyThread, "the other", handlers[1]);
        other.start();

        for (int i = 0; i < TLOOP_LEN; i++) {
            assertEquals("me", instance.getCurrentData());
        }
        assertThreadTestIsOk(otherTest);
        assertTrue(handlers[0].wasMatched());
        assertTrue(handlers[1].wasMatched());
    }

    /**
     * Verifies that the dispatcher properly discriminates using several checkers.
     */
    @Test
    public void testCheckerDispatching() {
        final MTDispatcher<String> instance = new MTDispatcher<String>();

        ThreadTest otherTest = new ThreadTest() {
            public void run() {
                boolean result = true;
                for (int i = 0; i < TLOOP_LEN && result; i++) {
                    result = "the other".equals(instance.getCurrentData());
                }
                endTest(result);
            }
        };

        Thread other = new Thread(otherTest);
        // Use the same checker twice, so that we are sure that they are
        // actually consumed.
        // Use the same checker twice, so that we are sure that they are
        // actually consumed.
        MatchingHandler[] handlers = {
            new MatchingHandler(), new MatchingHandler()
        };
        instance.register(ThreadChecker.equalTo(Thread.currentThread()), "me",
          handlers[0]);
        instance.register(ThreadChecker.equalTo(other), "the other", handlers[1]);
        other.start();

        for (int i = 0; i < TLOOP_LEN; i++) {
            assertEquals("me", instance.getCurrentData());
        }
        assertThreadTestIsOk(otherTest);
        assertTrue(handlers[0].wasMatched());
        assertTrue(handlers[1].wasMatched());
    }

    /**
     * Validates a chain of threads.
     *
     * @throws InterruptedException
     */
    @Test
    public void testDispatcherWithThreadSequence() throws InterruptedException {
        final MTDispatcher<String> instance = new MTDispatcher<String>();

        ThreadTest test1 = new ThreadTest() {
            public void run() {
                boolean result = true;
                String currentData = "";
                for (int i = 0; i < TLOOP_LEN && result; i++) {
                    currentData = instance.getCurrentData();
                    result = "1".equals(currentData);
                }
                endTest(result, currentData);
            }
        };
        Thread thr1 = new Thread(test1);

        ThreadTest test2 = new ThreadTest() {
            public void run() {
                boolean result = true;
                String currentData = "";
                for (int i = 0; i < TLOOP_LEN && result; i++) {
                    currentData = instance.getCurrentData();
                    result = "2".equals(currentData);
                }
                endTest(result, currentData);
            }
        };
        Thread thr2 = new Thread(test2);

        instance.registerSequence("0", "1", "2");

        assertEquals("0", instance.getCurrentData());
        thr1.start();
        // It seems that under specific circumstances, the second thread
        // starts its activity before the first thread... A small delay
        // is enough to solve this...
        Thread.sleep(20);
        thr2.start();
        assertThreadTestIsOk(test1);
        assertThreadTestIsOk(test2);
    }
}
