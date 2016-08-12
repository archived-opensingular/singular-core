package br.net.mirante.singular.server.commons.spring.security;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.server.commons.config.IServerContext;
import br.net.mirante.singular.server.commons.config.ServerContext;
import br.net.mirante.singular.server.commons.persistence.dao.flow.ActorDAO;

public class DefaultUserDetailService implements SingularUserDetailsService {

    @Inject
    private ActorDAO actorDAO;

    @Override
    public SingularUserDetails loadUserByUsername(String username, IServerContext context) throws UsernameNotFoundException {
        MUser user = actorDAO.buscarPorCodUsuario(username);
        return new DefaultUserDetails(username, new ArrayList<>(), Optional.ofNullable(user).map(MUser::getSimpleName).orElse(username), context);

    }

    @Override
    public IServerContext[] getContexts() {
        return ServerContext.values();
    }

    @Override
    public List<String> pesquisarAcessos(String idUsuarioLogado) {
        return Collections.emptyList();
    }
}
