/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.impl;

import com.vmware.lmock.exception.ExpectationError;
import com.vmware.lmock.exception.UnexpectedInvocationError;
import com.vmware.lmock.exception.UnsatisfiedOccurrenceError;

/**
 * Core engine validating the progress of a story regarding a list of
 * expectations.
 */
class StoryProcessor {

    /** Logs the processor activity. */
    private static final Logger logger = Logger.get(StoryProcessor.class);
    /** The list of expectations checked by this story. */
    private final ExpectationList expectationList;
    /** Handle default invocations. */
    private final InvocationHooks invocationHooks = new InvocationHooks();
    /** Keeps track of the expectations achieved by this processor. */
    private final StoryTracker storyTracker = new StoryTracker();

    /**
     * Creates a new processor, to validate a scenario.
     *
     * <p>
     * Registers the specified expectation list to validate the story.
     * </p>
     *
     * @param expectationList
     *            the list of expectations in the scenario
     */
    protected StoryProcessor(ExpectationList expectationList) {
        logger.trace("StoryProcessor", "expectationList=", expectationList);
        this.expectationList = expectationList;
    }

    /** @return The list of expectations processed by this. */
    protected ExpectationList getExpectationList() {
        return expectationList;
    }

    /**
     * @return <code>true</code> if we have not yet moved to the first
     *         expectation.
     */
    private boolean hasNotYetMovedToFirstExpectation() {
        return storyTracker.size() == 0;
    }

    /**
     * Moves to the first expectation if and only if not done yet.
     */
    private void moveToFirstExpectationIfNeeded() {
        logger.trace("moveToFirstExpectationIfNeeded", "expectationList=", expectationList);
        if (hasNotYetMovedToFirstExpectation()) {
            Expectation expect = expectationList.getCurrentExpectation();
            if (expect != null) {
                logger.trace("moveToFirstExpectationIfNeeded", "resetting");
                expect.getOccurrences().reset();
                storyTracker.addExpectationReport(expect);
            }
        }
    }

    /**
     * Adds an expectation at the end of the known expectation list.
     *
     * @param expectation
     *            the new expectation
     */
    protected void addExpectation(Expectation expectation) {
        logger.trace("addExpectation", "expectationList=", expectationList, "expectation=", expectation);
        expectationList.addExpectation(expectation);
        moveToFirstExpectationIfNeeded();
    }

    /**
     * Gets the next expectation from the list. Must be called if and only if
     * the current expectation is not null.
     *
     * <p>
     * The method cleans the pending resources for the current expectation, if
     * any.
     * </p>
     *
     * @return The next expectation, null if the scenario is complete.
     */
    private Expectation nextExpectation() {
        logger.trace("nextExpectation", "expectationList=", expectationList);
        Expectation currentExpectation = expectationList.getCurrentExpectation();
        currentExpectation.getOccurrences().reset();
        expectationList.nextExpectation();
        currentExpectation = expectationList.getCurrentExpectation();
        if (currentExpectation != null) {
            storyTracker.addExpectationReport(currentExpectation);
        }
        return currentExpectation;
    }

    /**
     * Gets the next expectation from the list.
     *
     * @return The next expectation, null if the scenario is complete.
     */
    private Expectation haveALookAtNextExpectation() {
        logger.trace("haveALookAtNextExpectation", "expectationList=", expectationList);
        return expectationList.nextExpectationWithoutChangingCurrent();
    }

    /**
     * Begins the story.
     */
    public void begin() {
        logger.trace("begin", "expectationList=", expectationList);
        storyTracker.clear();
        StoryTrack.get().registerTrackerIfNeeded(storyTracker);
        if (expectationList != null) {
            // Rewind in case of...
            expectationList.rewind();
            moveToFirstExpectationIfNeeded();
        }
    }

    /**
     * Verifies that there is no remaining expectation requiring an invocation
     * before the story ends.
     *
     * @throws UnsatisfiedOccurrenceException
     *             An unsatisfied expectation remains
     */
    private void checkEveryExpectationIsSatisfied() {
        logger.trace("checkEveryExpectationIsSatisfied", "expectationList=", expectationList);
        Expectation currentExpectation = expectationList.getCurrentExpectation();
        while (currentExpectation != null) {
            logger.trace("checkEveryExpectationIsSatisfied", "checking expectation=", currentExpectation);
            if (!currentExpectation.getOccurrences().canEndNow()) {
                logger.trace("checkEveryExpectationIsSatisfied", "occurrence=", currentExpectation, "can't end now!");
                throw new UnsatisfiedOccurrenceError(currentExpectation);
            }

            currentExpectation = nextExpectation();
        }
    }

    /** Cleans up the resources used by the processor. */
    private void cleanup() {
        logger.trace("cleanup", "expectationList=", expectationList);
        expectationList.unwind();
        Cleaner.cleanup();
    }

