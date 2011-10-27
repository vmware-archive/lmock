/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.example.geek;

import static com.vmware.lmock.checker.Occurrences.any;
import static com.vmware.lmock.checker.Occurrences.atLeast;
import static com.vmware.lmock.checker.Occurrences.atMost;
import static com.vmware.lmock.checker.Occurrences.between;
import static com.vmware.lmock.checker.Occurrences.exactly;
import static com.vmware.lmock.checker.Occurrences.never;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vmware.lmock.example.geek.Geek.StomachOverflowException;
import com.vmware.lmock.impl.Mock;
import com.vmware.lmock.impl.Scenario;
import com.vmware.lmock.impl.Story;

/**
 * An example of mocking using the Geek interface.
 */
public class GeekDayTest {
    /** A mock of a geek. */
    private final Geek geek = Mock.getObject(Geek.class);
    /** A mock of another geek. */
    private final Geek anotherGeek = Mock.getObject(Geek.class);

    /**
     * Very basic example of expectations.
     */
    @Test
    public void lazyMorning10LinesOfCode() {
        GeekDay geekDay = new GeekDay(geek);
        final int linesOfCode = 10;

        Story story = Story.create(new Scenario() {
            {
                expect(geek).drinksCoffee();

                expect(geek).writesCode();
                willReturn(linesOfCode);
            }
        });
        story.begin();
        geekDay.morning();
        assertEquals(linesOfCode, geekDay.getNumberOfCodeLinesWritten());
        assertTrue(geekDay.canEatMoreCookies());
        story.end();
    }

    /**
     * Example that shows expectations with various occurrences.
     *
     * @throws StomachOverflowException
     */
    @Test
    public void lessLazyMorning500LinesOfCode() throws StomachOverflowException {
        GeekDay geekDay = new GeekDay(geek);
        final int linesOfCode = 500;

        Story story = Story.create(new Scenario() {
            {
                expect(geek).drinksCoffee();
                occurs(exactly(1));

                expect(geek).writesCode();
                occurs(atLeast(1));
                willReturn(linesOfCode);

                expect(geek).drinksCoffee();
                occurs(any());

                expect(geek).eatsCookies(linesOfCode);
                occurs(between(0, 1));
            }
        });
        story.begin();
        geekDay.morning();
        assertEquals(linesOfCode, geekDay.getNumberOfCodeLinesWritten());
        assertTrue(geekDay.canEatMoreCookies());
        story.end();
    }

    /**
     * Example that shows how to expect an exception. It also shows that
     * Expectation method calls can be chained.
     *
     * @throws StomachOverflowException
     */
    @Test
    public void lessLazyMorning500LinesOfCodeButTooManyCookies()
      throws StomachOverflowException {
        GeekDay geekDay = new GeekDay(geek);
        final int linesOfCode = 500;

        Story story = Story.create(new Scenario() {
            {
                expect(geek).drinksCoffee();

                expect(geek).writesCode();
                occurs(atMost(1)).willReturn(linesOfCode);

                expect(geek).drinksCoffee();

                expect(geek).eatsCookies(linesOfCode);
                occurs(any()).willThrow(new StomachOverflowException());
            }
        });
        story.begin();
        geekDay.morning();
        assertEquals(linesOfCode, geekDay.getNumberOfCodeLinesWritten());
        assertFalse(geekDay.canEatMoreCookies());
        story.end();
    }

    /**
     * Example that shows varargs and array arguments in an expectation. Also
     * shows the usage of matchers for the arguments.
     *
     * @throws StomachOverflowException
     */
    @Test
    public void lazyAfternoonPutStuffOnDeskAndReadSlashDot()
      throws StomachOverflowException {
        GeekDay geekDay = new GeekDay(geek);
        Story story = Story.create(new Scenario() {
            {
                expect(geek).putsStuffOnDesk(with("Starwars mug"),
                  with("Scala book"), aNonNullOf(Object.class),
                  anyOf(Object.class));

                expect(geek).readsSlashDot(
                  new String[]{"Monkey Island on iPad",
                      "Quake3 on Galaxy S"});

                expect(geek).chatsOnIRC();

                expect(geek).getsStuffOnDesk();
                willReturn(new Object[4]);

                expect(geek).writesCode();
                occurs(never());
            }
        });
        story.begin();
        geekDay.afternoon();
        assertEquals(4, geekDay.listStuffPutOnDesk().size());
        assertEquals(2, geekDay.listNewsRead().size());
        story.end();
    }

    /**
     * Example that shows that a mock can be returned by one expectation and be
     * invoked itself.
     *
     * @throws StomachOverflowException
     */
    @Test
    public void lazyAfternoonChatWithAnotherGeek()
      throws StomachOverflowException {
        GeekDay geekDay = new GeekDay(geek);
        Story story = Story.create(new Scenario() {
            {
                expect(geek).putsStuffOnDesk(anyOf(Object.class),
                  anyOf(Object.class), anyOf(Object.class),
                  anyOf(Object.class));

                expect(geek).readsSlashDot(anyOf(String[].class));

                expect(geek).chatsOnIRC();
                willReturn(anotherGeek);

                expect(anotherGeek).chatsOnIRC();

                expect(geek).getsStuffOnDesk();
            }
        });
        story.begin();
        geekDay.afternoon();
        assertEquals(4, geekDay.listStuffPutOnDesk().size());
        assertEquals(2, geekDay.listNewsRead().size());
        story.end();
    }
}
