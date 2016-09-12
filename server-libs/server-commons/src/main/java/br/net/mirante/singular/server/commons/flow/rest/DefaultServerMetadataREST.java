/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.commons.flow.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import br.net.mirante.singular.server.commons.spring.security.AuthorizationService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.net.mirante.singular.commons.base.SingularUtil;
import br.net.mirante.singular.flow.core.Flow;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.form.SFormUtil;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.context.SFormConfig;
import br.net.mirante.singular.server.commons.config.SingularServerConfiguration;
import br.net.mirante.singular.server.commons.service.IServerMetadataREST;
import br.net.mirante.singular.server.commons.service.dto.FormDTO;
import br.net.mirante.singular.server.commons.service.dto.MenuGroup;
import br.net.mirante.singular.server.commons.service.dto.ProcessDTO;
import br.net.mirante.singular.server.commons.spring.security.PermissionResolverService;
import br.net.mirante.singular.server.commons.spring.security.SingularPermission;
import br.net.mirante.singular.support.spring.util.AutoScanDisabled;

@AutoScanDisabled
@RequestMapping("/rest/flow")
@RestController
public class DefaultServerMetadataREST implements IServerMetadataREST {

    @Inject
    protected SingularServerConfiguration singularServerConfiguration;

    @Inject
    protected AuthorizationService authorizationService;

    @Inject
    protected PermissionResolverService permissionResolverService;

    @Inject
    @Named("formConfigWithDatabase")
    protected SFormConfig<String> singularFormConfig;

    @Override
    @RequestMapping(value = PATH_LIST_MENU, method = RequestMethod.GET)
    public List<MenuGroup> listMenu(@RequestParam(MENU_CONTEXT) String context, @RequestParam(USER) String user) {

        List<MenuGroup> groups = listMenuGroups();

        filterAccessRight(groups, user);

        customizeMenu(groups, context, user);

        return groups;
    }

    protected List<MenuGroup> listMenuGroups() {
        List<MenuGroup>                      groups        = new ArrayList<>();
        Map<String, List<ProcessDefinition>> definitionMap = new HashMap<>();

        Flow.getDefinitions().forEach(d -> {
            if (!definitionMap.containsKey(d.getCategory())) {
                definitionMap.put(d.getCategory(), new ArrayList<>());
            }
            definitionMap.get(d.getCategory()).add(d);

        });

        definitionMap.forEach((category, definitions) -> {
            MenuGroup menuGroup = new MenuGroup();
            menuGroup.setId("BOX_" + SingularUtil.normalize(category).toUpperCase());
            menuGroup.setLabel(category);
            menuGroup.setProcesses(new ArrayList<>());
            menuGroup.setForms(new ArrayList<>());
            definitions.forEach(d ->
                            menuGroup
                                    .getProcesses()
                                    .add(
                                            new ProcessDTO(d.getKey(), d.getName(), singularServerConfiguration.getProcessDefinitionFormNameMap().get(d.getClass()))
                                    )
            );

            addForms(menuGroup);

            groups.add(menuGroup);
        });

        return groups;
    }

    protected void addForms(MenuGroup menuGroup) {
        for (Class<? extends SType<?>> formClass : singularServerConfiguration.getFormTypes()) {
            String name = SFormUtil.getTypeName(formClass);
            SType<?> sType = singularFormConfig.getTypeLoader().loadType(name).get();
            Class<? extends SType<?>> sTypeClass = (Class<? extends SType<?>>) sType.getClass();
            String label = sType.asAtr().getLabel();
            menuGroup.getForms().add(new FormDTO(name, SFormUtil.getTypeSimpleName(sTypeClass), label));
        }
    }

    protected void filterAccessRight(List<MenuGroup> groupDTOs, String user) {
        authorizationService.filterBoxWithPermissions(groupDTOs, user);
    }

    protected void customizeMenu(List<MenuGroup> groupDTOs, String menuContext, String user) {

    }

    @Override
    @RequestMapping(value = PATH_LIST_PERMISSIONS, method = RequestMethod.GET)
    public List<SingularPermission> listAllPermissions() {
        List<SingularPermission> permissions = new ArrayList<>();

        // Coleta permissões de caixa
        List<SingularPermission> menuPermissions = listMenuGroups().stream()
                .map(menuGroup -> new SingularPermission(menuGroup.getId(), null))
                .collect(Collectors.toList());

        //Agrupa permissoes do Form e do Flow
        permissions.addAll(menuPermissions);
        permissions.addAll(permissionResolverService.listAllTypePermissions());
        permissions.addAll(permissionResolverService.listAllProcessesPermissions());

        // Limpa o internal id por questão de segurança
        for (SingularPermission permission : permissions) {
            permission.setInternalId(null);
        }

        return permissions;
    }
}