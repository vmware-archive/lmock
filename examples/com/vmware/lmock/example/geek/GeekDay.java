/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.example.geek;

import java.util.ArrayList;
import java.util.List;

import com.vmware.lmock.example.geek.Geek.StomachOverflowException;

/**
 * Class representing a geek day.
 *
 * The geek can perform morning or afternoon actions. One can know the number of
 * code lines written so far, if the geek can eat more cookies, list the news
 * read he read, list the stuff on the geek's desk.
 */
public class GeekDay {
    private final Geek geek;
    private int linesOfCodeWritten = 0;
    private boolean canEatMoreCookies = true;
    private final List<Object> stuffOnDesk = new ArrayList<Object>();
    private final List<String> newsRead = new ArrayList<String>();

    /**
     * Constructs a GeekDay for a given geek.
     *
     * @param geek
     *            the geek
     */
    public GeekDay(Geek geek) {
        this.geek = geek;
    }

    /**
     * Performs the geek's morning actions.
     *
     * The geek drinks coffee, then writes some code. If he has written more
     * than 200 lines of code, he drinks coffee again and attempts to eat as
     * many cookies as lines of code written.
     */
    public void morning() {
        geek.drinksCoffee();

        int writesCode = geek.writesCode();
        linesOfCodeWritten += writesCode;

        if (writesCode > 200) {
            geek.drinksCoffee();
            if (canEatMoreCookies) {
                try {
                    geek.eatsCookies(writesCode);
                } catch (StomachOverflowException e) {
                    canEatMoreCookies = false;
                }
            }
        }
    }

    /**
     * Performs the geek's afternoon actions.
     *
     * The geek puts some stuff on his desk, reads some /. news, chats on IRC
     * with another geek and gets the stuff back from his desk.
     */
    public void afternoon() {
        String stuff1 = "Starwars mug";
        String stuff2 = "Scala book";
        String stuff3 = "Galaxy Tab";
        Object stuff4 = null;
        geek.putsStuffOnDesk(stuff1, stuff2, stuff3, stuff4);

        stuffOnDesk.add(stuff1);
        stuffOnDesk.add(stuff2);
        stuffOnDesk.add(stuff3);
        stuffOnDesk.add(stuff4);

        String[] news = new String[2];
        news[0] = "Monkey Island on iPad";
        news[1] = "Quake3 on Galaxy S";
        geek.readsSlashDot(news);
        newsRead.add(news[0]);
        newsRead.add(news[1]);

        Geek otherGeek = geek.chatsOnIRC();
        if (otherGeek != null) {
            otherGeek.chatsOnIRC();
        }

        geek.getsStuffOnDesk();
    }

    public int getNumberOfCodeLinesWritten() {
        return linesOfCodeWritten;
    }

    public boolean canEatMoreCookies() {
        return canEatMoreCookies;
    }

    public List<Object> listStuffPutOnDesk() {
        return new ArrayList<Object>(stuffOnDesk);
    }

    public List<String> listNewsRead() {
        return new ArrayList<String>(newsRead);
    }
}
