/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
