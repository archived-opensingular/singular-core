/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.flow.core.service;

import br.net.mirante.singular.flow.core.MUser;

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
