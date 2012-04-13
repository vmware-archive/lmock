/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.test;

import static com.vmware.lmock.checker.Occurrences.any;
import static com.vmware.lmock.checker.Occurrences.atLeast;
import static com.vmware.lmock.checker.Occurrences.atMost;
import static com.vmware.lmock.impl.InvocationResult.returnValue;
import static com.vmware.lmock.impl.InvocationResult.throwException;
import static com.vmware.lmock.masquerade.Schemer.aNonNullOf;
import static com.vmware.lmock.masquerade.Schemer.anyOf;
import static com.vmware.lmock.masquerade.Schemer.append;
import static com.vmware.lmock.masquerade.Schemer.begin;
import static com.vmware.lmock.masquerade.Schemer.end;
import static com.vmware.lmock.masquerade.Schemer.will;
import static com.vmware.lmock.masquerade.Schemer.willInvoke;
import static com.vmware.lmock.masquerade.Schemer.willReturn;
import static com.vmware.lmock.masquerade.Schemer.willThrow;
import static com.vmware.lmock.masquerade.Schemer.with;
import static com.vmware.lmock.test.Dalton.averell;
import static com.vmware.lmock.test.Dalton.jack;
import static com.vmware.lmock.test.Dalton.joe;
import static com.vmware.lmock.test.Dalton.william;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import com.vmware.lmock.checker.IntegerChecker;
import com.vmware.lmock.checker.StringChecker;
import com.vmware.lmock.exception.LMRuntimeException;
import com.vmware.lmock.exception.SchemerException;
import com.vmware.lmock.exception.UnexpectedInvocationError;
import com.vmware.lmock.exception.UnsatisfiedOccurrenceError;
import com.vmware.lmock.impl.Mock;
import com.vmware.lmock.impl.Scenario;
import com.vmware.lmock.impl.Stubs;
import com.vmware.lmock.masquerade.Role;
import com.vmware.lmock.test.Dalton.SpecialDaltonException;

/**
 * Creates and validates masquerades.
 *
 * <p>
 * These are very elementary tests of the masquerades. More extensive testing
 * can be found in the other tests, focusing on specific aspects of the
 * specifications.
 * </p>
 */
public class MasqueradeTest {
    /**
     * Verifies that empty masquerades cause no harm.
     */
    @Test
    public void testEmptyMasquerade() {
        begin();
        end();
    }

    /**
     * A basic expectation specification.
     *
     * @throws SpecialDaltonException
     */
    @Test
    public void testBasicExpectation() throws SpecialDaltonException {
        begin();
        willInvoke(1).of(joe).bother();
        joe.bother();
        end();
    }

    /**
     * A basic stub specification.
     */
    @Test
    public void testBasicStub() {
        begin();
        willReturn(1).when(joe).getInt();
        assertEquals(1, joe.getInt());
        end();
    }

    /**
     * A basic validation of a stub with a result.
     */
    @Test
    public void testBasicExpectationWithResult() {
        begin();
        willInvoke(1).willReturn(1).when(joe).getInt();
        assertEquals(1, joe.getInt());
        willInvoke(1).will(returnValue(2)).when(joe).getInt();
        assertEquals(2, joe.getInt());
        end();
    }

    /**
     * A basic validation of a stub with an exception as result.
     *
     * @throws SpecialDaltonException
     */
    @Test
    public void testBasicExpectationWithResultingException()
      throws SpecialDaltonException {
        begin();
        willInvoke(1).willThrow(new RuntimeException("gotcha!")).when(joe).
          bother();
        try {
            joe.bother();
            fail("expected to be interrupted");
        } catch (RuntimeException e) {
        }

        willInvoke(1).will(throwException(new SpecialDaltonException())).when(
          joe).bother();
        try {
            joe.bother();
            fail("expected to be interrupted");
        } catch (SpecialDaltonException e) {
        }
        end();
    }

    /**
     * Shows that an expectation can refer to another expectation or stub during
     * the specification phase as long as the invoked mock differs.
     */
    @Test
    public void testUseStubDuringDirectiveSpecification() {
        begin();
        willReturn(jack).when(joe).next();
        willReturn(william).when(jack).next();
        willReturn(averell).when(william).next();
        willReturn(3).when(joe).ping(jack.next());
        assertEquals(3, joe.ping(william));
        end();
    }

    /**
     * A limitation of the model: cannot invoke a method of a mock while
     * creating a directive for that mock.
     */
    @Test
    public void testWillFailIfInvokingAMockWhenBuildingAnInvocationForIt() {
        begin();
        willReturn(jack).when(joe).next();
        willReturn(william).when(jack).next();
        willReturn(averell).when(william).next();
        try {
            willReturn(3).when(joe).ping(joe.next());
            fail("passed an expected failure!");
        } catch (LMRuntimeException e) {
        }
        end();
    }

    /**
     * Checks what happens when ending a masquerade that did not start.
     */
    @Test
    public void testEndWhenNotBeginned() {
        // No exception should happen...
        end();
    }

