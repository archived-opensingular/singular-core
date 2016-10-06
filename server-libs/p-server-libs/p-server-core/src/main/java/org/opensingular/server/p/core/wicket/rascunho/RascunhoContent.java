package org.opensingular.server.p.core.wicket.rascunho;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$m;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.model.IModel;

import org.opensingular.server.commons.form.FormActions;
import org.opensingular.server.commons.persistence.dto.PeticaoDTO;
import org.opensingular.server.commons.persistence.filter.QuickFilter;
import org.opensingular.server.commons.service.PetitionService;
import org.opensingular.server.commons.service.dto.FormDTO;
import org.opensingular.server.commons.util.Parameters;
import org.opensingular.server.commons.wicket.view.util.DispatcherPageUtil;
import org.opensingular.server.core.wicket.ModuleLink;
import org.opensingular.server.p.core.wicket.view.AbstractPeticaoCaixaContent;
import org.opensingular.lib.wicket.util.datatable.BSDataTableBuilder;
import org.opensingular.lib.wicket.util.util.WicketUtils;

public class RascunhoContent extends AbstractPeticaoCaixaContent<PeticaoDTO> {


    @Inject
    protected PetitionService peticaoService;

    public RascunhoContent(String id, String moduleContext, String siglaProcesso) {
        super(id, moduleContext, siglaProcesso);
    }

    @Override
    public QuickFilter montarFiltroBasico() {
        return new QuickFilter()
                .withFilter(getFiltroRapidoModelObject())
                .withRascunho(true);
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
        builder.appendPropertyColumn(getMessage("label.table.column.number"), "p.id", PeticaoDTO::getCodPeticao);
        builder.appendPropertyColumn(getMessage("label.table.column.description"), "p.description", PeticaoDTO::getDescription);
        builder.appendPropertyColumn(getMessage("label.table.column.process"), "p.processName", PeticaoDTO::getProcessName);
        builder.appendPropertyColumn(getMessage("label.table.column.edition.date"), "p.editionDate", PeticaoDTO::getEditionDate);
        builder.appendPropertyColumn(getMessage("label.table.column.creation.date"), "p.creationDate", PeticaoDTO::getCreationDate);
    }

    @Override
    protected Pair<String, SortOrder> getSortProperty() {
        return Pair.of("p.editionDate", SortOrder.DESCENDING);
    }

    @Override
    protected IModel<?> getContentTitleModel() {
        return $m.ofValue("Rascunho");
    }

    @Override
    protected IModel<?> getContentSubtitleModel() {
        return $m.ofValue("Petições de rascunho");
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        if (getMenu() != null) {
            for (FormDTO form : getForms()) {
                if (getForms().size() > 1) {
                    String processUrl = DispatcherPageUtil
                            .baseURL(getBaseUrl())
                            .formAction(FormActions.FORM_FILL.getId())
                            .formId(null)
                            .param(Parameters.SIGLA_FORM_NAME, form.getName())
                            .build();
                    dropdownMenu.adicionarMenu(id -> new ModuleLink(id, WicketUtils.$m.ofValue(form.getDescription()), processUrl));
                } else {
                    String url = DispatcherPageUtil
                            .baseURL(getBaseUrl())
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
