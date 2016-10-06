/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.commons.spring.security;

import br.net.mirante.singular.commons.util.Loggable;
import org.opensingular.singular.form.SFormUtil;
import org.opensingular.singular.form.SType;
import org.opensingular.singular.form.context.SFormConfig;
import br.net.mirante.singular.form.persistence.entity.FormEntity;
import br.net.mirante.singular.form.persistence.entity.FormTypeEntity;
import br.net.mirante.singular.persistence.entity.ProcessDefinitionEntity;
import br.net.mirante.singular.persistence.entity.ProcessInstanceEntity;
import br.net.mirante.singular.persistence.entity.TaskInstanceEntity;
import br.net.mirante.singular.persistence.entity.TaskVersionEntity;
import br.net.mirante.singular.server.commons.form.FormActions;
import br.net.mirante.singular.server.commons.persistence.entity.form.PetitionEntity;
import br.net.mirante.singular.server.commons.service.PetitionService;
import br.net.mirante.singular.server.commons.service.dto.BoxItemAction;
import br.net.mirante.singular.server.commons.service.dto.FormDTO;
import br.net.mirante.singular.server.commons.service.dto.MenuGroup;
import br.net.mirante.singular.server.commons.wicket.SingularSession;
import com.google.common.base.Joiner;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Classe responsável por resolver as permissões do usuário em permissões do singular
 *
 * @author Vinicius Nunes
 */
public class AuthorizationService implements Loggable {

    @Inject
    protected PermissionResolverService permissionResolverService;

    @Inject
    protected PetitionService<PetitionEntity> petitionService;

    @Inject
    @Named("peticionamentoUserDetailService")
    private SingularUserDetailsService peticionamentoUserDetailService;

    @Inject
    @Named("formConfigWithDatabase")
    private Optional<SFormConfig<String>> singularFormConfig;

    public void filterBoxWithPermissions(List<MenuGroup> groupDTOs, String idUsuario) {
        List<SingularPermission> permissions = searchPermissions(idUsuario);

        for (Iterator<MenuGroup> it = groupDTOs.iterator(); it.hasNext(); ) {
            MenuGroup menuGroup = it.next();
            String permissionNeeded = menuGroup.getId().toUpperCase();
            if (!hasPermission(idUsuario, permissionNeeded, permissions)) {
                it.remove();
            } else {
                filterForms(menuGroup, permissions, idUsuario);
            }

        }
    }

    public void filterActions(String formType, Long petitionId, List<BoxItemAction> actions, String idUsuario) {
        List<SingularPermission> permissions = searchPermissions(idUsuario);
        filterActions(formType, petitionId, actions, idUsuario, permissions);
    }

    @SuppressWarnings("unchecked")
    public void filterActions(String formType, Long petitionId, List<BoxItemAction> actions, String idUsuario, List<SingularPermission> permissions) {
        PetitionEntity petitionEntity = null;
        if (petitionId != null) {
            petitionEntity = petitionService.find(petitionId);
        }
        for (Iterator<BoxItemAction> it = actions.iterator(); it.hasNext(); ) {
            BoxItemAction action = it.next();
            String permissionsNeeded;
            String typeAbbreviation = getFormSimpleName(formType);
            if (action.getFormAction() != null) {
                permissionsNeeded = buildPermissionKey(petitionEntity, typeAbbreviation, action.getFormAction().name());
            } else {
                permissionsNeeded = buildPermissionKey(petitionEntity, typeAbbreviation, action.getName());
            }
            if (!hasPermission(idUsuario, permissionsNeeded, permissions)) {
                it.remove();
            }
        }

    }

    public boolean hasPermission(Long petitionId, String formType, String idUsuario, String action) {
        PetitionEntity petitionEntity = petitionService.find(petitionId);
        return hasPermission(petitionEntity, formType, idUsuario, action);
    }

