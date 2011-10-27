/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.example.tutorial;

import java.util.List;

import org.junit.Test;

import com.vmware.lmock.impl.Mock;
import com.vmware.lmock.impl.Scenario;
import com.vmware.lmock.impl.Story;

import static com.vmware.lmock.checker.Occurrences.*;

/**
 * Tutorial example: specialization of classes with argument clauses.
 */
public class ClassSpecializationExample {
    @SuppressWarnings("unchecked")
    private final List<Object> list = Mock.getObject(List.class);

    @Test
    public void testAddWithStringsOnly() {
        Story story = Story.create(new Scenario() {
            {
                expect(list).add(aNonNullOf(String.class));
                occurs(atLeast(1));
            }
        });
        story.begin();
        list.add("hello world");
        list.add(5);
        story.end();
    }
}
