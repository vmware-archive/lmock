/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.checker;

/**
 * Controls the occurrences of an expectation.
 *
 * <p>
 * Such a checker is used by the story controller to verify that invocations are
 * legal. This check is based on two information:
 * </p>
 * <ul>
 * <li><code>canEndNow</code>: if true, then the pointer can move to the next
 * known expectation. But it may also remain where it stands</li>
 * <li><code>hasReachedLimit</code>: the current expectation is not valid
 * anymore. The pointer MUST move to the next expectation.</li>
 * </ul>
 *
 * <p>
 * An occurrence checker receives two notifications from the story controller:
 * </p>
 * <ul>
 * <li><code>increment</code>: the invocation has been validated, the pointer
 * remains on the same expectation until the next invocation</li>
 * <li><code>reset</code>: the story is restarting</li>
 * </ul>
 */
public interface OccurrenceChecker {
    /**
     * Invoked when the story controller has validated the related invocation.
     *
     * <p>
     * The pointer remains on the same position, so subsequent calls to this
     * checker will occur in the future.
     * </p>
     */
    public void increment();

    /**
     * Resets the checker.
     */
    public void reset();

    /**
     * Tells whether the expectation has reached the limit of its occurrences.
     *
     * <p>
     * In that case, the associated invocation is not allowed anymore.
     * </p>
     *
     * @return <code>true</code> if no additional occurrence is allowed.
     */
    public boolean hasReachedLimit();

    /**
     * Tells whether the occurrences of the expectation are satisfied.
     *
     * @return <code>true</code> if the pointer is allowed to move to the next
     *         expectation.
     */
    public boolean canEndNow();
}
