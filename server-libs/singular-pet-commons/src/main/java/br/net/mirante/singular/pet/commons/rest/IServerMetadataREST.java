/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.pet.commons.rest;

import java.util.List;

import org.springframework.web.bind.annotation.RestController;

import br.net.mirante.singular.pet.commons.rest.dto.MenuGroupDTO;

public interface IServerMetadataREST {

    static final String PATH_LIST_MENU = "/server/menu/list";

    public List<MenuGroupDTO> listMenu();

}
