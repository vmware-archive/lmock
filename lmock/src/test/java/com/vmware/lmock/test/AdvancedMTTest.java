/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.test;

import com.vmware.lmock.exception.UnsatisfiedOccurrenceError;
import com.vmware.lmock.exception.ExpectationError;
import com.vmware.lmock.exception.UnexpectedInvocationError;
import com.vmware.lmock.masquerade.Role;
import org.junit.Test;
import static com.vmware.lmock.masquerade.Schemer.*;
import static com.vmware.lmock.mt.Actor.*;
import static com.vmware.lmock.test.Messenger.*;
import static com.vmware.lmock.test.Dalton.*;
import static org.junit.Assert.*;

/**
 * A set of advanced tests with multiple threads.
 *
 * <p>
 * The purpose of those tests is to stress the multi-threaded model. As a
 * consequence, there is no real focus on the tests here-in, just a collection
 * of ideas and situations implying multiple threads.
 * </p>
 */
public class AdvancedMTTest {
    /**
     * An activity performed on a specific Dalton in a multi-threaded environment.
     *
     *
     * <p>
     * The goal of this class is to define a pretty standard scenario for tests,
     * basically consisting in forwarding a message to a given Dalton and passing
     * the hand to a buddy. Thus, an activity is defined by:
     * </p>
     * <ul>
     * <li>A Dalton, on which we perform operations</li>
     * <li>An optional buddy activity, that will get the hand after the processing
     * is done</li>
     * <li>A message, sent to the Dalton and the buddy</li>
     * </ul>
     *
     * <p>
     * The processing stage for one Dalton is always the same: use <code>setObject</code>
     * to provide the message, use <code>getBoolean</code> to check whether we
     * actually pass the hand to the buddy or not.
     * </p>
     */
    private class DaltonActivity extends Messenger {
        /**
         * Creates a new living thing representing a specific Dalton.
         *
         * @param dalton
         *            the Dalton to perform operations
         * @param text
         *            message used for the processing
         * @param buddy
         *            if not <code>null</code>, next activity in the processing chain
         */
        DaltonActivity(final Dalton dalton, final String text,
          final DaltonActivity buddy) {
            super(new MessengerProcessor() {
                public void process(String message) {
                    dalton.setObject(message);
                    if (dalton.getBoolean() && buddy != null) {
                        buddy.post(text);
                    }
                }
            });
            this.setName(dalton.toString());
        }

        /**
         * Creates a new living thing with no buddy.
         *
         * @param dalton
         *            the Dalton to perform operations
         */
        DaltonActivity(final Dalton dalton) {
            this(dalton, null, null);
        }
    };
    private DaltonActivity joeActivity, jackActivity, williamActivity, averellActivity;
    private Role _joe, _jack, _william, _averell;
    /** A common role shared by different actors. */
    private Role commonRole;
    /** Let the testing thread sleep to let the tests complete. */
    private static final int SLEEP_TIME = 1000;

    /**
     * Creates a set of activities and roles where each Dalton talks to his
     * brother ones, but Averell.
     */
    private void createSomeTalkativeBrothers() {
        averellActivity = new DaltonActivity(averell);
        _averell = aRoleForThread(averellActivity);
        williamActivity = new DaltonActivity(william, "a message to Averell",
          averellActivity);
        _william = aRoleForThread(williamActivity);
        jackActivity = new DaltonActivity(jack, "a message to William",
          williamActivity);
        _jack = aRoleForThread(jackActivity);
        joeActivity = new DaltonActivity(joe, "a message to Jack", jackActivity);
        _joe = aRoleForThread(joeActivity);
    }

    /**
     * Creates a set of activities that will all contribute to the same role.
     */
    private void createSomeTalkativeBrothersSharingCommonRole() {
        averellActivity = new DaltonActivity(averell);
        williamActivity = new DaltonActivity(william, "a message to Averell",
          averellActivity);
        jackActivity = new DaltonActivity(jack, "a message to William",
          williamActivity);
        joeActivity = new DaltonActivity(joe, "a message to Jack", jackActivity);
        commonRole = new Role(anActorForThread(joeActivity), anActorForThread(
          jackActivity), anActorForThread(williamActivity), anActorForThread(
          averellActivity));
    }

    /**
     * A Dalton is expected to receive a message and tell what to do next.
     *
     * @param role
     *            the role implementing the given Dalton
     * @param who
     *            the Dalton
     * @param message
     *            the expected message
     * @param next
     *            what to do next (<code>true</code> send a message to the
     *            next buddy)
     */
    private void willReceiveMessage(Role role, Dalton who, String message, boolean next) {
        role.willInvoke(1).of(who).setObject(message);
        role.willInvoke(1).willReturn(next).when(who).getBoolean();
    }

    /**
     * Unexpected invocation within an empty scenario.
     *
     * <p>
     * The purpose of this test is to validate a specific corner case: when
     * no scenario is defined at all, the test execution should issue an
     * exception trapped by a given thread. We must verify that this error
     * is actually guarded and reported when ending the story.
     * </p>
     *
     * @throws InterruptedException
     */
    @Test
    public void testEmptyScenarioAndMultipleThreadsWithUnexpectedInvocation()
      throws InterruptedException {
        createSomeTalkativeBrothers();
        begin(_joe, _jack, _william, _averell);
        startMessengers(averellActivity, williamActivity, jackActivity,
          joeActivity);
        joeActivity.post("a message to Joe");
        Thread.sleep(SLEEP_TIME);
        stopMessengers();
        try {
            end();
            fail(
              "successfully completed the story, but an unexpected invocation should have occurred");
        } catch (ExpectationError e) {
        }
    }

