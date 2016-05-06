package br.net.mirante.singular.server.p.core.wicket.rascunho;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.server.commons.config.ConfigProperties;
import br.net.mirante.singular.server.commons.form.FormActions;
import br.net.mirante.singular.server.commons.persistence.dto.PeticaoDTO;
import br.net.mirante.singular.server.commons.persistence.filter.QuickFilter;
import br.net.mirante.singular.server.commons.service.PetitionService;
import br.net.mirante.singular.server.commons.service.dto.ProcessDTO;
import br.net.mirante.singular.server.commons.util.Parameters;
import br.net.mirante.singular.server.commons.wicket.SingularSession;
import br.net.mirante.singular.server.commons.wicket.view.util.DispatcherPageUtil;
import br.net.mirante.singular.server.core.wicket.ModuleLink;
import br.net.mirante.singular.server.p.core.wicket.view.AbstractCaixaContent;
import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import br.net.mirante.singular.util.wicket.util.WicketUtils;

public class RascunhoContent extends AbstractCaixaContent<PeticaoDTO> {


    @Inject
    protected PetitionService peticaoService;

    public RascunhoContent(String id, String moduleContext, String siglaProcesso) {
        super(id, moduleContext, siglaProcesso);
    }

    @Override
    public QuickFilter montarFiltroBasico() {
        return new QuickFilter()
                .withFilter(getFiltroRapido())
                .withRascunho(true);
    }

    @Override
    protected long countQuickSearch(QuickFilter filter, List<String> processesNames) {
        return peticaoService.countQuickSearch(filter, processesNames);
    }

    @Override
    protected List<PeticaoDTO> quickSearch(QuickFilter filtro, List<String> siglasProcesso) {
        return peticaoService.quickSearch(filtro, siglasProcesso);
    }

    @Override
    protected String getBaseUrl() {
        return getModuleContext() + ConfigProperties.get(ConfigProperties.SINGULAR_MODULE_FORM_ENDERECO);
    }

    @Override
    protected void appendPropertyColumns(BSDataTableBuilder<PeticaoDTO, String, IColumn<PeticaoDTO, String>> builder) {
        builder.appendPropertyColumn(getMessage("label.table.column.number"), "p.id", PeticaoDTO::getCod);
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
    protected void onDelete(PeticaoDTO peticao) {
        peticaoService.delete(peticao);
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
            setProcesses(SingularSession.get().getMenuPorLabel(getMenu()).getProcesses());

            for (ProcessDTO process : getProcesses()) {
                if (getProcesses().size() > 1) {
                    String processUrl = DispatcherPageUtil
                            .baseURL(getBaseUrl())
                            .formAction(FormActions.FORM_FILL.getId())
                            .formId(null)
                            .param(Parameters.SIGLA_FORM_NAME, process.getFormName())
                            .build();
                    dropdownMenu.adicionarMenu(id -> new ModuleLink(id, WicketUtils.$m.ofValue(process.getName()), processUrl));
                } else {
                    String url = DispatcherPageUtil
                            .baseURL(getBaseUrl())
                            .formAction(FormActions.FORM_FILL.getId())
                            .formId(null)
                            .param(Parameters.SIGLA_FORM_NAME, process.getFormName())
                            .build();
                    adicionarBotaoGlobal(id -> new ModuleLink(id, getMessage("label.button.insert"), url));
                }
            }
        }
    }
}
