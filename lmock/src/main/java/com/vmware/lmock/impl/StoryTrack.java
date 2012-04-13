/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * Tracks a story as it goes.
 *
 * <p>
 * The story track is a singleton used to keep track of the different
 * expectations satisfied when playing stories. It basically consists in lists
 * of expectation reports stored by story trackers. In practice, there will be
 * one story tracker per thread.
 * </p>
 *
 * <p>
 * As a singleton, the story track can be checked at any moment, even if no
 * story is ongoing (which simplifies post-mortem analysis and reports of
 * invocations on mocks that do not contribute to the current story).
 * </p>
 */
public final class StoryTrack {
    /** THE story track, accessible via <code>get</code>. */
    private static final StoryTrack track = new StoryTrack();
    /** List of registered story trackers. */
    private final List<StoryTracker> trackers = new ArrayList<StoryTracker>();

    /**
     * @return The story track.
     */
    public static StoryTrack get() {
        return track;
    }

    /**
     * Registers a new story tracker reported by the track.
     *
     * <p>
     * Does nothing if the tracker is already registered.
     * </p>
     *
     * @param tracker
     *            the tracker
     */
    void registerTrackerIfNeeded(StoryTracker tracker) {
        if (!trackers.contains(tracker)) {
            trackers.add(tracker);
        }
    }

    /**
     * Unregisters ALL the trackers.
     */
    void clearTrackers() {
        trackers.clear();
    }

    /**
     * Produces a report of the story as plain text.
     *
     * <p>
     * The output includes the tracking of each thread, including the list of expectations that were successfully
     * fetched by Lmock.
     * </p>
     *
     * @return The story track.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(64);

        for (StoryTracker tracker : trackers) {
            builder.append(tracker.toString());
        }

        return builder.toString();
    }

    /**
     * @return The total number of reports in every registered tracker.
     */
    public int size() {
        int result = 0;

        for (StoryTracker tracker : trackers) {
            result += tracker.size();
        }

        return result;
    }
}