    /**
     * Multiple threads contribute to independent scenarios.
     *
     * <p>
     * This is the regular use case, leading to a success.
     * </p>
     *
     * @throws InterruptedException
     */
    @Test
    public void testMutipleScenariosWithMultipleThreads() throws InterruptedException {
        createSomeTalkativeBrothers();
        begin(_joe, _jack, _william, _averell);
        startMessengers(averellActivity, williamActivity, jackActivity,
          joeActivity);
        // The order doesn't matter, since every thread plays its own role.
        willReceiveMessage(_averell, averell, "a message to Averell", false);
        willReceiveMessage(_william, william, "a message to William", true);
        willReceiveMessage(_jack, jack, "a message to Jack", true);
        willReceiveMessage(_joe, joe, "a message to Joe", true);
        joeActivity.post("a message to Joe");
        Thread.sleep(SLEEP_TIME);
        stopMessengers();
        end();
    }

    /**
     * Test with multiple threads and an unexpected invocation.
     * @throws InterruptedException
     */
    @Test
    public void testMutlipleScenariosWithUnexpectedInvocation() throws InterruptedException {
        createSomeTalkativeBrothers();
        begin(_joe, _jack, _william, _averell);
        startMessengers(averellActivity, williamActivity, jackActivity,
          joeActivity);
        willReceiveMessage(_joe, joe, "a message to Joe", true);
        willReceiveMessage(_jack, jack, "a message to Jack", true);
        willReceiveMessage(_william, william, "a message to William", true);
        joeActivity.post("a message to Joe");
        Thread.sleep(SLEEP_TIME);
        stopMessengers();
        try {
            end();
            fail("was expecting an unexpected invocation");
        } catch (UnexpectedInvocationError e) {
        }
    }

    /**
     * Multiple threads contribute to the same scenario, which is not completed.
     * @throws InterruptedException
     */
    @Test
    public void testMultipleScenariosWithIncompeteStory() throws InterruptedException {
        createSomeTalkativeBrothers();
        begin(_joe, _jack, _william, _averell);
        startMessengers(averellActivity, williamActivity, jackActivity,
          joeActivity);
        willReceiveMessage(_joe, joe, "a message to Joe", true);
        willReceiveMessage(_jack, jack, "a message to Jack", true);
        willReceiveMessage(_william, william, "a message to William", true);
        willReceiveMessage(_averell, averell, "a message to Averell", false);
        // This expectation is not scheduled.
        willReceiveMessage(_joe, joe, "Another message to Jack", true);
        joeActivity.post("a message to Joe");
        Thread.sleep(SLEEP_TIME);
        stopMessengers();
        try {
            end();
            fail(
              "successfully completed the story, but an unexpected invocation should have occurred");
        } catch (UnsatisfiedOccurrenceError e) {
        }
    }

    /**
     * Multiple threads contribute to independent scenarios.
     *
     * <p>
     * This is the regular use case, leading to a success.
     * </p>
     *
     * @throws InterruptedException
     */
    @Test
    public void testSingleScenarioWithMultipleThreads() throws InterruptedException {
        createSomeTalkativeBrothersSharingCommonRole();
        begin(commonRole);
        startMessengers(averellActivity, williamActivity, jackActivity,
          joeActivity);
        willReceiveMessage(commonRole, joe, "a message to Joe", true);
        willReceiveMessage(commonRole, jack, "a message to Jack", true);
        willReceiveMessage(commonRole, william, "a message to William", true);
        willReceiveMessage(commonRole, averell, "a message to Averell", false);
        joeActivity.post("a message to Joe");
        Thread.sleep(SLEEP_TIME);
        stopMessengers();
        end();
    }

    /**
     * Several threads contribute to the same scenario, but an error occurs.
     * @throws InterruptedException
     */
    @Test
    public void testSingleScenarioWithUnexpectedInvocation() throws InterruptedException {
        createSomeTalkativeBrothersSharingCommonRole();
        begin(commonRole);
        startMessengers(averellActivity, williamActivity, jackActivity,
          joeActivity);
        willReceiveMessage(commonRole, joe, "a message to Joe", true);
        willReceiveMessage(commonRole, jack, "a message to Jack", true);
        willReceiveMessage(commonRole, william, "a message to William", true);
        // Will not happen:
        willReceiveMessage(commonRole, averell, "a message to Somebody", false);
        joeActivity.post("a message to Joe");
        Thread.sleep(SLEEP_TIME);
        stopMessengers();
        try {
            end();
            fail(
              "successfully completed the story, but an unexpected invocation should have occurred");
        } catch (UnsatisfiedOccurrenceError e) {
        }
    }

    /**
     * Rely on a single set of stubs to do the job.
     * @throws InterruptedException
     */
    @Test
    public void testSingleStubSetWithMultipleThreads() throws InterruptedException {
        createSomeTalkativeBrothersSharingCommonRole();
        begin(commonRole);
        startMessengers(averellActivity, williamActivity, jackActivity,
          joeActivity);
        commonRole.willReturn(null).when(joe).setObject(anyOf(String.class));
        commonRole.willReturn(true).when(joe).getBoolean();
        commonRole.willReturn(null).when(jack).setObject(anyOf(String.class));
        commonRole.willReturn(true).when(jack).getBoolean();
        commonRole.willReturn(null).when(william).setObject(anyOf(String.class));
        commonRole.willReturn(true).when(william).getBoolean();
        commonRole.willReturn(null).when(averell).setObject(anyOf(String.class));
        commonRole.willReturn(true).when(averell).getBoolean();

        for (int iter = 0; iter < 10000; iter++) {
            joeActivity.post("a message to joe");
        }
        Thread.sleep(SLEEP_TIME * 10);
        stopMessengers();
        end();
    }
}
