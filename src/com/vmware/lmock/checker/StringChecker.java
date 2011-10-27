/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.checker;

/**
 * Basic class from which the string checkers should derive.
 *
 * <p>
 * Such a checker compares the checked arguments with a reference pattern or
 * string. It may be specified as case insensitive, using the
 * <code>caseInsensitive</code> method.
 * </p>
 *
 * <p>
 * To test that an input value is compatible with this pattern, the checker:
 * </p>
 * <ul>
 * <li>verifies that the reference is null if the value is null</li>
 * <li>converts the value to a lower case string if the checker is case
 * insensitive</li>
 * <li>compares the value with the reference</li>
 * </ul>
 *
 * <p>
 * String checkers are created by specific factory methods, defining the type of
 * test done.
 * </p>
 */
public abstract class StringChecker implements Checker<String> {
    /** String describing the type of the reference pattern. */
    private final String type;
    /** Pattern or string to check against the user supplied values. */
    private String reference;
    /** Asserted when the checker is case-insensitive. */
    private boolean isCaseInsensitive;

    /**
     * Creates a new string checker.
     *
     * @param type
     *            string describing the type of the reference pattern
     * @param reference
     *            the pattern or string to check against the user supplied value
     */
    private StringChecker(String type, String reference) {
        this.type = type;
        this.reference = reference;
        isCaseInsensitive = false;
    }

    /**
     * Specifies that this checker is case insensitive.
     */
    public void caseInsensitive() {
        isCaseInsensitive = true;
        if (reference != null) {
            reference = reference.toLowerCase();
        }
    }

    @Override
    public String toString() {
        return type + "(" + reference + ")";
    }

    @Override
    public Class<?> getRelatedClass() {
        return String.class;
    }

    @Override
    public boolean valueIsCompatibleWith(String value) {
        if (value == null) {
            return reference == null;
        } else if (reference == null) {
            return false;
        } else {
            String argument = isCaseInsensitive ? value.toLowerCase() : value;
            return valueMatchesReference(reference, argument);
        }
    }

    /**
     * Compares the reference pattern or string with a checked value.
     *
     * <p>
     * Notice that neither the input reference string/pattern nor the value are
     * null. Also notice that case-insensitivity has already been resolved when
     * invoking this method.
     * </p>
     *
     * @param reference
     *            the reference string or pattern
     * @param value
     *            the checked value
     * @return <code>true</code> if the value matches the reference.
     */
    protected abstract boolean valueMatchesReference(String reference,
      String value);

    /**
     * Creates a new string checker that checks the equality of strings with a
     * reference value.
     *
     * @param reference
     *            the reference string
     */
    public static StringChecker valuesEqual(String reference) {
        return new StringChecker("equals", reference) {
            @Override
            protected boolean valueMatchesReference(String reference,
              String value) {
                return reference.equals(value);
            }
        };
    }

    /**
     * Creates a new string checker that checks the values contain a reference
     * value.
     *
     * @param reference
     *            the reference string
     */
    public static StringChecker valuesContain(String reference) {
        return new StringChecker("contains", reference) {
            @Override
            protected boolean valueMatchesReference(String reference,
              String value) {
                return value.contains(reference);
            }
        };
    }

    /**
     * Creates a new string checker that checks that string match a reference
     * regular expression.
     *
     * @param reference
     *            the reference regular expression
     */
    public static StringChecker valuesMatch(String reference) {
        return new StringChecker("matches", reference) {
            @Override
            protected boolean valueMatchesReference(String reference,
              String value) {
                return value.matches(reference);
            }
        };
    }
}
