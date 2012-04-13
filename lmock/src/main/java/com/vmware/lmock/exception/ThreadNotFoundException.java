/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.exception;

/**
 * Exceptions thrown when referencing a thread that is unknown by the system.
 */
public class ThreadNotFoundException extends MTException {
    /** Class version, for serialization. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates an error string for a given thread.
     *
     * @param thread
     *            the thread
     * @return The error string.
     */
    private static String makeThreadString(Thread thread) {
        StringBuilder builder = new StringBuilder(32);
        builder.append("thread ");
        builder.append("'");
        builder.append(thread.getName());
        builder.append("'");

        builder.append(" id=");
        builder.append(thread.getId());
        builder.append(" (");
        builder.append(thread.toString());
        builder.append(")");

        return builder.toString();
    }

    /**
     * Creates a new exception.
     *
     * @param thread
     *            the unrecognized thread
     */
    public ThreadNotFoundException(Thread thread) {
        super("could not match thread " + makeThreadString(thread));
    }
}
