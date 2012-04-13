/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.test;

import com.vmware.lmock.masquerade.Role;
import com.vmware.lmock.impl.StoryTrack;
import static com.vmware.lmock.masquerade.Schemer.*;
import static com.vmware.lmock.mt.Actor.*;
import static com.vmware.lmock.test.Messenger.*;
import org.junit.Test;
import static org.junit.Assert.*;
import static com.vmware.lmock.test.Dalton.*;
import static com.vmware.lmock.test.LMAsserts.*;

/**
 * Some tests to validate that what we provide to the user as the story track
 * looks like something meaningful.
 */
public class StoryTrackTest {
    /** An arbitrary delay in ms. to let the threads under test run. */
    private static final int A_DELAY = 1000;

    /**
     * Validates an empty track.
     */
    @Test
    public void testAnEmptyStoryTrack() {
        begin();
        end();
        assertEquals(0, StoryTrack.get().size());
        assertTrue(StoryTrack.get().toString().equals(""));
    }

    /**
     * Validates an empty track in a multi-threaded story.
     *
     * <p>The threads are not started</p>.
     */
    @Test
    public void testAnEmptyStoryTrackWithMultipleThreadsNotRunning() {
        Thread messenger1 = anEmptyMessenger();
        Thread messenger2 = anEmptyMessenger();
        Role[] roles = {
            new Role(anActorForThread(messenger1)),
            new Role(anActorForThread(messenger2))
        };
        begin(roles);
        end();

        assertEquals(0, StoryTrack.get().size());
        assertTrue(StoryTrack.get().toString().equals(""));
    }

    /**
     * Validates an empty track in a multi-threaded story.
     *
     * <p>The threads are started</p>.
     * @throws InterruptedException
     */
    @Test
    public void testAnEmptyStoryTrackWithMultipleThreadsRunning() throws
      InterruptedException {
        Messenger messenger1 = anEmptyMessenger();
        Messenger messenger2 = anEmptyMessenger();
        Role[] roles = {
            new Role(anActorForThread(messenger1)),
            new Role(anActorForThread(messenger2))
        };
        begin(roles);
        startMessengers(messenger1, messenger2);
        stopMessengers();
        end();

        assertEquals(0, StoryTrack.get().size());
        assertTrue(StoryTrack.get().toString().equals(""));
    }

    /**
     * Validates a track reporting a story including a single occurrence of
     * an invocation.
     */
    @Test
    public void testAStoryWithASingleOccurrence() {
        begin();
        willInvoke(1).of(joe).ping();
        joe.ping();
        end();

        String track = StoryTrack.get().toString();
        assertTrue(
          track.contains(
          "satisfied 1 time: interface com.vmware.lmock.test.Dalton.ping():void/[1..1]"));
        assertStringContains(track, "1 time from Thread\\[.*main");
    }

    /**
     * Validates a track reporting a story including a single occurrence of
     * an invocation executed by a separate thread.
     * @throws InterruptedException
     */
    @Test
    public void testAStoryWithSingleOccurrenceOnOneThread() throws
      InterruptedException {
        // Whatever the message is, ping joe.
        MessengerProcessor processor = new MessengerProcessor() {
            public void process(String message) {
                joe.ping();
            }
        };

        Messenger messenger = aMessenger(processor);
        Role role = new Role(anActorForThread(messenger));

        begin(role);
        role.willInvoke(1).of(joe).ping();
        startMessengers(messenger);
        messenger.post("please ping joe");
        Thread.sleep(A_DELAY);
        stopMessengers();
        end();

        String track = StoryTrack.get().toString();
        assertTrue(
          track.contains(
          "satisfied 1 time: interface com.vmware.lmock.test.Dalton.ping():void/[1..1]"));
        assertTrue(track.contains("1 time from Thread[" + messenger.getName()));
    }
}
