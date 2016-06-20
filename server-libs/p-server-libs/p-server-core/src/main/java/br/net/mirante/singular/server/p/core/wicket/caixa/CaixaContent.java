/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.p.core.wicket.caixa;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.server.commons.persistence.dto.PeticaoDTO;
import br.net.mirante.singular.server.commons.persistence.filter.QuickFilter;
import br.net.mirante.singular.server.p.core.wicket.view.AbstractCaixaContent;
import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;

public class CaixaContent extends AbstractCaixaContent<PeticaoDTO> {

    private String baseUrl;
    private Pair<String, SortOrder> sortProperty;
    private QuickFilter filtro;
    private IModel<?> title;
    private IModel<?> subTitle;

    public CaixaContent(String id, String processGroupCod, String menu) {
        super(id, processGroupCod, menu);
    }

    @Override
    protected String getBaseUrl() {
        return baseUrl;
    }

    @Override
    protected void appendPropertyColumns(BSDataTableBuilder<PeticaoDTO, String, IColumn<PeticaoDTO, String>> builder) {

    }

    @Override
    protected Pair<String, SortOrder> getSortProperty() {
        return sortProperty;
    }

    @Override
    protected void onDelete(PeticaoDTO peticao) {

    }

    @Override
    protected QuickFilter montarFiltroBasico() {
        return filtro;
    }

    @Override
    protected List<PeticaoDTO> quickSearch(QuickFilter filtro, List<String> siglasProcesso) {
        return null;
    }

    @Override
    protected long countQuickSearch(QuickFilter filter, List<String> processesNames) {
        return 0;
    }

    @Override
    protected IModel<?> getContentTitleModel() {
        return title;
    }

    @Override
    protected IModel<?> getContentSubtitleModel() {
        return subTitle;
    }
}
