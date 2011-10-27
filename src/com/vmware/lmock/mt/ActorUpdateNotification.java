/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.mt;

/**
 * Notification sent by an actor to specify that it was updated.
 */
class ActorUpdateNotification {
    private final ActorUpdateListener listener;
    private final Object userArgument;

    /**
     * Creates a new notification.
     *
     * @param listener
     *            the listener that will be notified of an update of the actor
     * @param userArgument
     *            a user supplied argument, registered when creating the listener
     */
    ActorUpdateNotification(ActorUpdateListener listener, Object userArgument) {
        this.listener = listener;
        this.userArgument = userArgument;
    }

    /**
     * Invokes the registered listener.
     *
     * @param actor
     *            the actor for which we notify a modification
     */
    void sendNotification(Actor actor) {
        listener.onUpdateOfScenarioOrStubs(actor, userArgument);
    }
}
