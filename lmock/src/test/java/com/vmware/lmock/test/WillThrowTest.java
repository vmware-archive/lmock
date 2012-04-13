/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.test;

import static com.vmware.lmock.test.Dalton.jack;
import static com.vmware.lmock.test.Dalton.joe;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.vmware.lmock.impl.InvocationResult;
import com.vmware.lmock.impl.Scenario;
import com.vmware.lmock.impl.Story;
import com.vmware.lmock.masquerade.Schemer;
import com.vmware.lmock.test.Dalton.SpecialDaltonException;

/**
 * Validation of the willClause clause in stories.
 *
 * <p>
 * This series of tests verify that the thrown exceptions specified in
 * expectations are what stories actually see.
 * </p>
 */
public class WillThrowTest {
    /**
     * Verifies that an exception declared by a willThrow clause is actually
     * thrown.
     *
     * @throws SpecialDaltonException
     */
    @Test
    public void testWillThrow() throws SpecialDaltonException {
        final SpecialDaltonException instance = new SpecialDaltonException(
          "run away!");
        Story story = Story.create(new Scenario() {
            {
                expect(joe).bother();
                willThrow(instance);
            }
        });

        story.begin();
        try {
            joe.bother();
            fail("expected exception not thrown");
        } catch (SpecialDaltonException e) {
            assertEquals(instance, e);
        }
        story.end();
    }

    /**
     * Verifies that an exception declared by a willThrow clause is actually
     * thrown.
     *
     * @throws SpecialDaltonException
     */
    @Test
    public void testWillThrowMA() throws SpecialDaltonException {
        Schemer.begin();
        final SpecialDaltonException instance = new SpecialDaltonException(
          "run away!");
        Schemer.willThrow(instance).when(joe).bother();
        try {
            joe.bother();
            fail("expected exception not thrown");
        } catch (SpecialDaltonException e) {
            assertEquals(instance, e);
        }
        Schemer.end();
    }

    /**
     * Uses the <code>will</code> clause provided by masquerades to specify an
     * exception.
     */
    @Test
    public void testWillThrowExceptionMA() throws SpecialDaltonException {
        Schemer.begin();
        final IllegalArgumentException instance = new IllegalArgumentException();
        Schemer.will(InvocationResult.throwException(instance)).when(joe).ping(Schemer.anyOf(Dalton.class));

        try {
            joe.ping(jack);
            fail("expected exception not thrown");
        } catch (IllegalArgumentException e) {
            assertEquals(instance, e);
        }

        Schemer.end();
    }
}
