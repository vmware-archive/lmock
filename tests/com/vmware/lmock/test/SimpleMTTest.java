/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.test;

import com.vmware.lmock.checker.ThreadChecker;
import com.vmware.lmock.exception.EmptyRoleException;
import com.vmware.lmock.exception.SchemerException;
import com.vmware.lmock.masquerade.Role;
import com.vmware.lmock.exception.UnexpectedInvocationError;
import com.vmware.lmock.exception.UnsatisfiedOccurrenceError;
import static com.vmware.lmock.mt.Actor.*;
import static com.vmware.lmock.impl.Story.createWithMultipleActors;
import static com.vmware.lmock.impl.Story.create;
import static com.vmware.lmock.test.Dalton.*;
import com.vmware.lmock.impl.InvocationResultProvider;
import com.vmware.lmock.impl.Scenario;
import com.vmware.lmock.impl.Story;
import com.vmware.lmock.impl.Stubs;
import com.vmware.lmock.masquerade.Schemer;
import com.vmware.lmock.mt.Actor;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * A set of very simple tests with multiple threads.
 */
public class SimpleMTTest {
    /** A standard timeout to <code>thread.join</code>. */
    private static final int JOIN_TIMESOUT_AFTER = 500;

    /** A simple invocation counter. */
    private class InvocationCounter implements InvocationResultProvider {
        private int count = 0;

        public Object apply() throws Throwable {
            count++;
            return null;
        }

        /** @return The number of invocations up to now. */
        public int getCount() {
            return count;
        }
    }

    /**
     * Performs a set of assertion to check that a thread terminated.
     *
     * @param actor
     *            actor defining the requested thread
     */
    private void assertActorMatchesADeadThread(Actor actor) {
        assertTrue(actor.assertIsPresent());
        assertTrue(actor.assertNoError());
        assertFalse(actor.assertIsAlive());
        assertFalse(actor.assertIsInterrupted());
    }

    /**
     * Asserts that all the actors having a given role match a dead thread.
     *
     * @param actors
     *            an iterator to the checked actors
     */
    private void assertEveryActorMatchesADeadThread(Iterable<Actor> actors) {
        for (Actor actor : actors) {
            assertActorMatchesADeadThread(actor);
        }
    }

    /**
     * Asserts that all the actors having a given role match a dead thread.
     *
     * @param actors
     *            the checked actors
     */
    private void assertEveryActorMatchesADeadThread(Actor... actors) {
        for (Actor actor : actors) {
            assertActorMatchesADeadThread(actor);
        }
    }

    /**
     * Performs a set of assertion to check that a thread encountered an error.
     *
     * @param actor
     *            the actor defining the requested thread
     */
    private void assertActorMatchesAThreadInError(Actor actor) {
        assertTrue(actor.assertIsPresent());
        assertFalse(actor.assertNoError());
        assertFalse(actor.assertIsAlive());
        assertFalse(actor.assertIsInterrupted());
    }

    /**
     * Verifies that some actors have all encountered an error.
     *
     * @param actors
     *            an iterator to the checked actors
     */
    private void assertEveryActorMatchesAThreadInError(Iterable<Actor> actors) {
        for (Actor actor : actors) {
            assertActorMatchesAThreadInError(actor);
        }
    }

    /**
     * Verifies that we can create a story with an empty actor.
     */
    @Test
    public void testTestWithEmptyActor() {
        Actor actor = anActorForAnyThread();
        Story story = createWithMultipleActors(actor);
        story.begin();
        story.end();
    }

    /**
     * Runs a simple story that only involves the current thread.
     */
    @Test
    public void testWithAnActorForCurrentThread() {
        Scenario scenario = new Scenario() {
            {
                expect(joe).ping();
                occurs(1);
            }
        };

        Actor actor = anActorForCurrentThread();
        actor.following(scenario);
        Story story = createWithMultipleActors(actor);

        story.begin();
        joe.ping();
        story.end();
    }

    /**
     * Runs a simple story that involves the current thread.
     *
     * <p>
     * In this context, the goal is to overwrite the role defined for the
     * default thread and verify that it does not alter the test.
     * </p>
     */
    @Test
    public void testWithAnActorForCurrentThreadMA() {
        Role role = new Role(anActorForCurrentThread());
        Schemer.begin(role);
        role.willInvoke(1).of(joe).ping();
        joe.ping();
        Schemer.end();
    }

