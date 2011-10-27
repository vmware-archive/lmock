/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.example.tutorial;

import com.vmware.lmock.masquerade.Role;
import static com.vmware.lmock.masquerade.Schemer.*;
import com.vmware.lmock.mt.Actor;
import static com.vmware.lmock.mt.Actor.*;
import org.junit.Test;

/**
 * One actor has its own role. Two other actors play the same role. The two roles share a common scenario.
 */
public class SimpleMTExampleWithMasquerade {
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

        Actor actor1 = anActorForThread(thread1);
        Actor actor2 = anActorForThread(thread2);
        Actor actor3 = anActorForThread(thread3);

        Role role1 = new Role(actor1);
        Role role2 = new Role(actor2, actor3);
        role2.shareScenarioWith(role1);

        begin(role1, role2);
        // TODO: write the directives, using roleX.will...
        // AND RUN THE TEST.
        end();
    }
}
