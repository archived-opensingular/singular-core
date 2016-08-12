/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.commons.spring.security;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import br.net.mirante.singular.server.commons.form.FormActions;
import br.net.mirante.singular.server.commons.service.dto.FormDTO;
import br.net.mirante.singular.server.commons.service.dto.MenuGroup;

public class PermissionResolverService {

    @Inject
    @Named("peticionamentoUserDetailService")
    private SingularUserDetailsService peticionamentoUserDetailService;

    public void filterBoxWithPermissions(List<MenuGroup> groupDTOs, String user) {
        List<String> perfis = peticionamentoUserDetailService.pesquisarAcessos(user);

        for (Iterator<MenuGroup> it = groupDTOs.iterator(); it.hasNext(); ) {
            MenuGroup menuGroup = it.next();
            String perfil = menuGroup.getId().toUpperCase();
            if (!perfis.contains(perfil)) {
                it.remove();
            } else {
                filterForms(menuGroup, perfis);
            }

        }
    }

    private void filterForms(MenuGroup menuGroup, List<String> perfis) {
        for (Iterator<FormDTO> it = menuGroup.getForms().iterator(); it.hasNext(); ) {
            FormDTO form = it.next();
            if (!perfis.contains(FormActions.FORM_FILL + "_" + form.getAbbreviation().toUpperCase())) {
                it.remove();
            }
        }
    }

}
