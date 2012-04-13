/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.trace;

/**
 * Sets up a trace produced by Lmock for external support.
 *
 * <p>
 * This singleton allows to trace the key activities of the framework in order to
 * be able to understand potential errors. This is mainly to provide an accurate
 * report of the mocks when executing tests.
 * </p>
 *
 * <p>
 * The trace module is setup by specifying an external tool, in charge of actually
 * displaying the output (an <code>ActivityLogger</code> object), using
 * <code>reportActivityTo</code>. This is canceled if invoking <code>dontReportActivity</code>.
 * </p>
 */
public final class Trace {

    /** Singleton in charge of managing traces. */
    private static final Trace trace = new Trace();
    /** The user supplied activity logger, <code>null</code> if none defined. */
    private ActivityLogger activityLogger;

    /**
     * Defines an activity logger used by Lmock to output its activity.
     *
     * @param logger
     *            the logger
     */
    private void setActivityLogger(ActivityLogger logger) {
        this.activityLogger = logger;
    }

    /**
     * Defines an activity logger used by Lmock to output its activity.
     *
     * @param logger
     *            the logger
     */
    public static void reportActivityTo(ActivityLogger logger) {
        trace.setActivityLogger(logger);
    }

    /** Instructs not to report any activity. */
    public static void dontReportActivity() {
        trace.setActivityLogger(null);
    }

    /** @return The user specified activity logger, <code>null</code> if none. */
    private ActivityLogger activityLogger() {
        return activityLogger;
    }

    /** @return The current activity logger, <code>null</code> if none.*/
    public static ActivityLogger getActivityLogger() {
        return trace.activityLogger();
    }
}
