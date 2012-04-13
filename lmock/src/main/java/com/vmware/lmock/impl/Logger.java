/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.impl;

import com.vmware.lmock.trace.ActivityLogger;
import com.vmware.lmock.trace.Trace;

/**
 * A specific implementation of loggers for this package.
 *
 * <p>
 * Provides a factory to produce loggers inter-acting with the trace interface.
 * </p>
 */
final class Logger {

    /** Class of the object using this logger. */
    private final Class<?> clazz;

    void trace(String methodName, Object... data) {
        writeIfNeeded(Trace.getActivityLogger(), clazz, methodName, data);
    }

    /**
     * Creates a new logger used by a given class of objects.
     *
     * @param clazz
     *            the user's class
     */
    private Logger(Class<?> clazz) {
        this.clazz = clazz;
    }

    /**
     * Appends a part of an output message to a string builder.
     *
     * @param data
     *            the object that needs to be added to the final message
     * @param builder
     *            the output builder
     */
    private static void appendObjectToMessage(Object data, StringBuilder builder) {
        builder.append(' ');
        if (data != null) {
            // Of course the data can be a mock, so we must avoid generating a fake invocation...
            builder.append(Mock.getObjectOrMock(data).toString());
        } else {
            builder.append("<null>");
        }
    }

    /**
     * Posts a message to the activity logger, if any.
     *
     * <p>
     * This method is already perform the automatic string conversion from objects, so that the invoker will no lose
     * time making it if useless.
     * </p>
     *
     * @param activityLogger
     *            the target activity logger, <code>null</code> if none.
     * @param clazz
     *            class of the invoking object
     * @param method
     *            method issuing this trace
     * @param data
     *            the data sent to the activity logger
     */
    private void writeIfNeeded(ActivityLogger activityLogger, Class<?> clazz, String method, Object... data) {
        if (data != null && activityLogger != null) {
            StringBuilder builder = new StringBuilder(512);
            builder.append("[Lmock.");
            builder.append(clazz.getSimpleName());
            builder.append('.');
            builder.append(method);
            builder.append("()]");

            for (Object current : data) {
                appendObjectToMessage(current, builder);
            }

            activityLogger.trace(builder.toString());
        }
    }

    /**
     * Creates a logger for a given class of objects.
     *
     * @param clazz
     *            class of the invoking method
     * @return The new tracer.
     */
    static Logger get(final Class<?> clazz) {
        return new Logger(clazz);
    }
}
