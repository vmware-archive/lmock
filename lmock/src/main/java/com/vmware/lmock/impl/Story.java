/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.impl;

import com.vmware.lmock.clauses.HasAppendClauses;
import com.vmware.lmock.mt.Actor;
import static com.vmware.lmock.impl.MockInvocationDispatcher.aDispatcherForMultipleActors;
import static com.vmware.lmock.impl.MockInvocationDispatcher.aDispatcherForSingleActor;

/**
 * Verifies the compliance of the test with a scenario.
 *
 * <p>
 * A story, associated to an expectation list representing a scenario, traps the
 * invocations to mocks in order to validate the test execution regarding the
 * scenario.
 * </p>
 *
 * <p>
 * For the user standpoint, the story will start when calling <code>begin</code>
 * and finish when calling <code>end</code>. The <code>end</code> method
 * verifies that all the stages of the scenario have been completed.
 * </p>
 *
 * <p>
 * <b>IMPORTANT:</b> never run several stories simultaneously.
 * </p>
 *
 * <p>
 * A story can operate in two modes:
 * </p>
 * <ul>
 * <li>Default, mono-threaded: the story provides a default execution for the
 * thread that controls the test (the <i>testing thread</i>).
 * Such a story should be created by providing an initial scenario
 * and set of stubs (via the <code>create</code> static method).</li>
 * <li>Multi-threaded: the story is created for a number of actors (via
 * <code>create(Actors...)</code>). The story also creates a default actor for the calling
 * thread, so that the <b>test does not have to manage an actor for the testing
 * thread</b>. The corresponding actor is given by <code>getDefaultActor</code>.
 * </li>
 * </ul>
 */
public final class Story implements HasAppendClauses {

    /** Logs the story activity. */
    private static final Logger logger = Logger.get(Story.class);
    /** Dispatches the invocations to mocks when running the test. */
    private MockInvocationDispatcher dispatcher;
    /** Default for configuration for the thread controlling the test. */
    private final StoryDefaults defaults;

    /**
     * Creates a story associated to a scenario and stubs.
     *
     * @param scenario
     *            the scenario to comply with
     * @param stubsList
     *            a set of stubs used when running this story
     * @return The resulting story.
     */
    public static Story create(Scenario scenario, Stubs... stubsList) {
        logger.trace("create", "scenario=", scenario, "stubsList=", stubsList);
        if (scenario != null) {
            return new Story(scenario, stubsList);
        } else {
            return new Story(new Scenario(), stubsList);
        }
    }

    /**
     * Creates a story associated to several actors.
     *
     * <p>
     * Notice that this constructor implicitly creates an actor for the
     * invoking thread, denoted as the <i>default</i> actor.
     * </p>
     *
     * @param actors
     *            the list of actors contributing to this story
     * @return The resulting story.
     */
    public static Story createWithMultipleActors(Actor... actors) {
        logger.trace("createWithMultipleActors", "actors=", actors);
        if (actors == null || actors.length == 0) {
            return new Story(new Scenario(), new Stubs());
        } else {
            return new Story(actors);
        }
    }

    /**
     * An alias of <code>createWithMultipleActors</code>.
     *
     * @param actors
     *            the list of actors contributing to this story
     * @return The resulting story.
     */
    public static Story create(Actor... actors) {
        return createWithMultipleActors(actors);
    }

    /**
     * Creates a new story, to validate a scenario.
     *
     * @param scenario
     *            the scenario this story should comply with
     * @param stubsList
     *            a set of stubs used by this story
     */
    private Story(Scenario scenario, Stubs... stubsList) {
        defaults = new StoryDefaults(scenario, stubsList);
        dispatcher = aDispatcherForSingleActor(defaults.getActor());
    }

    /**
     * Searches for an actor for current thread in a list of actors.
     *
     * @param actors
     *            the actors list (non null)
     * @return The fetched actor, if found, <code>null</code> otherwise.
     */
    private Actor searchForAnActorForCurrentThread(Actor... actors) {
        logger.trace("searchForAnActorForCurrentThread", "actors=", actors);
        for (Actor actor : actors) {
            if (actor.getChecker().valueIsCompatibleWith(Thread.currentThread())) {
                logger.trace("searchForAnActorForCurrentThread", "found actor", actor);
                return actor;
            }
        }

        logger.trace("searchForAnActorForCurrentThread", "no actor found");
        return null;
    }

    /**
     * Re-creates an array from an array, removing a given item.
     *
     * <p>
     * Must be invoked if and only if the specified item is actually in the
     * list.
     * </p>
     *
     * @param removed
     *            the removed item
     * @param array
     *            the processed array
     * @return The new array.
     */
    private static Actor[] excludeFromList(Actor removed, Actor[] array) {
        Actor[] result = new Actor[array.length - 1];
        int index = 0;
        for (Actor item : array) {
            if (!removed.equals(item)) {
                result[index++] = item;
            }
        }
        return result;
    }

    /**
     * Creates a new story to validate the scenarios of different actors.
     *
     * @param actors
     *            the actors participating in this story
     */
    private Story(Actor... actors) {
        Actor defaultActor = searchForAnActorForCurrentThread(actors);
        if (defaultActor == null) {
            defaults = new StoryDefaults();
            dispatcher = aDispatcherForMultipleActors(defaults.getActor(), actors);
        } else {
            Actor[] listWithExcludedDefaultActor = excludeFromList(defaultActor, actors);
            defaults = new StoryDefaults(defaultActor);
            dispatcher = aDispatcherForMultipleActors(defaults.getActor(),
              listWithExcludedDefaultActor);
        }
    }

    /**
     * Begins the story.
     */
    public void begin() {
        logger.trace("begin");
        // The story track is outdated.
        StoryTrack.get().clearTrackers();
        dispatcher.begin();
    }

    /**
     * Ends the story.
     *
     * <p>
     * Verifies that all the mandatory expectations were satisfied.
     * </p>
     * <p>
     * In case of failure, throws an exception (e.g.
     * <code>UnsatisfiedOccurrenceException</code>).
     * </p>
     */
    public void end() {
        logger.trace("end");
        dispatcher.end();
    }

    /**
     * Adds a bunch of expectations into the ongoing story.
     *
     * <p>
     * The scenario is added to a default actor, assigned to the current
     * thread. Thus it does not work in multi-threaded mode.
     * </p>
     *
     * @param actor
     *            the actor for which we want to modify the scenario (and
     *            any actor sharing that scenario)
     * @param scenario
     *            the scenario providing the list of new expectations
     */
    public void append(Actor actor, Scenario scenario) {
        logger.trace("append", "actor=", actor, "scenario=", scenario);
        dispatcher.append(actor, scenario);
    }

    @Override
    public void append(Scenario scenario) {
        logger.trace("append", "scenario=", scenario);
        append(defaults.getActor(), scenario);
    }

    /**
     * Adds a bunch of stubs into the ongoing story.
     *
     * <p>
     * The stubs are added to a default actor, assigned to the current
     * thread. Thus it does not work in multi-threaded mode.
     * </p>
     *
     * @param actor
     *            the actor for which we add stubs (and all actors sharing the
     *            same stub set)
     * @param stubs
     *            the list of new stubs
     */
    public void append(Actor actor, Stubs stubs) {
        logger.trace("append", "actor=", actor, "stubs=", stubs);
        dispatcher.append(actor, stubs);
    }

    @Override
    public void append(Stubs stubs) {
        logger.trace("append", "stubs=", stubs);
        append(defaults.getActor(), stubs);
    }
}
