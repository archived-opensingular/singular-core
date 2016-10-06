/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.server.p.core.wicket.box;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import static org.opensingular.singular.server.commons.service.IServerMetadataREST.PATH_BOX_SEARCH;
import static org.opensingular.singular.server.commons.util.Parameters.SIGLA_FORM_NAME;
import static org.opensingular.singular.util.wicket.util.WicketUtils.$b;
import static org.opensingular.singular.util.wicket.util.WicketUtils.$m;

import org.opensingular.singular.commons.lambda.IBiFunction;
import org.opensingular.singular.commons.lambda.IFunction;
import org.opensingular.flow.persistence.entity.Actor;
import org.opensingular.singular.server.commons.flow.rest.ActionAtribuirRequest;
import org.opensingular.singular.server.commons.flow.rest.ActionRequest;
import org.opensingular.singular.server.commons.flow.rest.ActionResponse;
import org.opensingular.singular.server.commons.form.FormActions;
import org.opensingular.singular.server.commons.persistence.filter.QuickFilter;
import org.opensingular.singular.server.commons.service.dto.BoxItemAction;
import org.opensingular.singular.server.commons.service.dto.FormDTO;
import org.opensingular.singular.server.commons.service.dto.ItemAction;
import org.opensingular.singular.server.commons.service.dto.ItemActionConfirmation;
import org.opensingular.singular.server.commons.service.dto.ItemActionType;
import org.opensingular.singular.server.commons.service.dto.ItemBox;
import org.opensingular.singular.server.commons.service.dto.ProcessDTO;
import org.opensingular.singular.server.commons.util.Parameters;
import org.opensingular.singular.server.commons.wicket.view.util.DispatcherPageUtil;
import org.opensingular.singular.server.core.wicket.ModuleLink;
import org.opensingular.singular.server.core.wicket.historico.HistoricoPage;
import org.opensingular.singular.server.p.core.wicket.model.BoxItemModel;
import org.opensingular.singular.server.p.core.wicket.view.AbstractCaixaContent;
import org.opensingular.singular.util.wicket.datatable.BSDataTableBuilder;
import org.opensingular.singular.util.wicket.datatable.IBSAction;
import org.opensingular.singular.util.wicket.datatable.column.BSActionColumn;
import org.opensingular.singular.util.wicket.modal.BSModalBorder;
import org.opensingular.singular.util.wicket.resource.Icone;

public class BoxContent extends AbstractCaixaContent<BoxItemModel> {

    static final Logger LOGGER = LoggerFactory.getLogger(BoxContent.class);

    private Pair<String, SortOrder> sortProperty;
    private ItemBox                 itemBoxDTO;
    private IModel<BoxItemModel>    currentModel;

    public BoxContent(String id, String processGroupCod, String menu, ItemBox itemBoxDTO) {
        super(id, processGroupCod, menu);
        this.itemBoxDTO = itemBoxDTO;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        showNew();
        configureQuickFilter();
    }

    private void configureQuickFilter() {
        getFiltroRapido().setVisible(isShowQuickFilter());
        getPesquisarButton().setVisible(isShowQuickFilter());
    }

    private void showNew() {
        if (isShowNew() && getMenu() != null) {
            for (FormDTO form : getForms()) {
                String url = DispatcherPageUtil
                        .baseURL(getBaseUrl())
                        .formAction(FormActions.FORM_FILL.getId())
                        .formId(null)
                        .param(Parameters.SIGLA_FORM_NAME, form.getName())
                        .params(getLinkParams())
                        .build();

                if (getForms().size() > 1) {
                    dropdownMenu.adicionarMenu(id -> new ModuleLink(id, $m.ofValue(form.getDescription()), url));
                } else {
                    adicionarBotaoGlobal(id -> new ModuleLink(id, getMessage("label.button.insert"), url));
                }
            }
        }

    }

    @Override
    protected void appendPropertyColumns(BSDataTableBuilder<BoxItemModel, String, IColumn<BoxItemModel, String>> builder) {
        for (Map.Entry<String, String> entry : getFieldsDatatable().entrySet()) {
            builder.appendPropertyColumn($m.ofValue(entry.getKey()), entry.getValue());
        }
    }

