package br.net.mirante.singular.server.commons.spring;

import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.service.IUserService;
import br.net.mirante.singular.server.commons.persistence.dao.flow.ActorDAO;
import br.net.mirante.singular.server.commons.wicket.SingularSession;
import org.apache.wicket.Application;

import javax.inject.Inject;

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
    public MUser saveUserIfNeeded(MUser mUser) {
        return actorDAO.saveUserIfNeeded(mUser);
    }

    @Override
    public MUser saveUserIfNeeded(String codUsuario) {
        return actorDAO.saveUserIfNeeded(codUsuario);
    }
}
