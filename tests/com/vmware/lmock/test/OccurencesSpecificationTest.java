/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.test;

import static com.vmware.lmock.checker.Occurrences.atLeast;
import static com.vmware.lmock.checker.Occurrences.atMost;
import static com.vmware.lmock.checker.Occurrences.between;
import static com.vmware.lmock.checker.Occurrences.exactly;
import static com.vmware.lmock.checker.Occurrences.never;
import static com.vmware.lmock.test.Dalton.joe;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vmware.lmock.exception.IllegalOccurrencesDefinitionException;
import com.vmware.lmock.impl.Scenario;

/**
 * Validates the specification of occurrences in expectations.
 *
 * <p>
 * These tests focus on:
 * <ul>
 * <li>wrong occurrences specification: should issue errors</li>
 * <li>correct occurrences: verify that they are correctly recorded</li>
 * </ul>
 * </p>
 */
public class OccurencesSpecificationTest {
    /**
     * Verifies that the default occurrences are 'any'.
     */
    @Test
    public void testDefaultOccurencesAreAny() {
        new Scenario() {
            {
                expect(joe).ping();
                String instance = expect().toString();
                assertTrue(instance.contains("Dalton.ping():void/[ANY]"));
            }
        };
    }

    /**
     * Verifies that a correct 'exactly' occurrence is properly handled.
     */
    @Test
    public void testOccurrencesExactly() {
        new Scenario() {
            {
                expect(joe).ping();
                String instance = occurs(exactly(11)).toString();
                assertTrue(instance.contains("Dalton.ping():void/[11..11]"));
            }
        };
    }

    /**
     * Verifies that we can't create a negative number of exact occurrences.
     */
    @Test
    public void testOccurencesExactlyWithNegativeArgument() {
        new Scenario() {
            {
                try {
                    expect(joe).ping();
                    occurs(exactly(-23));
                } catch (IllegalOccurrencesDefinitionException e) {
                }
            }
        };
    }

    /**
     * Verifies that we can create a 'never' occurrence with 'exactly'.
     */
    @Test
    public void testOccurrencesExtactlyMeansNever() {
        new Scenario() {
            {
                expect(joe).ping();
                String instance = occurs(exactly(0)).toString();
                assertTrue(instance.contains("Dalton.ping():void/[0..0]"));
            }
        };
    }

    /**
     * Verifies that the 'never' clause is correctly registered.
     */
    @Test
    public void testOccurrencesNever() {
        new Scenario() {
            {
                expect(joe).ping();
                String instance = occurs(never()).toString();
                assertTrue(instance.contains("Dalton.ping():void/[0..0]"));
            }
        };
    }

    /**
     * Verifies that a valid 'atLeast' clause is correctly registered.
     */
    @Test
    public void testOccurencesAtLeast() {
        new Scenario() {
            {
                expect(joe).ping();
                String instance = occurs(atLeast(84)).toString();
                assertTrue(instance.contains("Dalton.ping():void/[84..]"));
            }
        };
    }

    /**
     * Verifies that 'atLeast' does not accept negative values.
     */
    @Test
    public void testOccurencesAtLeastWithInvalidArgument() {
        new Scenario() {
            {
                try {
                    expect(joe).ping();
                    occurs(atLeast(-1));
                } catch (IllegalOccurrencesDefinitionException e) {
                }
            }
        };
    }

    /**
     * Verifies that 'atLeast' 0 times stands for 'any'.
     */
    @Test
    public void testOccurencesAtLeastMeansAny() {
        new Scenario() {
            {
                expect(joe).ping();
                String instance = occurs(atLeast(0)).toString();
                assertTrue(instance.contains("Dalton.ping():void/[ANY]"));
            }
        };
    }

    /**
     * Verifies that a valid 'atMost' clause is correctly registered.
     */
    @Test
    public void testOccurencesAtMost() {
        new Scenario() {
            {
                expect(joe).ping();
                String instance = occurs(atMost(1)).toString();
                assertTrue(instance.contains("Dalton.ping():void/[..1]"));
            }
        };
    }

