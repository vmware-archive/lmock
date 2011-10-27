/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.trace;

/**
 * Public interface provided by users to let Lmock log its activity.
 *
 * <p>
 * When the trace mode is turned on, the framework calls that logger to output the traces it produces.
 * </p>
 */
public interface ActivityLogger {

    /**
     * Handles a message written by Lmock.
     *
     * @param message
     *            the message
     */
    public void trace(String message);
}
