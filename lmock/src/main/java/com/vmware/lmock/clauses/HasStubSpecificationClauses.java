/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.clauses;

import com.vmware.lmock.exception.IllegalClauseException;
import com.vmware.lmock.exception.MockReferenceException;
import com.vmware.lmock.impl.InvocationCheckerClosureHandler;
import com.vmware.lmock.impl.Stub;

/**
 * Defines clauses that allow to create stubs.
 */
public interface HasStubSpecificationClauses {
    /**
     * @return The current stub under construction.
     * @throws IllegalClauseException
     *             Not currently specifying a stub.
     */
    public Stub stub();

    /**
     * Creates a new stub using a specific mock.
     *
     * @param <T>
     *            type of the invoked object
     * @param object
     *            the associated mock
     * @return The associated mock.
     * @throws MockReferenceException
     *             The specified object is not a mock.
     */
    public <T> T stub(T object);

    /**
     * Creates a new stub using a specific mock and a handler to the closure of
     * the stub specification.
     *
     * @param <T>
     *            type of the invoked object
     * @param object
     *            the associated mock
     * @param closureHandler
     *            handles the closure of the specification, or null
     * @return The associated mock.
     * @throws MockReferenceException
     *             The specified object is not a mock.
     */
    public <T> T stub(T object, InvocationCheckerClosureHandler closureHandler);
}
