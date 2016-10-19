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

package org.opensingular.server.core.wicket.inicio;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;
import static org.opensingular.lib.wicket.util.util.WicketUtils.$m;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;

import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.flow.persistence.entity.ProcessGroupEntity;
import org.opensingular.server.commons.exception.SingularServerException;
import org.opensingular.server.commons.form.FormActions;
import org.opensingular.server.commons.persistence.dto.TaskInstanceDTO;
import org.opensingular.server.commons.service.dto.FormDTO;
import org.opensingular.server.commons.service.dto.MenuGroup;
import org.opensingular.server.commons.util.DispatcherPageParameters;
import org.opensingular.server.commons.wicket.view.util.DispatcherPageUtil;
import org.opensingular.server.core.wicket.ModuleLink;
import org.opensingular.server.core.wicket.historico.HistoricoPage;
import org.opensingular.server.core.wicket.template.AbstractCaixaAnaliseContent;
import org.opensingular.lib.wicket.util.datatable.BSDataTable;
import org.opensingular.lib.wicket.util.datatable.BSDataTableBuilder;
import org.opensingular.lib.wicket.util.datatable.BaseDataProvider;
import org.opensingular.lib.wicket.util.datatable.column.MetronicStatusColumn;
import org.opensingular.lib.wicket.util.model.IReadOnlyModel;
import org.opensingular.lib.wicket.util.util.WicketUtils;

public class InicioContent extends AbstractCaixaAnaliseContent<TaskInstanceDTO> {

    public InicioContent(String id) {
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

    protected WebMarkupContainer criarLinkAnalise(IModel<TaskInstanceDTO> peticaoModel, String id) {
        WebMarkupContainer link = criarLink(id, peticaoModel, FormActions.FORM_FILL);
        link.add($b.visibleIf(
            (IReadOnlyModel<Boolean>) () -> !isAlocadoParaUsuarioLogado(peticaoModel.getObject())
                && peticaoModel.getObject().isPossuiPermissao()));
        return link;
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
        return $m.ofValue("Worklist");
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        for (Map.Entry<ProcessGroupEntity, List<MenuGroup>> entry : getMenuSessionConfig().getMap().entrySet()) {
            String moduleContext = getModuleContext(entry.getKey());
            for (MenuGroup menuGroupDTO : entry.getValue()) {
                setProcesses(menuGroupDTO.getProcesses());
                setForms(menuGroupDTO.getForms());
                for (FormDTO form : getForms()) {
                    if (getForms().size() > 1) {
                        String processUrl = DispatcherPageUtil
                            .baseURL(getBaseUrl(moduleContext))
                            .formAction(FormActions.FORM_FILL.getId())
                            .petitionId(null)
                            .param(DispatcherPageParameters.SIGLA_FORM_NAME, form.getName())
                            .build();
                        dropdownMenu.adicionarMenu(id -> new ModuleLink(id, WicketUtils.$m.ofValue(form.getName()), processUrl));
                    } else {
                        String url = DispatcherPageUtil
                            .baseURL(getBaseUrl(moduleContext))
                            .formAction(FormActions.FORM_FILL.getId())
                            .petitionId(null)
                            .param(DispatcherPageParameters.SIGLA_FORM_NAME, form.getName())
                            .build();
                        adicionarBotaoGlobal(id -> new ModuleLink(id, getMessage("label.button.insert"), url));
                    }
                }
            }
        }

    }

    public String getModuleContext(ProcessGroupEntity processGroupEntity) {
        final String groupConnectionURL = processGroupEntity.getConnectionURL();
        try {
            final String path = new URL(groupConnectionURL).getPath();
            return path.substring(0, path.indexOf("/", 1));
        } catch (Exception e) {
            throw new SingularServerException(String.format("Erro ao tentar fazer o parse da URL: %s", groupConnectionURL), e);
        }
    }

    public <X> void adicionarBotaoGlobal(IFunction<String, Link<X>> funcaoConstrutora) {
        botoes.add(funcaoConstrutora.apply(botoes.newChildId()));
    }

}
