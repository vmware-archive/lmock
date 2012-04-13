/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.checker;

import java.util.ArrayList;
import java.util.List;

import com.vmware.lmock.exception.CheckerCreationException;

/**
 * A checker to validate characters within ranges of values.
 *
 * <p>
 * A character checker allows to:
 * </p>
 * <ul>
 * <li>validate that a character is within a certain range</li>
 * <li>or verify that a character has a specific properties (such as
 * <code>isSpace</code>) using the constructor <code>verify</code></li>
 * </ul>
 *
 * <p>
 * This implementation only considers the most common character properties,
 * assuming that more advanced features are worth defining a new class of
 * checkers.
 * </p>
 */
public final class CharacterChecker extends ComparableChecker<Character> {
    /** When null, use the comparable checker, rely on the property otherwise. */
    private final List<CharacterPropertyChecker> propertyCheckers =
      new ArrayList<CharacterPropertyChecker>();

    /**
     * Creates a character checker to implement a comparison.
     *
     * @param min
     *            the lower bound of the allowed range (null if none)
     * @param max
     *            the upper bound of the allowed range (null if none)
     */
    private CharacterChecker(Character min, Character max) {
        super(Character.class, min, max);
    }

    /**
     * Creates a character checker to verify a set of properties.
     *
     * @param propertyCheckers
     *            the checkers that validates the property
     */
    private CharacterChecker(CharacterPropertyChecker... propertyCheckers) {
        super(Character.class, null, null);
        for (CharacterPropertyChecker checker : propertyCheckers) {
            this.propertyCheckers.add(checker);
        }
    }

    /** @return <code>true</code> if the validation relies on a property. */
    private boolean validateWithProperty() {
        return !propertyCheckers.isEmpty();
    }

    /** Checks a value regarding the set of properties, applying a logical or. */
    private boolean valueIsCompatibleWithProperties(Character value) {
        for (CharacterPropertyChecker checker : propertyCheckers) {
            if (checker.valueVerifies(value)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean valueIsCompatibleWith(Character value) {
        if (validateWithProperty()) {
            return valueIsCompatibleWithProperties(value);
        } else {
            return super.valueIsCompatibleWith(value);
        }
    }

    @Override
    public String toString() {
        if (validateWithProperty()) {
            StringBuilder builder = new StringBuilder("Character={");
            for (CharacterPropertyChecker checker : propertyCheckers) {
                builder.append(' ');
                builder.append(checker.getDescription());
            }
            builder.append(" }");
            return builder.toString();
        } else {
            return super.toString();
        }
    }

    /**
     * Creates a character checker to validate a value is lower or equal to a given character.
     *
     * @param min
     *            the minimum value allowed
     * @return A checker to test values greater or equal to the min.
     */
    public static CharacterChecker valuesGreaterOrEqualTo(char min) {
        return new CharacterChecker(min, null);
    }

    /**
     * Creates a character checker to validate a value is greater or equal to a given character.
     *
     * @param max
     *            the maximum value allowed by the checker
     * @return A checker to test values lower or equal to the max.
     */
    public static CharacterChecker valuesLowerOrEqualTo(char max) {
        return new CharacterChecker(null, max);
    }

    /**
     * Creates a character checker to validate a value within a range.
     *
     * <p>
     * The checker validates the values within the range <code>[min,max]</code>.
     * </p>
     *
     * @param min
     *            the lower bound
     * @param max
     *            the upper bound
     * @return The created checker.
     * @throws CheckerCreationException
     *             Incoherent range.
     */
    public static CharacterChecker valuesBetween(char min, char max) {
        return new CharacterChecker(min, max);
    }

    /**
     * Creates a character checker verifying that values comply with a certain
     * number of properties.
     *
     * <p>
     * The properties are expressed by the pre-defined static property checkers.
     * The method combines all those checkers together, so that an input
     * character is allowed if and only if it verifies at least one of the
     * defined properties (it's a <b>logical or</b>).
     * </p>
     *
     * <p>
     * <i>Note</i>: if no property is defined, the checker allows <b>all</b> the
     * values.
     * </p>
     *
     * @param properties
     *            the list of properties to verify
     * @return The created checker.
     */
    public static CharacterChecker verifyOneOf(CharacterPropertyChecker... properties) {
        return new CharacterChecker(properties);
    }
    /** The input value must be defined in unicode. */
    public static final CharacterPropertyChecker isDefined =
      new CharacterPropertyChecker() {
          public boolean valueVerifies(char value) {
              return Character.isDefined(value);
          }

          public String getDescription() {
              return "isDefined";
          }
      };
    /** The input value must be a digit. */
    public static final CharacterPropertyChecker isDigit =
      new CharacterPropertyChecker() {
          public boolean valueVerifies(char value) {
              return Character.isDigit(value);
          }

          public String getDescription() {
              return "isDigit";
          }
      };
    /** The input value must be an ISO control character. */
    public static final CharacterPropertyChecker isISOControl =
      new CharacterPropertyChecker() {
          public boolean valueVerifies(char value) {
              return Character.isISOControl(value);
          }

          public String getDescription() {
              return "isISOControl";
          }
      };
    /** The input value must be a letter. */
    public static final CharacterPropertyChecker isLetter =
      new CharacterPropertyChecker() {
          public boolean valueVerifies(char value) {
              return Character.isLetter(value);
          }

          public String getDescription() {
              return "isLetter";
          }
      };
    /** The input value must be a letter or a digit. */
    public static final CharacterPropertyChecker isLetterOrDigit =
      new CharacterPropertyChecker() {
          public boolean valueVerifies(char value) {
              return Character.isLetterOrDigit(value);
          }

          public String getDescription() {
              return "isLetterOrDigit";
          }
      };
    /** The input value must be lower case. */
    public static final CharacterPropertyChecker isLowerCase =
      new CharacterPropertyChecker() {
          public boolean valueVerifies(char value) {
              return Character.isLowerCase(value);
          }

          public String getDescription() {
              return "isLowerCase";
          }
      };
    /** The input value must be a Unicode space character. */
    public static final CharacterPropertyChecker isSpaceChar =
      new CharacterPropertyChecker() {
          public boolean valueVerifies(char value) {
              return Character.isSpaceChar(value);
          }

          public String getDescription() {
              return "isSpaceChar";
          }
      };
    /** The input value must be upper case. */
    public static final CharacterPropertyChecker isUpperCase =
      new CharacterPropertyChecker() {
          public boolean valueVerifies(char value) {
              return Character.isUpperCase(value);
          }

          public String getDescription() {
              return "isUpperCase";
          }
      };
    /** The input value is a Java whitespace. */
    public static final CharacterPropertyChecker isWhitespace =
      new CharacterPropertyChecker() {
          public boolean valueVerifies(char value) {
              return Character.isWhitespace(value);
          }

          public String getDescription() {
              return "isWhitespace";
          }
      };
}
