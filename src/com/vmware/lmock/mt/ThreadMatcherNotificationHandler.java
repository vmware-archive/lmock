/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.mt;

/**
 * Objects called back by a thread matcher when a matching thread is found.
 */
public interface ThreadMatcherNotificationHandler {
    /**
     * Called when a matching thread is found.
     *
     * @param thread
     *            the fetched thread
     */
    public void onMatchingThread(Thread thread);
}
