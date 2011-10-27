/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.example.tutorial;

import org.junit.Test;

import com.vmware.lmock.checker.Checker;
import com.vmware.lmock.impl.Mock;
import com.vmware.lmock.impl.Scenario;
import com.vmware.lmock.impl.Story;

/**
 * Tutorial example: using checkers.
 */
public class CheckerExample {
    class User {
        private final String firstName, lastName;

        User(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        String lastName() {
            return lastName;
        }

        String firstName() {
            return firstName;
        }
    }

    interface UserDao {
        void persist(User user);
    }

    class UserDaoService {
        private final UserDao dao;

        UserDaoService(UserDao dao) {
            this.dao = dao;
        }

        void createUser(String firstName, String lastName) {
            dao.persist(new User(firstName, lastName));
        }
    }

    static class UserChecker implements Checker<User> {
        private final String firstName, lastName;

        UserChecker(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        @Override
        public boolean valueIsCompatibleWith(User value) {
            return firstName.equals(value.firstName())
              && lastName.equals(value.lastName());
        }

        @Override
        public Class<?> getRelatedClass() {
            return User.class;
        }
    }

    @Test
    public void testUserChecker() {
        final UserDao dao = Mock.getObject(UserDao.class);
        Story story = Story.create(new Scenario() {
            {
                expect(dao).persist(with(new UserChecker("john", "doe")));
                occurs(1);
            }
        });

        story.begin();
        UserDaoService instance = new UserDaoService(dao);
        instance.createUser("john", "doe");
        story.end();
    }
}
