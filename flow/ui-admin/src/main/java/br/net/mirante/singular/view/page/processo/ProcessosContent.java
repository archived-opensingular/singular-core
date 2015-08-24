package br.net.mirante.singular.view.page.processo;

import java.util.Iterator;

import javax.inject.Inject;

import br.net.mirante.singular.dao.PesquisaDTO;
import br.net.mirante.singular.service.PesquisaService;
import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import br.net.mirante.singular.util.wicket.datatable.BaseDataProvider;
import br.net.mirante.singular.util.wicket.datatable.column.BSActionColumn;
import br.net.mirante.singular.util.wicket.resource.Icone;
import br.net.mirante.singular.view.SingularWicketContainer;
import br.net.mirante.singular.view.template.Content;

public class ProcessosContent extends Content implements SingularWicketContainer<ProcessosContent, Void> {

    @Inject
    private PesquisaService pesquisaService;

    public ProcessosContent(String id, boolean withSideBar) {
        super(id, false, withSideBar, true);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        BaseDataProvider<PesquisaDTO, String> dataProvider = new BaseDataProvider<PesquisaDTO, String>() {
            @Override
            public Iterator<? extends PesquisaDTO> iterator(int first, int count, String sortProperty, boolean ascending) {
                return pesquisaService.retrieveAll(first, count, sortProperty, ascending).iterator();
            }

            @Override
            public long size() {
                return pesquisaService.countAll();
            }
        };

        add(new BSDataTableBuilder<>(dataProvider)
                .appendPropertyColumn(getMessage("label.table.column.code"), "cod", PesquisaDTO::getCod)
                .appendPropertyColumn(getMessage("label.table.column.name"), "name", PesquisaDTO::getNome)
                .appendPropertyColumn(getMessage("label.table.column.category"), "category", PesquisaDTO::getCategoria)
                .appendPropertyColumn(getMessage("label.table.column.version"), "version", PesquisaDTO::getVersion)
                .appendColumn(new BSActionColumn<PesquisaDTO, String>($m.ofValue(""))
                        .appendAction(getMessage("label.table.column.view"), Icone.EYE, (target, model) -> {
                            System.out.println(new String(pesquisaService.retrieveProcessDiagram()));
                        }))
                .build("processos"));
    }

    @Override
    protected String getContentTitlelKey() {
        return "label.content.title";
    }

    @Override
    protected String getContentSubtitlelKey() {
        return "label.content.subtitle";
    }
}
