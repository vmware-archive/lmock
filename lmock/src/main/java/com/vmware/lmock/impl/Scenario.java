/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.impl;

import com.vmware.lmock.checker.OccurrenceChecker;
import com.vmware.lmock.checker.Occurrences;
import com.vmware.lmock.clauses.HasArgumentSpecificationClauses;
import com.vmware.lmock.clauses.HasExpectationSpecificationClauses;
import com.vmware.lmock.clauses.HasInvocationResultSpecificationClauses;
import com.vmware.lmock.clauses.HasOccurrencesSpecificationClauses;

/**
 * Definition of a scenario.
 *
 * <p>
 * A scenario represents a sequence of invocations to mocks. The story, when it
 * is told, must comply with this scenario.
 * </p>
 *
 * <p>
 * Basically, the user must describe the scenario upon its construction, by
 * putting a block of code describing the expectations:
 * </p>
 *
 * <pre>
 * <code>
 *     new Scenario() {{
 *         ...EXPECTATIONS...
 *     }};
 * </code>
 * </pre>
 *
 * <p>
 * The expression of an expectation is the combination of the expected
 * invocation itself, plus additional information, driven by the
 * <code>expect</code> method:
 * </p>
 *
 * <pre>
 * <code>
 *         expect(MOCK).INVOCATION;
 *         EXPECTATION_INFORMATION_IF_ANY
 * </code>
 * </pre>
 * <p>
 *
 * <p>
 * Where MOCK is the invoked mock and INVOCATION is a call to the expected
 * method, along with either the exact arguments, or arguments specified by
 * 'with' clauses.
 * </p>
 * <p>
 * The expectation information consist in common expectation clauses, such as
 * <code>occurs</code> etc.
 * </p>
 */
public class Scenario extends ExpectationListBuilder implements
  HasExpectationSpecificationClauses, HasArgumentSpecificationClauses,
  HasInvocationResultSpecificationClauses<Expectation>,
  HasOccurrencesSpecificationClauses<Expectation> {

    /** Logs the activity within this scenario. */
    private static Logger logger = Logger.get(Scenario.class);

    /**
     * Basic setup when creating a new scenario.
     */
    public Scenario() {
        this(true);
    }

    /**
     * Creates a new scenario that continues an ongoing story.
     *
     * <p>
     * The user may decide to clear the current story track, in order to avoid
     * to inject wrong information in the potential exception raised when
     * constructing the scenario.
     * </p>
     *
     * <p>
     * In practice, you can safely invoke <code>Scenario()</code> rather than this constructor.
     * </p>
     *
     * @param clearStoryTrack
     *            if <code>true</code> clean the story track.
     */
    public Scenario(boolean clearStoryTrack) {
        logger.trace("clearStoryTrack");
        if (clearStoryTrack) {
            StoryTrack.get().clearTrackers();
        }
    }

    @Override
    public final Expectation expect() {
        logger.trace("expect");
        return getCurrentChecker();
    }

    @Override
    public final <T> T expect(T object) {
        logger.trace("expect", "object=", object);
        createExpectation(object, null);
        return object;
    }

    @Override
    public final <T> T expect(T object, InvocationCheckerClosureHandler closureHandler) {
        logger.trace("expect", "closureHandler=", closureHandler);
        createExpectation(object, closureHandler);
        return object;
    }

    @Override
    public final Expectation occurs(OccurrenceChecker occurrences) {
        logger.trace("occurs", "occurrences=", occurrences);
        return expect().occurs(occurrences);
    }

    @Override
    public final Expectation occurs(int n) {
        return occurs(Occurrences.exactly(n));
    }

    @Override
    public final Expectation willReturn(Object result) {
        logger.trace("willReturn", "result=", result);
        return expect().willReturn(result);
    }

    @Override
    public final Expectation willThrow(Throwable excpt) {
        logger.trace("willThrow", "excpt=", excpt);
        return expect().willThrow(excpt);
    }

    @Override
    public Expectation will(InvocationResultProvider result) {
        logger.trace("will", "result=", result);
        return expect().will(result);
    }

    @Override
    public Expectation willDelegateTo(InvocationResultProvider provider) {
        logger.trace("willDelegateTo", "provider=", provider);
        if (provider == null) {
            throw new IllegalArgumentException("null provider specified");
        }

        return expect().willDelegateTo(provider);
    }
}
