package br.net.mirante.singular.flow.core.service;

import br.net.mirante.singular.flow.core.MUser;

public interface IUserService {


    /**
     * Retorna o usuário logado na aplicação caso exista, caso contrário retorna null
     *
     * @return Retorna uma instancia de MUser corretamente construída ou null
     */
    public MUser getUserIfAvailable();

    public boolean canBeAllocated(MUser user);

}
