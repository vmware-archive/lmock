/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.impl;

import java.util.ArrayList;
import java.util.List;

import com.vmware.lmock.checker.Checker;
import com.vmware.lmock.exception.IncoherentArgumentListException;

/**
 * Builds and maintains a list of checkers, to create expectations on the fly.
 *
 * <p>
 * The checker list is populated by adding some checkers to the list (
 * <code>add</code>). When the expectation is complete, <code>buildArray</code>
 * creates the corresponding array of checkers.
 * </p>
 */
class CheckerList {
    /** The built list. */
    private final List<Checker<?>> checkerList = new ArrayList<Checker<?>>();

    /**
     * Adds a new checker to the list.
     *
     * @param checker
     *            the new checker
     */
    protected void add(Checker<?> checker) {
        checkerList.add(checker);
    }

    /**
     * @return The number of registered argument checkers in the list.
     */
    protected int size() {
        return checkerList.size();
    }

    /**
     * Packs all the elements of the checker list into an array checker.
     *
     * @param first
     *            index of the first element to pack
     * @return The built checker.
     */
    private ArrayChecker pack(int first) {
        // Resulting array.
        Object[] resultTable = new Object[checkerList.size() - first];

        for (int index = first; index < checkerList.size(); index++) {
            resultTable[index - first] = checkerList.get(index);
        }

        return new ArrayChecker(resultTable);
    }

    /**
     * Copies or pack all the elements of the argument list into a checker or a
     * table of checkers.
     *
     * <p>
     * The method is supplied an initial index. If this is the last element of
     * the argument list, the method returns a checker for that element.
     * Otherwise it picks all the remaining element and puts the corresponding
     * checkers into an array.
     * </p>
     *
     * @param index
     *            the index of the first element to copy or pack
     * @return The resulting copy or object array.
     */
    private Checker<?> copyOrPack(int index) {
        if (index == checkerList.size() - 1) {
            return ObjectCheckerFactory.getExistingOrNewChecker(checkerList.get(index));
        } else {
            return ObjectCheckerFactory.getExistingOrNewChecker(pack(index));
        }
    }

    /**
     * Verifies that the user did not mix with clauses with arguments.
     *
     * <p>
     * This method verifies that when the user invokes a method the argument
     * specification is not a mix of with clauses with actual argument values.
     * </p>
     *
     * @param invocation
     *            the invocation description
     * @throws IncoherentArgumentListException
     *             Arguments are incoherent.
     */
    private void checkWithClausesNotMixedWithArgumentValues(
      Invocation invocation) {
        if (checkerList.size() < invocation.getArgs().length) {
            throw new IncoherentArgumentListException(
              "missing 'with' clauses - "
              + "you must specify ALL the arguments with this clause");
        }
    }

    /**
     * Creates an array of the checkers based on the arguments of an invocation.
     *
     * <p>
     * The method compares the profile given by the invocation and creates the
     * corresponding array, taking into account things like variable
     * arguments...
     * </p>
     *
     * <p>
     * <b>Important:</b> the specified invocation must not be such that
     * <code>getArgs()</code> returns null.
     * </p>
     *
     * @param invocation
     *            the invocation to compare to the argument list
     * @return The built array of checkers.
     * @throws IncoherentArgumentListException
     *             The argument list does not fit with the invocation.
     */
    protected Checker<?>[] buildArray(Invocation invocation) {
        checkWithClausesNotMixedWithArgumentValues(invocation);

        // Returned value: contains the same number of arguments than the
        // invocation. Additional elements in the list may be the result of
        // a varargs process.
        int actualNrOfArgs = invocation.getArgs().length;
        Checker<?>[] result = new Checker<?>[actualNrOfArgs];

        // No need to check the parameter coherence here... We assume
        // that the compiler and the 'with' clauses did their job
        // correctly.
        int index;
        for (index = 0; index < actualNrOfArgs - 1; index++) {
            result[index] = ObjectCheckerFactory.getExistingOrNewChecker(checkerList.get(index));
        }
        if (actualNrOfArgs > 0) {
            result[index] = copyOrPack(index);
        }

        return result;
    }
}
