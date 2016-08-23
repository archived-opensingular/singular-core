/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.commons.service;

import java.util.List;

import br.net.mirante.singular.server.commons.service.dto.MenuGroup;
import br.net.mirante.singular.server.commons.spring.security.SingularPermission;

public interface IServerMetadataREST {

    String PATH_LIST_MENU = "/server/menu/list";
    String PATH_LIST_PERMISSIONS = "/server/permissions";
    String MENU_CONTEXT = "menuContext";
    String USER = "user";
    String PATH_BOX_SEARCH = "/box/search";

    List<MenuGroup> listMenu(String context, String user);

    List<SingularPermission> listAllPermissions();

}
