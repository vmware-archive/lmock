/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.masquerade;

import static com.vmware.lmock.checker.Occurrences.exactly;
import static com.vmware.lmock.impl.InvocationResult.returnValue;
import static com.vmware.lmock.impl.InvocationResult.throwException;

import com.vmware.lmock.checker.OccurrenceChecker;
import com.vmware.lmock.clauses.HasAppendClauses;
import com.vmware.lmock.clauses.HasDirectiveClauses;
import com.vmware.lmock.clauses.HasInvocationResultSpecificationClauses;
import com.vmware.lmock.clauses.InnerSchemerFactoryClauses.HasExpectationClauses;
import com.vmware.lmock.clauses.InnerSchemerFactoryClauses.HasWhenClause;
import com.vmware.lmock.impl.InvocationCheckerClosureHandler;
import com.vmware.lmock.impl.InvocationResultProvider;
import com.vmware.lmock.impl.Scenario;
import com.vmware.lmock.impl.Stubs;

/**
 * Builds expectations and stubs.
 *
 * <p>
 * A factory is registering the different elements defined by the user with the
 * schemer clauses to produce the appropriate stub or expectation.
 * </p>
 *
 * <p>
 * For later argument specifications, the factory provides a specific
 * builder via <code>getArgumentSpecificationFactoryOrThrow</code>. Because
 * such a kind of constructor is outside the creation of a directive, the
 * implied role may be unbounded (in particular when using the static
 * helpers of the schemer). For that reason, this method is static and
 * requires a specific cleaning procedure, provided by <code>cleanup</code>.
 * </p>
 */
class SchemerFactory implements InvocationCheckerClosureHandler,
  HasDirectiveClauses, HasWhenClause, HasExpectationClauses {
    /** Occurrences scheme used by the directive under construction. */
    private OccurrenceChecker occurrences;
    /** Result of an invocation as defined by the directive under construction. */
    private InvocationResultProvider result;
    /** Temporary scenario object, used when building an expectation. */
    private Scenario temporaryScenario;
    /** Temporary stubs object, used when building a stub. */
    private Stubs temporaryStubs;
    /** Called back to register the built directive into the ongoing story. */
    private final HasAppendClauses controller;

    /**
     * Creates a new builder.
     *
     * <p>
     * The object expects to get a controller that will be invoked each time a
     * new directive needs to be added to the story.
     * </p>
     *
     * @param controller
     *            the controller
     */
    protected SchemerFactory(HasAppendClauses controller) {
        this.controller = controller;
    }

    /** @return <code>true</code> if the built element has occurrences. */
    private boolean hasOccurrences() {
        return occurrences != null;
    }

    /** @return <code>true</code> if the built element specifies a result. */
    private boolean hasResult() {
        return result != null;
    }

    /**
     * Cleans up all the temporary fields registered during the build.
     */
    protected void reset() {
        occurrences = null;
        result = null;
        temporaryScenario = null;
        temporaryStubs = null;
    }

    /**
     * Cleans up ALL the resources used by the factory.
     *
     * <p>
     * This method is stronger than <code>reset</code> since it also
     * deletes the factory used to build arguments to invocations.
     * </p>
     */
    protected void cleanup() {
        reset();
        ArgumentSpecificationProvider.cleanup();
    }

    /**
     * Calls back the story controller to register the lastly built directive.
     */
    private void registerToController() {
        if (temporaryScenario != null) {
            controller.append(temporaryScenario);
        } else {
            controller.append(temporaryStubs);
        }
    }

    /**
     * Registers the result of the built directive.
     *
     * @param resultHandler
     *            the handler that will get the clause
     */
    private void registerResultIfAny(
      HasInvocationResultSpecificationClauses<?> resultHandler) {
        if (hasResult()) {
            resultHandler.will(result);
        }
    }

    /**
     * Closes a specification by adding the registered clauses of the
     * expectation or stub to the ongoing scenario or stubs.
     */
    @Override
    public void onClosure() {
        // The target invocation result handler:
        HasInvocationResultSpecificationClauses<?> resultHandler;

        if (hasOccurrences()) {
            resultHandler = temporaryScenario.expect();
            temporaryScenario.expect().occurs(occurrences);
        } else {
            resultHandler = temporaryStubs.stub();
        }

        registerResultIfAny(resultHandler);
        registerToController();
        reset();
    }

    /**
     * Prepares the builder to build an expectation.
     *
     * <p>
     * This consists in creating a new scenario and plug a handler to the
     * closure of the specification, so that we can add the registered
     * parameters.
     * </p>
     *
     * @param mock
     *            The mock object for which we create an expectation.
     */
    private <T> void prepareToBuildExpectation(T mock) {
        temporaryScenario = new Scenario(false); // don't clear the story track
        temporaryScenario.expect(mock, this);
        ArgumentSpecificationProvider.setArgumentSpecificationBuilder(temporaryScenario);
    }

    /**
     * Prepares the builder to build a stub.
     *
     * <p>
     * This consists in creating a new stub and plug a handler to the closure of
     * the specification, so that we can add the registered parameters.
     * </p>
     *
     * @param mock
     *            The mock object for which we create an expectation.
     */
    private <T> void prepareToBuildStub(T mock) {
        temporaryStubs = new Stubs();
        temporaryStubs.stub(mock, this);
        ArgumentSpecificationProvider.setArgumentSpecificationBuilder(temporaryStubs);
    }

    /**
     * Prepares to register an expectation or a stub for a given mock.
     *
     * <p>
     * The method uses the previously specified clauses to guess what the best
     * solution is (scenario vs. stubs).
     * </p>
     */
    private <T> T prepareToBuild(T mock) {
        if (hasOccurrences()) {
            prepareToBuildExpectation(mock);
        } else {
            prepareToBuildStub(mock);
        }
        return mock;
    }

    @Override
    public <T> HasWhenClause willReturn(T result) {
        this.result = returnValue(result);
        return this;
    }

    @Override
    public <T extends Throwable> HasWhenClause willThrow(T excpt) {
        result = throwException(excpt);
        return this;
    }

    @Override
    public HasWhenClause will(InvocationResultProvider result) {
        this.result = result;
        return this;
    }

    @Override
    public HasWhenClause willDelegateTo(InvocationResultProvider provider) {
        result = provider;
        return this;
    }

    @Override
    public HasExpectationClauses willInvoke(OccurrenceChecker occurrences) {
        this.occurrences = occurrences;
        return this;
    }

    @Override
    public HasExpectationClauses willInvoke(int n) {
        occurrences = exactly(n);
        return this;
    }

    @Override
    public <T> T of(T mock) {
        return prepareToBuild(mock);
    }

    @Override
    public <T> T when(T mock) {
        return prepareToBuild(mock);
    }
}
