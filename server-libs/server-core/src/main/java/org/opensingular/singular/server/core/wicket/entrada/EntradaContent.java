package org.opensingular.singular.server.core.wicket.entrada;

import static org.opensingular.singular.util.wicket.util.WicketUtils.$m;

import java.util.Iterator;

import org.apache.wicket.Page;
import org.apache.wicket.model.IModel;

import org.opensingular.singular.server.commons.persistence.dto.TaskInstanceDTO;
import org.opensingular.singular.server.core.wicket.historico.HistoricoPage;
import org.opensingular.singular.server.core.wicket.template.AbstractCaixaAnaliseContent;
import org.opensingular.singular.util.wicket.datatable.BSDataTable;
import org.opensingular.singular.util.wicket.datatable.BSDataTableBuilder;
import org.opensingular.singular.util.wicket.datatable.BaseDataProvider;
import org.opensingular.singular.util.wicket.datatable.column.MetronicStatusColumn;

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

    @SuppressWarnings("unchecked")
    private BaseDataProvider<TaskInstanceDTO, String> createDataProvider() {
        return new BaseDataProvider<TaskInstanceDTO, String>() {

            @Override
            public long size() {
                return petitionService.countTasks(null, getUserRoleIds(), filtroRapido.getModelObject(), false);
            }

            @Override
            public Iterator<TaskInstanceDTO> iterator(int first, int count, String sortProperty, boolean ascending) {
                return (Iterator<TaskInstanceDTO>) petitionService.listTasks(first, count, sortProperty, ascending, null, getUserRoleIds(), filtroRapido.getModelObject(), false).iterator();
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
