/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.clauses;

import com.vmware.lmock.impl.Scenario;
import com.vmware.lmock.impl.Stubs;

/**
 * Clauses used to define the behavior of actors.
 */
public interface HasActorClauses {
    /**
     * Specifies a scenario followed by this actor.
     *
     * <p>
     * The given scenario can be specific to this actor, or shared by several
     * actors.
     * </p>
     *
     * @param scenario
     *            the assigned scenario
     * @return This clause provider, to cascade specifications.
     */
    public HasActorClauses following(Scenario scenario);

    /**
     * Specifies a set of stubs used by this actor.
     *
     * <p>
     * The given stubs can be specific to this actor, or common to several
     * actors. If the actor is already using other stubs, those are replaced
     * by the new stubs specified herein.
     * </p>
     *
     * @param stubsList
     *            the list of stubs
     * @return This clause provider, to cascade specifications.
     */
    public HasActorClauses using(Stubs... stubsList);
}
