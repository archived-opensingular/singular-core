package br.net.mirante.singular.server.commons.wicket.historico;

import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstance;
import br.net.mirante.singular.persistence.service.ProcessRetrieveService;
import br.net.mirante.singular.server.commons.util.Parameters;
import br.net.mirante.singular.server.commons.wicket.view.template.Content;
import br.net.mirante.singular.util.wicket.datatable.BSDataTable;
import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import br.net.mirante.singular.util.wicket.datatable.BaseDataProvider;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import javax.inject.Inject;
import java.util.Iterator;

public abstract class AbstractHistoricoContent extends Content {

    private static final long serialVersionUID = 8587873133590041152L;

    @Inject
    private ProcessRetrieveService processRetrieveService;

    private BSDataTable<IEntityTaskInstance, String> listTable;
    private IEntityProcessInstance                   entityProcessInstance;

    public AbstractHistoricoContent(String id) {
        super(id);
    }

    @Override
    protected IModel<?> getContentTitleModel() {
        return new ResourceModel("label.historico.title");
    }

    @Override
    protected IModel<?> getContentSubtitleModel() {
        return null;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        int instanceId = getPage().getPageParameters().get(Parameters.INSTANCE_ID).toInt();
        entityProcessInstance = processRetrieveService.retrieveProcessInstanceByCod(instanceId);

        listTable = setupDataTable();
        queue(listTable);

        queue(new Link("btnCancelar") {
            @Override
            public void onClick() {
                setResponsePage(getBackPage());
            }
        });
    }

    protected abstract Class<? extends Page> getBackPage();

    private BSDataTable<IEntityTaskInstance, String> setupDataTable() {
        return new BSDataTableBuilder<>(createDataProvider())
                .appendPropertyColumn(getMessage("label.table.column.task.name"),
                        t -> t.getTask().getName())
                .appendPropertyColumn(getMessage("label.table.column.begin.date"),
                        IEntityTaskInstance::getBeginDate)
                .appendPropertyColumn(getMessage("label.table.column.end.date"),
                        IEntityTaskInstance::getEndDate)
                .appendPropertyColumn(getMessage("label.table.column.allocated.user"),
                        "allocatedUser.simpleName")
                .build("tabela");
    }

    private BaseDataProvider<IEntityTaskInstance, String> createDataProvider() {
        return new BaseDataProvider<IEntityTaskInstance, String>() {

            @Override
            public long size() {
                return entityProcessInstance.getTasks().size();
            }

            @Override
            public Iterator<? extends IEntityTaskInstance> iterator(int first, int count, String sortProperty, boolean ascending) {
                return entityProcessInstance.getTasks().iterator();
            }
        };
    }
}
