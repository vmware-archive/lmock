/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.test;

import static com.vmware.lmock.masquerade.Schemer.begin;
import static com.vmware.lmock.masquerade.Schemer.end;
import static com.vmware.lmock.masquerade.Schemer.willDelegateTo;
import static com.vmware.lmock.test.Dalton.joe;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vmware.lmock.impl.InvocationResultProvider;
import com.vmware.lmock.impl.Scenario;
import com.vmware.lmock.impl.Story;

/**
 * Simple tests of the willDelegateTo clause.
 */
public class WillDelegateToTest {
    /**
     * Verifies that the delegation to a provider occurs.
     */
    @Test
    public void testWillDelegateTo() {
        final InvocationResultProvider provider = new InvocationResultProvider() {
            @Override
            public Object apply() throws Throwable {
                return 30222;
            }
        };

        Story story = Story.create(new Scenario() {
            {
                expect(joe).getInt();
                occurs(1).willDelegateTo(provider);
            }
        });
        story.begin();
        assertEquals(30222, joe.getInt());
        story.end();
    }

    /**
     * Verifies that the delegation to a provider occurs.
     */
    @Test
    public void testWillDelegateToMA() {
        final InvocationResultProvider provider = new InvocationResultProvider() {
            @Override
            public Object apply() throws Throwable {
                return 155678;
            }
        };

        begin();
        willDelegateTo(provider).when(joe).getInt();
        assertEquals(155678, joe.getInt());
        end();
    }
}
