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
import com.vmware.lmock.checker.ThreadChecker;
import com.vmware.lmock.clauses.InnerSchemerFactoryClauses.HasExpectationClauses;
import com.vmware.lmock.clauses.InnerSchemerFactoryClauses.HasWhenClause;
import com.vmware.lmock.exception.SchemerException;
import com.vmware.lmock.impl.InvocationResultProvider;
import com.vmware.lmock.impl.Scenario;
import com.vmware.lmock.impl.Stubs;
import com.vmware.lmock.mt.Actor;
import static com.vmware.lmock.mt.Actor.anActorForCurrentThread;
import static com.vmware.lmock.mt.Actor.anActorForThread;
import static com.vmware.lmock.mt.Actor.anActorForThreadLike;
import static com.vmware.lmock.mt.Actor.anActorForAnyThread;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines the whole test as a masquerade.
 *
 * <p>
 * The schemer is a unique object controlling the construction and execution of
 * masquerades. It allows to create expectations and stubs on the fly while
 * actually following the story.
 * </p>
 *
 * <p>
 * The schemer activity always start by an invocation to <code>begin</code> that
 * resets the old context of execution (if any) and prepares to execute a new
 * masquerade. The masquerade may terminate with an invocation to
 * <code>end</code>, which verifies that no unsatisfied expectation remains in
 * the story.
 * </p>
 *
 * <p>
 * To build the stubs and expectations (denoted as "directives"), you should
 * follow the syntax:
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
 * The schemer also implements the <code>append</code> methods, to include a
 * scenario or a set of stubs into the ongoing masquerade.
 * </p>
 */
public final class Schemer {
    /** Registers and controls a story on the fly. */
    private final StoryManager storyManager;
    /** Role associated to the default actor defined by the story manager. */
    private Role defaultRole;
    /** The schemer singleton. */
    private static final Schemer schemer = new Schemer();
    /** List of roles contributing to the masquerade. */
    private List<Role> roles = new ArrayList<Role>();

    /** @return The story manager associated to this schemer. */
    private StoryManager getStoryManager() {
        return storyManager;
    }

    /** @return The list of roles associated to this schemer. */
    private List<Role> getRoles() {
        return roles;
    }

    /**
     * Assigns an explicit value for the default role managed by this.
     *
     * @param role
     *            the new role value (<code>null</code> to disable that role)
     */
    private void setDefaultRoleTo(Role role) {
        defaultRole = role;
    }

    /** Sets the default role to <i>unassigned</i>. */
    private void invalidateDefaultRole() {
        setDefaultRoleTo(null);
    }

    /** @return <code>true</code> if the schemer is assigned a default role. */
    private boolean hasDefaultRole() {
        return defaultRole != null;
    }

    /** @return The role object used by this to manage the testing thread. */
    private Role getDefaultRole() {
        return defaultRole;
    }

    /** Creates the schemer. */
    private Schemer() {
        storyManager = new StoryManager();
    }

    /** @return The story manager. */
    private static StoryManager storyManager() {
        return schemer.getStoryManager();
    }

    /** @return The list of roles defined when constructing this. */
    private static List<Role> roles() {
        return schemer.getRoles();
    }

    /**
     * Provides the default role created when beginning the story.
     *
     * <p>
     * If no  such role exists, throws an exception.
     * </p>
     *
     * @return The default role defined for the testing thread.
     */
    private static Role getDefaultRoleOrThrow() {
        if (schemer.hasDefaultRole()) {
            return schemer.getDefaultRole();
        } else {
            throw new SchemerException("no story is ongoing now");
        }
    }

    /** Cleans the default role object managed by the schemer. */
    private static void cleanupDefaultRole() {
        schemer.invalidateDefaultRole();
    }

    /**
     * Creates a new role for the default actor managed with the story.
     *
     * <p>
     * The resulting role becomes the <code>getDefaultRoleOrThrow</code> managed by the
     * schemer.
     * </p>
     */
    private static void createDefaultRoleIfNotYetFound() {
        if (!schemer.hasDefaultRole()) {
            // Note: we do not explicitly need a story to get the default actor,
            // since the story was designed to automatically merge this default
            // actor with any specification like
            // <code>anActorForCurrentThread</code>.
            Role defaultRole = new Role(anActorForCurrentThread());
            addAndAllowRole(defaultRole);
            schemer.setDefaultRoleTo(defaultRole);
        }
    }

