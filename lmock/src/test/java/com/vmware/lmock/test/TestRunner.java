/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.test;

import com.vmware.lmock.trace.ActivityLogger;
import com.vmware.lmock.trace.Trace;

/**
 * Executes all the tests of the test suite.
 *
 * <p>
 * Simplifies the procedure for an automatic test. The class defines the static
 * <code>main</code> method that gathers the provides the list of test classes
 * to the JUnit core.
 * </p>
 * <p>
 * User can pass the argument <code>-t</code> to trigger the trace activity.
 * </p>
 */
public class TestRunner {
    /**
     * Creates the name of a test class.
     *
     * @param name
     *            the test name
     * @return The qualified test name
     */
    private static String test(String name) {
        return TestRunner.class.getPackage().getName() + "." + name;
    }
    /**
     * A rough list of tests.
     */
    private static final String[] testList = {
        test("AnyClauseSpecificationTest"), //
        test("AnyClauseTest"), //
        test("AppendTest"), //
        test("ByteCheckerTest"), //
        test("CharacterCheckerTest"), //
        test("DoubleCheckerTest"), //
        test("EnumCheckerTest"), //
        test("FloatCheckerTest"), //
        test("IntegerCheckerTest"), //
        test("InvocationHooksTest"), //
        test("LongCheckerTest"), //
        test("MasqueradeTest"), //
        test("MatchingExpectationSpecificationTest"), //
        test("MatchingExpectationTest"), //
        test("MatchingStubSpecificationTest"), //
        test("MiscellaneousTest"), //
        test("MockTest"), //
        test("MTDispatcherTest"), //
        test("OccurencesSpecificationTest"), //
        test("OccurrencesTest"), //
        test("ScenarioTest"), //
        test("ShortCheckerTest"), //
        test("SimpleMTTest"), //
        test("StoryTrackTest"), //
        test("StringCheckerTest"), //
        test("StubTest"), //
        test("ThreadCheckerTest"), //
        test("WillDelegateToSpecificationTest"), //
        test("WillDelegateToTest"), //
        test("WillReturnSpecificationTest"), //
        test("WillReturnTest"), //
        test("WillThrowSpecificationTest"), //
        test("WillThrowTest"), //
        test("WithCheckerClauseSpecificationTest"), //
        test("WithCheckerClauseTest"), //
        test("WithExpectationSpecificationTest"), //
        test("AdvancedMTTest")};

    /**
     * Starts the execution of the JUnit tests.
     *
     * @param args
     *            the user supplied arguments
     */
    private void run(String args[]) {
        collectUserArguments(args);
        org.junit.runner.JUnitCore.main(testList);
    }

    /**
     * Checks the arguments passed to the runner, if any.
     *
     * @param args
     *            the user arguments
     */
    private void collectUserArguments(String args[]) {
        if (args != null && args.length > 0 && args[0].equals("-t")) {
            Trace.reportActivityTo(new ActivityLogger() {
                public void trace(String message) {
                    System.out.println(message);
                }
            });
        }
    }

    /**
     * Main program entry.
     *
     * @param args
     *            the program arguments
     */
    public static void main(String args[]) {
        new TestRunner().run(args);
        // Not really useful, but closes the EMMA coverage and ensures that we get no jam.
        Trace.dontReportActivity();
    }
}
