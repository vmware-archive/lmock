/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.example.tutorial;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.vmware.lmock.impl.Mock;
import com.vmware.lmock.impl.Scenario;
import com.vmware.lmock.impl.Story;
import com.vmware.lmock.impl.Stubs;

/**
 * Tutorial example: shows how the stubs prevail on expectations.
 */
public class PriorityExample {
    @Test
    public void testStubAndExpectationPriority() {
        @SuppressWarnings("unchecked")
        final List<String> stringList = Mock.getObject(List.class);
        Story story = Story.create(new Scenario() {
            {
                expect(stringList).indexOf(anyOf(String.class));
                willReturn(0);
                occurs(1);
            }
        }, new Stubs() {
            {
                stub(stringList).indexOf(aNonNullOf(String.class));
                willReturn(1);
                stub(stringList).indexOf("hello world");
                willReturn(2);
            }
        });

        story.begin();
        assertEquals(2, stringList.indexOf("hello world"));
        assertEquals(1, stringList.indexOf("hello again!"));
        assertEquals(0, stringList.indexOf(null));
        story.end();
    }
}
