package org.opensingular.singular.studio.wicket;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.singular.form.wicket.enums.ViewMode;
import org.opensingular.singular.form.wicket.model.SInstanceRootModel;
import org.opensingular.singular.studio.core.CollectionCanvas;
import org.opensingular.singular.util.wicket.bootstrap.layout.BSContainer;
import org.opensingular.singular.util.wicket.bootstrap.layout.BSGrid;
import org.opensingular.singular.util.wicket.bootstrap.layout.IBSComponentFactory;
import org.opensingular.singular.util.wicket.datatable.BSDataTable;
import org.opensingular.singular.util.wicket.datatable.BSDataTableBuilder;
import org.opensingular.singular.util.wicket.resource.Icone;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.opensingular.form.persistence.SPackageFormPersistence.ATR_FORM_KEY;
import static org.opensingular.singular.util.wicket.util.Shortcuts.$m;

@SuppressWarnings({"serial", "unchecked"})
public class SingularStudioListPanel extends SingularStudioPanel {

    BSContainer listPanel = new BSContainer("list-panel");

    public SingularStudioListPanel(String id, SingularStudioCollectionPanel.PanelControl panelControl, CollectionCanvas canvas) {
        super(id, panelControl, canvas);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        addNewButton(listPanel);
        queue(listPanel);
        listPanel.appendTag("div", true, " class=\"dataTables_wrapper no-footer table-responsive  \" ", buildDataTable("listPanelContent"));
    }

    protected void addNewButton(BSContainer portletBodyContainer) {
        BSGrid grid = portletBodyContainer.newGrid();
        grid
                .appendTag("h4", true, "", new Label("title", $m.ofValue(collectionInfo().getTitle())))
                .appendTag("hr", true, "", new WebMarkupContainer("regua"))
                .appendTag("a", true, "class=\"btn\"", new AjaxLink("id") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        showForm(target, null);
                    }
                }.setBody($m.ofValue(getString("label.button.new"))));
    }

    protected BSContainer buildDataTable(String id) {
        BSDataTableBuilder<SInstance, String, ?> builder = new BSDataTableBuilder<>(dataProvider());

        buildDisplayColumns(builder);
        buildActionColumns(builder);

        BSContainer tableContainer = new BSContainer(id);
        tableContainer.appendTag("table", true, " class=\"table table-striped table-hover dataTable table-bordered\" ",
                (IBSComponentFactory<BSDataTable<SInstance, String>>) tableId -> builder.build(tableId));

        return tableContainer;
    }

    private void buildDisplayColumns(BSDataTableBuilder<SInstance, String, ?> builder) {
        for (Pair<String, String> p : editorConfig().getColumns()) {
            builder.appendPropertyColumn($m.ofValue(p.getKey()), p.getValue(), (SInstance i) -> {
                String subpath = p.getValue().replaceFirst(Pattern.quote(sType().getName()), "");
                String remainingPath = subpath.startsWith(".") ? subpath.substring(1) : subpath;
                if (i instanceof SIComposite) {
                    return Optional
                            .ofNullable((SIComposite) i)
                            .map(inst -> inst.getField(remainingPath))
                            .map(SInstance::toStringDisplay)
                            .orElse("");
                } else {
                    return Optional
                            .ofNullable(i)
                            .map(SInstance::toStringDisplay)
                            .orElse("");
                }
            });
        }
    }

    private void buildActionColumns(BSDataTableBuilder<SInstance, String, ?> builder) {
        builder.appendActionColumn(
                $m.ofValue(""),
                ac -> {
                    ac.appendAction(
                            $m.ofValue(getString("label.table.column.view")),
                            Icone.EYE,
                            (ajaxRequestTarget, model) -> showForm(ajaxRequestTarget, model.getObject().getAttributeValue(ATR_FORM_KEY), ViewMode.READ_ONLY));
                    ac.appendAction(
                            $m.ofValue(getString("label.table.column.edit")),
                            Icone.PENCIL,
                            (ajaxRequestTarget, model) -> showForm(ajaxRequestTarget, model.getObject().getAttributeValue(ATR_FORM_KEY)));
                    ac.appendAction(
                            $m.ofValue(getString("label.table.column.delete")),
                            Icone.TRASH,
                            (ajaxRequestTarget, model) -> {
                                repository().delete(model.getObject().getAttributeValue(ATR_FORM_KEY));
                                showList(ajaxRequestTarget);
                            });

                }
        );
        builder.setRowsPerPage(editorConfig().getRowsPerPage());
    }

    protected SortableDataProvider<SInstance, String> dataProvider() {
        return new SortableDataProvider<SInstance, String>() {
            @Override
            public Iterator<? extends SInstance> iterator(long first, long count) {
                return new ArrayList(repository().loadAll(first, count)).iterator();
            }

            @Override
            public long size() {
                return repository().countAll();
            }

            @Override
            public IModel<SInstance> model(SInstance object) {
                return new SInstanceRootModel<>(object);
            }
        };
    }

}
