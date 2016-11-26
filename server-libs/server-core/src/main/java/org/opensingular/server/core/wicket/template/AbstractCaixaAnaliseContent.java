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

package org.opensingular.server.core.wicket.template;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;
import static org.opensingular.lib.wicket.util.util.WicketUtils.$m;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.opensingular.server.commons.spring.security.SingularPermission;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.flow.persistence.entity.ProcessGroupEntity;
import org.opensingular.server.commons.config.ServerContext;
import org.opensingular.server.commons.form.FormActions;
import org.opensingular.server.commons.persistence.dto.TaskInstanceDTO;
import org.opensingular.server.commons.service.PetitionService;
import org.opensingular.server.commons.service.dto.FormDTO;
import org.opensingular.server.commons.service.dto.ProcessDTO;
import org.opensingular.server.commons.util.DispatcherPageParameters;
import org.opensingular.server.commons.wicket.SingularSession;
import org.opensingular.server.commons.wicket.view.template.Content;
import org.opensingular.server.commons.wicket.view.util.DispatcherPageUtil;
import org.opensingular.lib.wicket.util.datatable.BSDataTable;
import org.opensingular.lib.wicket.util.datatable.IBSAction;
import org.opensingular.lib.wicket.util.datatable.column.BSActionColumn;
import org.opensingular.lib.wicket.util.datatable.column.MetronicStatusColumn;
import org.opensingular.lib.wicket.util.metronic.menu.DropdownMenu;
import org.opensingular.lib.wicket.util.model.IReadOnlyModel;
import org.opensingular.lib.wicket.util.resource.Icone;
import static org.opensingular.flow.core.ws.BaseSingularRest.*;

public abstract class AbstractCaixaAnaliseContent<T extends TaskInstanceDTO> extends Content implements Loggable {


    private static final long serialVersionUID = 1767745739019654615L;

    public static final String ID_QUICK_FILTER = "filtroRapido";
    public static final String ID_FORM         = "form";

    protected static final Integer DEFAULT_ROWS_PER_PAGE = 10;

    protected final TextField<String> filtroRapido    = new TextField<>(ID_QUICK_FILTER, new Model<>());

