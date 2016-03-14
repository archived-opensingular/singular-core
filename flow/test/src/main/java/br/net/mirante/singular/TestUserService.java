/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular;

import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.service.IUserService;
import br.net.mirante.singular.persistence.entity.Actor;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

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
}
