package org.opensingular.server.commons.spring.security;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.opensingular.server.commons.config.SingularServerConfiguration;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.opensingular.flow.core.MUser;
import org.opensingular.server.commons.config.IServerContext;
import org.opensingular.server.commons.persistence.dao.flow.ActorDAO;

public class DefaultUserDetailService implements SingularUserDetailsService {

    @Inject
    private ActorDAO actorDAO;

    @Inject
    private SingularServerConfiguration singularServerConfiguration;

    @Override
    public SingularUserDetails loadUserByUsername(String username, IServerContext context) throws UsernameNotFoundException {
        MUser user = actorDAO.buscarPorCodUsuario(username);
        return new DefaultUserDetails(username, new ArrayList<>(), Optional.ofNullable(user).map(MUser::getSimpleName).orElse(username), context);

    }

    @Override
    public IServerContext[] getContexts() {
        return singularServerConfiguration.getContexts();
    }

    @Override
    public List<SingularPermission> searchPermissions(String idUsuarioLogado) {
        return Collections.emptyList();
    }
}
