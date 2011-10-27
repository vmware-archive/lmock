/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.mt;

/**
 * Objects listening to the modifications of actors during the test.
 *
 * <p>
 * These listeners are plugged to an actor to catch a modification of the associated scenario or stubs.
 * </p>
 */
public interface ActorUpdateListener {
    /**
     * Called back when the actor is following a new scenario or uses new stubs.
     *
     * <p>
     * Such routine is called upon invocations of <code>following</code> and <code>using</code>.
     * </p>
     *
     * @param actor
     *            the actor for which we notify an update
     * @param userArgument
     *            a user supplied argument, registered when creating the listener
     */
    public void onUpdateOfScenarioOrStubs(Actor actor, Object userArgument);
}
