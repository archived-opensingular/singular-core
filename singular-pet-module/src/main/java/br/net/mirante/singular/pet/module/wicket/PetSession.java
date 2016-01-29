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
}

