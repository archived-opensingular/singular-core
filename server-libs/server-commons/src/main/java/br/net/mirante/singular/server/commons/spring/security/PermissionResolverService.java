/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.commons.spring.security;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import br.net.mirante.singular.form.SFormUtil;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.context.SFormConfig;
import br.net.mirante.singular.form.persistence.entity.FormTypeEntity;
import br.net.mirante.singular.server.commons.form.FormActions;
import br.net.mirante.singular.server.commons.persistence.entity.form.PetitionEntity;
import br.net.mirante.singular.server.commons.service.PetitionService;
import br.net.mirante.singular.server.commons.service.dto.FormDTO;
import br.net.mirante.singular.server.commons.service.dto.MenuGroup;

public class PermissionResolverService {

    @Inject
    @Named("peticionamentoUserDetailService")
    private SingularUserDetailsService peticionamentoUserDetailService;

    @Inject
    protected PetitionService<PetitionEntity> petitionService;

    @Inject
    @Named("formConfigWithDatabase")
    private Optional<SFormConfig<String>> singularFormConfig;

    public void filterBoxWithPermissions(List<MenuGroup> groupDTOs, String user) {
        List<String> permissions = peticionamentoUserDetailService.searchPermissions(user);

        for (Iterator<MenuGroup> it = groupDTOs.iterator(); it.hasNext(); ) {
            MenuGroup menuGroup = it.next();
            String permissionNeeded = menuGroup.getId().toUpperCase();
            if (!permissions.contains(permissionNeeded)) {
                it.remove();
            } else {
                filterForms(menuGroup, permissions);
            }

        }
    }

    private void filterForms(MenuGroup menuGroup, List<String> permissions) {
        for (Iterator<FormDTO> it = menuGroup.getForms().iterator(); it.hasNext(); ) {
            FormDTO form = it.next();
            String permissionNeeded = FormActions.FORM_FILL + "_" + form.getAbbreviation().toUpperCase();
            if (!permissions.contains(permissionNeeded)) {
                it.remove();
            }
        }
    }

    public boolean hasPermission(Long id, String idUsuario, String action) {
        List<String> permissions = peticionamentoUserDetailService.searchPermissions(idUsuario);
        PetitionEntity petitionEntity = petitionService.find(id);
        FormTypeEntity formType = petitionEntity.getCurrentDraftEntity().getForm().getFormType();
        String permissionNeeded = "ACTION_" + action + "_" + getAbbreviation(formType);
        return permissions.contains(permissionNeeded.toUpperCase());
    }

    public List<String> searchPermissions(String idUsuario) {
        return peticionamentoUserDetailService.searchPermissions(idUsuario);
    }

    public String getAbbreviation(FormTypeEntity formType) {
        String formTypeName = formType.getAbbreviation();
        return getAbbreviation(formTypeName);
    }

    public String getAbbreviation(String formTypeName) {
        SType<?> sType = getSingularFormConfig().getTypeLoader().loadType(formTypeName).get();
        return SFormUtil.getTypeSimpleName((Class<? extends SType<?>>) sType.getClass()).toUpperCase();
    }

    private SFormConfig<String> getSingularFormConfig() {
        return singularFormConfig.get();
    }

}
