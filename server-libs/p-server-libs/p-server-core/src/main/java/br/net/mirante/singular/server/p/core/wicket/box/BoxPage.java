/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.p.core.wicket.box;

import static br.net.mirante.singular.server.commons.util.Parameters.*;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;

import br.net.mirante.singular.server.commons.persistence.filter.QuickFilter;
import br.net.mirante.singular.server.commons.service.dto.ItemBox;
import br.net.mirante.singular.server.commons.service.dto.MenuGroup;
import br.net.mirante.singular.server.commons.wicket.SingularSession;
import br.net.mirante.singular.server.commons.wicket.view.template.Content;
import br.net.mirante.singular.server.commons.wicket.view.template.MenuSessionConfig;
import br.net.mirante.singular.server.core.wicket.template.ServerTemplate;

public class BoxPage extends ServerTemplate {

    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(BoxPage.class);

    @Override
    protected Content getContent(String id) {

        final String            processGroupCod   = getPageParameters().get(PROCESS_GROUP_PARAM_NAME).toString();
        final String            menu              = getPageParameters().get(MENU_PARAM_NAME).toString();
        final String            item              = getPageParameters().get(ITEM_PARAM_NAME).toString();
        final MenuSessionConfig menuSessionConfig = SingularSession.get().getMenuSessionConfig();
        final MenuGroup menuGroup         = menuSessionConfig.getMenuPorLabel(menu);
        final ItemBox itemBoxDTO        = menuGroup.getItemPorLabel(item);

        /**
         * itemBoxDTO pode ser nulo quando nenhum item está selecionado.
         */
        if (itemBoxDTO != null) {
            return new BoxContent(id, processGroupCod, menuGroup.getLabel(), itemBoxDTO);
        } else {
            /**
             * Fallback
             */
            LOGGER.warn("Não existe correspondencia para o label " + String.valueOf(item));
            return new EmptyBoxContent(id);
        }

    }

    protected Map<String, String> createLinkParams() {
        return new HashMap<>();
    }

    protected QuickFilter createFilter() {
        return new QuickFilter();
    }

}