    protected final AjaxButton        pesquisarButton = new AjaxButton("pesquisar") {
        @Override
        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
            super.onSubmit(target, form);
            onFiltroRapido(filtroRapido.getModel(), target);
        }
    };

    protected BSDataTable<T, String> listTable;

    /**
     * Botões globais
     */
    protected RepeatingView botoes = new RepeatingView("_botoes");

    protected DropdownMenu dropdownMenu = new DropdownMenu("_novos");

    private List<ProcessDTO> processes;

    private List<FormDTO> forms;

    @Inject
    protected PetitionService<?> petitionService;

    protected abstract BSDataTable<T, String> setupDataTable();

    protected abstract Class<? extends Page> getHistoricoPage();

    public AbstractCaixaAnaliseContent(String id) {
        super(id, false, false);
    }

    public AbstractCaixaAnaliseContent(String id, boolean withInfoLink, boolean withBreadcrumb) {
        super(id, withInfoLink, withBreadcrumb);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(AbstractCaixaAnaliseContent.class, "AbstractCaixaAnaliseContent.js")));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new Form<Void>(ID_FORM) {
            @Override
            protected void onSubmit() {
                super.onSubmit();
            }
        });
        queue(filtroRapido, pesquisarButton, listTable = setupDataTable(), botoes, dropdownMenu);
    }

    protected void onFiltroRapido(IModel<String> model, AjaxRequestTarget target) {
        target.add(listTable);
    }

    protected String getBaseUrl(String processGroupContext) {
        return  processGroupContext+ ServerContext.WORKLIST.getUrlPath();
    }

    protected BSActionColumn<T, String> buildActionColumn() {
        final BSActionColumn<T, String> actionColumn = new BSActionColumn<>(getMessage("label.table.column.actions"));
        appendAtribuirAction(actionColumn);
        appendAnalisarAction(actionColumn);

        actionColumn
                .appendStaticAction(getMessage("label.table.column.view"),
                        Icone.EYE, this::criarLinkVisualizar)
                .appendStaticAction(getMessage("label.table.column.history"),
                        Icone.HISTORY, this::criarLinkHistorico);

        return actionColumn;
    }

    protected void appendAnalisarAction(BSActionColumn<T, String> actionColumn) {
        actionColumn
                .appendStaticAction(getMessage("label.table.column.analysis"),
                        Icone.PENCIL, this::criarLinkAnalise);
    }

    protected void appendAtribuirAction(BSActionColumn<T, String> actionColumn) {
        actionColumn
                .appendAction(getMessage("label.botao.atribuir.mim"),
                        Icone.ARROW_DOWN, new IBSAction<T>() {
                            @Override
                            public boolean isVisible(IModel<T> model) {
                                return model.getObject().getTaskType().isPeople()
                                        && isAlocadoParaUsuarioLogado(model.getObject())
                                        && model.getObject().isPossuiPermissao();
                            }

                            @Override
                            public void execute(AjaxRequestTarget target, IModel<T> model) {
                                AbstractCaixaAnaliseContent.this.atribuir(target, model);
                            }
                        });
    }

    protected boolean isAlocadoParaUsuarioLogado(T peticao) {
        return !SingularSession.get().getUsername().equals(
                Optional.ofNullable(peticao)
                        .map(T::getCodUsuarioAlocado)
                        .orElse(null));
    }

    protected WebMarkupContainer criarLinkVisualizar(String id, IModel<T> peticao) {
        return criarLink(id, peticao, FormActions.FORM_ANALYSIS_VIEW);
    }

    protected WebMarkupContainer criarLinkAnalise(String id, IModel<T> peticao) {
        WebMarkupContainer link = criarLink(id, peticao, FormActions.FORM_ANALYSIS);
        link.add($b.visibleIf((IReadOnlyModel<Boolean>) () -> !isAlocadoParaUsuarioLogado(peticao.getObject()) && peticao.getObject().isPossuiPermissao()));
        return link;
    }

    protected WebMarkupContainer criarLink(String id, IModel<T> peticaoModel, FormActions formActions) {
        T peticao = peticaoModel.getObject();
        String href = DispatcherPageUtil
                .baseURL(getBaseUrl(peticao.getProcessGroupContext()) + DispatcherPageUtil.DISPATCHER_PAGE_PATH)
                .formAction(formActions.getId())
                .petitionId(peticao.getCodPeticao())
                .param(DispatcherPageParameters.FORM_NAME, peticao.getType())
                .build();
        WebMarkupContainer link = new WebMarkupContainer(id);
        link.add($b.attr("href", href));
        link.add($b.attr("target", "_tab" + peticao.getCodPeticao()));
        return link;
    }

    protected void atribuir(AjaxRequestTarget target, IModel<T> model) {
        // TODO parametrizar qual o flow
        try {
            T taskInstanceDTO = model.getObject();

            final ProcessGroupEntity processGroup = petitionService.findByProcessGroupCod(taskInstanceDTO.getProcessGroupCod());
            final String url = UriComponentsBuilder.fromUriString(processGroup.getConnectionURL() + RELOCATE_TASK)
                    .queryParam(PROCESS_ABBREVIATION, taskInstanceDTO.getProcessType())
                    .queryParam(COD_PROCESS_INSTANCE, Long.valueOf(taskInstanceDTO.getProcessInstanceId()))
                    .queryParam(USERNAME, SingularSession.get().getUsername())
                    .queryParam(LAST_VERSION, taskInstanceDTO.getVersionStamp())
                    .build().toUriString();

            new RestTemplate().getForObject(url, Void.class);
            addToastrSuccessMessage("message.allocate.success");
        } catch (Exception e) {
            addToastrErrorMessage("global.analise.atribuir.msg.error");
            getLogger().error(e.getMessage(), e);
        }
        target.add(listTable);
    }

    protected WebMarkupContainer criarLinkHistorico(String id, IModel<T> peticao) {
        PageParameters pageParameters = new PageParameters();
        pageParameters.add(DispatcherPageParameters.INSTANCE_ID, peticao.getObject().getProcessInstanceId());

        return new BookmarkablePageLink<>(id, getHistoricoPage(), pageParameters);
    }

    protected MetronicStatusColumn.BagdeType badgeMapper(IModel<Object> cellModel, IModel<T> rowModel) {
        if (rowModel.getObject().getTaskName().toLowerCase().contains("gerente")) {
            return MetronicStatusColumn.BagdeType.NONE;
        } else if (rowModel.getObject().getTaskName().toLowerCase().contains("aguardando")) {
            return MetronicStatusColumn.BagdeType.DANGER;
        } else if (rowModel.getObject().getTaskName().toLowerCase().contains("exigência")) {
            return MetronicStatusColumn.BagdeType.WARNING;
        } else {
            return MetronicStatusColumn.BagdeType.INFO;
        }
    }
    
    protected List<SingularPermission> getUserRoleIds() {
        return SingularSession.get().getUserDetails().getPermissions();
    }

    @Override
    protected IModel<?> getContentSubtitleModel() {
        return $m.ofValue("Petições");
    }

    protected Integer getRowsperPage() {
        return DEFAULT_ROWS_PER_PAGE;
    }

    public List<ProcessDTO> getProcesses() {
        return processes;
    }

    public void setProcesses(List<ProcessDTO> processes) {
        this.processes = processes;
    }

    public List<FormDTO> getForms() {
        return forms;
    }

    public void setForms(List<FormDTO> forms) {
        this.forms = forms;
    }
}