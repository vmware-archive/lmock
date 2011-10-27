/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.test;

import com.vmware.lmock.exception.ExpectationError;
import static com.vmware.lmock.checker.Occurrences.exactly;
import static com.vmware.lmock.test.Dalton.jack;
import static com.vmware.lmock.test.Dalton.joe;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import com.vmware.lmock.exception.IncompatibleReturnValueException;
import com.vmware.lmock.exception.UnexpectedInvocationError;
import com.vmware.lmock.impl.Mock;
import com.vmware.lmock.impl.Scenario;
import com.vmware.lmock.impl.Story;
import static com.vmware.lmock.test.LMAsserts.*;

/**
 * Validates the invocation hooks handling default invocations to common method.
 */
public class InvocationHooksTest {
    /** The mock. */
    @SuppressWarnings("unchecked")
    private static final List<Object> mock = Mock.getObject(List.class);

    /**
     * An interface that "redefines" the hooked functions.
     */
    private interface Foo {
        public String toString(int id);

        public boolean equals(int number, String that);

        public boolean equals(String that);

        public int hashCode(int id);
    }

    /**
     * Verifies that we have a default behavior for <code>toString</code>.
     */
    @Test
    public void testDefaultToString() {
        final Object item = new Object();
        Story story = Story.create(new Scenario() {
            {
                expect(mock).add(item);
                occurs(exactly(1));
                expect(mock).isEmpty();
                willReturn(false).occurs(exactly(1));
            }
        });

        story.begin();
        mock.add(item);
        assertTrue(mock.toString().contains("Mock(List)"));
        assertFalse(mock.isEmpty());
        story.end();
    }

    /**
     * Verifies that <code>toString</code> can be redefined by an expectation.
     */
    @Test
    public void testOverwriteToString() {
        final Object item = new Object();
        Story story = Story.create(new Scenario() {
            {
                expect(mock).add(item);
                occurs(exactly(1));
                expect(mock).toString();
                willReturn("hello!");
                expect(mock).isEmpty();
                willReturn(false).occurs(exactly(1));
            }
        });

        story.begin();
        mock.add(item);
        assertEquals("hello!", mock.toString());
        assertFalse(mock.isEmpty());
        story.end();
    }

    /**
     * Verifies that the redefinition of <code>toString</code> does not clobber
     * the common expectation tests.
     */
    @Test
    public void testOverwriteToStringInvalid() {
        final Object item = new Object();
        new Scenario() {
            {
                expect(mock).add(item);
                occurs(exactly(1));
                try {
                    expect(mock).toString();
                    willReturn(item);
                    fail("common lmock tests were forgotten");
                } catch (IncompatibleReturnValueException e) {
                }
                expect(mock).isEmpty();
                willReturn(false).occurs(exactly(1));
            }
        };
    }

    /**
     * Verifies that the hooks do not clobber another definition of
     * <code>toString</code>.
     */
    @Test
    public void testDoNotConfuseToString() {
        final Foo fooMock = Mock.getObject(Foo.class);
        Story story = Story.create(new Scenario() {
            {
                expect(fooMock).toString(0);
                willReturn("hello!").occurs(exactly(1));
            }
        });
        story.begin();
        assertEquals("hello!", fooMock.toString(0));
        assertTrue(fooMock.toString().contains("Mock(Foo)"));
        story.end();
    }

    /**
     * Verifies that we have a default behavior for the <code>equals</code>
     * method.
     *
     * Exercise this default behavior with different arguments.
     */
    @Test
    public void testDefaultEquals() {
        final Object item = new Object();
        Story story = Story.create(new Scenario() {
            {
                expect(mock).add(item);
                occurs(exactly(1));
                expect(mock).isEmpty();
                willReturn(false).occurs(exactly(1));
            }
        });

        story.begin();
        mock.add(item);
        assertEquals(mock, mock);
        assertFalse(mock.equals(null));
        assertFalse(mock.equals(item));
        assertFalse(mock.isEmpty());
        story.end();
    }

    /**
     * Verifies that we can overwrite the <code>equals</code>.
     */
    @Test
    public void testOverwriteEquals() {
        final Object item = new Object();
        Story story = Story.create(new Scenario() {
            {
                expect(mock).add(item);
                occurs(exactly(1));
                // Make some stupid equalities.
                expect(mock).equals(mock);
                willReturn(false);
                expect(mock).equals(null);
                willReturn(true);
                expect(mock).equals(item);
                willReturn(true);
                expect(mock).isEmpty();
                willReturn(false).occurs(exactly(1));
            }
        });

        story.begin();
        mock.add(item);
        assertFalse(mock.equals(mock));
        assertEquals(mock, null);
        assertEquals(mock, item);
        assertFalse(mock.isEmpty());
        story.end();
    }

