package br.net.mirante.singular.flow.core.service;

import br.net.mirante.singular.flow.core.MUser;

public interface IUserService {

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

}
