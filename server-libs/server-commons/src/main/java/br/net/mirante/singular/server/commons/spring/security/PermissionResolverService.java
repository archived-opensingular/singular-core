/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.commons.spring.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import br.net.mirante.singular.flow.core.Flow;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.form.SFormUtil;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.context.SFormConfig;
import br.net.mirante.singular.form.persistence.entity.FormTypeEntity;
import br.net.mirante.singular.server.commons.config.SingularServerConfiguration;
import br.net.mirante.singular.server.commons.flow.rest.ActionConfig;
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

    @Inject
    private SingularServerConfiguration singularServerConfiguration;
    
    public void filterBoxWithPermissions(List<MenuGroup> groupDTOs, String user) {
        List<String> permissions = searchPermissionsSingular(user);

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
        List<String> permissions = searchPermissionsSingular(idUsuario);
        PetitionEntity petitionEntity = petitionService.find(id);
        FormTypeEntity formType = petitionEntity.getMainForm().getFormType();
        String permissionNeeded = "ACTION_" + action + "_" + getAbbreviation(formType);
        return permissions.contains(permissionNeeded.toUpperCase());
    }

    public List<SingularPermission> searchPermissions(String idUsuario) {
        return peticionamentoUserDetailService.searchPermissions(idUsuario);
    }

    public List<String> searchPermissionsSingular(String idUsuario) {
        return peticionamentoUserDetailService.searchPermissions(idUsuario)
                .stream().map(SingularPermission::getSingularId)
                .collect(Collectors.toList());
    }

    public List<Serializable> searchPermissionsInternal(String idUsuario) {
        return peticionamentoUserDetailService.searchPermissions(idUsuario)
                .stream().map(SingularPermission::getInternalId)
                .collect(Collectors.toList());
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

    public List<? extends SingularPermission> listAllTypePermissions() {
        List<SingularPermission> permissions = new ArrayList<>();

        List<String> typeNames = listAllTypeNames();

        for (String typeName : typeNames) {
            for (FormActions action : FormActions.values()) {
                String singularId = action + "_" + typeName;
                permissions.add(new SingularPermission(singularId, null));
            }
        }

        return permissions;
    }

    private List<String> listAllTypeNames() {
        return singularServerConfiguration.getFormTypes()
                .stream()
                .map(clazz -> SFormUtil.getTypeSimpleName(clazz).toUpperCase())
                .collect(Collectors.toList());
    }

    public List<? extends SingularPermission> listAllProcessesPermissions() {
        List<SingularPermission> permissions = new ArrayList<>();

        for (Class<? extends ProcessDefinition> clazz : singularServerConfiguration.getProcessDefinitionFormNameMap().keySet()) {
            permissions.addAll(listPermissions(clazz));
        }

        return permissions;
    }

    private List<? extends SingularPermission> listPermissions(Class<? extends ProcessDefinition> clazz) {
        ProcessDefinition processDefinition = Flow.getProcessDefinition(clazz);
        ActionConfig      actionConfig      = (ActionConfig) processDefinition.getMetaDataValue(ActionConfig.KEY);

        if (actionConfig == null) {
            return Collections.emptyList();
        }

        List<String> actions = new ArrayList<>();
        actions.addAll(actionConfig.getDefaultActions());
        actions.addAll(actionConfig.getCustomActions().keySet());

        return actions.stream()
                .map(n -> buildPermission(n, processDefinition.getKey()))
                .collect(Collectors.toList());
    }

    private SingularPermission buildPermission(String actionName, String processName) {
        String singularId = "ACTION_" + actionName + "_" + processName;
        singularId = singularId.toUpperCase();
        return new SingularPermission(singularId, null);
    }


}
