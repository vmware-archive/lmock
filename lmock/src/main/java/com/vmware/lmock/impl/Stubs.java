/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.impl;

import com.vmware.lmock.clauses.HasArgumentSpecificationClauses;
import com.vmware.lmock.clauses.HasInvocationResultSpecificationClauses;
import com.vmware.lmock.clauses.HasStubSpecificationClauses;

/**
 * Definition of stub lists.
 *
 * <p>
 * A stub list represents a sequence of invocations to mocks automatically
 * handled by stubs.
 * </p>
 *
 * <p>
 * Basically, the user must describe the stub list upon its construction, by
 * putting a block of code describing the stubs:
 * </p>
 *
 * <pre>
 * <code>
 *     new Stubs() {{
 *         ...STUBS...
 *     }};
 * </code>
 * </pre>
 *
 * <p>
 * The expression of a stub is the combination of the expected invocation
 * itself, plus additional information, driven by the <code>stub</code> method:
 * </p>
 *
 * <pre>
 * <code>
 *         stub(MOCK).INVOCATION;
 *         STUB_INFORMATION_IF_ANY
 * </code>
 * </pre>
 * <p>
 *
 * <p>
 * Where MOCK is the invoked mock and INVOCATION is a call to the expected
 * method, along with either the exact arguments, or arguments specified by
 * 'with' clauses.
 * </p>
 * <p>
 * The stub information consist in the result of the stub invocation.
 * </p>
 */
public class Stubs extends StubListBuilder implements
  HasStubSpecificationClauses, HasArgumentSpecificationClauses,
  HasInvocationResultSpecificationClauses<Stub> {
    /**
     * Basic setup when creating a new scenario.
     */
    public Stubs() {
        super();
    }

    @Override
    public Stub stub() {
        return getCurrentChecker();
    }

    @Override
    public <T> T stub(T object) {
        createStub(object, null);
        return object;
    }

    @Override
    public <T> T stub(T object, InvocationCheckerClosureHandler closureHandler) {
        createStub(object, closureHandler);
        return object;
    }

    @Override
    public Stub willReturn(Object result) {
        return stub().willReturn(result);
    }

    @Override
    public Stub willThrow(Throwable excpt) {
        return stub().willThrow(excpt);
    }

    @Override
    public Stub will(InvocationResultProvider result) {
        return stub().will(result);
    }

    @Override
    public Stub willDelegateTo(InvocationResultProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("null provider specified");
        }

        return stub().willDelegateTo(provider);
    }
}
