/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.example.tutorial;

import static com.vmware.lmock.checker.Occurrences.exactly;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import com.vmware.lmock.impl.Mock;
import com.vmware.lmock.impl.Scenario;
import com.vmware.lmock.impl.Story;

/**
 * Tutorial example: introducing the basic concepts.
 */
public class SimpleMockExample {
    @SuppressWarnings("unchecked")
    private final List<String> stringList = Mock.getObject(List.class);
    Scenario scenario = new Scenario() {
        {
            expect(stringList).get(0);
            willReturn(null).occurs(exactly(1));
            expect(stringList).add("hello");
            occurs(exactly(1));
        }
    };

    @Test
    public void testWillPass() {
        Story story = Story.create(scenario);
        story.begin();
        if (stringList.get(0) == null) {
            stringList.add("hello");
        }
        story.end();
    }

    @Test
    public void testWillFail() {
        Story story = Story.create(scenario);
        story.begin();
        stringList.add("hello");
        story.end();
    }

    @Test
    public void testWillPass2() {
        Story story = Story.create(new Scenario() {
            {
                expect(stringList).get(0);
                occurs(1);
                willReturn(null);
                expect(stringList).add("hello");
                occurs(1);
                expect(stringList).get(0);
                willReturn("hello");
            }
        });
        story.begin();
        if (stringList.get(0) == null) {
            stringList.add("hello");
            assertEquals("hello", stringList.get(0));
        } else {
            fail("was not expecting data in the list...");
        }
        story.end();
    }

    @Test
    public void testWillFail2() {
        Story story = Story.create(new Scenario() {
            {
                expect(stringList).get(0);
                occurs(1);
                willReturn(null);
                expect(stringList).add("hello");
                occurs(1);
                expect(stringList).get(0);
                willReturn("hello");
                expect(stringList).get(1);
                willThrow(new IndexOutOfBoundsException());
            }
        });
        story.begin();
        if (stringList.get(0) == null) {
            stringList.add("hello world");
            assertEquals("hello", stringList.get(0));
        } else {
            fail("was not expecting data in the list...");
        }
        story.end();
    }
}
