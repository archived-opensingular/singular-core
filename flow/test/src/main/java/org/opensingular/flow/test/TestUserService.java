/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.test;

import org.opensingular.flow.core.MUser;
import org.opensingular.flow.core.service.IUserService;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class TestUserService implements IUserService {


    @Inject
    private TestDAO testDAO;

    @Override
    public MUser getUserIfAvailable() {
        return (MUser)testDAO.getSomeUser(1);
    }

    @Override
    public boolean canBeAllocated(MUser user) {
        return false;
    }

    @Override
    public MUser findUserByCod(String username) {
        return (MUser)testDAO.getSomeUser(1);
    }

    @Override
    public MUser saveUserIfNeeded(String codUsuario) {
        return (MUser)testDAO.getSomeUser(Integer.parseInt(codUsuario));
    }

    @Override
    public MUser findByCod(Integer cod) {
        return (MUser)testDAO.getSomeUser(1);
    }
}