    /**
     * Verifies that we can't create new directives when no masquerade is
     * ongoing.
     */
    @Test
    public void testNoSpecificationAllowedIfNoMasquerade() {
        // Just be sure that the states are reset by the end directive.
        begin();
        end();

        // And now, let's go:
        try {
            willInvoke(1);
            fail("called expect with no masquerade");
        } catch (SchemerException e) {
        }

        try {
            willInvoke(atLeast(1));
            fail("called expect with no masquerade");
        } catch (SchemerException e) {
        }

        try {
            append(new Scenario());
            fail("called append with no masquerade");
        } catch (SchemerException e) {
        }

        try {
            append(new Stubs());
            fail("called append with no masquerade");
        } catch (SchemerException e) {
        }

        try {
            willReturn(1);
            fail("called willReturn with no masquerade");
        } catch (SchemerException e) {
        }

        try {
            willThrow(new RuntimeException());
            fail("called willThrow with no masquerade");
        } catch (SchemerException e) {
        }

        try {
            will(throwException(new RuntimeException()));
            fail("called will with no masquerade");
        } catch (SchemerException e) {
        }
    }

    /**
     * Verifies that the masquerade is correctly cleared even if the ongoing
     * story is not complete.
     */
    @Test
    public void testEndThrowsUnsatisfiedOccurrenceException() {
        begin();
        willInvoke(1).of(joe).ping();
        try {
            end();
        } catch (UnsatisfiedOccurrenceError e) {
        }

        begin();
        willInvoke(1).willReturn(1).when(joe).getInt();
        assertEquals(1, joe.getInt());
        end();
    }

    /**
     * Verifies that the masquerade is correctly cleared even if the previous
     * story abruptly ended.
     */
    @Test
    public void testAbruptEndOfMasquerade() {
        begin();
        willReturn(23).when(joe).getInt();
        willInvoke(atMost(3)).of(joe).ping();
        try {
            for (int i = 0; i < 5; i++) {
                joe.ping();
            }
        } catch (UnexpectedInvocationError e) {
        }

        // The mock should have been cleaned.
        try {
            assertEquals(23, joe.getInt());
        } catch (UnexpectedInvocationError e) {
        }
    }

    /**
     * Verifies that argument clauses are not allowed if no directive exists.
     */
    @Test
    public void testInvalidAnyOfClause() {
        begin();
        try {
            anyOf(String.class);
            fail("defined an anyOf clause out of a directive specification");
        } catch (SchemerException e) {
        }
        end();

        begin();
        try {
            aNonNullOf(String.class);
            fail("defined an anyOf clause out of a directive specification");
        } catch (SchemerException e) {
        }
        end();

        begin();
        try {
            with(3);
            fail("defined an anyOf clause out of a directive specification");
        } catch (SchemerException e) {
        }
        end();

        begin();
        try {
            with(IntegerChecker.negativeValues);
            fail("defined an anyOf clause out of a directive specification");
        } catch (SchemerException e) {
        }
        end();
    }

    /**
     * A typical usage of stubs: import a set of pre-defined stubs into the
     * test.
     *
     * <p>
     * This test mixes the different techniques and may be a bit unreadable...
     * </p>
     */
    @Test
    public void testMasqueradeImportingStubs() {
        @SuppressWarnings("unchecked")
        final List<String> list = Mock.getObject(List.class);

        final Stubs emptyListStubs = new Stubs() {
            {
                stub(list).isEmpty();
                willReturn(true);
                stub(list).get(anyOf(int.class));
                willThrow(new IndexOutOfBoundsException());
            }
        };

        final Stubs feedListStubs = new Stubs() {
            {
                stub(list).add(aNonNullOf(String.class));
                willReturn(true);
            }
        };

        begin();
        append(emptyListStubs);
        willInvoke(any()).willReturn(new String[]{"dollar$", "euros"}).when(joe).
          emptyPocket();
        willInvoke(1).of(joe).ping(with(jack), aNonNullOf(String.class));
        if (list.isEmpty()) {
            try {
                list.get(0);
                fail("stub should have thrown an exception");
            } catch (IndexOutOfBoundsException e) {
            }

            append(feedListStubs);
            willReturn(true).when(list).contains(
              with(StringChecker.valuesContain("$")));

            list.add("joe's pocket...");
            String[] joesPocket = (String[]) joe.emptyPocket();
            for (String stuff : joesPocket) {
                list.add(stuff);
            }

            if (list.contains("dollar$")) {
                joe.ping(jack, "done!");
            }
        }
        end();
    }

    /**
     * Verifies that we can't create expectations for a role that has no actor.
     */
    @Test
    public void testCannotCreateExpectationForAnEmptyRole() {
        Role role = new Role();
        begin();
        try {
            role.willInvoke(1).of(joe).doNothing();
            fail("was able to create an assertion for a role that has no actor");
        } catch (SchemerException e) {
        }
        end();
    }
}
