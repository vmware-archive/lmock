/* **************************************************************************
 * Copyright (C) 2012 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.test;

import com.vmware.lmock.impl.Mock;
import com.vmware.lmock.trace.ActivityLogger;
import com.vmware.lmock.trace.Trace;

import java.util.List;
import org.junit.After;
import org.junit.Assert;

import org.junit.Test;

/**
 * Validation of traces mechanism.
 */
public class TraceTest {

    private class GetObjectLogChecker implements ActivityLogger {
        /** The index of the trace we expect */
        private int expectedTraceIndex;

        /**
         * Verify that the content of the trace fits a trace emitted by lmock
         * getObject method.
         *
         * @param message the message emitted by lmock
         */
        private void checkGetObjectTraces(String message) {
            Assert.assertTrue(message.contains("getObject()"));
            Assert.assertTrue(message.contains("myMock"));
            Assert.assertTrue(message.contains("class"));
            Assert.assertTrue(message.contains("interface"));
            Assert.assertTrue(message.contains("java.util.List"));
        }

        /**
         * Verify that the content of the trace fits a trace emitted by lmock
         * when it creates a mock object.
         *
         * @param message the message emitted by lmock
         */
        private void checkMockCreationTraces(String message) {
            Assert.assertTrue(message.contains("Mock()"));
            Assert.assertTrue(message.contains("new mock"));
            Assert.assertTrue(message.contains("class"));
            Assert.assertTrue(message.contains("interface"));
            Assert.assertTrue(message.contains("java.util.List"));
            Assert.assertTrue(message.contains("proxy"));
            Assert.assertTrue(message.contains("myMock"));
        }

        @Override
        public void trace(String message) {
            if (expectedTraceIndex == 0) {
                checkGetObjectTraces(message);
                expectedTraceIndex++;
            } else if (expectedTraceIndex == 1) {
                checkMockCreationTraces(message);
                expectedTraceIndex++;
            } else {
                Assert.fail("An unexpected trace have been emitted.");
            }
        }
    }

    /**
     * Validate a simple emission of traces.
     */
    @Test
    public void testExpectedTraces() {
        Trace.reportActivityTo(new GetObjectLogChecker());
        List<?> myMock = Mock.getObject("myMock", List.class);
    }

    /**
     * Whatever the test result is, ensure that traces are disabled when living
     * this test class.
     */
    @After
    public void tearDown() {
        Trace.dontReportActivity();
    }
}
