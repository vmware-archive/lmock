/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.impl;

import com.vmware.lmock.exception.ExpectationError;
import com.vmware.lmock.exception.LMRuntimeException;
import com.vmware.lmock.mt.Actor;
import com.vmware.lmock.mt.ActorUpdateListener;
import com.vmware.lmock.mt.MTDispatcher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dispatches the invocation of a mock during the execution of a story to the
 * proper stub and scenario.
 *
 * <p>
 * Such a dispatcher supports multi-threaded environments: it is able to forward
 * the invocation on the basis of a thread specific environment.
 * </p>
 * <p>
 * It also handles the cases of failures, intercepting the exceptions to
 * properly process the error.
 * </p>
 */
class MockInvocationDispatcher extends MTDispatcher<MockInvocationDispatcher.Item>
  implements MockInvocationHandler, ActorUpdateListener {
    /** Logs the dispatcher activity. */
    private static final Logger logger = Logger.get(MockInvocationDispatcher.class);

    /**
     * Items internally managed by the invocation dispatcher.
     *
     * <p>
     * Keeps track of the declaring actor and has an underlying invocation
     * processor.
     * </p>
     */
    static final class Item {
        private Actor actor;
        private InvocationProcessor processor;

        /**
         * Creates a new item.
         *
         * @param actor
         *            the tracked actor
         */
        Item(Actor actor) {
            this.actor = actor;
        }

        /** @return The actor defining the handled thread. */
        Actor getActor() {
            return actor;
        }

        /** @return The associated invocation processor. */
        InvocationProcessor getProcessor() {
            return processor;
        }

        /**
         * Updates the processor value.
         *
         * @param processor
         *            the new processor
         */
        void setProcessor(InvocationProcessor processor) {
            this.processor = processor;
        }

        @Override
        public String toString() {
            return "actor=" + actor + " processor=" + processor;
        }
    };
    /**
     * List of stub processors known by the dispatcher.
     *
     * <p>
     * There is one processor per <b>group</b> of stubs. Which means that
     * if we assume two sets of stubs <code>S1</code> and <code>S2</code>, there
     * may be - for example - one processor for <code>{S1,S2}</code>, another
     * one for <code>{S1}</code>, etc.
     * </p>
     * <p>
     * We use a commutative discriminator to identify the groups (which means
     * that <code>{S1,S2}</code> equals <code>{S2,S1}</code>, thus an integer
     * key.
     * </p>
     */
    private final Map<Integer, StubProcessor> stubProcessorMap =
      new HashMap<Integer, StubProcessor>();
    /** List of story processors known by the dispatcher. */
    private final Map<Scenario, StoryProcessor> storyProcessorMap =
      new HashMap<Scenario, StoryProcessor>();
    /** The list of created story processors. */
    private final List<StoryProcessor> storyProcessors =
      new ArrayList<StoryProcessor>();
    /** Associates the mocks to this invocation handler. */
    private final MockLinker linker = new MockLinker(this);

    /**
     * Computes the signature of a set of stubs, used to access the stub
     * processor list.
     *
     * @param stubsList
     *            the stubs list
     * @return Their signature.
     */
    private int getStubsSignature(Iterable<Stubs> stubsList) {
        int key = 0;
        for (Stubs stubs : stubsList) {
            key += stubs.hashCode();
        }
        return key;
    }

    /**
     * Creates a new stub map from a set of stubs.
     *
     * @param stubsList
     *            the initial set of stubs
     * @return The new stub map.
     */
    private StubMap createStubMap(Iterable<Stubs> stubsList) {
        StubMap stubMap = new StubMap();

        for (Stubs stubs : stubsList) {
            logger.trace("createStubMap", "adding stubs", stubs);
            stubs.addStubsToMap(stubMap);
        }

        linker.registerNewMocks(stubMap);
        return stubMap;
    }

    /**
     * Creates or reuses a stub processor for a given set of stubs.
     *
     * <p>
     * If the specified set of stubs was already used for another thread, then
     * return the same stub processor.
     * </p>
     *
     * @param stubsList
     *            the set of stubs
     * @return The corresponding stub processor.
     */
    private StubProcessor getStubProcessor(Iterable<Stubs> stubsList) {
        if (stubsList != null) {
            int key = getStubsSignature(stubsList);
            StubProcessor result = stubProcessorMap.get(key);
            if (result == null) {
                logger.trace("getStubProcessor", "create new processor", "key=", key);
                result = new StubProcessor(createStubMap(stubsList));
                stubProcessorMap.put(key, result);
            }

            return result;
        } else {
            return new StubProcessor(new StubMap());
        }
    }

    /**
     * Creates and registers a new story processor.
     *
     * @param scenario
     *            the scenario followed by this processor
     * @return The new processor.
     */
    private StoryProcessor createStoryProcessor(Scenario scenario) {
        logger.trace("createStoryProcessor", "scenario=", scenario);
        StoryProcessor result = new StoryProcessor(new ExpectationList(scenario.createExpectations()));
        storyProcessorMap.put(scenario, result);
        storyProcessors.add(result);

        linker.registerNewMocks(result);
        return result;
    }

    /**
     * Creates or reuses a story processor for a given scenario.
     *
     * <p>
     * If the specified scenario was already used for another thread, then
     * return the same story processor (so that the two threads will collaborate
     * in the achievement of the scenario).
     * </p>
     *
     * @param scenario
     *            the requested scenario (can be null)
     * @return The corresponding story processor.
     */
    private StoryProcessor getStoryProcessor(Scenario scenario) {
        logger.trace("getStoryProcessor", "scenario=", scenario);
        if (scenario != null) {
            StoryProcessor result = storyProcessorMap.get(scenario);
            if (result == null) {
                logger.trace("getStoryProcessor", "no processor yet...");
                result = createStoryProcessor(scenario);
            }

            return result;
        } else {
            return createStoryProcessor(new Scenario());
        }
    }

    /**
     * Creates an invocation processor handling the specification of a given actor.
     *
     * @param actor
     *            the actor
     * @return The generated processor.
     */
    private InvocationProcessor createInvocationProcessor(Actor actor) {
        logger.trace("createInvocationProcessor", "actor=", actor);
        StubProcessor stubProcessor = getStubProcessor(actor.getStubsList());
        StoryProcessor storyProcessor = getStoryProcessor(actor.getScenario());
        InvocationProcessor processor = new InvocationProcessor(stubProcessor, storyProcessor);
        logger.trace("createInvocationProcessor", "actor=", actor, "returns", processor);
        return processor;
    }

    @Override
    public void onUpdateOfScenarioOrStubs(Actor actor, Object userArgument) {
        Item item = (Item) userArgument;
        logger.trace("onUpdate", "actor=", actor, "userArgument=", item);
        InvocationProcessor processor = createInvocationProcessor(actor);
        item.setProcessor(processor);
    }

    /**
     * Parses a list of actors to create the associated resources and register
     * those actors.
     *
     * @param actors
     *            the actor list
     */
    private void setupAndRegisterActors(Actor... actors) {
        for (Actor actor : actors) {
            logger.trace("setupAndRegisterActors", "new actor=", actor);
            InvocationProcessor processor = createInvocationProcessor(actor);
            // The actor will keep track of the invoking thread when found.
            Item item = new Item(actor);
            item.setProcessor(processor);
            register(actor.getChecker(), item, actor);
            actor.willListenToUpdates(this, item);
        }
    }

    /**
     * Creates a new dispatcher, inspecting a set of threads.
     *
     * @param actor
     *            a default actor, describing a handled thread
     * @param actors
     *            the list of actors describing the handled threads
     */
    private MockInvocationDispatcher(Actor actor, Actor... actors) {
        setupAndRegisterActors(actor);
        setupAndRegisterActors(actors);
    }

    /**
     * Creates a new dispatcher for a single thread.
     *
     * @param actor
     *            the actor describing the thread
     * @return The created dispatcher.
     */
    static MockInvocationDispatcher aDispatcherForSingleActor(Actor actor) {
        return new MockInvocationDispatcher(actor);
    }

    /**
     * Creates a new dispatcher for multiple threads.
     *
     * @param defaultActor
     *            a mandatory actor contributing to this
     * @param actors
     *            the actors describing the threads
     * @return The created dispatcher.
     */
    static MockInvocationDispatcher aDispatcherForMultipleActors(
      Actor defaultActor, Actor... actors) {
        return new MockInvocationDispatcher(defaultActor, actors);
    }

    /**
     * Records an exception raised by the invocation processor.
     *
     * <p>
     * The exception is guarded and reported as an error to the actor.
     * </p>
     *
     * @param actor
     *            the actor describing the invoking thread
     * @param excpt
     *            the tracked exception
     */
    private void handleException(Actor actor, LMRuntimeException excpt) {
        logger.trace("handleException", "actor=", actor);
        ExceptionGuard.get().record(excpt);
        actor.setLastException(excpt);
    }

    /**
     * Records an error raised by the invocation processor.
     *
     * <p>
     * The exception is guarded and reported as an error to the actor.
     * </p>
     *
     * @param actor
     *            the actor describing the invoking thread
     * @param error
     *            the tracked error
     */
    private void handleError(Actor actor, ExpectationError error) {
        logger.trace("handleError", "actor=", actor);
        ExceptionGuard.get().record(error);
        actor.setLastException(error);
    }

    /**
     * Invokes a method using a given processor and handles exceptions.
     *
     * @param invocation
     *            the invocation
     * @param item
     *            the item providing the invocation processor
     * @return The invocation result.
     */
    private InvocationResultProvider invokeProcessor(Invocation invocation, Item item) {
        logger.trace("invoke", "invocation=", invocation, "item=", item);
        try {
            return item.getProcessor().invoke(invocation);
        } catch (LMRuntimeException e) {
            handleException(item.getActor(), e);
            throw e;
        } catch (ExpectationError e) {
            handleError(item.getActor(), e);
            throw e;
        }
    }

    public synchronized InvocationResultProvider invoke(Invocation invocation) {
        return invokeProcessor(invocation, getCurrentData());
    }

    /**
     * Links all the mocks known by this to this.
     *
     * <p>
     * Uses the list of registered mocks as reference.
     * </p>
     */
    private void linkKnownMocksToThis() {
        linker.linkHandlerToRegisteredMocks();
    }

    /**
     * Unlinks all the mocks known by this from this.
     *
     * <p>
     * Uses the list of registered mocks as reference.
     * </p>
     */
    private void unlinkKnownMocksFromThis() {
        linker.unlinkHandlerFromRegisteredMocks();
    }

    /** Starts all the story processors. */
    private void beginStoryProcessors() {
        for (StoryProcessor processor : storyProcessors) {
            processor.begin();
        }
    }

    /** Ends all the story processors. */
    private void endStoryProcessors() {
        for (StoryProcessor processor : storyProcessors) {
            processor.end();
        }
    }

    /**
     * Prepares the dispatcher to run a new story.
     */
    synchronized void begin() {
        logger.trace("begin", "preparing for a new story...");
        ExceptionGuard.get().enable();
        linkKnownMocksToThis();
        beginStoryProcessors();
    }

    /**
     * Ends all the story processors known by the dispatcher.
     *
     * <p>
     * If one of the thread participating to the test finished encountered an
     * exception, forwards this exception.
     * </p>
     */
    synchronized void end() {
        try {
            logger.trace("end", "ending story processors");
            unlinkKnownMocksFromThis();
            endStoryProcessors();
            ExceptionGuard.get().throwIfPresent();
        } finally {
            ExceptionGuard.get().disable();
        }
    }

    /**
     * Finds the story processor used by a specific actor.
     *
     * @param actor
     *            the requested actor
     * @return The corresponding processor, <code>null</code>> if not found.
     */
    private StoryProcessor getAssignedStoryProcessor(Actor actor) {
        Scenario key = actor.getScenario();
        StoryProcessor processor = storyProcessorMap.get(key);
        logger.trace("getAssignedStoryProcessor", "actor=", actor, "key=", key, "returns", processor);
        return processor;
    }

    /**
     * Finds the stub processor used by a specific actor.
     *
     * @param actor
     *            the requested actor
     * @return The corresponding processor, <code>null</code> if not found.
     */
    private StubProcessor getAssignedStubProcessor(Actor actor) {
        int key = getStubsSignature(actor.getStubsList());
        StubProcessor processor = stubProcessorMap.get(key);
        logger.trace("getAssignedStubProcessor", "actor=", actor, "key=", key, "returns", processor);
        return processor;
    }

    /**
     * Appends a scenario to the expectation list of a given actor.
     *
     * @param actor
     *            the target actor
     * @param scenario
     *            the appended scenario
     */
    void append(Actor actor, Scenario scenario) {
        logger.trace("append", "actor=", actor, "scenario=", scenario);
        StoryProcessor storyProcessor = getAssignedStoryProcessor(actor);
        // Should not be null. If this is the case, let the runtime exception
        // go...
        for (Expectation expectation : scenario) {
            storyProcessor.addExpectation(expectation);
            linker.registerAndLinkNewMocks(expectation);
        }
    }

    /**
     * Appends stubs to the list of stubs of a given actor.
     *
     * @param actor
     *            the target actor
     * @param stubs
     *            the appended stubs
     */
    void append(Actor actor, Stubs stubs) {
        logger.trace("append", "actor=", actor, "stubs=", stubs);
        StubProcessor stubProcessor = getAssignedStubProcessor(actor);
        // Should not be null. If this is the case, let the runtime exception
        // go...
        for (Stub stub : stubs) {
            stubProcessor.addStub(stub);
            linker.registerAndLinkNewMocks(stub);
        }
    }
}
