/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.commons.flow.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

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
import br.net.mirante.singular.support.spring.util.AutoScanDisabled;

@AutoScanDisabled
@RequestMapping("/rest/flow")
@RestController
public class DefaultServerMetadataREST implements IServerMetadataREST {

    @Inject
    protected SingularServerConfiguration singularServerConfiguration;

    @Inject
    protected PermissionResolverService permissionResolverService;

    @Inject
    @Named("formConfigWithDatabase")
    protected SFormConfig<String> singularFormConfig;

    @Override
    @RequestMapping(value = PATH_LIST_MENU, method = RequestMethod.GET)
    public List<MenuGroup> listMenu(@RequestParam(MENU_CONTEXT) String context, @RequestParam(USER) String user) {

        List<MenuGroup> groups = new ArrayList<>();
        Map<String, List<ProcessDefinition>> definitionMap = new HashMap<>();
        Flow.getDefinitions().forEach(d -> {
            if (!definitionMap.containsKey(d.getCategory())) {
                definitionMap.put(d.getCategory(), new ArrayList<>());
            }
            definitionMap.get(d.getCategory()).add(d);

        });

        definitionMap.forEach((category, definitions) -> {
            MenuGroup menuGroup = new MenuGroup();
            menuGroup.setId("CAIXA_" + SingularUtil.normalize(category).toUpperCase());
            menuGroup.setLabel(category);
            menuGroup.setProcesses(new ArrayList<>());
            menuGroup.setForms(new ArrayList<>());
            definitions.forEach(d ->
                            menuGroup
                                    .getProcesses()
                                    .add(
                                            new ProcessDTO(d.getKey(), d.getName(), singularServerConfiguration.processDefinitionFormNameMap().get(d.getClass()))
                                    )
            );

            addForms(menuGroup);

            groups.add(menuGroup);
        });

        filterAccessRight(groups, user);

        customizeMenu(groups, context, user);

        return groups;
    }

    protected void addForms(MenuGroup menuGroup) {
        for (String name : singularServerConfiguration.getFormPackagesTypeMap().values()) {
            SType<?> sType = singularFormConfig.getTypeLoader().loadType(name).get();
            Class<? extends SType<?>> sTypeClass = (Class<? extends SType<?>>) sType.getClass();
            String label = sType.asAtr().getLabel();
            menuGroup.getForms().add(new FormDTO(name, SFormUtil.getTypeSimpleName(sTypeClass), label));
        }
    }

    protected void filterAccessRight(List<MenuGroup> groupDTOs, String user) {
        permissionResolverService.filterBoxWithPermissions(groupDTOs, user);
    }

    protected void customizeMenu(List<MenuGroup> groupDTOs, String menuContext, String user) {

    }


}