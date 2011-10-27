/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.example.list;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;

import com.vmware.lmock.impl.Mock;
import com.vmware.lmock.impl.Scenario;
import com.vmware.lmock.impl.Story;

/**
 * An example of mocking using the well-known java list interface.
 */
public class StringRepositoryTest {
    /** A mock of the list used by the tested string repository. */
    @SuppressWarnings("unchecked")
    private final List<String> stringList = Mock.getObject(List.class);

    /**
     * Gets a string from an empty repository.
     */
    @Test
    public void testGetFromEmptyList() {
        StringRepository instance = new StringRepository(stringList);
        Story story = Story.create(new Scenario() {
            {
                expect(stringList).isEmpty();
                willReturn(true);
            }
        });
        story.begin();
        assertNull(instance.get());
        story.end();
    }

    /**
     * Puts and gets strings to/from a repository.
     */
    @Test
    public void testPutAndGet() {
        StringRepository instance = new StringRepository(stringList);
        Story story = Story.create(new Scenario() {
            {
                // Allow new strings as many times as needed. Don't check the
                // added strings, and don't check the occurrences.
                expect(stringList).add(anyOf(String.class));
                // The user will extract the different strings added during
                // the test until getting an empty list.
                expect(stringList).isEmpty();
                willReturn(false);
                expect(stringList).remove(0);
                willReturn("hello");

                expect(stringList).isEmpty();
                willReturn(false);

                expect(stringList).remove(0);
                willReturn("world");

                expect(stringList).isEmpty();
                willReturn(true);
            }
        });

        story.begin();
        instance.put("hello");
        instance.put("world");
        assertEquals("hello", instance.get());
        assertEquals("world", instance.get());
        assertNull(instance.get());
        story.end();
    }
}
