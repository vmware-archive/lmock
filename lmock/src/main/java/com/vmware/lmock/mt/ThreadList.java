/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.mt;

import java.util.HashMap;
import java.util.Map;

/**
 * Maintains a list of threads, along with data.
 *
 * @param <T>
 *            The type of data associated to the threads
 */
class ThreadList<T> {
    /** The thread list. Use the id for performance and safety reasons. */
    private final Map<Long, T> map = new HashMap<Long, T>();

    /**
     * Adds a thread to a list, if not registered yet.
     *
     * @param thread
     *            the new or existing thread
     * @param data
     *            the data associated to the thread
     */
    void addOrUpdate(Thread thread, T data) {
        map.put(thread.getId(), data);
    }

    /**
     * Gets the data associated to a thread.
     *
     * @param thread
     *            the requested thread
     * @return The thread data, null if not found.
     */
    T getData(Thread thread) {
        return map.get(thread.getId());
    }
}
