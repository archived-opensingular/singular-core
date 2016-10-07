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

package org.opensingular.server.core.wicket.view.permission;

import static org.opensingular.server.commons.service.IServerMetadataREST.PATH_LIST_PERMISSIONS;
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
import org.opensingular.server.commons.service.PetitionService;
import org.opensingular.server.commons.spring.security.SingularPermission;
import org.opensingular.server.commons.wicket.view.template.Content;
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
