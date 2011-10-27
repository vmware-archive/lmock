/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.impl;

import com.vmware.lmock.exception.ExpectationError;
import com.vmware.lmock.exception.LMRuntimeException;

/**
 * Stores the exceptions and errors run during a test.
 *
 * <p>
 * An exception guard is necessary for multi-threaded tests, in which each
 * thread potentially raises an exception. In this case, we would abort to
 * stall the overall test execution when one thread fails and report that
 * failure onto the main thread.
 * </p>
 *
 * <p>
 * An exception guard provides methods to:
 * </p>
 * <ul>
 * <li>record the very first exception that have happened</li>
 * <li>give the current state of execution (i.e. an exception is recorded or not)</li>
 * <li>throw the recorded exception on demand</li>
 * </ul>
 *
 * <p>
 * In practice, the system implements a single guard, which can be accessed
 * at any time from any method, but exceptions are guarded if and only if
 * the guard is enabled.
 * </p>
 *
 * <p>
 * <i>Implementation note: the choice for a singleton is more a matter of
 * simplicity since in practice guarded exceptions can be thrown by the
 * invocation processor or by a mock (unexpected invocation).</i>
 * </p>
 */
final class ExceptionGuard {

    /** Logs the guard activity. */
    private static final Logger logger = Logger.get(ExceptionGuard.class);
    /** The recorded exception, <code>null</code> if everything is OK. */
    private LMRuntimeException lastException;
    /** The recorded error, <code>null</code> if everything is OK. */
    private ExpectationError lastError;
    /** Set to <code>true</code> when the exception guard is turned on. */
    private boolean enabled;
    /** The unique exception guard in the system. */
    private static final ExceptionGuard guard = new ExceptionGuard();

    /**
     * Creates the exception guard, disabled for the moment.
     */
    private ExceptionGuard() {
        this.enabled = false;
    }

    /**
     * @return <code>true</code> if no exception or error is reported yet.
     */
    private boolean recordedNothing() {
        return lastException == null && lastError == null;
    }

    /**
     * Records an exception, if no exception or error occurred yet.
     *
     * @param excpt
     *            the exception
     */
    void record(LMRuntimeException excpt) {
        logger.trace("record", "LMRuntimeException", excpt);
        if (enabled && recordedNothing()) {
            this.lastException = excpt;
        }
    }

    /**
     * Records an expectation error, if no exception or error occurred yet.
     *
     * @param error
     *            the error
     */
    void record(ExpectationError error) {
        logger.trace("record", "ExpectationError", error);
        if (enabled && recordedNothing()) {
            this.lastError = error;
        }
    }

    /**
     * Clears the recorded exceptions and changes the state of the guard.
     *
     * @param enabled
     *            <code>true</code> to enable the guard
     */
    private void clearToState(boolean enabled) {
        lastException = null;
        lastError = null;
        this.enabled = enabled;
    }

    /** If an exception or error was guarded, throw it. */
    void throwIfPresent() {
        if (enabled) {
            if (lastException != null) {
                logger.trace("throwIfPresent", "throwing guarded exception");
                throw lastException;
            } else if (lastError != null) {
                logger.trace("throwIfPresent", "throwing guarded error");
                throw lastError;
            }
        }
    }

    /**
     * Enables the exception guard.
     *
     * <p>
     * From that point, the functions to record, report and throw exceptions
     * are active.
     * </p>
     */
    void enable() {
        logger.trace("enable");
        clearToState(true);
    }

    /**
     * Disables the exception guard.
     *
     * <p>
     * The guard is cleaned up (nothing recorded). Any further invocation
     * to record or throw exceptions is dummy.
     * </p>
     */
    void disable() {
        logger.trace("disable");
        clearToState(false);
    }

    /** @return The singleton. */
    static ExceptionGuard get() {
        return guard;
    }
}
