/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.test;

import static com.vmware.lmock.test.Dalton.averell;
import static com.vmware.lmock.test.Dalton.jack;
import static com.vmware.lmock.test.Dalton.joe;
import static com.vmware.lmock.test.Dalton.william;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.vmware.lmock.impl.Scenario;
import com.vmware.lmock.impl.Story;
import com.vmware.lmock.impl.Stubs;
import com.vmware.lmock.masquerade.Schemer;

/**
 * Validation of stubs at runtime.
 */
public class StubTest {
    /**
     * Verifies that stubs work with empty scenarios.
     */
    @Test
    public void testSimpleStubInvocation() {
        Story story = Story.create(null, new Stubs() {
            {
                stub(joe).getInt();
                willReturn(999);
                stub(joe).getChar();
                willReturn('J');
            }
        });

        story.begin();
        for (int occur = 0; occur < 10; occur++) {
            assertEquals(999, joe.getInt());
            assertEquals('J', joe.getChar());
        }
        story.end();
    }

    /**
     * Verifies that stubs work with empty scenarios.
     */
    @Test
    public void testSimpleStubInvocationMA() {
        Schemer.begin();
        Schemer.willReturn(999).when(joe).getInt();
        Schemer.willReturn('J').when(joe).getChar();
        for (int occur = 0; occur < 10; occur++) {
            assertEquals(999, joe.getInt());
            assertEquals('J', joe.getChar());
        }
        Schemer.end();
    }

    /**
     * Verifies that the proper stub is triggered when specifying different
     * expectations of the same method.
     */
    @Test
    public void testMultipleStubsOnTheSameMethod() {
        Story story = Story.create(null, new Stubs() {
            {
                stub(joe).ping(joe, "hello");
                willReturn(0);
                stub(joe).ping(joe, "gimmeANumber");
                willReturn(66);
                stub(joe).ping(jack, "hello");
                willReturn(1);
                stub(joe).ping(william, "hello");
                willReturn(2);
                stub(joe).ping(averell, "hello");
                willReturn(3);
            }
        });

        story.begin();
        assertEquals(66, joe.ping(joe, "gimmeANumber"));
        assertEquals(3, joe.ping(averell, "hello"));
        assertEquals(2, joe.ping(william, "hello"));
        assertEquals(1, joe.ping(jack, "hello"));
        assertEquals(0, joe.ping(joe, "hello"));
        story.end();
    }

    /**
     * Verifies that the proper stub is triggered when specifying different
     * expectations of the same method.
     */
    @Test
    public void testMultipleStubsOnTheSameMethodMA() {
        Schemer.begin();
        Schemer.willReturn(0).when(joe).ping(joe, "hello");
        Schemer.willReturn(66).when(joe).ping(joe, "gimmeANumber");
        Schemer.willReturn(1).when(joe).ping(jack, "hello");
        Schemer.willReturn(2).when(joe).ping(william, "hello");
        Schemer.willReturn(3).when(joe).ping(averell, "hello");
        assertEquals(66, joe.ping(joe, "gimmeANumber"));
        assertEquals(3, joe.ping(averell, "hello"));
        assertEquals(2, joe.ping(william, "hello"));
        assertEquals(1, joe.ping(jack, "hello"));
        assertEquals(0, joe.ping(joe, "hello"));
        Schemer.end();
    }

    /**
     * Verifies that the stubs are prioritized by their order of declaration.
     */
    @Test
    public void testValidateStubPriority() {
        Story story = Story.create(null, new Stubs() {
            {
                stub(joe).ping(with(jack), anyOf(String.class));
                willThrow(new RuntimeException());
            }
        }, new Stubs() {
            {
                stub(joe).ping(with(jack), aNonNullOf(String.class));
                willReturn(-2);
                stub(joe).ping(jack, "woo");
                willReturn(-1);
            }
        });

        story.begin();
        assertEquals(-1, joe.ping(jack, "woo"));
        assertEquals(-2, joe.ping(jack, "waa"));
        try {
            joe.ping(jack, (String) null);
            fail("the final stub did not throw an exception");
        } catch (RuntimeException e) {
        }
        story.end();
    }

