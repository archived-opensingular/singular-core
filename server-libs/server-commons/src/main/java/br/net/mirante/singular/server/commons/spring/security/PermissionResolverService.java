/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.commons.spring.security;

import br.net.mirante.singular.commons.util.Loggable;
import br.net.mirante.singular.flow.core.Flow;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.form.SFormUtil;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.context.SFormConfig;
import br.net.mirante.singular.form.persistence.entity.FormTypeEntity;
import br.net.mirante.singular.server.commons.config.SingularServerConfiguration;
import br.net.mirante.singular.server.commons.flow.rest.ActionConfig;
import br.net.mirante.singular.server.commons.flow.rest.ActionDefinition;
import br.net.mirante.singular.server.commons.form.FormActions;
import br.net.mirante.singular.server.commons.persistence.entity.form.PetitionEntity;
import br.net.mirante.singular.server.commons.service.PetitionService;
import br.net.mirante.singular.server.commons.service.dto.BoxItemAction;
import br.net.mirante.singular.server.commons.service.dto.FormDTO;
import br.net.mirante.singular.server.commons.service.dto.MenuGroup;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Classe responsável por resolver as permissões do usuário em permissões do singular
 * e verificar se o usuário possui o acesso.
 *
 * @author Delfino Filho
 */
public class PermissionResolverService implements Loggable {

    @Inject
    protected PetitionService<PetitionEntity> petitionService;
    @Inject
    @Named("peticionamentoUserDetailService")
    private   SingularUserDetailsService      peticionamentoUserDetailService;
    @Inject
    @Named("formConfigWithDatabase")
    private   Optional<SFormConfig<String>>   singularFormConfig;

    @Inject
    private SingularServerConfiguration singularServerConfiguration;

    public void filterBoxWithPermissions(List<MenuGroup> groupDTOs, String user) {
        List<String> permissions = searchPermissionsSingular(user);

        for (Iterator<MenuGroup> it = groupDTOs.iterator(); it.hasNext(); ) {
            MenuGroup menuGroup = it.next();
            String permissionNeeded = menuGroup.getId().toUpperCase();
            if (!permissions.contains(permissionNeeded)) {
                getLogger().debug(String.format(" Usuário logado %s não possui a permissão %s ", user, permissionNeeded));
                it.remove();
            } else {
                filterForms(menuGroup, permissions, user);
            }

        }
    }

    protected void filterForms(MenuGroup menuGroup, List<String> permissions, String user) {
        for (Iterator<FormDTO> it = menuGroup.getForms().iterator(); it.hasNext(); ) {
            FormDTO form = it.next();
            String permissionNeeded = FormActions.FORM_FILL + "_" + form.getAbbreviation().toUpperCase();
            if (!permissions.contains(permissionNeeded)) {
                getLogger().debug(String.format(" Usuário logado %s não possui a permissão %s ", user, permissionNeeded));
                it.remove();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void filterActions(List<Map<String, Object>> result, String idUsuarioLogado) {
        List<String> permissions = searchPermissionsSingular(idUsuarioLogado);
        for (Map<String, Object> resultItem : result) {
            List<BoxItemAction> actions = (List<BoxItemAction>) resultItem.get("actions");
            for (Iterator<BoxItemAction> it = actions.iterator(); it.hasNext(); ) {
                BoxItemAction action = it.next();
                String permissionsNeeded;
                String typeAbbreviation = getAbbreviation((String) resultItem.get("type"));
                if (action.getFormAction() != null) {
                    permissionsNeeded = action.getFormAction().toString() + "_" + typeAbbreviation;
                } else {
                    permissionsNeeded = "ACTION_" + action.getName().toUpperCase() + "_" + typeAbbreviation;
                }
                if (!permissions.contains(permissionsNeeded)) {
                    getLogger().debug(String.format(" Usuário logado %s não possui a permissão %s ", idUsuarioLogado, permissionsNeeded));
                    it.remove();
                }
            }
        }
    }

    public boolean hasFormPermission(Long id, String idUsuario, String action) {
        PetitionEntity petitionEntity = petitionService.find(id);
        return hasFormPermission(petitionEntity, idUsuario, action);
    }

    public boolean hasFormPermission(PetitionEntity petitionEntity, String idUsuario, String action) {
        FormTypeEntity formType         = petitionEntity.getMainForm().getFormType();
        String         permissionNeeded = "ACTION_" + action + "_" + getAbbreviation(formType);
        return hasPermission(idUsuario, permissionNeeded);
    }

    public boolean hasFlowPermission(Long id, String idUsuario, String action) {
        PetitionEntity petitionEntity = petitionService.find(id);
        return hasFlowPermission(petitionEntity, idUsuario, action);
    }

    public boolean hasFlowPermission(PetitionEntity petitionEntity, String idUsuario, String action) {
        String permissionNeeded = "ACTION_" + action + "_" + petitionEntity.getProcessDefinitionEntity().getKey();
        return hasPermission(idUsuario, permissionNeeded);
    }

    public boolean hasPermission(String idUsuario, String permissionNeeded) {
        List<String> permissions = searchPermissionsSingular(idUsuario);
        if (!permissions.contains(permissionNeeded.toUpperCase())) {
            getLogger().debug(String.format(" Usuário logado %s não possui a permissão %s ", idUsuario, permissionNeeded));
            return false;
        }
        return true;
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

        List<ActionDefinition> actions = new ArrayList<>();
        actions.addAll(actionConfig.getDefaultActions());
        actions.addAll(actionConfig.getCustomActions().keySet());

        return actions.stream()
                .map(n -> buildPermission(n.getName(), processDefinition.getKey()))
                .collect(Collectors.toList());
    }

    private SingularPermission buildPermission(String actionName, String processName) {
        String singularId = "ACTION_" + actionName + "_" + processName;
        singularId = singularId.toUpperCase();
        return new SingularPermission(singularId, null);
    }


}
