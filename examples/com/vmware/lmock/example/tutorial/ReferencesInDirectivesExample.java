/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.example.tutorial;

import static com.vmware.lmock.masquerade.Schemer.begin;
import static com.vmware.lmock.masquerade.Schemer.end;
import static com.vmware.lmock.masquerade.Schemer.willInvoke;
import static com.vmware.lmock.masquerade.Schemer.willReturn;

import java.util.List;

import org.junit.Test;

import com.vmware.lmock.impl.Mock;

public class ReferencesInDirectivesExample {
    @SuppressWarnings("unchecked")
    private static final List<String> list1 = Mock.getObject(List.class);
    @SuppressWarnings("unchecked")
    private static final List<String> list2 = Mock.getObject(List.class);

    @Test
    public void testWithReference() {
        begin();
        willReturn("hello").when(list1).get(0);
        willInvoke(1).of(list2).add(list1.get(0));

        list2.add(list1.get(0));
        end();
    }

    @Test
    public void testWithCyclicReference() {
        begin();
        willReturn("hello").when(list1).get(0);
        willInvoke(1).of(list1).add(list1.get(0));
        end();
    }
}