    /**
     * Verifies that 'atMost' does not accept negative values.
     */
    @Test
    public void testOccurencesAtMostWithInvalidArgument() {
        new Scenario() {
            {
                try {
                    expect(joe).ping();
                    occurs(atMost(-1));
                } catch (IllegalOccurrencesDefinitionException e) {
                }
            }
        };
    }

    /**
     * Verifies that 'atMost' 0 times stands for 'never'.
     */
    @Test
    public void testOccurencesAtLeastMostMeansNever() {
        new Scenario() {
            {
                expect(joe).ping();
                String instance = occurs(atMost(0)).toString();
                assertTrue(instance.contains("Dalton.ping():void/[0..0]"));
            }
        };
    }

    /**
     * Verifies that a valid 'between' clause is correctly registered.
     */
    @Test
    public void testOccurencesBetween() {
        new Scenario() {
            {
                expect(joe).ping();
                String instance = occurs(between(1, 2)).toString();
                assertTrue(instance.contains("Dalton.ping():void/[1..2]"));
            }
        };
    }

    /**
     * Verifies that a 'between' clause validates the minimum and maximum range.
     */
    @Test
    public void testOccurencesBetweenMinGreaterThanMax() {
        new Scenario() {
            {
                try {
                    expect(joe).ping();
                    occurs(between(2, 1));
                } catch (IllegalOccurrencesDefinitionException e) {
                }
            }
        };
    }

    /**
     * Verifies that a 'between' clause does not accept a negative minimum
     * value.
     */
    @Test
    public void testOccurrencesBetweenMinNegative() {
        new Scenario() {
            {
                try {
                    expect(joe).ping();
                    occurs(between(-1, 0));
                } catch (IllegalOccurrencesDefinitionException e) {
                }
            }
        };
    }

    /**
     * Verifies that a 'between' clause does not accept a negative minimum
     * value.
     */
    @Test
    public void testOccurrencesBetweenMaxNegative() {
        new Scenario() {
            {
                try {
                    expect(joe).ping();
                    occurs(between(0, -1));
                } catch (IllegalOccurrencesDefinitionException e) {
                }
            }
        };
    }

    /**
     * Verifies that 'between' 0 and 0 stands for 'never'.
     */
    @Test
    public void testOccurrencesBetweenMeansNever() {
        new Scenario() {
            {
                expect(joe).ping();
                String instance = occurs(between(0, 0)).toString();
                assertTrue(instance.contains("Dalton.ping():void/[0..0]"));
            }
        };
    }

    /**
     * Verifies that 'between' 0 and a maximum value stands for 'atMost'.
     */
    @Test
    public void testOccurrencesBetweenMeansAtMost() {
        new Scenario() {
            {
                expect(joe).ping();
                String instance = occurs(between(0, 100)).toString();
                assertTrue(instance.contains("Dalton.ping():void/[0..100]"));
            }
        };
    }

    /**
     * Validates the equivalence of <code>occurs(n)</code> and
     * <code>occurs(exactly(n))</code>.
     */
    @Test
    public void testOccursWithIntIsEquivalentToExactly() {
        new Scenario() {
            {
                expect(joe).ping();
                String instance = occurs(1).toString();
                assertTrue(instance.contains("Dalton.ping():void/[1..1]"));
            }
        };
    }

    /**
     * Validates the equivalence of <code>occurs(n)</code> and
     * <code>occurs(exactly(n))</code>.
     */
    @Test
    public void testOccursWithIntIsEquivalentToExactlyWithinExpectation() {
        new Scenario() {
            {
                expect(joe).ping();
                String instance = expect().occurs(122).toString();
                assertTrue(instance.contains("Dalton.ping():void/[122..122]"));
            }
        };
    }
}
