/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.commons.spring.security;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import br.net.mirante.singular.server.commons.service.dto.MenuGroup;

public class PermissionResolverService {

    @Inject
    @Named("peticionamentoUserDetailService")
    private SingularUserDetailsService peticionamentoUserDetailService;

    public void filterBoxWithPermissions(List<MenuGroup> groupDTOs, String user) {
        List<String> perfis = peticionamentoUserDetailService.pesquisarPerfis(user);

        for (Iterator<MenuGroup> it = groupDTOs.iterator(); it.hasNext(); ) {
            MenuGroup menuGroup = it.next();
            String perfil = menuGroup.getId().toUpperCase();
            if(!perfis.contains(perfil)) {
                it.remove();
            }

        }
    }
}
