/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.mt;

import com.vmware.lmock.checker.ThreadChecker;
import com.vmware.lmock.exception.ThreadNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Fetches threads with data.
 *
 * <p>
 * A matcher is provided a list of thread checkers invoked whenever a new
 * thread is identified and is not yet associated to data.
 * </p>
 * <p>
 * Each checker is associated to a thread data, which is returned when a thread
 * is matched, thus creating the association between the threads and their data.
 * </p>
 * <p>
 * The checkers, registered by <code>registerNewChecker</code>, are removed from
 * the list whenever they recognized one thread. This allows to provide multiple
 * checkers with similar properties without any confusion.
 * </p>
 *
 * @param <T>
 *            the type of data associated to threads
 */
class ThreadMatcher<T> {
    /**
     * Association of checkers with thread data.
     */
    private class MatcherData {
        private final ThreadChecker checker;
        private final T data;
        private final ThreadMatcherNotificationHandler callback;

        /**
         * Creates a new pair of data.
         *
         * @param checker
         *            the checker
         * @param data
         *            the data that will be associated to the thread
         * @param callback
         *            called back when the matching thread is found
         *            (<code>null</code> if none)
         */
        MatcherData(ThreadChecker checker, T data,
          ThreadMatcherNotificationHandler callback) {
            this.checker = checker;
            this.data = data;
            this.callback = callback;
        }

        /** @return The checker enclosed by this. */
        ThreadChecker getChecker() {
            return checker;
        }

        /** @return The data that will be associated to the fetched thread. */
        T getData() {
            return data;
        }

        /**
         * Calls the specified callback routine when a thread is found.
         *
         * @param thread
         *            the fetched thread
         */
        void callbackWhenThreadFound(Thread thread) {
            if (callback != null) {
                callback.onMatchingThread(thread);
            }
        }
    }
    /** The list of checkers. */
    private final List<MatcherData> checkers = new ArrayList<MatcherData>();

    /**
     * Registers a new checker to consider when facing an unknown thread.
     *
     * @param checker
     *            the new checker
     * @param data
     *            the data that will be associated to the thread when fetched
     * @param callback
     *            if not <code>null</code>, called when a matching thread is
     *            found.
     */
    synchronized void registerNewChecker(ThreadChecker checker, T data,
      ThreadMatcherNotificationHandler callback) {
        checkers.add(new MatcherData(checker, data, callback));
    }

    /**
     * Searches for a thread recognized by one registered checker.
     *
     * <p>
     * When the thread is matching a checker, that checker is unregistered.
     * </p>
     *
     * @param thread
     *            the checked thread
     * @return The assigned data.
     * @throws ThreadNotFoundException
     *            The specified thread was not recognized by the known checkers.
     */
    synchronized T searchDataForThread(Thread thread) {
        Iterator<MatcherData> iterator = checkers.iterator();

        while (iterator.hasNext()) {
            MatcherData data = iterator.next();
            if (data.getChecker().valueIsCompatibleWith(thread)) {
                iterator.remove();
                data.callbackWhenThreadFound(thread);
                return data.getData();
            }
        }

        throw new ThreadNotFoundException(thread);
    }
}
