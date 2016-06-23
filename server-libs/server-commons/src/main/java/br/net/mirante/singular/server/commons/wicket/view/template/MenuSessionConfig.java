/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.commons.wicket.view.template;

import static br.net.mirante.singular.server.commons.service.IServerMetadataREST.PATH_LIST_MENU;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.client.RestTemplate;

import br.net.mirante.singular.persistence.entity.ProcessGroupEntity;
import br.net.mirante.singular.server.commons.service.dto.MenuGroupDTO;

/**
 * Cache de sessão da configuração de menus que são exibidos para o usuário.
 */
public class MenuSessionConfig implements Serializable {

    private boolean initialized = false;

    private Map<ProcessGroupEntity, List<MenuGroupDTO>> map = new HashMap<>();
    private Map<String, MenuGroupDTO> mapMenu = new HashMap<>();

    public Map<ProcessGroupEntity, List<MenuGroupDTO>> getMap() {
        return Collections.unmodifiableMap(map);
    }

    public void initialize(List<ProcessGroupEntity> categorias) {
        for (ProcessGroupEntity categoria : categorias) {
            final List<MenuGroupDTO> menuGroupDTOs = listMenus(categoria);
            addMenu(categoria, menuGroupDTOs);
        }

        initialized = true;
    }

    private List<MenuGroupDTO> listMenus(ProcessGroupEntity processGroup) {

        final String url = processGroup.getConnectionURL() + PATH_LIST_MENU;
        try {
            return Arrays.asList(new RestTemplate().getForObject(url, MenuGroupDTO[].class));
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private void addMenu(ProcessGroupEntity categoria, List<MenuGroupDTO> menusGroupDTO) {
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

    public boolean isInitialized() {
        return initialized;
    }
}
