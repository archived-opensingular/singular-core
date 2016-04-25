package br.net.mirante.singular.server.core.spring.security;



import br.net.mirante.singular.server.commons.config.IServerContext;
import br.net.mirante.singular.server.commons.spring.security.SingularUserDetails;

import java.util.ArrayList;
import java.util.List;

public class WUserDetails implements SingularUserDetails {

    private String displayName;

    private List<String> roles = new ArrayList<>();

    private IServerContext serverContext;

    private String username;

    public WUserDetails(String username, List<String> roles, String displayName, IServerContext context) {
        this.username = username;
        this.roles = roles;
        this.displayName = displayName;
        this.serverContext = context;
    }

    @Override
    public void addRole(String role) {
        roles.add(role);
    }

    @Override
    public IServerContext getServerContext() {
        return serverContext;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public List<String> getRoles() {
        return roles;
    }

    @Override
    public String getUsername() {
        return username;
    }

}
