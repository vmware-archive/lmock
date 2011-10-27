/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.impl;

import java.util.Properties;

/**
 * System specific behaviors.
 *
 * <p>
 * Some underlying features of the mocking system may not be available depending
 * on the VM it works on, due to specific behaviors regarding reflection.
 * </p>
 * <p>
 * This static class checks the running VM and turns on or off those features.
 * </p>
 */
public final class SystemSpecifics {
    private final boolean willCheckExceptionIntegrity;
    /** Singleton representing the system specific information. */
    private static final SystemSpecifics systemSpecifics = new SystemSpecifics();

    /** @return <code>true</code> if the system checks the exception types. */
    private boolean getWillCheckExceptionIntegrity() {
        return willCheckExceptionIntegrity;
    }

    /**
     * Checks the system properties to create the system specific information.
     */
    private SystemSpecifics() {
        Properties properties = System.getProperties();
        String vmName = properties.getProperty("java.vm.name");
        willCheckExceptionIntegrity = !vmName.equals("Dalvik");
    }

    /**
     * @return <code>true</code> if the <code>willThrow</code> clause can check
     *         that the declared exception complies with the expected method
     *         prototype.
     */
    public static boolean willCheckExceptionIntegrity() {
        return systemSpecifics.getWillCheckExceptionIntegrity();
    }
}
