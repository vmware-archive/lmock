/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.masquerade;

import com.vmware.lmock.checker.Checker;
import com.vmware.lmock.checker.OccurrenceChecker;
import com.vmware.lmock.clauses.HasAppendClauses;
import com.vmware.lmock.clauses.HasArgumentSpecificationClauses;
import com.vmware.lmock.clauses.HasDirectiveClauses;
import com.vmware.lmock.clauses.InnerSchemerFactoryClauses.HasExpectationClauses;
import com.vmware.lmock.clauses.InnerSchemerFactoryClauses.HasWhenClause;
import com.vmware.lmock.exception.EmptyRoleException;
import com.vmware.lmock.exception.SchemerException;
import com.vmware.lmock.impl.InvocationResultProvider;
import com.vmware.lmock.impl.Scenario;
import com.vmware.lmock.impl.Stubs;
import com.vmware.lmock.mt.Actor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * A specific role played by one or several actors during a masquerade.
 *
 * <p>
 * A role represents a scenario and a set of stubs built when specifying the
 * masquerade. It can be associated to one or several actors (that will thus
 * share both the same story and the same set of stubs). Those actors can
 * be specified at construction time.
 *
 * <p>
 * The associated scenario and stubs are built on the fly by invoking the
 * directives used to create masquerades:
 * </p>
 * <ul>
 * <li><code>willInvoke(O).of(M).I</code>: expects that the invocation
 * <code>M.I</code> occurs according to the occurrences scheme defined by
 * <code>O</code></li>
 * <li><code>willXXX(V).when(M).I</code>: specifies that the result of any
 * invocation of <code>M.I</code> is <code>V</code> (<code>XXX</code> is return
 * or throw)</li>
 * <li><code>willInvoke(O).willXXX(V).when(M).I</code>: expects that the invocation
 * of <code>M.I</code> occurs according to the occurrences scheme defined by
 * <code>O</code> and that the result is <code>V</code> (<code>XXX</code> is
 * return or throw)</li>
 * </ul>
 *
 * <p>
 * By default, a role builds its own set of expectations and stubs. But it may
 * share one or the other with another role, using
 * <code>shareScenarioWith</code> and <code>shareStubsWith</code>.
 * </p>
 * <p>
 * Users may also use the <code>append</code> clauses to concatenate pre-defined
 * scenarios or sets of stubs.
 * </p>
 *
 * <p>
 * <b>Important</b>: notice that a role will not be allowed to create new
 * directives until it is registered to a masquerade (via a <code>begin</code>
 * directive).
 * </p>
 */
public class Role implements HasDirectiveClauses, HasArgumentSpecificationClauses, Iterable<Actor> {
    /** Factory used to build and register new directives. */
    private final SchemerFactory factory;
    /** A set of actors referencing this role. */
    private final List<Actor> actors = new ArrayList<Actor>();
    /** The scenario built by this. */
    private Scenario scenario = new Scenario();
    /** The set of stubs built by this. */
    private Stubs stubs = new Stubs();
    /** A story manager, that will register the new invocation checkers. */
    private StoryManager storyManager;
    /** A controller that actually builds a scenario and stubs for this role. */
    private HasAppendClauses controller = new HasAppendClauses() {
        /** Throws an exception if we have no story manager yet (i.e. masquerade not started). */
        private void validateStoryManagerOrThrow() {
            if (storyManager == null) {
                throw new SchemerException("no story ongoing now");
            }
        }

        public void append(Scenario scenario) {
            // At this level, we are sure that the story manager is valid.
            // No need to make additional checking.
            validateStoryManagerOrThrow();
            storyManager.getStory().append(anActorInThisRole(), scenario);
        }

        public void append(Stubs stubs) {
            validateStoryManagerOrThrow();
            storyManager.getStory().append(anActorInThisRole(), stubs);
        }
    };

    /**
     * Creates a new role.
     *
     * <p>
     * By default, the role is building its own scenario and set of stubs and
     * can be immediately associated to a set of actors.
     * </p>
     *
     * @param actors
     *            an initial set of actors following the role
     */
    public Role(Actor... actors) {
        factory = new SchemerFactory(controller);
        registerActors(actors);
        associateActorsToThis();
    }

    /**
     * Links a story manager to this role.
     *
     * <p>
     * This story manager is used to register new invocation checkers while
     * running the story.
     * </p>
     *
     * @param storyManager
     *            the registered story manager
     */
    protected void registerStoryManager(StoryManager storyManager) {
        factory.cleanup();
        this.storyManager = storyManager;
    }