    /**
     * Verifies that the stubs are prioritized by their order of declaration.
     */
    @Test
    public void testValidateStubPriorityMA() {
        Schemer.begin();
        Schemer.willThrow(new RuntimeException()).when(joe).ping(Schemer.with(jack), Schemer.anyOf(String.class));
        Schemer.willReturn(-2).when(joe).ping(Schemer.with(jack), Schemer.aNonNullOf(String.class));
        Schemer.willReturn(-1).when(joe).ping(jack, "woo");

        assertEquals(-1, joe.ping(jack, "woo"));
        assertEquals(-2, joe.ping(jack, "waa"));
        try {
            joe.ping(jack, (String) null);
            fail("the final stub did not throw an exception");
        } catch (RuntimeException e) {
        }
        Schemer.end();
    }

    /**
     * Verifies that stubs prevail on expectations and relays to expectations if
     * no stub was found.
     */
    @Test
    public void testStubsOverwriteAndRelayExpectations() {
        Story story = Story.create(new Scenario() {
            {
                expect(joe).ping(aNonNullOf(Dalton.class), anyOf(String.class),
                  anyOf(String.class));
                willReturn(1);
                expect(jack).ping();
                willReturn(2);
            }
        }, new Stubs() {
            {
                stub(joe).ping(with(jack), with("dollars"), anyOf(String.class));
                willReturn(0);
            }
        });

        story.begin();
        assertEquals(0, joe.ping(jack, "dollars", "100"));
        assertEquals(1, joe.ping(jack, "euros", "10"));
        assertEquals(1, joe.ping(averell, "dollars", "0"));
        assertEquals(2, jack.ping());
        story.end();
    }

    /**
     * Verifies that stubs prevail on expectations and relays to expectations if
     * no stub was found.
     */
    @Test
    public void testStubsOverwriteAndRelayExpectationsMA() {
        Schemer.begin();
        Schemer.willReturn(1).when(joe).ping(Schemer.aNonNullOf(Dalton.class),
          Schemer.anyOf(String.class),
          Schemer.anyOf(String.class));
        Schemer.willReturn(2).when(jack).ping();
        Schemer.willReturn(0).when(joe).ping(Schemer.with(jack), Schemer.with("dollars"),
          Schemer.anyOf(String.class));

        assertEquals(0, joe.ping(jack, "dollars", "100"));
        assertEquals(1, joe.ping(jack, "euros", "10"));
        assertEquals(1, joe.ping(averell, "dollars", "0"));
        assertEquals(2, jack.ping());
        Schemer.end();
    }

    /**
     * A test that combines the result of a stub with an expectation.
     */
    @Test
    public void testStubCombinedWithExpecation() {
        Story story = Story.create(new Scenario() {
            {
                expect(joe).ping(averell);
                expect().occurs(1).willReturn(22);
            }
        }, new Stubs() {
            {
                stub(joe).next();
                willReturn(jack);
                stub(jack).next();
                willReturn(william);
                stub(william).next();
                willReturn(averell);
            }
        });

        story.begin();
        assertEquals(22, joe.ping(jack.next().next()));
        story.end();
    }

    /**
     * A test that combines the result of a stub with an expectation.
     */
    @Test
    public void testStubCombinedWithExpecationMA() {
        Schemer.begin();
        Schemer.willInvoke(1).willReturn(22).when(joe).ping(averell);
        Schemer.willReturn(jack).when(joe).next();
        Schemer.willReturn(william).when(jack).next();
        Schemer.willReturn(averell).when(william).next();
        assertEquals(22, joe.ping(jack.next().next()));
        Schemer.end();
    }
}
