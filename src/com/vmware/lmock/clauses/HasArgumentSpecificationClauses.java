/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.clauses;

import com.vmware.lmock.checker.Checker;

/**
 * Defines clauses that specify invocation argument values.
 */
public interface HasArgumentSpecificationClauses {
    /**
     * Registers any argument of a given class for the expectation under
     * construction.
     *
     * @param <T>
     *            the type of argument
     * @param clazz
     *            the expected class of arguments
     * @return An object of the requested class.
     */
    public <T> T anyOf(Class<T> clazz);

    /**
     * Registers any non-null argument of a given class for the expectation
     * under construction.
     *
     * @param <T>
     *            the type of argument
     * @param clazz
     *            the expected class of arguments
     * @return An object of the requested class.
     */
    public <T> T aNonNullOf(Class<T> clazz);

    /**
     * Registers an argument for the current expectation under construction.
     *
     * <p>
     * This clause specifies the exact value expected during the invocations.
     * </p>
     *
     * @param <T>
     *            the type of argument
     * @param object
     *            the registered object
     * @return An object of the requested class.
     */
    public <T> T with(T object);

    /**
     * Registers an explicit checker to an argument for the current expectation
     * under construction.
     *
     * @param <T>
     *            the type of argument
     * @param checker
     *            the registered checker
     * @return An object of the related class of the checker
     */
    public <T> T with(Checker<T> checker);
}
