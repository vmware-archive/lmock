/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.checker;

/**
 * Checker dedicated to the matching of thread.
 *
 * <p>
 * This abstract class allows to define a user specific procedure to recognize
 * threads. It also comes with a set of standard matchers, defined by factory
 * methods.
 * </p>
 */
public abstract class ThreadChecker implements Checker<Thread> {
    public Class<?> getRelatedClass() {
        return Thread.class;
    }

    /**
     * Creates a checker that matches exactly one thread.
     *
     * @param thread
     *            the reference thread object
     * @return The created checker.
     */
    public static ThreadChecker equalTo(final Thread thread) {
        return new ThreadChecker() {
            public boolean valueIsCompatibleWith(Thread value) {
                return thread == value;
            }
        };
    }

    /**
     * Creates a checker that matches a thread on the basis of its name.
     *
     * @param name
     *            the thread name
     * @return The created checker.
     */
    public static ThreadChecker threadsCalled(final String name) {
        assert name != null;
        return new ThreadChecker() {
            public boolean valueIsCompatibleWith(Thread value) {
                return value.getName() != null && value.getName().equals(name);
            }
        };
    }

    /**
     * Creates a checker that matches threads implementing a specific class.
     *
     * <p>
     * This checker ideally fits the situation where the application specializes
     * the implemented threads with specific classes.
     * </p>
     *
     * @param <T>
     *            the actual class of the checked threads
     * @param clazz
     *            the actual class of the checked threads
     * @return The created checker.
     */
    public static <T> ThreadChecker instancesOf(final Class<T> clazz) {
        assert clazz != null;
        return new ThreadChecker() {
            public boolean valueIsCompatibleWith(Thread value) {
                return clazz.isAssignableFrom(value.getClass());
            }
        };
    }
    /**
     * A checker that accepts any thread.
     */
    public static final ThreadChecker anyThread = new ThreadChecker() {
        public boolean valueIsCompatibleWith(Thread value) {
            return true;
        }
    };
}
