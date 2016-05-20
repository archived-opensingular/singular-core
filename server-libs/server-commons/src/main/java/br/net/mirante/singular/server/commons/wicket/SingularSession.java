package br.net.mirante.singular.server.commons.wicket;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.springframework.security.core.context.SecurityContextHolder;

import br.net.mirante.singular.persistence.entity.ProcessGroupEntity;
import br.net.mirante.singular.server.commons.config.IServerContext;
import br.net.mirante.singular.server.commons.service.dto.MenuGroupDTO;
import br.net.mirante.singular.server.commons.spring.security.SingularUserDetails;

public class SingularSession extends AuthenticatedWebSession {

    private ProcessGroupEntity categoriaSelecionada;

    private Map<ProcessGroupEntity, List<MenuGroupDTO>> map = new HashMap<>();
    private Map<String, MenuGroupDTO> mapMenu = new HashMap<>();

    public SingularSession(Request request, Response response) {
        super(request);
    }

    public static SingularSession get() {
        return (SingularSession) Session.get();
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

    public List<String> getRoleIds() {
        return getRoles().stream().collect(Collectors.toList());
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

    /**
     * @return O contexto atual da sessão ou null caso ainda não tenha sido definido.
     */
    public IServerContext getServerContext() {
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

    public Map<ProcessGroupEntity, List<MenuGroupDTO>> getMap() {
        return Collections.unmodifiableMap(map);
    }

    public void addMenu(ProcessGroupEntity categoria, List<MenuGroupDTO> menusGroupDTO) {
        mapMenu = null;
        map.put(categoria, menusGroupDTO);
    }

    public List<MenuGroupDTO> getMenusPorCategoria(ProcessGroupEntity categoria) {
        return map.get(categoria);
    }

    public MenuGroupDTO getMenuPorLabel(String label) {
        return getMapMenu().get(label);
    }

    private Map<String,MenuGroupDTO> getMapMenu() {
        if (mapMenu == null) {
            mapMenu = new HashMap<>();
        }

        for (Map.Entry<ProcessGroupEntity, List<MenuGroupDTO>> processGroupEntityListEntry : map.entrySet()) {
            for (MenuGroupDTO menuGroupDTO : processGroupEntityListEntry.getValue()) {
                mapMenu.put(menuGroupDTO.getLabel(), menuGroupDTO);
            }
        }

        return mapMenu;
    }
}

