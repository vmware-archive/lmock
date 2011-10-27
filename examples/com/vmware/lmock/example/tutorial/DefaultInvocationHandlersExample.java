/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.example.tutorial;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.Test;

import com.vmware.lmock.impl.Mock;
import com.vmware.lmock.impl.Scenario;
import com.vmware.lmock.impl.Story;
import com.vmware.lmock.impl.Stubs;

/**
 * Tutorial example: overriding default handlers.
 */
public class DefaultInvocationHandlersExample {
    @SuppressWarnings("unchecked")
    final List<String> list1 = Mock.getObject(List.class);
    @SuppressWarnings("unchecked")
    final List<String> list2 = Mock.getObject(List.class);

    @Test
    public void testDefaultInvocationHandlerExample() {
        // The default implementation of common methods:
        assertFalse(list1.equals(list2));
        assertEquals("Mock(List)$0", list1.toString());
        assertEquals("Mock(List)$1", list2.toString());

        // Overwite those methods and replay the test:
        Story story = Story.create(new Scenario(), new Stubs() {
            {
                stub(list1).equals(list2);
                willReturn(true);
                stub(list1).toString();
                willReturn("list#1");
                stub(list2).toString();
                willReturn("list#2");
            }
        });

        story.begin();
        assertEquals(list1, list2);
        assertEquals("list#1", list1.toString());
        assertEquals("list#2", list2.toString());
        story.end();
    }
}
