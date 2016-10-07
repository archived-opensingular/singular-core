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

package org.opensingular.flow.core.service;

import org.opensingular.flow.core.MUser;

import javax.servlet.http.HttpSession;

public interface IUserService {

    /**
     * Solução temporária para adicionar o username na sessão no caso de chamadas via WS
     * Essa constante deve sumir em favor de uma solução de apdatação da API do flow.
     */
    public static final String USERNAME_SESSION_PARAMETER = "USERNAME_SESSION_PARAMETER";


    public static void setUsername(HttpSession session, String username) {
        session.setAttribute(USERNAME_SESSION_PARAMETER, username);
    }

    public static String getUsername(HttpSession session, String username) {
        return (String) session.getAttribute(USERNAME_SESSION_PARAMETER);
    }

    /**
     * Retorna o usuário logado na aplicação caso exista, caso contrário retorna null
     *
     * @return Retorna uma instancia de MUser corretamente construída ou null
     */
    public MUser getUserIfAvailable();

    /**
     * Verifica se o flow pode alocar uma task para o usuário passado como
     * parâmetro.
     *
     * @return Retorna true caso possa e false caso contrário
     */
    public boolean canBeAllocated(MUser user);

    public MUser findUserByCod(String username);

    default MUser saveUserIfNeeded(MUser mUser) {
        return mUser;
    }

    default MUser saveUserIfNeeded(String codUsuario) {
        return null;
    }

    MUser findByCod(Integer cod);

    public default Integer getUserCodIfAvailable() {
        MUser mUser = getUserIfAvailable();
        return mUser != null ? mUser.getCod() : null;
    }
}
