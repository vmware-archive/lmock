/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.clauses;

import com.vmware.lmock.clauses.InnerSchemerFactoryClauses.HasExpectationFactoryClauses;
import com.vmware.lmock.clauses.InnerSchemerFactoryClauses.HasResultClauses;

/**
 * Implements the grammar used to weave stubs or expectations.
 *
 * <p>
 * This interface defines a hierarchy on the different methods provided by the
 * schemer's factory to create stubs an expectations.
 * </p>
 *
 * <p>
 * The resulting grammar implements the following:
 * </p>
 * <ul>
 * <li><code>willInvoke(O).of(M).I</code>: expects that the invocation <code>M.I</code> occurs
 * according to the occurrences scheme defined by <code>O</code></li>
 * <li><code>willXXX(V).when(M).I</code>: specifies the result of any invocation
 * of <code>M.I</code> is <code>V</code> (<code>XXX</code> is return, throw or delegate)</li>
 * <li><code>willInvoke(O).willXXX(V).when(M).I</code>: expects that the invocation
 * of <code>M.I</code> occurs according to the occurrences scheme defined by <code>O</code> and that the
 * result is <code>V</code> (<code>XXX</code> is return, throw or delegate)</li>
 * </ul>
 */
public interface HasDirectiveClauses extends HasResultClauses,
  HasExpectationFactoryClauses {
}
