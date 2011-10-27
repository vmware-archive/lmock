/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.test;

import static com.vmware.lmock.impl.Story.create;
import static com.vmware.lmock.impl.Story.createWithMultipleActors;
import static com.vmware.lmock.test.Dalton.joe;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import org.junit.Test;

import com.vmware.lmock.exception.MissingInvocationException;
import com.vmware.lmock.exception.MockReferenceException;
import com.vmware.lmock.exception.UnexpectedInvocationError;
import com.vmware.lmock.impl.Scenario;
import com.vmware.lmock.impl.Story;
import com.vmware.lmock.impl.StoryTrack;
import com.vmware.lmock.impl.Stubs;
import com.vmware.lmock.trace.Trace;

/**
 * Miscellaneous tests on scenarios and stories.
 */
public class MiscellaneousTest {

    /**
     * Verifies that a null scenario is considered as an empty scenario by a
     * story.
     */
    @Test
    public void testNullScenario() {
        Story story = create((Scenario) null);
        story.begin();
        story.end();
    }

    /**
     * Verifies that a story with multiple instances but no actual instance is
     * an empty story.
     */
    @Test
    public void testEmptyInstances() {
        Story story = createWithMultipleActors();
        story.begin();
        story.end();
    }

    /**
     * Creates an empty scenario and an empty story and verifies that everything
     * is fine.
     */
    @Test
    public void testEmptyScenario() {
        Story story = Story.create(new Scenario() {

            {
            }
        });
        story.begin();
        story.end();
    }

    /**
     * Creates a story with an empty scenario and verifies that an unexpected
     * mock invocation (not in the scenario) is correctly trapped.
     */
    @Test
    public void testEmptyScenarioAndUnexpectedInvocation() {
        Story story = Story.create(new Scenario() {

            {
            }
        });
        story.begin();
        try {
            joe.ping();
            fail("called an unexpected mock");
        } catch (UnexpectedInvocationError e) {
            // The story track should be empty.
            assertEquals(0, StoryTrack.get().size());
        }
        // Don't end the story, otherwise another useless
        // error is reported.
        //story.end();
    }

    /**
     * Verifies that the invocation of a method outside the scope of a scenario
     * and a story triggers an exception.
     */
    @Test
    public void testInvocationOutOfScope() {
        Story story = Story.create(new Scenario() {

            {
                expect(joe).ping();
            }
        });

        try {
            joe.ping();
            fail("mock invocation out of scope");
        } catch (UnexpectedInvocationError e) {
        }

        story.begin();
        joe.ping();
        story.end();

        try {
            joe.ping();
            fail("mock invocation out of scope");
        } catch (UnexpectedInvocationError e) {
        }
    }

    /**
     * Verifies that there's no confusion between mocks and other proxies.
     */
    @Test
    public void testCreateWithInvalidProxy() {
        final Object nasty = Proxy.newProxyInstance(this.getClass().
          getClassLoader(), new Class<?>[]{List.class},
          new InvocationHandler() {

              @Override
              public Object invoke(Object arg0, Method arg1, Object[] arg2)
                throws Throwable {
                  // There is one corner case with traces: they rely on toString to display
                  // the information... Which means that a fake mock may cause a serious
                  // problem.
                  if (Trace.getActivityLogger() == null) {
                      fail("don't know how I arrived there, but sure I am - method=" + arg1);
                  }
                  return null;
              }
          });
        assertNotNull(nasty);
        new Scenario() {

            {
                try {
                    expect(nasty).getClass();
                    fail("considered a proxy as a vulgar mock");
                } catch (MockReferenceException e) {
                }
            }
        };
    }

    /**
     * Verifies that erroneous expectation or stub definitions are correctly
     * reported.
     *
     * <p>
     * In particular, the story track should be empty at this stage.
     * </p>
     */
    @Test
    public void testUnexpectedInvocationWithinSpecifications() {
        new Scenario() {

            {
                try {
                    expect(joe.ping());
                    fail("made an erroneous invocation within a scenario");
                } catch (UnexpectedInvocationError e) {
                    assertEquals(0, StoryTrack.get().size());
                }
            }
        };

        new Stubs() {

            {
                try {
                    stub(joe.ping());
                    fail("made an erroneous invocation within a scenario");
                } catch (UnexpectedInvocationError e) {
                    assertEquals(0, StoryTrack.get().size());
                }
            }
        };
    }

    /**
     * Verifies that the specification of a stub or expectation requires an
     * invocation.
     */
    @Test
    public void testMissingInvocation() {
        new Scenario() {

            {
                expect(joe);
                try {
                    expect(joe).ping();
                    fail(
                      "create a new expectation while the previous is not closed");
                } catch (MissingInvocationException e) {
                }
            }
        };

        new Stubs() {

            {
                stub(joe);
                try {
                    stub(joe).ping();
                    fail("create a new stub while the previous is not closed");
                } catch (MissingInvocationException e) {
                }
            }
        };
    }

    /**
     * Verifies that the specification of a story requires all the expectations
     * to be complete.
     */
    @Test
    public void testMissingInvocationWhenCreatingScenario() {
        Scenario scenario = new Scenario() {

            {
                expect(joe);
            }
        };
        try {
            Story.create(scenario);
            fail("create a story while the scenario is not closed");
        } catch (MissingInvocationException e) {
        }
    }

    /**
     * Verifies that the specification of a story requires all stubs to be
     * complete.
     */
    @Test
    public void testMissingInvocationWhenCreatingStubs() {
        Stubs stubs = new Stubs() {

            {
                stub(joe);
            }
        };
        try {
            Story.create(null, stubs);
            fail("create a story while the stubs are not closed");
        } catch (MissingInvocationException e) {
        }
    }
}
