package br.net.mirante.singular.pet.module.wicket;

import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

public class PetSession extends AuthenticatedWebSession {

    private Roles roles = new Roles();
    private String name;
    private String avatar;
    private String userId;

    private Map<String, Object> params = new HashMap<>();

    public PetSession(Request request, Response response) {
        super(request);
        this.roles.add(Roles.USER);
        Principal principal = ((HttpServletRequest) request.getContainerRequest()).getUserPrincipal();
        this.userId = principal == null ? null : principal.getName();
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


    public void put(String key, Object value) {
        params.put(key, value);
    }

    public <T> T get(String key) {
        return (T) params.get(key);
    }

    public String getAvatar() {
        return avatar;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }
}

