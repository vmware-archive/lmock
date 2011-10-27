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

/**
 * Dispatches data to threads.
 *
 * <p>
 * A thread dispatcher is defining a two stages process: the population phase
 * and the dispatching phase.
 * </p>
 * <p>
 * During the population phase, the user provides thread specific information,
 * along with a checker that allows to identify the corresponding thread at
 * runtime. An association checker/data is maintained for one and only one
 * thread.
 * </p>
 * <p>
 * During the dispatching phase, the dispatcher is requested to fetch the
 * current thread with a data. To do so, it checks if this thread is already
 * known, in which case it returns the associated data, otherwise it tries
 * to match the thread with one of the checker defined during the population
 * phase.
 * </p>
 *
 * @param <T>
 *            the type of data associated to threads
 */
public class MTDispatcher<T> {
    /** The list of registered (known) thread. */
    private final ThreadList<T> knownThreads = new ThreadList<T>();
    /** The list of checkers. */
    private final ThreadMatcher<T> matcher = new ThreadMatcher<T>();

    /**
     * Registers a checker for a given thread, along with data.
     *
     * @param checker
     *            the registered thread checker
     * @param data
     *            the associated data
     * @param callback
     *            if not <code>null</code>, called when a matching thread is
     *            found.
     */
    public synchronized void register(ThreadChecker checker, T data,
      ThreadMatcherNotificationHandler callback) {
        matcher.registerNewChecker(checker, data, callback);
    }

    /**
     * Registers a set of data associated with threads following a known
     * sequence to start.
     *
     * <p>
     * The goal of this function is to implement a pretty simple pattern in
     * which the user knows in which order the threads will appear in the story.
     * </p>
     * <p>
     * In this case, he may use this method, by specifying the thread data with
     * respect to this sequence.
     * </p>
     *
     * <p>
     * <b>BE CAREFUL</b>: the sequence here is not based on the moment the
     * threads are created but the order in which they invoke a mock
     * contributing to the story.
     * </p>
     * <p>
     * For example, say that you have two mocks (<code>m1</code>,
     * <code>m2</code>) and two threads <code><i>T1</i></code> and
     * <code><i>T2</i></code>. Assume that the sequence of code is:
     * </p>
     * <pre><code>
     *          T1
     *          ...                 T2
     *          ...                 invoke m1
     *          invoke m2           ...
     * </code></pre>
     * <p>
     * Although <i>T1</i> starts before <i>T2</i>, the second task invokes
     * a mock before the first one. So the sequence to consider is
     * <code>(<i>T2</i>, <i>T1</i>)</code>.
     * </p>
     *
     * @param data
     *            the sequence of associated data
     */
    public synchronized void registerSequence(T... data) {
        for (T currentData : data) {
            register(ThreadChecker.anyThread, currentData, null);
        }
    }

    /**
     * Tries to associate one thread with data.
     *
     * @param thread
     *            the requested thread
     * @return The thread data, always valid.
     * @throws ThreadNotFoundException
     *            The current thread is unknown by the system.
     */
    private T getThreadData(Thread thread) {
        T data = knownThreads.getData(thread);
        if (data == null) {
            data = matcher.searchDataForThread(thread);
            knownThreads.addOrUpdate(thread, data);
        }
        return data;
    }

    /**
     * Tries to associate the current thread with data.
     *
     * @return The thread data, always valid.
     * @throws ThreadNotFoundException
     *            The current thread is unknown by the system.
     */
    public synchronized T getCurrentData() {
        return getThreadData(Thread.currentThread());
    }
}
