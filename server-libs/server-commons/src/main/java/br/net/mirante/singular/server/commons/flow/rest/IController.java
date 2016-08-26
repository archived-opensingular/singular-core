/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.commons.flow.rest;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import br.net.mirante.singular.form.SFormUtil;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.context.SFormConfig;
import br.net.mirante.singular.form.persistence.entity.FormTypeEntity;
import br.net.mirante.singular.server.commons.persistence.entity.form.PetitionEntity;
import br.net.mirante.singular.server.commons.spring.security.PermissionResolverService;

public abstract class IController {

    @Inject
    private PermissionResolverService     permissionResolverService;

    @Inject
    @Named("formConfigWithDatabase")
    private Optional<SFormConfig<String>> singularFormConfig;

    public ActionResponse run(PetitionEntity petition, Action action) {
        boolean hasPermission = permissionResolverService.hasPermission(action.getIdUsuario(), getPermissionName(petition));
        if (hasPermission) {
            return execute(petition, action);
        } else {
            return new ActionResponse("Você não tem permissão para executar esta ação.", false);
        }
    }

    protected abstract ActionResponse execute(PetitionEntity petition, Action action);

    private String getPermissionName(PetitionEntity petition) {
        String targetTypeName;
        if (getType() == Type.PROCESS) {
            targetTypeName = getProcessName(petition);

        } else {
            targetTypeName = getFormTypeName(petition);

        }
        return "ACTION_" + getActionName() + "_" + targetTypeName;
    }

    public abstract String getActionName();

    public boolean isExecutable() {
        return true;
    }

    protected Type getType() {
        return Type.PROCESS;
    }

    public String getProcessName(PetitionEntity petition) {
        return petition.getProcessDefinitionEntity().getKey();
    }

    public String getFormTypeName(PetitionEntity petition) {
        FormTypeEntity formType = petition.getMainForm().getFormType();
        SType<?>       sType    = singularFormConfig.get().getTypeLoader().loadType(formType.getAbbreviation()).get();
        return SFormUtil.getTypeSimpleName((Class<? extends SType<?>>) sType.getClass()).toUpperCase();
    }

    protected enum Type {
        PROCESS, FORM
    }
}
