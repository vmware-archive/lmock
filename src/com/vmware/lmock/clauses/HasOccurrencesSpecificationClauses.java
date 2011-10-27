/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.clauses;

import com.vmware.lmock.checker.OccurrenceChecker;

/**
 * Defines clauses that allow to specify occurrences schemes.
 *
 * <p>
 * Because such clauses can be cascaded, the implementer must define the related
 * class as <code>T</code>.
 * </p>
 * @param <T>
 *            the class implementing this interface (e.g. expectation)
 */
public interface HasOccurrencesSpecificationClauses<T> {
    /**
     * Defines an occurrences scheme.
     *
     * @param occurrences
     *            the occurrences scheme.
     * @return The item under construction.
     */
    public T occurs(OccurrenceChecker occurrences);

    /**
     * An alias to an exact number of occurrences.
     *
     * @param n
     *            the exact number of expected occurrences.
     * @return The item under construction.
     */
    public T occurs(int n);
}
