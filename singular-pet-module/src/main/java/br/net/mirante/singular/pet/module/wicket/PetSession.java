package br.net.mirante.singular.pet.module.wicket;

import br.net.mirante.singular.flow.core.MUser;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.springframework.security.core.context.SecurityContextHolder;

public class PetSession extends AuthenticatedWebSession {

    private Roles roles = new Roles();
    private String name;
    private String avatar;
    private String logout;

    public PetSession(Request request, Response response) {
        super(request);
        this.roles.add(Roles.USER);
    }

    public static PetSession get() {
        return (PetSession) Session.get();
    }

    @Override
    protected boolean authenticate(String username, String password) {
        return false;
    }

    @Override
    public Roles getRoles() {
        return roles;
    }

    public void addRole(String roleKey) {
        roles.add(roleKey);
    }

    public String getUserId() {
        return getUser().getCod().toString();
    }

    public MUser getUser() {
        return (MUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public boolean hasAdminRole() {
        return roles.hasRole(Roles.ADMIN);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLogout() {
        return logout;
    }

    public void setLogout(String logout) {
        this.logout = logout;
    }
}

