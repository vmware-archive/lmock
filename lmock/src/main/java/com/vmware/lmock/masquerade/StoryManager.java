/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.masquerade;

import com.vmware.lmock.impl.Cleaner;
import com.vmware.lmock.impl.Story;
import com.vmware.lmock.mt.Actor;

/**
 * Drives a story lead by a masquerade.
 *
 * <p>
 * Manages an "ongoing story" (created by <code>createNewStory</code> and
 * terminated by <code>endStory</code>) and allows to register a new expectation
 * or scenario on the fly.
 * </p>
 */
class StoryManager {
    /** The ongoing story, <code>null</code> if nothing runs now. */
    private Story story;

    /** @return <code>true</code> if a story is ongoing. */
    protected boolean isStoryOngoing() {
        return story != null;
    }

    /**
     * Creates a new story and begins it.
     *
     * <p>
     * The story is registered with a set of actors contributing to this
     * story, assuming that there's always an implicit actor (the <i>default</i>
     * actor) for the thread that controls the test.
     * </p>
     *
     * @param actors
     *            the list of actors involved in the story
     * @return The created story.
     */
    protected Story createAndBeginNewStory(Actor... actors) {
        // Be sure that we restart from a clean environment.
        // We can't end the story at this level, because we may refer to
        // old (but incomplete) elements, leading to spurious unsatisfied
        // occurrence exceptions.
        Cleaner.cleanup();
        story = Story.createWithMultipleActors(actors);
        story.begin();
        return story;
    }

    /**
     * Ends and unregisters the current story.
     *
     * @throws UnsatisfiedOccurrenceException
     *             The ongoing story is incomplete.
     */
    protected void endStory() {
        try {
            if (isStoryOngoing()) {
                story.end();
            }
        } finally {
            story = null;
        }
    }

    /**
     * @return The ongoing story, <code>null</code> if none is ongoing.
     */
    protected Story getStory() {
        return story;
    }
}
