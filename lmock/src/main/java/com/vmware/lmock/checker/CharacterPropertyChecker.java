/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.checker;

/**
 * Checks one property of a character.
 *
 * <p>
 * A character property checker is a specific unit of test of a character
 * checker, focusing on a single property of the checked characters.
 * </p>
 */
public interface CharacterPropertyChecker {
    /**
     * Checks the value of a character regarding a specific property.
     *
     * @param value
     *            the checked value
     * @return <code>true</code> if the checked property is verified.
     */
    boolean valueVerifies(char value);

    /** @return A string representing the checked property */
    String getDescription();
}
