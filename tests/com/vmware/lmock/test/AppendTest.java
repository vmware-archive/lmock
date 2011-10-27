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

import org.junit.Test;

import com.vmware.lmock.impl.Scenario;
import com.vmware.lmock.impl.Story;

/**
 * Test of the 'append' feature of invocation checker list builders.
 */
public class AppendTest {
    /**
     * Verifies that we can compose a new scenario by appending different pieces
     * together.
     */
    @Test
    public void testAppendScenarioToScenario() {
        Scenario joeScenario = new Scenario() {
            {
                expect(joe).ping(jack);
                occurs(1).willReturn(0);
                expect(joe).ping(william);
                occurs(1).willReturn(1);
                expect(joe).ping(averell);
                occurs(1).willReturn(2);
            }
        };
        Scenario jackScenario = new Scenario() {
            {
                expect(jack).ping(joe);
                occurs(1).willReturn(3);
                expect(jack).ping(william);
                occurs(1).willReturn(4);
                expect(jack).ping(averell);
                occurs(1).willReturn(5);
            }
        };
        Scenario williamScenario = new Scenario() {
            {
                expect(william).ping(joe);
                occurs(1).willReturn(6);
                expect(william).ping(jack);
                occurs(1).willReturn(7);
                expect(william).ping(averell);
                occurs(1).willReturn(8);
            }
        };

        Scenario scenario = new Scenario();
        scenario.append(joeScenario);
        scenario.append(jackScenario);
        scenario.append(williamScenario);

        // Run a story to verify that the scenario is OK.
        // It will fail if something was not considered.
        Story story = Story.create(scenario);

        story.begin();
        assertEquals(0, joe.ping(jack));
        assertEquals(1, joe.ping(william));
        assertEquals(2, joe.ping(averell));
        assertEquals(3, jack.ping(joe));
        assertEquals(4, jack.ping(william));
        assertEquals(5, jack.ping(averell));
        assertEquals(6, william.ping(joe));
        assertEquals(7, william.ping(jack));
        assertEquals(8, william.ping(averell));
        story.end();
    }
}