    public boolean hasPermission(PetitionEntity petitionEntity, String formType, String idUsuario, String action) {
        String formSimpleName = getFormSimpleName(formType);
        if (petitionEntity != null) {
            FormEntity formEntity = petitionEntity.getMainForm();
            if (formEntity == null) {
                formEntity = petitionEntity.getCurrentDraftEntity().getForm();
            }
            formSimpleName = getFormSimpleName(formEntity.getFormType());
        }
        return hasPermission(idUsuario, buildPermissionKey(petitionEntity, formSimpleName, action));
    }


    protected List<SingularPermission> searchPermissions(String userPermissionKey) {
        if (SingularSession.exists()) {
            SingularUserDetails userDetails = SingularSession.get().getUserDetails();
            if (userPermissionKey.equals(userDetails.getUserPermissionKey())) {
                if (CollectionUtils.isEmpty(userDetails.getPermissions())) {
                    userDetails.addPermissions(peticionamentoUserDetailService.searchPermissions((String) userDetails.getUserPermissionKey()));
                }
                return userDetails.getPermissions();
            }
        }
        return permissionResolverService.searchPermissions(userPermissionKey);
    }


    protected void filterForms(MenuGroup menuGroup, List<SingularPermission> permissions, String idUsuario) {
        for (Iterator<FormDTO> it = menuGroup.getForms().iterator(); it.hasNext(); ) {
            FormDTO form = it.next();
            String permissionNeeded = buildPermissionKey(null, form.getAbbreviation(), FormActions.FORM_FILL.name());
            if (!hasPermission(idUsuario, permissionNeeded, permissions)) {
                it.remove();
            }
        }
    }


    protected String buildPermissionKey(PetitionEntity petitionEntity, String formSimpleName, String action) {
        String permission = Joiner.on("_")
                .skipNulls()
                .join(
                        Optional.ofNullable(action)
                                .map(String::toUpperCase)
                                .orElse(null),
                        Optional.ofNullable(formSimpleName)
                                .map(String::toUpperCase)
                                .orElse(null),
                        Optional.ofNullable(petitionEntity)
                                .map(PetitionEntity::getProcessDefinitionEntity)
                                .map(ProcessDefinitionEntity::getKey)
                                .orElse(null),
                        Optional.ofNullable(petitionEntity)
                                .map(PetitionEntity::getProcessInstanceEntity)
                                .map(ProcessInstanceEntity::getCurrentTask)
                                .map(TaskInstanceEntity::getTask)
                                .map(TaskVersionEntity::getAbbreviation)
                                .orElse(null)
                )
                .toUpperCase();
        if (getLogger().isTraceEnabled()) {
            getLogger().debug(String.format("Nome de permissão computada %s", permission));
        }
        return permission;
    }


    protected boolean hasPermission(String idUsuario, String permissionNeeded) {
        List<SingularPermission> permissions = searchPermissions(idUsuario);
        return hasPermission(idUsuario, permissionNeeded, permissions);
    }

    private String removeTask(String permissionId) {
        int idx = permissionId.lastIndexOf("_");
        if (idx > -1) {
            return permissionId.substring(0, idx);
        }
        return permissionId;
    }


    protected boolean hasPermission(String idUsuario, String permissionNeeded, List<SingularPermission> permissions) {
        if (permissions.stream().filter(ps -> ps.getSingularId().equals(permissionNeeded)).findFirst().isPresent()) {
            return true;
        }

        String definitionPermission = removeTask(permissionNeeded);
        if (permissions.stream().filter(ps -> ps.getSingularId().equals(definitionPermission)).findFirst().isPresent()) {
            return true;
        }

        getLogger().info(String.format(" Usuário logado %s não possui a permissão %s ", idUsuario, permissionNeeded));
        return false;
    }

    protected String getFormSimpleName(FormTypeEntity formType) {
        if (formType == null) {
            return null;
        }
        String formTypeName = formType.getAbbreviation();
        return getFormSimpleName(formTypeName);
    }

    protected String getFormSimpleName(String formTypeName) {
        if (StringUtils.isBlank(formTypeName)) {
            return null;
        }
        SType<?> sType = singularFormConfig.get().getTypeLoader().loadType(formTypeName).get();
        return SFormUtil.getTypeSimpleName((Class<? extends SType<?>>) sType.getClass()).toUpperCase();
    }


}
