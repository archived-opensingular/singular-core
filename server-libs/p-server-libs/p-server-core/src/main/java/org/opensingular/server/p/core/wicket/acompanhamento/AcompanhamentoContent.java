package org.opensingular.server.p.core.wicket.acompanhamento;


import static org.opensingular.lib.wicket.util.util.WicketUtils.$m;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.model.IModel;

import org.opensingular.server.commons.persistence.dto.PeticaoDTO;
import org.opensingular.server.commons.persistence.filter.QuickFilter;
import org.opensingular.server.commons.service.PetitionService;
import org.opensingular.server.p.core.wicket.view.AbstractPeticaoCaixaContent;
import org.opensingular.lib.wicket.util.datatable.BSDataTableBuilder;
import org.opensingular.lib.wicket.util.datatable.column.BSActionColumn;

public class AcompanhamentoContent extends AbstractPeticaoCaixaContent<PeticaoDTO> {

    @Inject
    protected PetitionService peticaoService;


    public AcompanhamentoContent(String id, String moduleContext, String siglaProcesso) {
        super(id, moduleContext, siglaProcesso);
    }

    @Override
    public QuickFilter montarFiltroBasico() {
        return new QuickFilter()
                .withFilter(getFiltroRapidoModelObject())
                .withRascunho(false);
    }


    @Override
    protected long countQuickSearch(QuickFilter filter, List<String> processesNames, List<String> formNames) {
        return peticaoService.countQuickSearch(filter, processesNames, formNames);
    }

    @Override
    protected List<PeticaoDTO> quickSearch(QuickFilter filtro, List<String> siglasProcesso, List<String> formNames) {
        return peticaoService.quickSearch(filtro, siglasProcesso, formNames);
    }

    @Override
    protected void appendPropertyColumns(BSDataTableBuilder<PeticaoDTO, String, IColumn<PeticaoDTO, String>> builder) {
//        builder.appendPropertyColumn(getMessage("label.table.column.process.number"), "t.numeroProcesso", PeticaoDTO::getProcessNumber);
        builder.appendPropertyColumn(getMessage("label.table.column.process"), "p.processName", PeticaoDTO::getProcessName);
        builder.appendPropertyColumn(getMessage("label.table.column.in.date"), "pie.beginDate", PeticaoDTO::getProcessBeginDate);
        builder.appendPropertyColumn(getMessage("label.table.column.situation"), "task.name", PeticaoDTO::getSituation);
        builder.appendPropertyColumn(getMessage("label.table.column.situation.date"), "ta.beginDate", PeticaoDTO::getSituationBeginDate);
    }

    @Override
    protected Pair<String, SortOrder> getSortProperty() {
        return Pair.of("pie.beginDate", SortOrder.DESCENDING);
    }

    @Override
    protected IModel<?> getContentTitleModel() {
        return $m.ofValue("Acompanhamento");
    }

    @Override
    protected IModel<?> getContentSubtitleModel() {
        return $m.ofValue("Petições em andamento");
    }

    @Override
    protected void appendActionColumns(BSDataTableBuilder<PeticaoDTO, String, IColumn<PeticaoDTO, String>> builder) {
        BSActionColumn<PeticaoDTO, String> actionColumn = new BSActionColumn<>(getMessage("label.table.column.actions"));
        appendViewAction(actionColumn);
        builder.appendColumn(actionColumn);
    }
}
