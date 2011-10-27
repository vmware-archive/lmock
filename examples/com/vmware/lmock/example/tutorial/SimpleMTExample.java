/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.example.tutorial;

import com.vmware.lmock.impl.Scenario;
import com.vmware.lmock.mt.Actor;
import static com.vmware.lmock.mt.Actor.*;
import com.vmware.lmock.impl.Story;
import com.vmware.lmock.impl.Stubs;
import org.junit.Test;

/**
 * An illustration of how to create a story with multiple actors.
 */
public class SimpleMTExample {
    /**
     * Creates a story with multiple actors, defining different combinations
     * allowed by Lmock.
     */
    @Test
    public void testWithMultipleThread() {
        // TODO DEFINE ACTUAL THREADS HERE
        Thread thread1 = new Thread();
        Thread thread2 = new Thread();
        Thread thread3 = new Thread();

        Scenario scenario12 = new Scenario() {
            {
                // TODO FILL IN THE SCENARIO HERE
            }
        };
        Stubs stubs23 = new Stubs() {
            {
                // TODO FILL IN THE STUBS HERE
            }
        };
        Actor actor1 = anActorForThread(thread1).following(scenario12);
        Actor actor2 = anActorForThread(thread2).following(scenario12).using(stubs23);
        Actor actor3 = anActorForThread(thread3).using(stubs23);
        Story story = Story.create(actor1, actor2, actor3);
        story.begin();
        // TODO IMPLEMENT THE TEST
        story.end();
    }
}
