package br.net.mirante.singular.server.core.wicket.concluida;


import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

import java.util.Iterator;

import org.apache.wicket.Page;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.server.commons.persistence.dto.TaskInstanceDTO;
import br.net.mirante.singular.server.core.wicket.historico.HistoricoPage;
import br.net.mirante.singular.server.core.wicket.template.AbstractCaixaAnaliseContent;
import br.net.mirante.singular.util.wicket.datatable.BSDataTable;
import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import br.net.mirante.singular.util.wicket.datatable.BaseDataProvider;
import br.net.mirante.singular.util.wicket.datatable.column.MetronicStatusColumn;

public class ConcluidaContent extends AbstractCaixaAnaliseContent<TaskInstanceDTO> {

    private static final long serialVersionUID = 4477032935138424989L;

    public ConcluidaContent(String id) {
        super(id);
    }

    public ConcluidaContent(String id, boolean withInfoLink, boolean withBreadcrumb) {
        super(id, withInfoLink, withBreadcrumb);
    }



    @Override
    protected BSDataTable<TaskInstanceDTO, String> setupDataTable() {
        return new BSDataTableBuilder<>(createDataProvider())
                .appendPropertyColumn(getMessage("label.table.column.in.date"), "processBeginDate", TaskInstanceDTO::getProcessBeginDate)
//                .appendPropertyColumn(getMessage("label.table.column.number"), "id", TaskInstanceDTO::getNumeroProcesso)
//                .appendPropertyColumn(getMessage("label.table.column.requester"), "requester", TaskInstanceDTO::getSolicitante)
                .appendPropertyColumn(getMessage("label.table.column.description"), "description", TaskInstanceDTO::getDescricao)
                .appendPropertyColumn(getMessage("label.table.column.situation.date"), "situationBeginDate", TaskInstanceDTO::getSituationBeginDate)
                .appendColumn(new MetronicStatusColumn<>(getMessage("label.table.column.state"), "state", TaskInstanceDTO::getTaskName, this::badgeMapper))
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
                return petitionService.countTasks(null, getUserRoleIds(), filtroRapido.getModelObject(), true);
            }

            @Override
            public Iterator<TaskInstanceDTO> iterator(int first, int count, String sortProperty, boolean ascending) {
                return (Iterator<TaskInstanceDTO>) petitionService.listTasks(first, count, sortProperty, ascending, null, getUserRoleIds(), filtroRapido.getModelObject(), true).iterator();
            }
        };
    }

    @Override
    protected IModel<?> getContentTitleModel() {
        return $m.ofValue("Concluídas");
    }

}
