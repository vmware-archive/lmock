/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.test;

import org.junit.Test;
import static org.junit.Assert.*;
import com.vmware.lmock.exception.MockCreationException;
import com.vmware.lmock.impl.Mock;
import java.util.List;

/**
 * Validation of the creation of mocks.
 */
public class MockTest {
    /**
     * Verifies that we cannot mock something that is not an interface.
     */
    @Test
    public void testMockInvalidClass() {
        try {
            Mock.getObject(String.class);
            fail("mocked strings");
        } catch (MockCreationException e) {
        }
    }

    /**
     * Verifies that a mock can be assigned a name.
     */
    @Test
    public void testMockWithName() {
        List<?> myMock = Mock.getObject("myMock", List.class);
        assertEquals("myMock", myMock.toString());
    }
}
