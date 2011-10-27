/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.clauses;

import com.vmware.lmock.impl.Stubs;

/**
 * Provides a clause to append a set of stubs to an existing set.
 */
public interface HasAppendStubsClause {
    /**
     * Adds a set of stubs to an existing set.
     *
     * @param stubs
     *            the list of added stubs
     */
    public void append(Stubs stubs);
}
