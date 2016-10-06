/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.server.core.wicket.inicio;

import static org.opensingular.singular.util.wicket.util.WicketUtils.$b;
import static org.opensingular.singular.util.wicket.util.WicketUtils.$m;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;

import org.opensingular.singular.commons.lambda.IFunction;
import org.opensingular.flow.persistence.entity.ProcessGroupEntity;
import org.opensingular.singular.server.commons.exception.SingularServerException;
import org.opensingular.singular.server.commons.form.FormActions;
import org.opensingular.singular.server.commons.persistence.dto.TaskInstanceDTO;
import org.opensingular.singular.server.commons.service.dto.FormDTO;
import org.opensingular.singular.server.commons.service.dto.MenuGroup;
import org.opensingular.singular.server.commons.util.Parameters;
import org.opensingular.singular.server.commons.wicket.view.util.DispatcherPageUtil;
import org.opensingular.singular.server.core.wicket.ModuleLink;
import org.opensingular.singular.server.core.wicket.historico.HistoricoPage;
import org.opensingular.singular.server.core.wicket.template.AbstractCaixaAnaliseContent;
import org.opensingular.singular.util.wicket.datatable.BSDataTable;
import org.opensingular.singular.util.wicket.datatable.BSDataTableBuilder;
import org.opensingular.singular.util.wicket.datatable.BaseDataProvider;
import org.opensingular.singular.util.wicket.datatable.column.MetronicStatusColumn;
import org.opensingular.singular.util.wicket.model.IReadOnlyModel;
import org.opensingular.singular.util.wicket.util.WicketUtils;

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
                            .formId(null)
                            .param(Parameters.SIGLA_FORM_NAME, form.getName())
                            .build();
                        dropdownMenu.adicionarMenu(id -> new ModuleLink(id, WicketUtils.$m.ofValue(form.getName()), processUrl));
                    } else {
                        String url = DispatcherPageUtil
                            .baseURL(getBaseUrl(moduleContext))
                            .formAction(FormActions.FORM_FILL.getId())
                            .formId(null)
                            .param(Parameters.SIGLA_FORM_NAME, form.getName())
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
