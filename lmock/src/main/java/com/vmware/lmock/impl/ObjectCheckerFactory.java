/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.impl;

import com.vmware.lmock.checker.Checker;

/**
 * A factory that produces simple object checkers or array checkers.
 *
 * <p>
 * The goal of this factory is to hide the details about how to construct one
 * checker or the other.
 * </p>
 *
 * <p>
 * Notice that the factory is created as a singleton.
 * </p>
 */
class ObjectCheckerFactory {
    /** The factory object, defined as a singleton. */
    private static final ObjectCheckerFactory factory = new ObjectCheckerFactory();

    /**
     * Creates a new checker to validate the arguments regarding a reference
     * value.
     *
     * <p>
     * The value can be:
     * </p>
     * <ul>
     * <li>A checker, in which case this is the returned value.</li>
     * <li>An array, in which case the factory produces an array checker.</li>
     * <li>Any other type of object, in which case the factory produces a simple
     * object checker.</li>
     * </ul>
     *
     * @param object
     *            the object representing the reference value
     * @param preserveCheckers
     *            if true, then if the object is a checker, return that object,
     *            otherwise create a checker for that checker
     * @return The proper checker for the supplied object.
     */
    @SuppressWarnings({"unchecked", "unchecked"})
    private Checker<Object> get(Object object, boolean preserveCheckers) {
        if (object == null) {
            return new SimpleObjectChecker(null);
        } else if (object instanceof Checker<?> && preserveCheckers) {
            return (Checker<Object>) object;
        } else if (object.getClass().isArray()) {
            return new ArrayChecker(object);
        } else {
            return new SimpleObjectChecker(object);
        }
    }

    /**
     * Creates a new checker to validate the arguments regarding a reference
     * value.
     *
     * <p>
     * If the object is a checker, then return that checker instead of creating
     * a new checker.
     * </p>
     *
     * @param object
     *            the object representing the reference value
     * @return The proper checker for the supplied object.
     */
    protected static Checker<Object> getExistingOrNewChecker(Object object) {
        return factory.get(object, true);
    }

    /**
     * Creates a new checker.
     *
     * <p>
     * If the object is a checker, then encapsulate that checker into a new one
     * (so that we validate a checker, not the validated object).
     * </p>
     *
     * @param object
     *            the object representing the reference value
     * @return The new checker for the supplied object.
     */
    protected static Checker<Object> getNewChecker(Object object) {
        return factory.get(object, false);
    }
}
