/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.server.core.wicket.view.permission;

import static org.opensingular.singular.server.commons.service.IServerMetadataREST.PATH_LIST_PERMISSIONS;
import static org.opensingular.lib.wicket.util.util.WicketUtils.$m;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.springframework.web.client.RestTemplate;

import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.flow.persistence.entity.ProcessGroupEntity;
import org.opensingular.singular.server.commons.service.PetitionService;
import org.opensingular.singular.server.commons.spring.security.SingularPermission;
import org.opensingular.singular.server.commons.wicket.view.template.Content;
import org.opensingular.lib.wicket.util.datatable.BSDataTable;
import org.opensingular.lib.wicket.util.datatable.BSDataTableBuilder;
import org.opensingular.lib.wicket.util.datatable.BaseDataProvider;

public class PermissionContent extends Content implements Loggable {

    @Inject
    protected PetitionService petitionService;

    protected BSDataTable<Result, String> listTable;
    private   List<Result>                resultado;

    public PermissionContent(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        carregarPermissoes();
        queue(listTable = setupDataTable());
    }

    private void carregarPermissoes() {
        List<ProcessGroupEntity> categorias = petitionService.listAllProcessGroups();
        resultado = new ArrayList<>();

        for (ProcessGroupEntity categoria : categorias) {
            List<SingularPermission> permissions = listPermissions(categoria);
            List<Result>             parsed      = permissions.stream()
                    .map(permission -> new Result(categoria.getName(), permission))
                    .collect(Collectors.toList());
            resultado.addAll(parsed);
        }

    }

    private List<SingularPermission> listPermissions(ProcessGroupEntity processGroup) {

        final String url = processGroup.getConnectionURL() + PATH_LIST_PERMISSIONS;

        try {
            return Arrays.asList(new RestTemplate().getForObject(url, SingularPermission[].class));
        } catch (Exception e) {
            getLogger().error("Erro ao acessar serviço: " + url, e);
            return Collections.emptyList();
        }
    }

    protected BSDataTable<Result, String> setupDataTable() {
        return new BSDataTableBuilder<>(createDataProvider())
                .appendPropertyColumn(getMessage("label.table.column.modulo"), Result::getModulo)
                .appendPropertyColumn(getMessage("label.table.column.permissao"), Result::getSingularPermissionId)
                .setRowsPerPage(Long.MAX_VALUE)
                .setStripedRows(false)
                .setBorderedTable(false)
                .build("tabela");
    }

    private BaseDataProvider<Result, String> createDataProvider() {
        return new BaseDataProvider<Result, String>() {

            @Override
            public long size() {
                return Long.MAX_VALUE;
            }

            @Override
            public Iterator<Result> iterator(int first, int count, String sortProperty, boolean ascending) {
                return resultado.iterator();
            }
        };
    }

    @Override
    protected IModel<?> getContentTitleModel() {
        return new ResourceModel("label.content.title.permission");
    }

    @Override
    protected IModel<?> getContentSubtitleModel() {
        return $m.ofValue("");
    }


    private static class Result implements Serializable {
        private String modulo;
        private SingularPermission singularPermission;

        public Result(String modulo, SingularPermission singularPermission) {
            this.modulo = modulo;
            this.singularPermission = singularPermission;
        }

        public String getModulo() {
            return modulo;
        }

        public SingularPermission getSingularPermission() {
            return singularPermission;
        }

        public String getSingularPermissionId() {
            return singularPermission.getSingularId();
        }
    }
}
