/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.test;

import static com.vmware.lmock.test.Dalton.joe;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vmware.lmock.checker.IntegerChecker;
import com.vmware.lmock.impl.Scenario;

/**
 * Validates the specification of with clauses using user-supplied checkers.
 *
 * <p>
 * These tests focus on:
 * </p>
 * <ul>
 * <li>Valid clause definitions: verify that the with clauses are properly
 * recorded</li>
 * <li>invalid specifications: check the coherence of such a clause</li>
 * </ul>
 */
public class WithCheckerClauseSpecificationTest {
    /**
     * Verifies that a with clause works with common checkers.
     */
    @Test
    public void testWithClauseWithChecker() {
        new Scenario() {
            {
                expect(joe).setInt(with(IntegerChecker.valuesBetween(-1, 2)));
                String instance = expect().toString();
                assertTrue(instance.contains("setInt(Integer=[-1,2]"));
            }
        };
    }
}
