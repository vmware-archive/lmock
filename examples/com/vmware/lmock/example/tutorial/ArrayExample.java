/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.example.tutorial;

import org.junit.Test;

import com.vmware.lmock.impl.Mock;
import com.vmware.lmock.impl.Scenario;
import com.vmware.lmock.impl.Story;

/**
 * Tutorial example: using arrays as arguments.
 */
public class ArrayExample {
    interface Reservoir {
        void fill(String[] data);
    }

    static class Supplier {
        private final Reservoir reservoir;

        Supplier(Reservoir reservoir) {
            this.reservoir = reservoir;
        }

        void supply(String... data) {
            reservoir.fill(data);
        }
    }
    final Reservoir reservoir = Mock.getObject(Reservoir.class);

    @Test
    public void testArrayWithConstantReference() {
        Story story = Story.create(new Scenario() {
            {
                expect(reservoir).fill(new String[]{"water", "mud"});
            }
        });
        story.begin();
        new Supplier(reservoir).supply("water", "mud");
        story.end();
    }

    @Test
    public void testArrayWithVariableReference() {
        final String[] reference = new String[]{"water", "mud"};
        Story story = Story.create(new Scenario() {
            {
                expect(reservoir).fill(reference);
            }
        });
        story.begin();
        new Supplier(reservoir).supply("water", "mud");
        reference[1] = "oil";
        new Supplier(reservoir).supply("water", "oil");
        story.end();

    }
}