    /**
     * Runs a simple story that involves the current thread, defined as "any thread" (i.e. the first thread found in
     * the test).
     *
     * <p>
     * In this context, the goal is to overwrite the role defined for the
     * default thread and verify that it does not alter the test.
     * </p>
     */
    @Test
    public void testWithAnActorForAnyThreadMA() {
        Role role = Schemer.aRoleForAnyThread();
        Schemer.begin(role);
        role.willInvoke(1).of(joe).ping();
        joe.ping();
        Schemer.end();
    }

    /**
     * Runs a test with two threads successfully sharing the same scenario.
     *
     * @throws InterruptedException
     */
    @Test
    public void testTwoThreadsOkOnSameScenario() throws InterruptedException {
        final Thread thread2 = new Thread(new Runnable() {
            public void run() {
                jack.ping(joe);
            }
        });

        final Thread thread1 = new Thread(new Runnable() {
            public void run() {
                joe.ping(jack);
                thread2.start();
            }
        });

        Scenario scenario = new Scenario() {
            {
                expect(joe).ping(jack);
                occurs(1);
                expect(jack).ping(joe);
                occurs(1);
            }
        };

        Actor[] actors = {
            anActorForThread(thread1).following(scenario),
            anActorForThread(thread2).following(scenario)
        };

        Story story = createWithMultipleActors(actors);
        story.begin();
        thread1.start();
        thread1.join(JOIN_TIMESOUT_AFTER);
        thread2.join(JOIN_TIMESOUT_AFTER);
        // Validate the final state of the actors
        assertEveryActorMatchesADeadThread(actors);
        story.end();
    }

    /**
     * Runs a test with two threads successfully sharing the same scenario.
     *
     * @throws InterruptedException
     */
    @Test
    public void testTwoThreadsOkOnSameScenarioMA() throws InterruptedException {
        final Thread thread2 = new Thread(new Runnable() {
            public void run() {
                jack.ping(joe);
            }
        });

        final Thread thread1 = new Thread(new Runnable() {
            public void run() {
                joe.ping(jack);
                thread2.start();
            }
        });

        Role[] roles = {
            Schemer.aRoleForThread(thread1),
            Schemer.aRoleForThread(thread2)
        };
        roles[1].shareScenarioWith(roles[0]);

        Schemer.begin(roles);
        roles[0].willInvoke(1).of(joe).ping(jack);
        roles[1].willInvoke(1).of(jack).ping(joe);
        thread1.start();
        thread1.join(JOIN_TIMESOUT_AFTER);
        thread2.join(JOIN_TIMESOUT_AFTER);
        // Validate the final state of the actors
        for (Role role : roles) {
            assertEveryActorMatchesADeadThread(role);
        }
        Schemer.end();
    }

    /**
     * Runs a test with two threads successfully sharing the same scenario. One role is the default one.
     *
     * @throws InterruptedException
     */
    @Test
    public void testThreadAndDefaultRoleOkOnSameScenarioMA() throws InterruptedException {
        final Thread thread1 = new Thread(new Runnable() {
            public void run() {
                joe.ping(jack);
            }
        });

        Role role1 = Schemer.aRoleForThread(thread1);

        Schemer.begin(role1);
        role1.shareScenarioWith(Schemer.defaultRole());
        // Let the two threads invoke the same method, to avoid any timing issues (who will invoke the first).
        Schemer.willInvoke(2).of(joe).ping(jack);
        thread1.start();
        thread1.join(JOIN_TIMESOUT_AFTER);
        joe.ping(jack);
        // Validate the final state of the actors
        assertEveryActorMatchesADeadThread(role1);
        Schemer.end();
    }

    /**
     * Runs two tests sharing the same stubs.
     *
     * @throws InterruptedException
     */
    @Test
    public void testTwoThreadsOkOnSameStubs() throws InterruptedException {
        final InvocationCounter invocationCounter = new InvocationCounter();

        final Thread thread2 = new Thread(new Runnable() {
            public void run() {
                joe.fillPocket(1, 2, 3);
            }
        });

        final Thread thread1 = new Thread(new Runnable() {
            public void run() {
                joe.fillPocket(1, 2, 3);
            }
        });

        Stubs stubs = new Stubs() {
            {
                stub(joe).fillPocket(1, 2, 3);
                willDelegateTo(invocationCounter);
            }
        };

        Actor[] actors = {
            anActorForThread(thread1).using(stubs),
            anActorForThread(thread2).using(stubs)
        };

        Story story = createWithMultipleActors(actors);
        story.begin();
        thread1.start();
        thread2.start();
        thread1.join(JOIN_TIMESOUT_AFTER);
        thread2.join(JOIN_TIMESOUT_AFTER);
        assertEquals(2, invocationCounter.getCount());
        assertEveryActorMatchesADeadThread(actors);
        story.end();
    }

