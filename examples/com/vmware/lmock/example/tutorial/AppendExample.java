/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.example.tutorial;

import static com.vmware.lmock.checker.Occurrences.atLeast;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;

import com.vmware.lmock.impl.Mock;
import com.vmware.lmock.impl.Scenario;
import com.vmware.lmock.impl.Story;

/**
 * Tutorial example: an illustration of the append clause.
 */
public class AppendExample {
    @SuppressWarnings("unchecked")
    private final List<String> list = Mock.getObject(List.class);
    private final Scenario expectFirstElementReturnsNull = new Scenario() {
        {
            expect(list).get(0);
            expect().willReturn(null).occurs(atLeast(1));
        }
    };

    @Test
    public void testWithAppend() {
        Story story = Story.create(new Scenario() {
            {
                append(expectFirstElementReturnsNull);
                expect(list).add("hello!");
                occurs(1);
            }
        });

        story.begin();
        assertNull(list.get(0));
        list.add("hello!");
        story.end();
    }

    @Test
    public void testOmitToPickFirstElementFromList() {
        Story story = Story.create(new Scenario() {
            {
                append(expectFirstElementReturnsNull);
                expect(list).add("hello!");
                occurs(1);
            }
        });

        story.begin();
        list.add("hello!");
        story.end();
    }
}