    /**
     * Verifies that the re-definition of equals does not clobber the common
     * expectation tests.
     */
    @Test
    public void testOverwriteEqualsInvalid() {
        final Object item = new Object();
        new Scenario() {
            {
                expect(mock).add(item);
                occurs(exactly(1));
                try {
                    expect(mock).equals(null);
                    willReturn("hello!");
                    fail("common lmock tests were forgotten");
                } catch (IncompatibleReturnValueException e) {
                }
                expect(mock).isEmpty();
                willReturn(false).occurs(exactly(1));
            }
        };
    }

    /**
     * Verifies that the hooks do not clobber another definition of
     * <code>equals</code>.
     */
    @Test
    public void testDoNotConfuseEqualsWithDifferentNumberOfParameters() {
        ExpectationError lastError = null;

        final Foo fooMock = Mock.getObject(Foo.class);
        Story story = Story.create(new Scenario() {
            {
                // Create a dummy expectation, otherwise any invocation to
                // the mock will be unexpected.
                expect(fooMock).hashCode(0);
            }
        });
        story.begin();
        assertEquals(fooMock, fooMock);
        try {
            assertTrue(fooMock.equals(12, "who?"));
            fail("mistaken with equals");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);
    }

    /**
     * Verifies that the hooks do not clobber another definition of
     * <code>equals</code>.
     */
    @Test
    public void testDoNotConfuseEqualsWithOtherParameter() {
        ExpectationError lastError = null;

        final Foo fooMock = Mock.getObject(Foo.class);
        Story story = Story.create(new Scenario() {
            {
                // Create a dummy expectation, otherwise any invocation to
                // the mock will be unexpected.
                expect(fooMock).hashCode(0);
            }
        });
        story.begin();
        assertEquals(fooMock, fooMock);
        try {
            assertTrue(fooMock.equals("who?"));
            fail("mistaken with equals");
        } catch (UnexpectedInvocationError e) {
            lastError = e;
        }
        assertEndReportsError(story, lastError);
    }

    /**
     * Verifies that we have a default behavior for the <code>hashCode</code>
     * method.
     */
    @Test
    public void testDefaultHashCode() {
        final Object item = new Object();
        Story story = Story.create(new Scenario() {
            {
                expect(mock).add(item);
                occurs(exactly(1));
                expect(mock).isEmpty();
                willReturn(false).occurs(exactly(1));
            }
        });

        story.begin();
        mock.add(item);
        assertFalse(mock.hashCode() == item.hashCode());
        assertFalse(mock.isEmpty());
        story.end();
    }

    /**
     * Verifies that we can overwrite the <code>hashCode</code> method.
     */
    @Test
    public void testOverwriteHashCode() {
        final Object item = new Object();
        Story story = Story.create(new Scenario() {
            {
                expect(mock).add(item);
                occurs(exactly(1));
                expect(mock).hashCode();
                willReturn(item.hashCode());
                expect(mock).isEmpty();
                willReturn(false).occurs(exactly(1));
            }
        });

        story.begin();
        mock.add(item);
        assertEquals(mock.hashCode(), item.hashCode());
        assertFalse(mock.isEmpty());
        story.end();
    }

    /**
     * Verifies that we do not clobber the default expectation tests when
     * overwriting <code>hashCode</code>.
     */
    @Test
    public void testOverwriteHashCodeInvalid() {
        final Object item = new Object();
        new Scenario() {
            {
                expect(mock).add(item);
                occurs(exactly(1));
                try {
                    expect(mock).hashCode();
                    willReturn("hello!");
                    fail("common lmock tests were forgotten");
                } catch (IncompatibleReturnValueException e) {
                }
                expect(mock).isEmpty();
                willReturn(false).occurs(exactly(1));
            }
        };
    }

    /**
     * Verifies that the hooks do not clobber another definition of
     * <code>hashCode</code>.
     */
    @Test
    public void testDoNotConfuseHashCode() {
        final Foo fooMock = Mock.getObject(Foo.class);
        Story story = Story.create(new Scenario() {
            {
                expect(fooMock).hashCode(11);
                willReturn(23).occurs(exactly(1));
            }
        });
        story.begin();
        assertEquals(23, fooMock.hashCode(11));
        story.end();
    }

    /**
     * Invocation of the default methods for mocks that do not belong to a
     * scenario.
     */
    @Test
    public void testInvocationOfDefaultMethodOnDummyMock() {
        Story story = Story.create(new Scenario() {
            {
                expect(joe).next();
                willReturn(jack);
            }
        });

        story.begin();
        assertEquals(jack, joe.next());
        story.end();
    }
}
