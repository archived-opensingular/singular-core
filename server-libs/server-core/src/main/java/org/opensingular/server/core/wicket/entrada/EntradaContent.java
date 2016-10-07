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

package org.opensingular.server.core.wicket.entrada;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$m;

import java.util.Iterator;

import org.apache.wicket.Page;
import org.apache.wicket.model.IModel;

import org.opensingular.server.commons.persistence.dto.TaskInstanceDTO;
import org.opensingular.server.core.wicket.historico.HistoricoPage;
import org.opensingular.server.core.wicket.template.AbstractCaixaAnaliseContent;
import org.opensingular.lib.wicket.util.datatable.BSDataTable;
import org.opensingular.lib.wicket.util.datatable.BSDataTableBuilder;
import org.opensingular.lib.wicket.util.datatable.BaseDataProvider;
import org.opensingular.lib.wicket.util.datatable.column.MetronicStatusColumn;

public class EntradaContent extends AbstractCaixaAnaliseContent<TaskInstanceDTO> {

    public EntradaContent(String id) {
        super(id);
    }

    @Override
    protected BSDataTable<TaskInstanceDTO, String> setupDataTable() {
        return new BSDataTableBuilder<>(createDataProvider())
                .appendPropertyColumn(getMessage("label.table.column.in.date"),
                        "processBeginDate", TaskInstanceDTO::getProcessBeginDate)
//                .appendPropertyColumn(getMessage("label.table.column.number"),
//                        "id", TaskInstanceDTO::getNumeroProcesso)
//                .appendPropertyColumn(getMessage("label.table.column.requester"),
//                        "requester", TaskInstanceDTO::getSolicitante)
                .appendPropertyColumn(getMessage("label.table.column.description"),
                        "description", TaskInstanceDTO::getDescricao)
                .appendPropertyColumn(getMessage("label.table.column.situation.date"),
                        "situationBeginDate", TaskInstanceDTO::getSituationBeginDate)
                .appendColumn(new MetronicStatusColumn<>(getMessage("label.table.column.state"),
                        "state", TaskInstanceDTO::getTaskName,
                        this::badgeMapper))
                .appendPropertyColumn(getMessage("label.table.column.alocado"),
                        "user", TaskInstanceDTO::getNomeUsuarioAlocado)
                .appendColumn(buildActionColumn())
                .setRowsPerPage(getRowsperPage())
                .build("tabela");
    }

    @Override
    protected Class<? extends Page> getHistoricoPage() {
        return HistoricoPage.class;
    }

    @SuppressWarnings("unchecked")
    private BaseDataProvider<TaskInstanceDTO, String> createDataProvider() {
        return new BaseDataProvider<TaskInstanceDTO, String>() {

            @Override
            public long size() {
                return petitionService.countTasks(null, getUserRoleIds(), filtroRapido.getModelObject(), false);
            }

            @Override
            public Iterator<TaskInstanceDTO> iterator(int first, int count, String sortProperty, boolean ascending) {
                return (Iterator<TaskInstanceDTO>) petitionService.listTasks(first, count, sortProperty, ascending, null, getUserRoleIds(), filtroRapido.getModelObject(), false).iterator();
            }
        };
    }

    @Override
    protected IModel<?> getContentTitleModel() {
        return $m.ofValue("Caixa de entrada");
    }

    @Override
    protected IModel<?> getContentSubtitleModel() {
        return $m.ofValue("Petições");
    }

}
