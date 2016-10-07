/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.server.commons.wicket.view.template;

import static org.opensingular.server.commons.service.IServerMetadataREST.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import org.opensingular.flow.persistence.entity.ProcessGroupEntity;
import org.opensingular.server.commons.service.dto.MenuGroup;

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

    public void initialize(List<ProcessGroupEntity> categorias, String menuContext, String idUsername) {
        for (ProcessGroupEntity categoria : categorias) {
            final List<MenuGroup> menuGroupDTOs = listMenus(categoria, menuContext, idUsername);
            addMenu(categoria, menuGroupDTOs);
        }

        initialized = true;
    }

    private List<MenuGroup> listMenus(ProcessGroupEntity processGroup, String menuContext, String idUsername) {

        final String url = processGroup.getConnectionURL() + PATH_LIST_MENU
                + "?" + MENU_CONTEXT + "=" + menuContext
                + "&" + USER + "=" + idUsername;
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

    public void reset() {
        this.initialized = false;
    }
}
