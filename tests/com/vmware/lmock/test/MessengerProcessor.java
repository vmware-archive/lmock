/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.test;

/**
 * Standard interface of a test object that processes messages received by
 * messengers.
 */
public interface MessengerProcessor {
    /**
     * Processes a message.
     *
     * @param message
     *            the received message
     */
    public void process(String message);
}