    /**
     * Unregisters the story manager previously set by
     * <code>registerStoryManager</code>.
     */
    protected void unregisterStoryManager() {
        factory.cleanup();
        this.storyManager = null;
    }

    /**
     * @return An arbitrary actor playing this role.
     */
    private Actor anActorInThisRole() {
        if (actors.isEmpty()) {
            throw new EmptyRoleException();
        } else {
            return actors.get(0);
        }
    }

    /**
     * Creates the list of actors associated to this.
     *
     * @param actorList
     *            the list of actors
     */
    private void registerActors(Actor... actorList) {
        actors.addAll(Arrays.asList(actorList));
    }

    /**
     * Associates a set of actors to this, so that they refer the scenario
     * and stubs constructed by this.
     */
    private void associateActorsToThis() {
        for (Actor actor : actors) {
            actor.following(scenario);
            actor.using(stubs);
        }
    }

    /**
     * Shares a scenario with another role, so that both build the same set
     * of expectations.
     *
     * @param other
     *            the role to cooperate with
     */
    public void shareScenarioWith(Role other) {
        this.scenario = other.getScenario();
        // Don't forget to re-focus the actors.
        associateActorsToThis();
    }

    /**
     * Shares the set of stubs with another role, so that both build the same
     * set.
     *
     * @param other
     *            the role to cooperate with
     */
    public void shareStubsWith(Role other) {
        this.stubs = other.getStubs();
        // Don't forget to re-focus the actors
        associateActorsToThis();
    }

    /** @return The scenario associated to this role. */
    Scenario getScenario() {
        return scenario;
    }

    /** @return The stubs associated to this role. */
    Stubs getStubs() {
        return stubs;
    }

    /**
     * Verifies that the user can use a factory method (i.e. there's a story
     * ongoing) and throws an exception if not.
     *
     * @throws SchemerException
     *             No story ongoing for now...
     */
    private void validateRequestOrThrow() {
        if (storyManager == null) {
            throw new SchemerException("no story is ongoing now");
        }
    }

    /**
     * Prepares to start a new directive.
     *
     * <p>
     * Verifies that we can actually start a request (throws an exception if
     * not) and resets the factory resources.
     * </p>
     *
     * @return The factory that builds requests.
     */
    private SchemerFactory factoryReady() {
        validateRequestOrThrow();
        factory.reset();
        return factory;
    }

    public <T> HasWhenClause willReturn(T result) {
        return factoryReady().willReturn(result);
    }

    public <T extends Throwable> HasWhenClause willThrow(T excpt) {
        return factoryReady().willThrow(excpt);
    }

    public HasWhenClause will(InvocationResultProvider result) {
        return factoryReady().will(result);
    }

    public HasWhenClause willDelegateTo(InvocationResultProvider provider) {
        return factoryReady().willDelegateTo(provider);
    }

    public HasExpectationClauses willInvoke(OccurrenceChecker occurrences) {
        return factoryReady().willInvoke(occurrences);
    }

    public HasExpectationClauses willInvoke(int n) {
        return factoryReady().willInvoke(n);
    }

    /**
     * Appends a scenario to the scenario under construction.
     *
     * @param scenario
     *            the appended set of expectations
     */
    public void append(Scenario scenario) {
        controller.append(scenario);
    }

    /**
     * Adds a set of stubs to the stubs under construction.
     *
     * @param stubs
     *            the added stubs
     */
    public void append(Stubs stubs) {
        controller.append(stubs);
    }

    public Iterator<Actor> iterator() {
        return actors.iterator();
    }

    public <T> T anyOf(Class<T> clazz) {
        validateRequestOrThrow();
        return ArgumentSpecificationProvider.getArgumentSpecificationBuilderOrThrow().anyOf(clazz);
    }

    public <T> T aNonNullOf(Class<T> clazz) {
        validateRequestOrThrow();
        return ArgumentSpecificationProvider.getArgumentSpecificationBuilderOrThrow().aNonNullOf(clazz);
    }

    public <T> T with(T object) {
        validateRequestOrThrow();
        return ArgumentSpecificationProvider.getArgumentSpecificationBuilderOrThrow().with(object);
    }

    public <T> T with(Checker<T> checker) {
        validateRequestOrThrow();
        return ArgumentSpecificationProvider.getArgumentSpecificationBuilderOrThrow().with(checker);
    }
}