    /**
     * Scans a list of roles, in search of an actor for the testing thread.
     *
     * <p>
     * If such actor is found, the role becomes a default role.
     * </p>
     * <p>
     * Notice that the function does nothing if there's already a default role.
     * </p>
     *
     * @param role
     *            the inspected role
     */
    private static void assignDefaultRoleIfMatchingActorFound(Role role) {
        if (!schemer.hasDefaultRole()) {
            for (Actor actor : role) {
                if (actor.getChecker().valueIsCompatibleWith(Thread.currentThread())) {
                    schemer.setDefaultRoleTo(role);
                    return;
                }
            }
        }
    }

    /**
     * Adds a role to the list of roles and allow it to create directives.
     *
     * @param role
     *            the role
     */
    private static void addAndAllowRole(Role role) {
        roles().add(role);
        role.registerStoryManager(storyManager());
    }

    /**
     * Registers a set of roles contributing to the masquerade.
     *
     * @param additionalRoles
     *            the roles
     */
    private static void registerRoles(Role... additionalRoles) {
        cleanupDefaultRole();
        for (Role role : additionalRoles) {
            addAndAllowRole(role);
            assignDefaultRoleIfMatchingActorFound(role);
        }
        createDefaultRoleIfNotYetFound();
    }

    /**
     * Unregisters all the roles contributing to the current masquerade.
     */
    private static void unregisterRoles() {
        for (Role role : roles()) {
            role.unregisterStoryManager();
        }
        roles().clear();
        cleanupDefaultRole();
    }

    /** @return The list of actors implied by the registered roles. */
    private static Actor[] getCurrentActors() {
        int numberOfActors = 0;
        ArrayList<Actor> actorList = new ArrayList<Actor>();
        for (Role role : roles()) {
            for (Actor actor : role) {
                actorList.add(actor);
                numberOfActors++;
            }
        }
        Actor[] result = new Actor[numberOfActors];
        return actorList.toArray(result);
    }

    /**
     * Resets the schemer context to begin a new masquerade.
     *
     * <p>
     * By default, the method creates a default role for the current thread
     * (i.e. the thread executing the test). The user may specify additional
     * roles to this.
     * </p>
     * <p>
     * Notice that if the list of additional roles includes an actor that
     * represents the default role, the system considers those two roles as a
     * single one.
     * </p>
     *
     * @param additionalRoles
     *            a list of roles participating to the masquerade
     */
    public static void begin(Role... additionalRoles) {
        // Be sure that we do not have remaining crumbs
        unregisterRoles();
        registerRoles(additionalRoles);
        storyManager().createAndBeginNewStory(getCurrentActors());
        // Implicitly, we now have a default actor for the thread controlling the
        // test... We don't need to create a role for it, since the user does
        // not see it explicitly.
    }

    /**
     * Verifies that the ongoing story is complete.
     */
    public static void end() {
        unregisterRoles();
        storyManager().endStory();
    }

    /**
     * Includes a scenario into the masquerade.
     *
     * @param scenario
     *            the included scenario
     */
    public static void append(Scenario scenario) {
        getDefaultRoleOrThrow().append(scenario);
    }

    /**
     * Includes a set of stubs into the masquerade.
     *
     * @param stubs
     *            the included stubs
     */
    public static void append(Stubs stubs) {
        getDefaultRoleOrThrow().append(stubs);
    }

    /**
     * Declares an expectation.
     *
     * <p>
     * This clause creates a new expectation that will be registered into the
     * ongoing story when the definition is complete.
     * </p>
     *
     * @param occurrences
     *            the expected occurrences of this expectation
     * @return The object used to specify the different clauses of the
     *         expectation.
     */
    public static HasExpectationClauses willInvoke(OccurrenceChecker occurrences) {
        return getDefaultRoleOrThrow().willInvoke(occurrences);
    }

    /**
     * Declares an exact expectation.
     *
     * <p>
     * This clause creates an expectation to exactly match a number of
     * occurrences.
     * </p>
     *
     * @param n
     *            the expected number of occurrences
     * @return The object used to specify the different clauses of the
     *         expectation.
     */
    public static HasExpectationClauses willInvoke(int n) {
        return getDefaultRoleOrThrow().willInvoke(n);
    }

