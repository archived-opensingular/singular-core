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
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import br.net.mirante.singular.server.commons.form.FormActions;
import br.net.mirante.singular.server.commons.persistence.filter.QuickFilter;
import br.net.mirante.singular.server.commons.service.dto.ItemAction;
import br.net.mirante.singular.server.commons.service.dto.ItemBox;
import br.net.mirante.singular.server.commons.service.dto.ProcessDTO;
import br.net.mirante.singular.server.commons.util.Parameters;
import br.net.mirante.singular.server.commons.util.ServerActionConstants;
import br.net.mirante.singular.server.commons.wicket.view.util.DispatcherPageUtil;
import br.net.mirante.singular.server.core.wicket.ModuleLink;
import br.net.mirante.singular.server.p.core.wicket.model.BoxModel;
import br.net.mirante.singular.server.p.core.wicket.view.AbstractCaixaContent;
import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import br.net.mirante.singular.util.wicket.datatable.column.BSActionColumn;

public class BoxContent extends AbstractCaixaContent<BoxModel> {

    static final Logger LOGGER = LoggerFactory.getLogger(BoxContent.class);

    private Pair<String, SortOrder> sortProperty;
    private ItemBox itemBoxDTO;

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
    protected void appendPropertyColumns(BSDataTableBuilder<BoxModel, String, IColumn<BoxModel, String>> builder) {
        for (Map.Entry<String, String> entry : getFieldsDatatable().entrySet()) {
            builder.appendPropertyColumn($m.ofValue(entry.getKey()), entry.getValue());
        }
    }

    @Override
    protected void appendActionColumns(BSDataTableBuilder<BoxModel, String, IColumn<BoxModel, String>> builder) {
        BSActionColumn<BoxModel, String> actionColumn = new BSActionColumn<>(getMessage("label.table.column.actions"));

        for (ItemAction itemAction : itemBoxDTO.getActions().values()) {
            if (itemAction.getName().equalsIgnoreCase(ServerActionConstants.ACAO_ALTERAR)) {
                appendEditAction(actionColumn);
            } else if (itemAction.getName().equalsIgnoreCase(ServerActionConstants.ACAO_VISUALIZAR)) {
                appendViewAction(actionColumn);
            } else if (itemAction.getName().equalsIgnoreCase(ServerActionConstants.ACAO_EXCLUIR)) {
                appendDeleteAction(actionColumn);
            } else {

            }
        }

        builder.appendColumn(actionColumn);
    }

    @Override
    protected Pair<String, SortOrder> getSortProperty() {
        return sortProperty;
    }

    @Override
    protected void onDelete(BoxModel peticao) {
        petitionService.delete(peticao.getCod());
    }

    @Override
    protected QuickFilter montarFiltroBasico() {
        BoxPage boxPage = (BoxPage) getPage();
        return boxPage.createFilter()
                .withFilter(getFiltroRapidoModelObject())
                .withProcessesAbbreviation(getProcessesNames())
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

    @Override
    protected List<BoxModel> quickSearch(QuickFilter filter, List<String> siglasProcesso) {
        final String connectionURL = getProcessGroup().getConnectionURL();
        final String url = connectionURL + PATH_BOX_SEARCH + getSearchEndpoint();
        try {
            return (List<BoxModel>) Arrays.asList(new RestTemplate().postForObject(url, filter, Map[].class))
                    .stream().map(BoxModel::new).collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error("Erro ao acessar serviço: " + url, e);
            return Collections.emptyList();
        }
    }

    protected WebMarkupContainer criarLink(BoxModel item, String id, FormActions formActions){

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

    protected Map<String, String> getCriarLinkParameters(BoxModel item){
        final Map<String, String> linkParameters = new HashMap<>();
        linkParameters.putAll(getLinkParams());
        return linkParameters;
    }

    private Map<String, String> getLinkParams() {
        final BoxPage page = (BoxPage) getPage();
        return page.createLinkParams();
    }

    @Override
    protected long countQuickSearch(QuickFilter filter, List<String> processesNames) {
        final String connectionURL = getProcessGroup().getConnectionURL();
        final String url = connectionURL + PATH_BOX_SEARCH + getCountEndpoint();
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
