/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.p.core.wicket.box;

import static br.net.mirante.singular.server.commons.service.IServerMetadataREST.PATH_BOX_SEARCH;
import static br.net.mirante.singular.server.commons.util.Parameters.SIGLA_FORM_NAME;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import br.net.mirante.singular.commons.lambda.IBiFunction;
import br.net.mirante.singular.commons.lambda.IFunction;
import br.net.mirante.singular.server.commons.flow.rest.Action;
import br.net.mirante.singular.server.commons.flow.rest.ActionResponse;
import br.net.mirante.singular.server.commons.form.FormActions;
import br.net.mirante.singular.server.commons.persistence.filter.QuickFilter;
import br.net.mirante.singular.server.commons.service.dto.BoxItemAction;
import br.net.mirante.singular.server.commons.service.dto.ItemAction;
import br.net.mirante.singular.server.commons.service.dto.ItemActionConfirmation;
import br.net.mirante.singular.server.commons.service.dto.ItemActionType;
import br.net.mirante.singular.server.commons.service.dto.ItemBox;
import br.net.mirante.singular.server.commons.service.dto.ProcessDTO;
import br.net.mirante.singular.server.commons.util.Parameters;
import br.net.mirante.singular.server.commons.wicket.view.util.DispatcherPageUtil;
import br.net.mirante.singular.server.core.wicket.ModuleLink;
import br.net.mirante.singular.server.p.core.wicket.model.BoxItemModel;
import br.net.mirante.singular.server.p.core.wicket.view.AbstractCaixaContent;
import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import br.net.mirante.singular.util.wicket.datatable.IBSAction;
import br.net.mirante.singular.util.wicket.datatable.column.BSActionColumn;
import br.net.mirante.singular.util.wicket.datatable.column.BSActionPanel.ActionConfig;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder;

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
            for (ProcessDTO process : getProcesses()) {
                String url = DispatcherPageUtil
                        .baseURL(getBaseUrl())
                        .formAction(FormActions.FORM_FILL.getId())
                        .formId(null)
                        .param(Parameters.SIGLA_FORM_NAME, process.getFormName())
                        .params(getLinkParams())
                        .build();

                if (getProcesses().size() > 1) {
                    dropdownMenu.adicionarMenu(id -> new ModuleLink(id, $m.ofValue(process.getName()), url));
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

        for (ItemAction itemAction : itemBoxDTO.getActions().values()) {

            if (itemAction.getType() == ItemActionType.POPUP) {
                actionColumn.appendStaticAction($m.ofValue(itemAction.getLabel()), itemAction.getIcon(), linkFunction(itemAction, getBaseUrl(), getLinkParams()), visibleFunction(itemAction),
                    c -> c.styleClasses($m.ofValue("worklist-action-btn")));
            } else if (itemAction.getType() == ItemActionType.ENDPOINT) {
                actionColumn.appendAction($m.ofValue(itemAction.getLabel()), itemAction.getIcon(), dynamicLinkFunction(itemAction, getProcessGroup().getConnectionURL(), getLinkParams()), visibleFunction(itemAction),
                    c -> c.styleClasses($m.ofValue("worklist-action-btn")));
            }
        }

        builder.appendColumn(actionColumn);
    }

    @Override
    protected WebMarkupContainer criarLinkEdicao(BoxItemModel peticao, String id) {
        if (peticao.getProcessBeginDate() == null) {
            return criarLink(peticao, id, FormActions.FORM_FILL);
        } else {
            return criarLink(peticao, id, FormActions.FORM_FILL_WITH_ANALYSIS);
        }
    }

    public IBiFunction<BoxItemModel,String,MarkupContainer> linkFunction(ItemAction itemAction, String baseUrl, Map<String, String> additionalParams) {
        return (boxItemModel, id) -> {
            String url = mountStaticUrl(itemAction, baseUrl, additionalParams, boxItemModel);

            WebMarkupContainer link = new WebMarkupContainer(id);
            link.add($b.attr("target", String.format("_%s", boxItemModel.getCod())));
            link.add($b.attr("href", url));
            return link;
        };
    }

    private String mountStaticUrl(ItemAction itemAction, String baseUrl, Map<String, String> additionalParams, BoxItemModel boxItemModel) {
        final BoxItemAction action = boxItemModel.getActionByName(itemAction.getName());
        return baseUrl
                + action.getEndpoint()
                + appendParameters(additionalParams);
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
            return (target, model) -> executeDynamicAction(itemAction, baseUrl, additionalParams, model.getObject());
        }
    }

    private void executeDynamicAction(ItemAction itemAction, String baseUrl, Map<String, String> additionalParams, BoxItemModel boxItem) {
        final BoxItemAction boxAction = boxItem.getActionByName(itemAction.getName());

        String url = baseUrl
                + boxAction.getEndpoint()
                + appendParameter("id", boxItem.getCod())
                + appendParameters(additionalParams);

        try {
            callModule(url, buildCallObject(boxAction, boxItem));
        } catch (Exception e) {
            LOGGER.error("Erro ao acessar serviço: " + url, e);
            addToastrErrorMessage("Não foi possível executar esta ação.");
        }
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
        if (boxAction.isUseExecute()) {
            Action action = new Action();
            action.setName(boxAction.getName());
            return action;
        } else {
            return boxItem.getCod();
        }
    }

    protected BSModalBorder construirModalConfirmationBorder(ItemAction itemAction, String baseUrl, Map<String, String> additionalParams) {
        final ItemActionConfirmation confirmation = itemAction.getConfirmation();
        BSModalBorder confirmationModal = new BSModalBorder("confirmationModal", $m.ofValue(confirmation.getTitle()));
        confirmationModal.addOrReplace(new Label("message", $m.ofValue(confirmation.getConfirmationMessage())));
        confirmationModal.addButton(BSModalBorder.ButtonStyle.EMPTY, $m.ofValue(confirmation.getCancelButtonLabel()), new AjaxButton("cancel-delete-btn", confirmationForm) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                currentModel = null;
                confirmationModal.hide(target);
            }
        });
        confirmationModal.addButton(BSModalBorder.ButtonStyle.DANGER, $m.ofValue(confirmation.getConfirmationButtonLabel()), new AjaxButton("delete-btn", confirmationForm) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                executeDynamicAction(itemAction, baseUrl, additionalParams, currentModel.getObject());
                target.add(tabela);
                confirmationModal.hide(target);
            }
        });

        return confirmationModal;
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

    private String appendParameter(String key, Object value) {
        return "&" + key + "=" + value;
    }

    private IFunction<IModel, Boolean> visibleFunction(ItemAction itemAction) {
        return (model) -> {
            BoxItemModel boxItemModel = (BoxItemModel) model.getObject();
            return boxItemModel.hasAction(itemAction);
        };
    }

    @Override
    protected Pair<String, SortOrder> getSortProperty() {
        return sortProperty;
    }

    @Override
    protected void onDelete(BoxItemModel peticao) {

    }

    @Override
    protected QuickFilter montarFiltroBasico() {
        BoxPage boxPage = (BoxPage) getPage();
        return boxPage.createFilter()
                .withFilter(getFiltroRapidoModelObject())
                .withProcessesAbbreviation(getProcessesNames())
                .withTypesNames(getFormNames())
                .withRascunho(isWithRascunho());
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
        if (getProcesses() == null) {
            return Collections.emptyList();
        } else {
            return getProcesses()
                    .stream()
                    .map(ProcessDTO::getFormName)
                    .collect(Collectors.toList());
        }
    }

    @Override
    protected List<BoxItemModel> quickSearch(QuickFilter filter, List<String> siglasProcesso, List<String> formNames) {
        final String connectionURL  = getProcessGroup().getConnectionURL();
        final String url            = connectionURL + PATH_BOX_SEARCH + getSearchEndpoint();
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

    protected WebMarkupContainer criarLink(BoxItemModel item, String id, FormActions formActions) {

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
        final BoxPage page = (BoxPage) getPage();
        return page.createLinkParams();
    }

    @Override
    protected long countQuickSearch(QuickFilter filter, List<String> processesNames, List<String> formNames) {
        final String connectionURL  = getProcessGroup().getConnectionURL();
        final String url            = connectionURL + PATH_BOX_SEARCH + getCountEndpoint();
        try {
            return new RestTemplate().postForObject(url, filter, Long.class);
        } catch (Exception e) {
            LOGGER.error("Erro ao acessar serviço: " + url, e);
            return 0;
        }
    }

    public void setSortProperty(Pair<String, SortOrder> sortProperty) {
        this.sortProperty = sortProperty;
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
