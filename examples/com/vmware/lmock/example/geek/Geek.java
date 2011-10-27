/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.example.geek;

/**
 * Interface representing a geek.
 */
public interface Geek {
    /**
     * Exception thrown when the geek cannot eat anymore.
     */
    public class StomachOverflowException extends Exception {
        private static final long serialVersionUID = 3061385086334819193L;
    }

    /**
     * Drinks coffee.
     */
    public void drinksCoffee();

    /**
     * Eats some cookies.
     *
     * @param count
     *            the number of cookies eaten
     * @throws StomachOverflowException
     *             When the geek ate too much cookies.
     */
    public void eatsCookies(int count) throws StomachOverflowException;

    /**
     * @return An array containing all the stuff on the geek's desk.
     */
    public Object[] getsStuffOnDesk();

    /**
     * Puts a variable number of objects on the geek's desk.
     *
     * @param misc
     *            the varargs list of objects
     */
    public void putsStuffOnDesk(Object... misc);

    /**
     * Reads /. news.
     *
     * @param newTitles
     *            the array of news titles
     */
    public void readsSlashDot(String[] newTitles);

    /**
     * Chats on IRC.
     *
     * @return Another geek if any, so can be null.
     *
     */
    public Geek chatsOnIRC();

    /**
     * Writes some code.
     *
     * @return The number of written lines.
     */
    public int writesCode();
}
