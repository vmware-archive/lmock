/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.impl;

import com.vmware.lmock.mt.Actor;
import static com.vmware.lmock.mt.Actor.anActorForCurrentThread;

/**
 * A default execution context for the stories.
 *
 * <p>
 * Such an environment basically consists in a default actor, which can be
 * either created (as an actor for the current thread) or forced to be a given
 * actor.
 * </p>
 */
final class StoryDefaults {
    private Actor actor;

    /**
     * @return A default empty scenario.
     */
    private static Scenario aDefaultScenario() {
        return new Scenario();
    }

    /**
     * @return A default empty stub set.
     */
    private static Stubs[] aDefaultStubSet() {
        Stubs[] newSet = new Stubs[1];
        newSet[0] = new Stubs();
        return newSet;
    }

    /**
     * Checks the validity of a stub set to be assigned as defaults.
     *
     * @param stubs
     *            the checked set of stubs
     * @return <code>true</code> if a set of stubs is invalid for default.
     */
    private static boolean invalidStubSet(Stubs... stubs) {
        return stubs == null || stubs.length <= 0;
    }

    /**
     * Creates a new default environment.
     *
     * @param scenario
     *            the scenario that will be considered as the default one
     *            (<code>null</code> to create an empty one)
     * @param stubs
     *            the default stub set that will be considered as the default
     *            one (<code>null</code> to create an empty set)
     */
    StoryDefaults(Scenario scenario, Stubs... stubs) {
        Scenario theScenario = (scenario == null) ? aDefaultScenario()
          : scenario;
        Stubs[] theStubs = invalidStubSet(stubs) ? aDefaultStubSet() : stubs;
        actor = anActorForCurrentThread().following(theScenario).using(theStubs);
    }

    /**
     * Creates a new default environment with an empty scenario and an empty
     * stub set.
     */
    StoryDefaults() {
        actor = anActorForCurrentThread().following(aDefaultScenario()).using(
          aDefaultStubSet());
    }

    /**
     * Creates a new default environment with a given actor as the default one.
     *
     * @param actor
     *            the default actor
     */
    StoryDefaults(Actor actor) {
        this.actor = actor;
    }

    /** @return The actor playing the "main" thread. */
    Actor getActor() {
        return actor;
    }
}
