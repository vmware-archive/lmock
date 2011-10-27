/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.exception;

import com.vmware.lmock.impl.StoryTrack;

/**
 * Base errors raised by the mocking framework.
 *
 * <p>
 * These errors are issued the story is not compliant with the scenario.
 * </p>
 */
public class ExpectationError extends AssertionError {
    /** Class version, for serialization. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new exception, along with an error message.
     *
     * @param msg
     *            the string representing the invocation that lead to this error
     */
    public ExpectationError(String msg) {
        super("expectation error: " + msg + "\n" + StoryTrack.get());
    }
}
