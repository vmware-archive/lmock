/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.masquerade;

import com.vmware.lmock.clauses.HasArgumentSpecificationClauses;
import com.vmware.lmock.exception.SchemerException;

/**
 * Maintains an object to specify the arguments of an invocation within a directive.
 *
 * <p>
 * The life-cycle of such an object is longer than the basic directive definition, since it involves computing
 * the arguments. Which means that we must wait for the closure of the specification before releasing the builder.
 * </p>
 *
 * <p>
 * The provider allows to:
 * </p>
 * <ul>
 * <li>Clearly specify an argument specification builder, using <code>setArgumentSpecificationBuilder</code></li>
 * <li>Retrieve the current builder, if any, using <code>getArgumentSpecificationBuilderOrThrow</code></li>
 * <li>Cleanup the last builder set, using <code>cleanup</code></li>
 * </ul>
 *
 * <p>
 * The provider is maintained as a singleton, ensuring a safe life-cycle in the directive definition.
 * </p>
 */
final class ArgumentSpecificationProvider {
    /**  The maintained object. */
    private HasArgumentSpecificationClauses builder;
    /** The unique argument specification provider. */
    private static final ArgumentSpecificationProvider provider = new ArgumentSpecificationProvider();

    /**
     * Specifies a new builder.
     *
     * @param builder
     *            the assigned builder
     */
    private void set(HasArgumentSpecificationClauses builder) {
        this.builder = builder;
    }

    /**
     * Provides the current builder. Throws a <code>SchemerException</code> if we have nothing yet.
     *
     * @return The current provider, never <code>null</code>.
     */
    private HasArgumentSpecificationClauses getOrThrow() {
        if (builder == null) {
            throw new SchemerException("not currently building an invocation");
        } else {
            return builder;
        }
    }

    /** @return The argument specification provider. */
    private static ArgumentSpecificationProvider getProvider() {
        return provider;
    }

    /**
     * Specifies a new builder.
     *
     * @param builder
     *            the assigned builder
     */
    static void setArgumentSpecificationBuilder(HasArgumentSpecificationClauses builder) {
        getProvider().set(builder);
    }

    /**
     * Invalidates the last builder registered.
     */
    static void cleanup() {
        getProvider().set(null);
    }

    /**
     * Provides the current builder. Throws a <code>SchemerException</code> if we have nothing yet.
     *
     * @return The current provider, never <code>null</code>.
     */
    static HasArgumentSpecificationClauses getArgumentSpecificationBuilderOrThrow() {
        return getProvider().getOrThrow();
    }
}