    /**
     * Ends the story.
     *
     * <p>
     * Verifies that all the mandatory expectations were satisfied.
     * </p>
     *
     * @throws UnsatisfiedOccurrenceException
     *             This invocation comes while the previous expectation was not
     *             complete.
     */
    public void end() {
        logger.trace("end", "expectationList=", expectationList);
        try {
            checkEveryExpectationIsSatisfied();
        } finally {
            cleanup();
        }
    }

    /**
     * Ends the story and issues an error.
     *
     * <p>
     * This is to ensure that if the story fails, no additional errors will
     * occur.
     * </p>
     *
     * @param error
     *            the thrown error
     */
    private void end(ExpectationError error) {
        logger.trace("end", "expectationList=", expectationList, "error=", error);
        cleanup();
        throw error;
    }

    /**
     * Validates the invocation of a mock regarding the scenario.
     *
     * <p>
     * If the validation is successful, the method returns the invocation result
     * specified in the scenario.
     * </p>
     *
     * @throws UnexpectedInvocationException
     *             This invocation was not expected in the scenario.
     * @throws UnsatisfiedOccurrenceException
     *             This invocation comes while the previous expectation was not
     *             complete.
     */
    public InvocationResultProvider invoke(Invocation invocation) {
        logger.trace("invoke", "invocation=", invocation, "expectationList=", expectationList);
        Expectation currentExpectation;

        // In fact the current expectation is not necessarily what we actually
        // want to check. For example, if the occurrence is "any", and the
        // user invokes another method, we must search for the corresponding
        // expectation.
        // Loop until an exception is thrown or we have an invocation result.
        InvocationResultProvider result = null;
        do {
            currentExpectation = expectationList.getCurrentExpectation();

            // Note: we cannot have currentExpectation null and result non
            // null, so we can safely perform the following test.
            if (currentExpectation == null) {
                logger.trace("invoke", "currentExpectation=null", "trying default invocation hook");
                // There may be a default invocation hook that can be called
                result = invocationHooks.tryInvocation(invocation);
                if (result != null) {
                    logger.trace("invoke", "currentExpectation=null", "default invocation hook found");
                    return result;
                } else {
                    logger.trace("invoke", "currentExpectation=null",
                      "no default invocation hook => unexpected invocation");
                    end(new UnexpectedInvocationError(invocation.toString()));
                }
            }

            logger.trace("invoke", "checking that expectation", currentExpectation, "is compatible with", invocation);
            if (currentExpectation.valueIsCompatibleWith(invocation)) {
                logger.trace("invoke", "value is compatible with invocation, checking occurrence limit");
                // We can call the method, but we may not be allowed to... This
                // case is not necessarily an error, because a subsequent
                // expectation may be OK.
                if (currentExpectation.getOccurrences().hasReachedLimit()) {
                    logger.trace("invoke", "reached the limit of expectation", currentExpectation,
                      "trying next expectation");
                    nextExpectation();
                } else {
                    logger.trace("invoke", "invocation is compatible with", currentExpectation, " => SUCCESSs");
                    result = currentExpectation.getResult();
                    storyTracker.getCurrentExpectationReport().record();
                }
            } else {
                logger.trace("invoke", "current expectation is not compatible with invocation... can we end it now?");
                // We can search for another expectation if and only if the
                // current one can be completed
                if (currentExpectation.getOccurrences().canEndNow()) {
                    logger.trace("invoke", "can end expectation", currentExpectation, "checking occurrence limit");
                    if (currentExpectation.getOccurrences().hasReachedLimit()) {
                        // If the current expectation can end now and cannot
                        // continue, we need to go to the next expectation for
                        // sure
                        logger.trace("invoke", "can try next expectation");
                        nextExpectation();
                    } else {
                        // If the current expectation can end now but could continue, there are 2 cases:
                        // * If the next expectation can satisfy the invocation,
                        // then move to the next expectation
                        // * If the next expectation would not satisfy the
                        // invocation either, then our last chance is to apply
                        // a default hook
                        logger.trace("invoke", "can continue to the next expectation", "checking the next one");
                        Expectation next = haveALookAtNextExpectation();
                        if (next != null && next.valueIsCompatibleWith(invocation)) {
                            logger.trace("invoke", "next expectation continues current expectation, going forward");
                            nextExpectation();
                        } else {
                            logger.trace("invoke", "could not go further... trying default hook");
                            result = invocationHooks.tryInvocation(invocation);
                            if (result == null) {
                                logger.trace("invoke", "finally, let's try the next expectation");
                                nextExpectation();
                            }
                        }
                    }
                } else {
                    logger.trace("invoke", "can't end ", currentExpectation, "now... trying default invocation hook");
                    // There may be a default invocation hook that can be called
                    result = invocationHooks.tryInvocation(invocation);
                    if (result != null) {
                        logger.trace("invoke", "hook is OK");
                        return result;
                    } else {
                        logger.trace("invoke", "expectation", currentExpectation, "is not satisfied by", invocation);
                        end(new UnsatisfiedOccurrenceError(currentExpectation));
                    }
                }
            }
        } while (result == null);

        logger.trace("invoke", "returning", result);
        return result;
    }
}
