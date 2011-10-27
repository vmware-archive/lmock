/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.example.tutorial;

import static com.vmware.lmock.checker.Occurrences.atLeast;
import static com.vmware.lmock.masquerade.Schemer.aNonNullOf;
import static com.vmware.lmock.masquerade.Schemer.begin;
import static com.vmware.lmock.masquerade.Schemer.end;
import static com.vmware.lmock.masquerade.Schemer.willInvoke;
import static com.vmware.lmock.masquerade.Schemer.willReturn;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.vmware.lmock.impl.Mock;

/**
 * Tutorial example: use of masquerades.
 */
public class MasqueradeExample {
    @Test
    public void testAList() {
        @SuppressWarnings("unchecked")
        final List<String> list = Mock.getObject(List.class);

        begin();

        // The user may verify that the list is empty and add strings.
        willReturn(true).when(list).isEmpty();
        willInvoke(atLeast(1)).of(list).add(aNonNullOf(String.class));

        // Run this portion of test...
        if (list.isEmpty()) {
            list.add("string 1");
            list.add("string 2");
        }

        // Now the user will read back the values.
        willInvoke(1).willReturn("string 1").when(list).get(0);
        willInvoke(1).willReturn("string 2").when(list).get(1);

        assertEquals("string 1", list.get(0));
        assertEquals("string 2", list.get(1));

        end();
    }
}
