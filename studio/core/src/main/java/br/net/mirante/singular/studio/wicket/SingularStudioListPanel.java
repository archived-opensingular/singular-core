package br.net.mirante.singular.studio.wicket;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.studio.core.CollectionEditorConfig;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.datatable.BSDataTable;
import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.panel.Panel;

import static br.net.mirante.singular.util.wicket.util.Shortcuts.$m;

public class SingularStudioListPanel<TYPE extends SType<?>> extends Panel {

    private final SingularStudioCollectionPanel.PanelControl panelControl;
    private final BSContainer container;

    public SingularStudioListPanel(String id, SingularStudioCollectionPanel.PanelControl panelControl, CollectionEditorConfig collectionEditorConfig) {
        super(id);
        this.panelControl = panelControl;
        this.container = new BSContainer("listPanelContent");
        this.container.appendComponent(tableId -> buildDataTable(tableId, collectionEditorConfig));
    }

    protected BSDataTable<SInstance, String> buildDataTable(String id, CollectionEditorConfig collectionEditorConfig) {
        BSDataTableBuilder<SInstance, String, ?> builder = new BSDataTableBuilder<>(dataProvider());
        for (Pair<String, String> p : collectionEditorConfig.getColumns()) {
            builder.appendPropertyColumn($m.ofValue(p.getKey()), p.getValue(), SInstance::toStringDisplay);
        }
        builder.setRowsPerPage(10);
        return builder.build(id);
    }

    protected SortableDataProvider<SInstance, String> dataProvider() {
        return null;
    }
}
