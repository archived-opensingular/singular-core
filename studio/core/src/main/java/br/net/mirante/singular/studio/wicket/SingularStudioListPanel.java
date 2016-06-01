package br.net.mirante.singular.studio.wicket;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.wicket.model.MInstanceRootModel;
import br.net.mirante.singular.studio.core.CollectionCanvas;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.util.wicket.bootstrap.layout.IBSComponentFactory;
import br.net.mirante.singular.util.wicket.datatable.BSDataTable;
import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import java.util.Iterator;

import static br.net.mirante.singular.util.wicket.util.Shortcuts.$m;

@SuppressWarnings({"serial", "unchecked"})
public class SingularStudioListPanel extends SingularStudioPanel {

    public SingularStudioListPanel(String id,
                                   SingularStudioCollectionPanel.PanelControl panelControl,
                                   CollectionCanvas canvas) {
        super(id, panelControl, canvas);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        BSContainer portletBodyContainer = new BSContainer("portletBodyContainer");
        addNewButton(portletBodyContainer);
        queue(new WebMarkupContainer("portletContainer"));
        queue(portletBodyContainer);
        portletBodyContainer
                .appendTag("div", true, " class=\"dataTables_wrapper no-footer table-responsive  \" ", buildDataTable("listPanelContent"));
    }

    protected void addNewButton(BSContainer portletBodyContainer) {
        BSGrid grid = portletBodyContainer.newGrid();
        grid
                .newRow()
                .appendTag("a", true, "class=\"btn blue\"", new AjaxLink("id") {

                    @Override
                    public IModel<?> getBody() {
                        return $m.ofValue("Novo");
                    }

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        showForm(target, null);
                    }
                });
    }

    protected BSContainer buildDataTable(String id) {

        BSDataTableBuilder<SInstance, String, ?> builder = new BSDataTableBuilder<>(dataProvider());
        for (Pair<String, String> p : editorConfig().getColumns()) {
            builder.appendPropertyColumn($m.ofValue(p.getKey()), p.getValue(), SInstance::toStringDisplay);
        }
        builder.setRowsPerPage(10);


        BSContainer tableContainer = new BSContainer(id);
        tableContainer.appendTag("table", true, " class=\"table table-striped table-hover dataTable table-bordered\" ",
                (IBSComponentFactory<BSDataTable<SInstance, String>>) tableId -> builder.build(tableId));

        return tableContainer;
    }

    protected SortableDataProvider<SInstance, String> dataProvider() {
        return new SortableDataProvider<SInstance, String>() {
            @Override
            public Iterator<? extends SInstance> iterator(long first, long count) {
                return repository().loadAllAsIterable().iterator();
            }

            @Override
            public long size() {
                return Integer.MAX_VALUE;
            }

            @Override
            public IModel<SInstance> model(SInstance object) {
                return new MInstanceRootModel<>(object);
            }
        };
    }

}