    @Override
    protected void appendActionColumns(BSDataTableBuilder<BoxItemModel, String, IColumn<BoxItemModel, String>> builder) {
        BSActionColumn<BoxItemModel, String> actionColumn = new BSActionColumn<>(getMessage("label.table.column.actions"));

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(String.format("A caixa %s permite a apresentação apenas das ações %s", itemBoxDTO.getName(), Arrays.toString(itemBoxDTO.getActions().keySet().toArray())));
        }
        for (ItemAction itemAction : itemBoxDTO.getActions().values()) {

            if (itemAction.getType() == ItemActionType.POPUP) {
                actionColumn.appendStaticAction(
                        $m.ofValue(itemAction.getLabel()),
                        itemAction.getIcon(),
                        linkFunction(itemAction, getBaseUrl(), getLinkParams()),
                        visibleFunction(itemAction),
                        c -> c.styleClasses($m.ofValue("worklist-action-btn")));
            } else if (itemAction.getType() == ItemActionType.ENDPOINT) {
                actionColumn.appendAction(
                        $m.ofValue(itemAction.getLabel()),
                        itemAction.getIcon(),
                        dynamicLinkFunction(itemAction, getProcessGroup().getConnectionURL(), getLinkParams()),
                        visibleFunction(itemAction),
                        c -> c.styleClasses($m.ofValue("worklist-action-btn")));
            }
        }

        actionColumn
                .appendStaticAction(
                        getMessage("label.table.column.history"),
                        Icone.HISTORY,
                        this::criarLinkHistorico,
                        (x) -> true,
                        c -> c.styleClasses($m.ofValue("worklist-action-btn")));