    /**
     * Creates a new stub or expectation, specifying the invocation result.
     *
     * @param <T>
     *            type of the returned value
     * @param result
     *            the value returned by the mock when invoked
     * @return The object used to specify the different clauses of the
     *         expectation or stub.
     */
    public static <T> HasWhenClause willReturn(T result) {
        return getDefaultRoleOrThrow().willReturn(result);
    }

    /**
     * Creates a new stub or expectation, specifying the invocation result.
     *
     * @param <T>
     *            type of the thrown exception
     * @param excpt
     *            the exception thrown by the mock when invoked
     * @return The object used to specify the different clauses of the
     *         expectation or stub.
     */
    public static <T extends Throwable> HasWhenClause willThrow(T excpt) {
        return getDefaultRoleOrThrow().willThrow(excpt);
    }

    /**
     * Creates a new stub or expectation, specifying the invocation result.
     *
     * @param provider
     *            a result provider
     * @return The object used to specify the different clauses of the
     *         expectation or stub.
     */
    public static HasWhenClause willDelegateTo(InvocationResultProvider provider) {
        return getDefaultRoleOrThrow().willDelegateTo(provider);
    }

    /**
     * Creates a new stub or expectation, specifying the invocation result.
     *
     * @param result
     *            the invocation result
     * @return The object used to specify the different clauses of the
     *         expectation or stub.
     */
    public static HasWhenClause will(InvocationResultProvider result) {
        return getDefaultRoleOrThrow().will(result);
    }

    /**
     * Registers any non-null argument of a given class for the expectation
     * under construction.
     *
     * @param <T>
     *            type of the expected argument
     * @param clazz
     *            the expected class of arguments
     * @return An object of the requested class.
     */
    public static <T> T aNonNullOf(Class<T> clazz) {
        return getDefaultRoleOrThrow().aNonNullOf(clazz);
    }

    /**
     * Registers any argument of a given class for the expectation under
     * construction.
     *
     * @param <T>
     *            type of the expected argument
     * @param clazz
     *            the expected class of arguments
     * @return An object of the requested class.
     */
    public static <T> T anyOf(Class<T> clazz) {
        return getDefaultRoleOrThrow().anyOf(clazz);
    }

    /**
     * Registers an argument for the current expectation under construction.
     *
     * <p>
     * This clause specifies the exact value expected during the invocations.
     * </p>
     *
     * @param <T>
     *            type of the expected argument
     * @param object
     *            the registered object
     * @return An object of the requested class.
     */
    public static <T> T with(T object) {
        return getDefaultRoleOrThrow().with(object);
    }

    /**
     * Registers an explicit checker to an argument for the current expectation
     * under construction.
     *
     * @param <T>
     *            type handled by the specified checker
     * @param checker
     *            the registered checker
     * @return An object of the related class of the checker
     */
    public static <T> T with(Checker<T> checker) {
        return getDefaultRoleOrThrow().with(checker);
    }

    /**
     * Common construction pattern for roles.
     *
     * <p>
     * Creates a role played by a single actor operating on a well-known thread.
     * </p>
     *
     * @param thread
     *            the thread
     * @return The new role.
     */
    public static Role aRoleForThread(Thread thread) {
        return new Role(anActorForThread(thread));
    }

    /**
     * Common construction pattern for roles.
     *
     * <p>
     * Creates a role played by a single actor operating on a thread identified by a given matcher.
     * </p>
     *
     * @param checker
     *            the checker identifying the thread
     * @return The new role.
     */
    public static Role aRoleForAThreadLike(ThreadChecker checker) {
        return new Role(anActorForThreadLike(checker));
    }

    /**
     * Common construction pattern for roles.
     *
     * <p>
     * Creates a role played by a single actor matching any thread.
     * </p>
     *
     * @return The new role
     */
    public static Role aRoleForAnyThread() {
        return new Role(anActorForAnyThread());
    }

    /**
     * Provides the default role managed by the schemer to handle the testing thread.
     *
     * <p>
     * Throws an exception if this request is outside of the "regular schemer context". In particular, no such
     * role is available until you begin the masquerade.
     * </p>
     *
     * @return The default role.
     */
    public static Role defaultRole() {
        return getDefaultRoleOrThrow();
    }
}
