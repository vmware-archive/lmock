/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.test;

import static com.vmware.lmock.test.Dalton.joe;

import org.junit.Test;
import static org.junit.Assert.*;

import com.vmware.lmock.impl.InvocationResultProvider;
import com.vmware.lmock.impl.Scenario;
import com.vmware.lmock.impl.Stubs;
import com.vmware.lmock.test.Dalton.SpecialDaltonException;

/**
 * Validation of a willDelegateTo specification.
 *
 * <p>
 * Validates the clause in the context of scenarios and stubs.
 * </p>
 */
public class WillDelegateToSpecificationTest {
    /**
     * Verifies that the clause is registered.
     *
     * @throws SpecialDaltonException
     */
    @Test
    public void testWillDelegateToAProvider() throws SpecialDaltonException {
        final InvocationResultProvider provider = new InvocationResultProvider() {
            @Override
            public Object apply() throws Throwable {
                return null;
            }

            @Override
            public String toString() {
                return "the provider under test";
            }
        };

        new Scenario() {
            {
                expect(joe).bother();
                willDelegateTo(provider);
                String instance = expect().toString();
                assertTrue(instance.contains(":the provider under test"));
            }
        };

        new Stubs() {
            {
                stub(joe).ping();
                willDelegateTo(provider);
                String instance = stub().toString();
                assertTrue(instance.contains(":the provider under test"));
            }
        };
    }

    /**
     * Verifies that null providers are not allowed.
     *
     * @throws com.vmware.lmock.test.Dalton.SpecialDaltonException
     */
    @Test
    public void testNullProviderInScenario() throws SpecialDaltonException {
        new Scenario() {
            {
                expect(joe).bother();
                try {
                    willDelegateTo(null);
                    fail("delegated invocation to a null provider");
                } catch (IllegalArgumentException e) {
                }
            }
        };
    }

    /**
     * Verifies that null providers are not allowed.
     *
     * @throws com.vmware.lmock.test.Dalton.SpecialDaltonException
     */
    @Test
    public void testNullProviderInStubs() throws SpecialDaltonException {
        new Stubs() {
            {
                stub(joe).bother();
                try {
                    willDelegateTo(null);
                    fail("delegated invocation to a null provider");
                } catch (IllegalArgumentException e) {
                }
            }
        };
    }
}
