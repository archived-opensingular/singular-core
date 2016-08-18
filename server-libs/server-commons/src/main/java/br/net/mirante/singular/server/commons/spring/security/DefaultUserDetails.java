package br.net.mirante.singular.server.commons.spring.security;


import java.util.ArrayList;
import java.util.List;

import br.net.mirante.singular.server.commons.config.IServerContext;

public class DefaultUserDetails implements SingularUserDetails {

    private String displayName;

    private List<SingularPermission> permissions = new ArrayList<>();

    private IServerContext serverContext;

    private String username;

    public DefaultUserDetails(String username, List<SingularPermission> roles, String displayName, IServerContext context) {
        this.username = username;
        this.permissions = roles;
        this.displayName = displayName;
        this.serverContext = context;
    }

    @Override
    public void addPermission(SingularPermission role) {
        permissions.add(role);
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
    public List<SingularPermission> getPermissions() {
        return permissions;
    }

    @Override
    public String getUsername() {
        return username;
    }

}
