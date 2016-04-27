package br.net.mirante.singular.server.core.wicket.entrada;



import br.net.mirante.singular.server.commons.persistence.dto.TaskInstanceDTO;
import br.net.mirante.singular.server.core.wicket.historico.HistoricoPage;
import br.net.mirante.singular.server.core.wicket.template.AbstractCaixaAnaliseContent;
import br.net.mirante.singular.util.wicket.datatable.BSDataTable;
import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import br.net.mirante.singular.util.wicket.datatable.BaseDataProvider;
import br.net.mirante.singular.util.wicket.datatable.column.MetronicStatusColumn;
import org.apache.wicket.Page;
import org.apache.wicket.model.IModel;

import java.util.Iterator;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

public class EntradaContent extends AbstractCaixaAnaliseContent<TaskInstanceDTO> {

    public EntradaContent(String id) {
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

    @Override
    protected Class<? extends Page> getHistoricoPage() {
        return HistoricoPage.class;
    }

    private BaseDataProvider<TaskInstanceDTO, String> createDataProvider() {
        return new BaseDataProvider<TaskInstanceDTO, String>() {

            @Override
            public long size() {
                return analisePeticaoService.countTasks(null, filtroRapido.getModelObject(), false);
            }

            @Override
            public Iterator<TaskInstanceDTO> iterator(int first, int count, String sortProperty, boolean ascending) {
                return (Iterator<TaskInstanceDTO>) analisePeticaoService.listTasks(first, count, sortProperty, ascending, null, filtroRapido.getModelObject(), false).iterator();
            }
        };
    }

    @Override
    protected IModel<?> getContentTitleModel() {
        return $m.ofValue("Caixa de entrada");
    }

    @Override
    protected IModel<?> getContentSubtitleModel() {
        return $m.ofValue("Petições");
    }

}