    /**
     * Runs two tests sharing the same stubs.
     *
     * @throws InterruptedException
     */
    @Test
    public void testTwoThreadsOkOnSameStubsMA() throws InterruptedException {
        final InvocationCounter invocationCounter = new InvocationCounter();

        final Thread thread2 = new Thread(new Runnable() {
            public void run() {
                joe.fillPocket(1, 2, 3);
            }
        });

        final Thread thread1 = new Thread(new Runnable() {
            public void run() {
                joe.fillPocket(1, 2, 3);
            }
        });

        Role[] roles = {
            Schemer.aRoleForThread(thread1),
            Schemer.aRoleForThread(thread2)
        };
        roles[0].shareStubsWith(roles[1]);

        Schemer.begin(roles);
        roles[0].willDelegateTo(invocationCounter).when(joe).fillPocket(1, 2, 3);
        thread1.start();
        thread2.start();
        thread1.join(JOIN_TIMESOUT_AFTER);
        thread2.join(JOIN_TIMESOUT_AFTER);
        assertEquals(2, invocationCounter.getCount());
        for (Role role : roles) {
            assertEveryActorMatchesADeadThread(role);
        }
        Schemer.end();
    }

    /**
     * Runs a story in which one thread does not respect the scenario.
     *
     * @throws InterruptedException
     */
    @Test
    public void testTwoThreadsWithTwoScenariosAndFailure() throws
      InterruptedException {
        final Thread threadOk = new Thread() {
            @Override
            public void run() {
                joe.fillPocket("data ok");
            }
        };

        threadOk.setName("test OK");

        final Thread threadNok = new Thread() {
            @Override
            public void run() {
                joe.fillPocket("data !ok");
            }
        };

        threadNok.setName("test !OK");

        Scenario scenarioOk = new Scenario() {
            {
                expect(joe).fillPocket("data ok");
                occurs(1);
            }
        };

        Scenario scenarioNok = new Scenario() {
            {
                expect(joe).fillPocket("data ok");
                occurs(1);
            }
        };

        Actor[] actors = {
            anActorForThread(threadOk).following(scenarioOk),
            anActorForThread(threadNok).following(scenarioNok)
        };

        Story story = createWithMultipleActors(actors);
        story.begin();
        threadOk.start();
        threadNok.start();
        threadOk.join(JOIN_TIMESOUT_AFTER);
        threadNok.join(JOIN_TIMESOUT_AFTER);
        assertActorMatchesADeadThread(actors[0]);
        assertActorMatchesAThreadInError(actors[1]);
        try {
            story.end();
            fail("successfully ended the story... missed error");
        } catch (UnsatisfiedOccurrenceError e) {
        }
    }

    /**
     * Runs a story in which one thread does not respect the scenario.
     *
     * @throws InterruptedException
     */
    @Test
    public void testTwoThreadsWithTwoScenariosAndFailureMA() throws
      InterruptedException {
        final Thread threadOk = new Thread() {
            @Override
            public void run() {
                joe.fillPocket("data ok");
            }
        };

        threadOk.setName("test OK");

        final Thread threadNok = new Thread() {
            @Override
            public void run() {
                joe.fillPocket("data !ok");
            }
        };

        threadNok.setName("test !OK");

        Role roleOk = Schemer.aRoleForThread(threadOk);
        Role roleNok = Schemer.aRoleForThread(threadNok);

        Schemer.begin(roleOk, roleNok);
        roleOk.willInvoke(1).of(joe).fillPocket("data ok");
        roleNok.willInvoke(1).of(joe).fillPocket("data ok");
        threadOk.start();
        threadNok.start();
        threadOk.join(JOIN_TIMESOUT_AFTER);
        threadNok.join(JOIN_TIMESOUT_AFTER);
        assertEveryActorMatchesADeadThread(roleOk);
        assertEveryActorMatchesAThreadInError(roleNok);
        try {
            Schemer.end();
            fail("successfully ended the story... missed error");
        } catch (UnsatisfiedOccurrenceError e) {
        }
    }

