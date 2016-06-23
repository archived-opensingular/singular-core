/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.p.core.wicket.box;

import static br.net.mirante.singular.server.commons.util.Parameters.*;

import java.util.HashMap;
import java.util.Map;

import br.net.mirante.singular.server.commons.service.dto.ItemBoxDTO;
import br.net.mirante.singular.server.commons.service.dto.MenuGroupDTO;
import br.net.mirante.singular.server.commons.wicket.SingularSession;
import br.net.mirante.singular.server.commons.wicket.view.template.Content;
import br.net.mirante.singular.server.commons.wicket.view.template.MenuSessionConfig;
import br.net.mirante.singular.server.core.wicket.template.ServerTemplate;

public class BoxPage extends ServerTemplate {

    @Override
    protected Content getContent(String id) {

        final String processGroupCod = getPageParameters().get(PROCESS_GROUP_PARAM_NAME).toString();
        final String menu = getPageParameters().get(MENU_PARAM_NAME).toString();
        final String item = getPageParameters().get(ITEM_PARAM_NAME).toString();

        final MenuSessionConfig menuSessionConfig = SingularSession.get().getMenuSessionConfig();
        MenuGroupDTO menuGroup = menuSessionConfig.getMenuPorLabel(menu);
        ItemBoxDTO itemBoxDTO = menuGroup.getItemPorLabel(item);

        return new BoxContent(id, processGroupCod, menuGroup.getLabel(), itemBoxDTO);
    }

    protected Map<String, String> createLinkParams() {
        return new HashMap<>();
    }
}
