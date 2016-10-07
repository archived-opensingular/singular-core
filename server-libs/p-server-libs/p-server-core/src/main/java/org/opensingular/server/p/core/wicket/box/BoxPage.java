/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.server.p.core.wicket.box;

import static org.opensingular.server.commons.util.Parameters.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.opensingular.server.commons.wicket.error.AccessDeniedContent;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.LoggerFactory;

import org.opensingular.flow.persistence.entity.ProcessGroupEntity;
import org.opensingular.server.commons.persistence.filter.QuickFilter;
import org.opensingular.server.commons.service.dto.ItemBox;
import org.opensingular.server.commons.service.dto.MenuGroup;
import org.opensingular.server.commons.spring.security.SingularUserDetails;
import org.opensingular.server.commons.wicket.SingularSession;
import org.opensingular.server.commons.wicket.view.template.Content;
import org.opensingular.server.commons.wicket.view.template.MenuSessionConfig;
import org.opensingular.server.core.wicket.template.ServerTemplate;

public class BoxPage extends ServerTemplate {

    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(BoxPage.class);

    @Override
    protected Content getContent(String id) {

        String                  processGroupCod   = getPageParameters().get(PROCESS_GROUP_PARAM_NAME).toString();
        String                  menu              = getPageParameters().get(MENU_PARAM_NAME).toString();
        String                  item              = getPageParameters().get(ITEM_PARAM_NAME).toString();
        final MenuSessionConfig menuSessionConfig = SingularSession.get().getMenuSessionConfig();

        if (processGroupCod == null
                && menu == null
                && item == null) {

            for (Iterator<Map.Entry<ProcessGroupEntity, List<MenuGroup>>> it = menuSessionConfig.getMap().entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<ProcessGroupEntity, List<MenuGroup>> entry =  it.next();
                if (!entry.getValue().isEmpty()) {
                    processGroupCod = entry.getKey().getCod();
                    MenuGroup mg = entry.getValue().get(0);
                    menu = mg.getLabel();
                    item = mg.getItemBoxes().get(0).getName();
                    PageParameters pageParameters = new PageParameters();
                    pageParameters.add(PROCESS_GROUP_PARAM_NAME, processGroupCod);
                    pageParameters.add(MENU_PARAM_NAME,  menu);
                    pageParameters.add(ITEM_PARAM_NAME, item);
                    throw new RestartResponseException(getPage().getClass(), pageParameters);
                }
            }



        }

        final MenuGroup         menuGroup         = menuSessionConfig.getMenuPorLabel(menu);

        if (menuGroup != null) {
            final ItemBox itemBoxDTO = menuGroup.getItemPorLabel(item);
            /**
             * itemBoxDTO pode ser nulo quando nenhum item está selecionado.
             */
            if (itemBoxDTO != null) {
                return new BoxContent(id, processGroupCod, menuGroup.getLabel(), itemBoxDTO);
            }
        }

        /**
         * Fallback
         */
        LOGGER.warn("Não existe correspondencia para o label " + String.valueOf(item));
        return new AccessDeniedContent(id);
    }

    protected Map<String, String> createLinkParams() {
        return new HashMap<>();
    }

    protected QuickFilter createFilter() {
        return new QuickFilter();
    }

    protected String getIdUsuario() {
        SingularUserDetails userDetails = SingularSession.get().getUserDetails();
        return Optional.ofNullable(userDetails)
                .map(SingularUserDetails::getUsername)
                .orElse(null);
    }

}