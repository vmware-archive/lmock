/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.impl;

/**
 * A common interface to create a list of expectation.
 *
 * <p>
 * This root class handles the common part of an expectation specification,
 * providing:
 * </p>
 * <ul>
 * <li>A list of expectation builders, constructed on the fly</li>
 * <li>Base methods used by programmers to construct their list</li>
 * </ul>
 *
 * <p>
 * The result of the construction process is a list of expectations, produced by
 * <code>createExpectations</code>.
 * </p>
 */
class ExpectationListBuilder extends InvocationCheckerListBuilder<Expectation, ExpectationBuilder> {
    @Override
    protected ExpectationBuilder createBuilder(Object object,
      InvocationCheckerClosureHandler closureHandler) {
        return new ExpectationBuilder(object, closureHandler);
    }

    /**
     * Creates and registers a new builder for an expectation.
     *
     * @param object
     *            the mock for which the expectation applies
     * @param closureHandler
     *            handles the closure of the specification, or null
     */
    protected void createExpectation(Object object,
      InvocationCheckerClosureHandler closureHandler) {
        registerCheckerBuilder(object, closureHandler);
    }

    /**
     * Creates the list of expectations registered when constructing this.
     *
     * @return The table of built expectations.
     */
    protected Expectation[] createExpectations() {
        // Returned value.
        Expectation[] result = new Expectation[getListSize()];
        int index = 0;

        for (Expectation expectation : this) {
            result[index++] = expectation;
        }

        return result;
    }
}