    /**
     * Verifies that when one thread fails, an error is propagated to other
     * threads
     *
     * @throws InterruptedException
     */
    @Test
    public void testTwoThreadsWithForeignException() throws
      InterruptedException {
        // Will use the main thread to perform regular tests...
        final Thread threadNok = new Thread() {
            @Override
            public void run() {
                joe.fillPocket("data !ok");
            }
        };

        threadNok.setName("test !OK");

        Scenario scenarioOk = new Scenario() {
            {
                expect(joe).fillPocket("data ok");
                occurs(1);
            }
        };

        Scenario scenarioNok = new Scenario() {
            {
                expect(joe).fillPocket("data ok");
                occurs(1);
            }
        };

        Actor[] actors = {
            anActorForCurrentThread().following(scenarioOk),
            anActorForThread(threadNok).following(scenarioNok)
        };

        Story story = create(actors);
        story.begin();
        threadNok.start();
        threadNok.join(JOIN_TIMESOUT_AFTER);
        try {
            joe.fillPocket("data ok");
            fail("should have received a foreign error exception");
        } catch (UnexpectedInvocationError e) {
        }
        assertActorMatchesAThreadInError(actors[1]);
        // When ending the story, I should receive the foreign exception.
        try {
            story.end();
            fail("did not received the foreign exception");
        } catch (UnsatisfiedOccurrenceError e) {
        }
    }

    /**
     * Verifies that when one thread fails, an error is propagated to other
     * threads
     *
     * @throws InterruptedException
     */
    @Test
    public void testTwoThreadsWithForeignExceptionMA() throws
      InterruptedException {
        // Will use the main thread to perform regular tests...
        final Thread threadNok = new Thread("test !OK") {
            @Override
            public void run() {
                joe.fillPocket("data !ok");
            }
        };

        Role roleOk = new Role(anActorForCurrentThread());
        Role roleNok = Schemer.aRoleForAThreadLike(ThreadChecker.threadsCalled("test !OK"));

        Schemer.begin(roleOk, roleNok);
        roleOk.willInvoke(1).of(joe).fillPocket("data ok");
        roleNok.willInvoke(1).of(joe).fillPocket("data ok");

        threadNok.start();
        threadNok.join(JOIN_TIMESOUT_AFTER);
        try {
            joe.fillPocket("data ok");
            fail("should have received a foreign error exception");
        } catch (UnexpectedInvocationError e) {
        }

        assertEveryActorMatchesAThreadInError(roleNok);
        // When ending the story, I should receive the foreign exception.
        try {
            Schemer.end();
            fail("did not received the foreign exception");
        } catch (UnsatisfiedOccurrenceError e) {
        }
    }

    /**
     * Can't append a scenario to default role if the masquerade has not begun.
     */
    @Test
    public void testCantAppendScenarioToDefaultRoleWhenNoMasquerade() {
        try {
            Schemer.append(new Scenario() {
                {
                }
            });
            fail("append a scenario, but no masquerade");
        } catch (SchemerException e) {
        }
    }

    /**
     * Can't append a scenario to any role if the masquerade has not begun.
     */
    @Test
    public void testCantAppendScenarioToRoleWhenNoMasquerade() {
        Role role = new Role();
        try {
            role.append(new Scenario() {
                {
                }
            });
            fail("append a scenario, but no masquerade");
        } catch (SchemerException e) {
        }
    }

    /**
     * Verifies that we can't append a scenario to a role that has no actor.
     */
    @Test
    public void testCantAppendScenarioToEmptyRole() {
        Role role = new Role();
        try {
            Schemer.begin(role);
            role.append(new Scenario() {
                {
                }
            });
            fail("append a scenario to a role with no actor");
        } catch (EmptyRoleException e) {
        } finally {
            Schemer.end();

        }
    }

    /**
     * Can't append stubs to default role if the masquerade has not begun.
     */
    @Test
    public void testCantAppendStubsToDefaultRoleWhenNoMasquerade() {
        try {
            Schemer.append(new Stubs() {
                {
                }
            });
            fail("append stubs, but no masquerade");
        } catch (SchemerException e) {
        }
    }

    /**
     * Can't append stubs to any role if the masquerade has not begun.
     */
    @Test
    public void testCantAppendStubsToRoleWhenNoMasquerade() {
        Role role = new Role();
        try {
            role.append(new Stubs() {
                {
                }
            });
            fail("append stubs, but no masquerade");
        } catch (SchemerException e) {
        }
    }

    /**
     * Verifies that we can't append stubs to a role that has no actor.
     */
    @Test
    public void testCantAppendStubsToEmptyRole() {
        Role role = new Role();
        try {
            Schemer.begin(role);
            role.append(new Stubs() {
                {
                }
            });
            fail("append stubs to a role with no actor");
        } catch (EmptyRoleException e) {
        } finally {
            Schemer.end();
        }
    }
}
