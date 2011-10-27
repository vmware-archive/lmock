/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.example.tutorial;

import org.junit.Test;

import com.vmware.lmock.impl.Mock;
import com.vmware.lmock.impl.Story;
import com.vmware.lmock.impl.Stubs;

/**
 * Tutorial example: introducing stubs.
 */
public class StubExample {
    interface IntlContext {
        String getLanguage();

        String getCountry();

        String getCurrency();

        String getTimezone();

        String getDateTimeFormat();

        String getMetricSystem();

        String getTemperatureUnit();
    }

    static public class TimeUtil {
        private final IntlContext context;

        public TimeUtil(IntlContext context) {
            this.context = context;
        }

        public void displayTime() {
            System.out.println("using intl context: " + context.getTimezone()
              + " " + context.getDateTimeFormat());
            // Implement the actual method, using the context.
        }
    }

    @Test
    public void testDisplayTime() {
        final IntlContext context = Mock.getObject(IntlContext.class);
        Stubs stubs = new Stubs() {
            {
                stub(context).getDateTimeFormat();
                willReturn("yy/mm/dd");
                stub(context).getTimezone();
                willReturn("UTC+1");
            }
        };

        Story story = Story.create(null, stubs);
        story.begin();
        TimeUtil tu = new TimeUtil(context);
        tu.displayTime();
        story.end();
    }
}
