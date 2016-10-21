/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.server.commons.flow.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.opensingular.form.SInfoType;
import org.opensingular.server.commons.spring.security.AuthorizationService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.opensingular.lib.commons.base.SingularUtil;
import org.opensingular.flow.core.Flow;
import org.opensingular.flow.core.ProcessDefinition;
import org.opensingular.form.SFormUtil;
import org.opensingular.form.SType;
import org.opensingular.form.context.SFormConfig;
import org.opensingular.server.commons.config.SingularServerConfiguration;
import org.opensingular.server.commons.service.IServerMetadataREST;
import org.opensingular.server.commons.service.dto.FormDTO;
import org.opensingular.server.commons.service.dto.MenuGroup;
import org.opensingular.server.commons.service.dto.ProcessDTO;
import org.opensingular.server.commons.spring.security.PermissionResolverService;
import org.opensingular.server.commons.spring.security.SingularPermission;
import org.opensingular.lib.support.spring.util.AutoScanDisabled;

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
                                            new ProcessDTO(d.getKey(), d.getName(), null)
                                    )
            );

            addForms(menuGroup);

            groups.add(menuGroup);
        });

        return groups;
    }

    protected void addForms(MenuGroup menuGroup) {
        for (Class<? extends SType<?>> formClass : singularServerConfiguration.getFormTypes()) {
            SInfoType                 annotation = formClass.getAnnotation(SInfoType.class);
            if (annotation.newable()) {
                String                    name       = SFormUtil.getTypeName(formClass);
                SType<?>                  sType      = singularFormConfig.getTypeLoader().loadType(name).get();
                Class<? extends SType<?>> sTypeClass = (Class<? extends SType<?>>) sType.getClass();
                String                    label      = sType.asAtr().getLabel();
                menuGroup.getForms().add(new FormDTO(name, SFormUtil.getTypeSimpleName(sTypeClass), label));
            }
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