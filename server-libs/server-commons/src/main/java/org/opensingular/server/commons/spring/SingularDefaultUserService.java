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

package org.opensingular.server.commons.spring;

import javax.inject.Inject;

import org.apache.wicket.Application;
import org.springframework.transaction.annotation.Transactional;

import org.opensingular.flow.core.MUser;
import org.opensingular.flow.core.service.IUserService;
import org.opensingular.server.commons.persistence.dao.flow.ActorDAO;
import org.opensingular.server.commons.wicket.SingularSession;

public class SingularDefaultUserService implements IUserService {


    @Inject
    private ActorDAO actorDAO;

    @Override
    public MUser getUserIfAvailable() {
        String username = null;

        if (Application.exists() && SingularSession.exists()) {
            username = SingularSession.get().getUsername();
        }

        if (username != null) {
            return actorDAO.buscarPorCodUsuario(username);
        } else {
            return null;
        }

    }

    @Override
    public boolean canBeAllocated(MUser mUser) {
        return true;
    }

    @Override
    public MUser findUserByCod(String username) {
        return actorDAO.buscarPorCodUsuario(username);
    }

    @Override
    @Transactional
    public MUser saveUserIfNeeded(MUser mUser) {
        return actorDAO.saveUserIfNeeded(mUser);
    }

    @Override
    @Transactional
    public MUser saveUserIfNeeded(String codUsuario) {
        return actorDAO.saveUserIfNeeded(codUsuario);
    }

    @Override
    @Transactional
    public MUser findByCod(Integer cod) {
        return actorDAO.get(cod);
    }
}
