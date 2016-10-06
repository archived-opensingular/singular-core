/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.server.commons.spring.security;

import org.opensingular.singular.commons.util.Loggable;
import org.opensingular.flow.core.Flow;
import org.opensingular.flow.core.ProcessDefinition;
import org.opensingular.form.SFormUtil;
import org.opensingular.singular.server.commons.config.SingularServerConfiguration;
import org.opensingular.singular.server.commons.flow.rest.ActionConfig;
import org.opensingular.singular.server.commons.flow.rest.ActionDefinition;
import org.opensingular.singular.server.commons.form.FormActions;
import org.opensingular.singular.server.commons.persistence.entity.form.PetitionEntity;
import org.opensingular.singular.server.commons.service.PetitionService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe responsável por resolver as permissões do usuário em permissões do singular
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
    private SingularServerConfiguration singularServerConfiguration;


    public List<SingularPermission> searchPermissions(String idUsuario) {
        return peticionamentoUserDetailService.searchPermissions(idUsuario);
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
                .map(n -> buildActionPermission(n.getName(), processDefinition.getKey()))
                .collect(Collectors.toList());
    }

    private SingularPermission buildActionPermission(String actionName, String processName) {
        String singularId = "ACTION_" + actionName + "_" + processName;
        singularId = singularId.toUpperCase();
        return new SingularPermission(singularId, null);
    }


}
