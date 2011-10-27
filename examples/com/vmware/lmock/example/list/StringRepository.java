/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.example.list;

import java.util.List;

/**
 * A repository of strings, using lists.
 *
 * <p>
 * Implements a FIFO policy to store and retrieve strings.
 * </p>
 */
public class StringRepository {
    /** List of strings currently in the repository. */
    private final List<String> stringList;

    /**
     * Creates a new repository, using a specific list to store the strings.
     *
     * @param stringList
     *            the list into which this object stores and retrieves strings.
     */
    public StringRepository(List<String> stringList) {
        this.stringList = stringList;
    }

    /**
     * Extracts a string from the list, using a FIFO policy.
     *
     * @return The extracted string, null if the FIFO is empty.
     */
    public String get() {
        if (stringList.isEmpty()) {
            return null;
        } else {
            String result = stringList.remove(0);
            return result;
        }
    }

    /**
     * Adds a string to the repository.
     *
     * @param data
     *            the new string
     */
    public void put(String data) {
        stringList.add(data);
    }
}