        builder.appendColumn(actionColumn);
    }

    private MarkupContainer criarLinkHistorico(String id, IModel<BoxItemModel> boxItemModel) {
        BoxItemModel   boxItem        = boxItemModel.getObject();
        PageParameters pageParameters = new PageParameters();
        if (boxItem.getProcessInstanceId() != null) {
            pageParameters.add(Parameters.INSTANCE_ID, boxItem.getProcessInstanceId());
            pageParameters.add(Parameters.PROCESS_GROUP_PARAM_NAME, getProcessGroup().getCod());
        }

        BookmarkablePageLink<?> historiLink = new BookmarkablePageLink<>(id, HistoricoPage.class, pageParameters);
        historiLink.setVisible(boxItem.getProcessBeginDate() != null);
        return historiLink;
    }

    @Override
    protected WebMarkupContainer criarLinkEdicao(String id, IModel<BoxItemModel> peticao) {
        if (peticao.getObject().getProcessBeginDate() == null) {
            return criarLink(id, peticao, FormActions.FORM_FILL);
        } else {
            return criarLink(id, peticao, FormActions.FORM_FILL_WITH_ANALYSIS);
        }
    }

    public IBiFunction<String, IModel<BoxItemModel>, MarkupContainer> linkFunction(ItemAction itemAction, String baseUrl, Map<String, String> additionalParams) {
        return (id, boxItemModel) -> {
            String url = mountStaticUrl(itemAction, baseUrl, additionalParams, boxItemModel);

            WebMarkupContainer link = new WebMarkupContainer(id);
            link.add($b.attr("target", String.format("_%s", boxItemModel.getObject().getCod())));
            link.add($b.attr("href", url));
            return link;
        };
    }

    private String mountStaticUrl(ItemAction itemAction, String baseUrl, Map<String, String> additionalParams, IModel<BoxItemModel> boxItemModel) {
        final BoxItemAction action = boxItemModel.getObject().getActionByName(itemAction.getName());
        if (action.getEndpoint().startsWith("http")) {
            return action.getEndpoint();
        } else {
            return baseUrl
                    + action.getEndpoint()
                    + appendParameters(additionalParams);
        }
    }

    private IBSAction<BoxItemModel> dynamicLinkFunction(ItemAction itemAction, String baseUrl, Map<String, String> additionalParams) {
        if (itemAction.getConfirmation() != null) {
            return (target, model) -> {
                currentModel = model;
                final BSModalBorder confirmationModal = construirModalConfirmationBorder(itemAction, baseUrl, additionalParams);
                confirmationForm.addOrReplace(confirmationModal);
                confirmationModal.show(target);
            };
        } else {
            return (target, model) -> executeDynamicAction(itemAction, baseUrl, additionalParams, model.getObject(), target);
        }
    }

    private void executeDynamicAction(ItemAction itemAction, String baseUrl, Map<String, String> additionalParams, BoxItemModel boxItem, AjaxRequestTarget target) {
        final BoxItemAction boxAction = boxItem.getActionByName(itemAction.getName());

        String url = baseUrl
                + boxAction.getEndpoint()
                + appendParameters(additionalParams);

        try {
            callModule(url, buildCallObject(boxAction, boxItem));
        } catch (Exception e) {
            LOGGER.error("Erro ao acessar serviço: " + url, e);
            addToastrErrorMessage("Não foi possível executar esta ação.");
        } finally {
            target.add(tabela);
        }
    }

    private void relocate(ItemAction itemAction, String baseUrl, Map<String, String> additionalParams, BoxItemModel boxItem, AjaxRequestTarget target, Actor actor) {
        final BoxItemAction boxAction = boxItem.getActionByName(itemAction.getName());

        String url = baseUrl
                + boxAction.getEndpoint()
                + appendParameters(additionalParams);

        try {
            callModule(url, buildCallAtribuirObject(boxAction, boxItem, actor));
        } catch (Exception e) {
            LOGGER.error("Erro ao acessar serviço: " + url, e);
            addToastrErrorMessage("Não foi possível executar esta ação.");
        } finally {
            target.add(tabela);
        }
    }

    private Object buildCallAtribuirObject(BoxItemAction boxAction, BoxItemModel boxItem, Actor actor) {
        ActionAtribuirRequest actionRequest = new ActionAtribuirRequest();
        actionRequest.setIdUsuario(getBoxPage().getIdUsuario());
        actionRequest.setIdUsuarioDestino(actor.getCodUsuario());
        if (boxAction.isUseExecute()) {
            actionRequest.setName(boxAction.getName());
            actionRequest.setLastVersion(boxItem.getVersionStamp());
        }

        return actionRequest;
    }

    private void callModule(String url, Object arg) {
        ActionResponse response = new RestTemplate().postForObject(url, arg, ActionResponse.class);
        if (response.isSuccessful()) {
            addToastrSuccessMessage(response.getResultMessage());
        } else {
            addToastrErrorMessage(response.getResultMessage());
        }
    }

    private Object buildCallObject(BoxItemAction boxAction, BoxItemModel boxItem) {
        ActionRequest actionRequest = new ActionRequest();
        actionRequest.setIdUsuario(getBoxPage().getIdUsuario());
        if (boxAction.isUseExecute()) {
            actionRequest.setName(boxAction.getName());
            actionRequest.setLastVersion(boxItem.getVersionStamp());
        }

        return actionRequest;
    }

    protected BSModalBorder construirModalConfirmationBorder(ItemAction itemAction, String baseUrl, Map<String, String> additionalParams) {
        final ItemActionConfirmation confirmation      = itemAction.getConfirmation();
        BSModalBorder                confirmationModal = new BSModalBorder("confirmationModal", $m.ofValue(confirmation.getTitle()));
        confirmationModal.addOrReplace(new Label("message", $m.ofValue(confirmation.getConfirmationMessage())));

        Model<Actor> model  = new Model<>();
        IModel<List<Actor>>  actorsModel = $m.get(() -> buscarUsuarios(currentModel, confirmation));
        confirmationModal.addOrReplace(criarDropDown(actorsModel, model))
            .setVisible(StringUtils.isNotBlank(confirmation.getSelectEndpoint()));

        confirmationModal.addButton(BSModalBorder.ButtonStyle.CANCEl, $m.ofValue(confirmation.getCancelButtonLabel()), new AjaxButton("cancel-delete-btn", confirmationForm) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                currentModel = null;
                confirmationModal.hide(target);
            }
        });

        if (StringUtils.isNotBlank(confirmation.getSelectEndpoint())) {
            confirmationModal.addButton(BSModalBorder.ButtonStyle.CONFIRM, $m.ofValue(confirmation.getConfirmationButtonLabel()), new AjaxButton("delete-btn", confirmationForm) {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    relocate(itemAction, baseUrl, additionalParams, currentModel.getObject(), target, model.getObject());
                    target.add(tabela);
                    confirmationModal.hide(target);
                }
            });
        } else {
            confirmationModal.addButton(BSModalBorder.ButtonStyle.CONFIRM, $m.ofValue(confirmation.getConfirmationButtonLabel()), new AjaxButton("delete-btn", confirmationForm) {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    executeDynamicAction(itemAction, baseUrl, additionalParams, currentModel.getObject(), target);
                    target.add(tabela);
                    confirmationModal.hide(target);
                }
            });
        }

        return confirmationModal;
    }

    private DropDownChoice criarDropDown(IModel<List<Actor>> actorsModel, Model<Actor> model) {
        return new DropDownChoice<>("selecao",
                model,
                actorsModel,
                new ChoiceRenderer<>("nome", "codUsuario"));
    }

    @SuppressWarnings("unchecked")
    private List<Actor> buscarUsuarios(IModel<BoxItemModel> currentModel, ItemActionConfirmation confirmation) {
        final String connectionURL = getProcessGroup().getConnectionURL();
        final String url           = connectionURL + PATH_BOX_SEARCH + confirmation.getSelectEndpoint();

        try {
            return Arrays.asList(new RestTemplate().postForObject(url, currentModel.getObject(), Actor[].class));
        } catch (Exception e) {
            LOGGER.error("Erro ao acessar serviço: " + url, e);
            return Collections.emptyList();
        }
    }

    private String appendParameters(Map<String, String> additionalParams) {
        String paramsValue = "";
        if (!additionalParams.isEmpty()) {
            for (Map.Entry<String, String> entry : additionalParams.entrySet()) {
                paramsValue += "&" + entry.getKey() + "=" + entry.getValue();
            }
        }
        return paramsValue;
    }

    private IFunction<IModel<BoxItemModel>, Boolean> visibleFunction(ItemAction itemAction) {
        return (model) -> {
            BoxItemModel boxItemModel = (BoxItemModel) model.getObject();
            boolean visible = boxItemModel.hasAction(itemAction);
            if (!visible) {
                getLogger().debug(String.format("Action %s não está disponível para o item (%s: código da petição) da listagem ", itemAction.getName(), boxItemModel.getCod()));
            }

            return visible;
        };
    }

    @Override
    protected Pair<String, SortOrder> getSortProperty() {
        return sortProperty;
    }

    public void setSortProperty(Pair<String, SortOrder> sortProperty) {
        this.sortProperty = sortProperty;
    }

    @Override
    protected void onDelete(BoxItemModel peticao) {

    }

    @Override
    protected QuickFilter montarFiltroBasico() {
        BoxPage boxPage = getBoxPage();
        return boxPage.createFilter()
                .withFilter(getFiltroRapidoModelObject())
                .withProcessesAbbreviation(getProcessesNames())
                .withTypesNames(getFormNames())
                .withRascunho(isWithRascunho())
                .withEndedTasks(itemBoxDTO.getEndedTasks());
    }

    private BoxPage getBoxPage() {
        return (BoxPage) getPage();
    }

    private List<String> getProcessesNames() {
        if (getProcesses() == null) {
            return Collections.emptyList();
        } else {
            return getProcesses()
                    .stream()
                    .map(ProcessDTO::getAbbreviation)
                    .collect(Collectors.toList());
        }
    }

    private List<String> getFormNames() {
        if (getForms() == null) {
            return Collections.emptyList();
        } else {
            return getForms()
                    .stream()
                    .map(FormDTO::getName)
                    .collect(Collectors.toList());
        }
    }

    @Override
    protected List<BoxItemModel> quickSearch(QuickFilter filter, List<String> siglasProcesso, List<String> formNames) {
        final String connectionURL = getProcessGroup().getConnectionURL();
        final String url           = connectionURL + PATH_BOX_SEARCH + getSearchEndpoint();
        try {
            return (List<BoxItemModel>) Arrays
                    .asList(new RestTemplate().postForObject(url, filter, Map[].class))
                    .stream()
                    .map(BoxItemModel::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error("Erro ao acessar serviço: " + url, e);
            return Collections.emptyList();
        }
    }

    @Override
    protected WebMarkupContainer criarLink(String id, IModel<BoxItemModel> itemModel, FormActions formActions) {
        BoxItemModel item = itemModel.getObject();
        String href = DispatcherPageUtil
                .baseURL(getBaseUrl())
                .formAction(formActions.getId())
                .formId(item.getCod())
                .param(SIGLA_FORM_NAME, item.get("type"))
                .params(getCriarLinkParameters(item))
                .build();

        WebMarkupContainer link = new WebMarkupContainer(id);
        link.add($b.attr("target", String.format("_%s", item.getCod())));
        link.add($b.attr("href", href));
        return link;
    }

    protected Map<String, String> getCriarLinkParameters(BoxItemModel item) {
        final Map<String, String> linkParameters = new HashMap<>();
        linkParameters.putAll(getLinkParams());
        return linkParameters;
    }

    private Map<String, String> getLinkParams() {
        final BoxPage page = getBoxPage();
        return page.createLinkParams();
    }

    @Override
    protected long countQuickSearch(QuickFilter filter, List<String> processesNames, List<String> formNames) {
        final String connectionURL = getProcessGroup().getConnectionURL();
        final String url           = connectionURL + PATH_BOX_SEARCH + getCountEndpoint();
        try {
            return new RestTemplate().postForObject(url, filter, Long.class);
        } catch (Exception e) {
            LOGGER.error("Erro ao acessar serviço: " + url, e);
            return 0;
        }
    }

    @Override
    public IModel<?> getContentTitleModel() {
        return $m.ofValue(itemBoxDTO.getName());
    }

    @Override
    public IModel<?> getContentSubtitleModel() {
        return $m.ofValue(itemBoxDTO.getDescription());
    }

    public boolean isShowNew() {
        return itemBoxDTO.isShowNewButton();
    }

    public boolean isShowQuickFilter() {
        return itemBoxDTO.isQuickFilter();
    }

    public Map<String, String> getFieldsDatatable() {
        return itemBoxDTO.getFieldsDatatable();
    }

    public String getSearchEndpoint() {
        return itemBoxDTO.getSearchEndpoint();
    }

    public String getCountEndpoint() {
        return itemBoxDTO.getCountEndpoint();
    }

    public boolean isWithRascunho() {
        return itemBoxDTO.isShowDraft();
    }
}
