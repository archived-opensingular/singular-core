/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.commons.wicket.view.template;

import static br.net.mirante.singular.server.commons.service.IServerMetadataREST.MENU_CONTEXT;
import static br.net.mirante.singular.server.commons.service.IServerMetadataREST.PATH_LIST_MENU;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import br.net.mirante.singular.persistence.entity.ProcessGroupEntity;
import br.net.mirante.singular.server.commons.service.dto.MenuGroup;

/**
 * Cache de sessão da configuração de menus que são exibidos para o usuário.
 */
public class MenuSessionConfig implements Serializable {

    static final Logger LOGGER = LoggerFactory.getLogger(MenuSessionConfig.class);

    private boolean initialized = false;

    private Map<ProcessGroupEntity, List<MenuGroup>> map = new HashMap<>();
    private Map<String, MenuGroup> mapMenu = new HashMap<>();

    public Map<ProcessGroupEntity, List<MenuGroup>> getMap() {
        return Collections.unmodifiableMap(map);
    }

    public void initialize(List<ProcessGroupEntity> categorias, String menuContext) {
        for (ProcessGroupEntity categoria : categorias) {
            final List<MenuGroup> menuGroupDTOs = listMenus(categoria, menuContext);
            addMenu(categoria, menuGroupDTOs);
        }

        initialized = true;
    }

    private List<MenuGroup> listMenus(ProcessGroupEntity processGroup, String menuContext) {

        final String url = processGroup.getConnectionURL() + PATH_LIST_MENU
                + "?" + MENU_CONTEXT + "=" + menuContext;
        try {
            return Arrays.asList(new RestTemplate().getForObject(url, MenuGroup[].class));
        } catch (Exception e) {
            LOGGER.error("Erro ao acessar serviço: " + url, e);
            return Collections.emptyList();
        }
    }

    private void addMenu(ProcessGroupEntity categoria, List<MenuGroup> menusGroupDTO) {
        mapMenu = null;
        map.put(categoria, menusGroupDTO);
    }

    public List<MenuGroup> getMenusPorCategoria(ProcessGroupEntity categoria) {
        return map.get(categoria);
    }

    public MenuGroup getMenuPorLabel(String label) {
        return getMapMenu().get(label);
    }

    private Map<String,MenuGroup> getMapMenu() {
        if (mapMenu == null) {
            mapMenu = new HashMap<>();
        }

        for (Map.Entry<ProcessGroupEntity, List<MenuGroup>> processGroupEntityListEntry : map.entrySet()) {
            for (MenuGroup menuGroupDTO : processGroupEntityListEntry.getValue()) {
                mapMenu.put(menuGroupDTO.getLabel(), menuGroupDTO);
            }
        }

        return mapMenu;
    }

    public boolean isInitialized() {
        return initialized;
    }
}
