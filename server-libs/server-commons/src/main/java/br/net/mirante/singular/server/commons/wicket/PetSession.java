package br.net.mirante.singular.server.commons.wicket;

import br.net.mirante.singular.persistence.entity.ProcessGroupEntity;
import br.net.mirante.singular.server.commons.spring.security.ServerContext;
import br.net.mirante.singular.server.commons.spring.security.SingularUserDetails;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.springframework.security.core.context.SecurityContextHolder;

public class PetSession extends AuthenticatedWebSession {

    private ProcessGroupEntity categoriaSelecionada;

    public PetSession(Request request, Response response) {
        super(request);
    }

    public static PetSession get() {
        return (PetSession) Session.get();
    }

    @Override
    protected boolean authenticate(String username, String password) {
        return true;
    }

    @Override
    public Roles getRoles() {
        if (getUserDetails() != null) {
            return new Roles(getUserDetails().getRoles().toArray(new String[0]));
        }
        return new Roles();

    }


    public String getName() {
        if (getUserDetails() != null) {
            return getUserDetails().getDisplayName();
        }
        return "";
    }

    public String getUsername() {
        if (getUserDetails() != null) {
            return getUserDetails().getUsername();
        }
        return "";
    }

    public boolean isAuthtenticated() {
        return getUserDetails() != null;
    }

    public boolean isAnalise() {
        return isAuthtenticated() && getUserDetails().isAnalise();
    }

    public boolean isPeticionamento() {
        return isAuthtenticated() && getUserDetails().isPeticionamento();
    }

    /**
     * @return O contexto atual da sessão ou null caso ainda não tenha sido definido.
     */
    public ServerContext getServerContext() {
        if (isAuthtenticated()) {
            return getUserDetails().getServerContext();
        }
        return null;
    }


    public <T extends SingularUserDetails> T getUserDetails() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof SingularUserDetails) {
            return (T) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        return null;
    }

    public ProcessGroupEntity getCategoriaSelecionada() {
        return categoriaSelecionada;
    }

    public void setCategoriaSelecionada(ProcessGroupEntity categoriaSelecionada) {
        this.categoriaSelecionada = categoriaSelecionada;
    }
}

