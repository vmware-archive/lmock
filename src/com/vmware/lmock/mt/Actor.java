/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.mt;

import com.vmware.lmock.checker.ThreadChecker;
import static com.vmware.lmock.checker.ThreadChecker.equalTo;
import com.vmware.lmock.clauses.HasActorClauses;
import com.vmware.lmock.impl.Scenario;
import com.vmware.lmock.impl.Stubs;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Inspect and control the activity of a thread.
 *
 * <p>
 * An actor is representing one execution flow under test by the mock system.
 * It is executed (associated to) one (and only one) thread.
 * </p>
 * <p>
 * An actor is specified the thread that will run it, using one of the following factory methods:
 * </p>
 * <ul>
 * <li><code>anActorForThread</code>: the actor is run by a well-known thread
 * </li>
 * <li><code>anActorForCurrentThread</code>: the actor is associated to the
 * current thread</li>
 * <li><code>anActorForThreadLike</code>: the actor is assigned to an unknown
 * thread, matching a specified thread checker</li>
 * </ul>
 *
 * <p>
 * Each actor may:
 * </p>
 * <ul>
 * <li>Follow a specific scenario (eventually shared with other actors),
 * specified with <code>following</code> clause</li>
 * <li>Rely on a specific set of threads, specified with the <code>using</code>
 * clause</li>
 * </ul>
 *
 * <p>
 * Users can follow the activity of the actors (the associated thread) using a
 * set of assertion clauses.
 * </p>
 */
public final class Actor implements HasActorClauses, ThreadMatcherNotificationHandler {
    /** Automatically incremented for each new actor, to assign a unique id. */
    private static long uidCount = 0L;
    /** A unique actor id, to have a comprehensive name. */
    private final long uid;
    private Scenario scenario = new Scenario();
    private List<Stubs> stubsList = new ArrayList<Stubs>();
    private final ThreadChecker checker;
    /** List of notification requests when updating the actor. */
    private final List<ActorUpdateNotification> notifications = new ArrayList<ActorUpdateNotification>();
    /**
     * The thread associated to this actor (<code>null</code> if no association
     * occurred).
     */
    private Thread thread;
    /**
     * Mock exception reported by the associated thread (<code>null</code> if
     * none.
     */
    private Throwable lastException;

    /**
     * Creates a new actor.
     *
     * @param checker
     *            the checker used to identify the thread
     */
    private Actor(ThreadChecker checker) {
        uid = uidCount++;
        this.checker = checker;
    }

    /**
     * Registers an internal routine that manages the updates of this actor.
     *
     * @param listener
     *            the new listener
     * @param arg
     *            provided as the user argument in the notification
     */
    public void willListenToUpdates(ActorUpdateListener listener, Object arg) {
        notifications.add(new ActorUpdateNotification(listener, arg));
    }

    /**
     * Creates an actor run by a specific thread.
     *
     * @param thread
     *            the thread running this actor
     * @return The new actor.
     */
    public static Actor anActorForThread(Thread thread) {
        return new Actor(equalTo(thread));
    }

    /**
     * Creates an actor run by the current thread.
     *
     * <p>
     * <b>In practice, you should not need to use this, since Lmock implicitly manages a default actor for
     * the testing thread.</b>
     * </p>
     *
     * @return The new actor.
     */
    public static Actor anActorForCurrentThread() {
        return anActorForThread(Thread.currentThread());
    }

    /**
     * Creates an actor run by a thread matching a given checker.
     *
     * <p>
     * This method must be used when the user does not know a priori the
     * assigned thread, but some of its properties, given by the checker. The
     * first unused thread matching the checker will be assigned to this actor.
     * </p>
     *
     * @param checker
     *            the thread checker providing the requested thread properties
     * @return The new actor.
     */
    public static Actor anActorForThreadLike(ThreadChecker checker) {
        return new Actor(checker);
    }

    /**
     * Creates an actor for the very first thread that will invoke a mock.
     *
     * <p>
     * This method must be used when the user does not know a priori the assigned thread, but does not care because
     * any thread would fit.
     * </p>
     *
     * @return The new actor.
     */
    public static Actor anActorForAnyThread() {
        return anActorForThreadLike(ThreadChecker.anyThread);
    }

    /**
     * Notifies an update listener that the scenario changed.
     */
    private void notifyThatActorChangedIfNeeded() {
        for (ActorUpdateNotification notification : notifications) {
            notification.sendNotification(this);
        }
    }

    public Actor following(Scenario scenario) {
        this.scenario = scenario;
        notifyThatActorChangedIfNeeded();
        return this;
    }

    public Actor using(Stubs... stubsList) {
        this.stubsList.clear();
        this.stubsList.addAll(Arrays.asList(stubsList));
        notifyThatActorChangedIfNeeded();
        return this;
    }

    /** @return The checker used to identify the associated thread. */
    public ThreadChecker getChecker() {
        return checker;
    }

    /** @return The scenario followed by the associated thread. */
    public Scenario getScenario() {
        return scenario;
    }

    /** @return The list of stubs used by the associated thread. */
    public Iterable<Stubs> getStubsList() {
        return new Iterable<Stubs>() {
            public Iterator<Stubs> iterator() {
                return stubsList.iterator();
            }
        };
    }

    @Override
    public void onMatchingThread(Thread thread) {
        this.thread = thread;
    }

    /**
     * Records an exception or error raised by the associated thread.
     *
     * @param excpt
     *            the exception or error
     */
    public void setLastException(Throwable excpt) {
        this.lastException = excpt;
    }

    /**
     * Verifies that this actor is associated to a thread.
     *
     * @return <code>false</code> if the expected thread never seemed to have
     *  an activity in the mocking system.
     */
    public boolean assertIsPresent() {
        return thread != null;
    }

    /** @return <code>true</code> if the thread is present and active. */
    public boolean assertIsAlive() {
        return assertIsPresent() && thread.isAlive();
    }

    /** @return <code>true</code> if the thread is present and interrupted. */
    public boolean assertIsInterrupted() {
        return assertIsPresent() && thread.isInterrupted();
    }

    /**
     * @return <code>true</code> if no error was reported by mocks while the
     *   associated thread was running.
     */
    public boolean assertNoError() {
        return lastException == null;
    }

    @Override
    public String toString() {
        return "Actor$" + String.valueOf(uid);
    }
}
