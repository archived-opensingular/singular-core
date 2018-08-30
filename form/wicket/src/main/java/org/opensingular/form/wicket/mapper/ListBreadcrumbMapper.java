/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.wicket.mapper;

import com.google.common.base.Strings;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.io.SFormXMLUtil;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.view.SViewBreadcrumb;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.mapper.common.util.ColumnType;
import org.opensingular.form.wicket.mapper.components.MetronicPanel;
import org.opensingular.form.wicket.model.SInstanceFieldModel;
import org.opensingular.form.wicket.model.SInstanceListItemModel;
import org.opensingular.internal.lib.commons.xml.MElement;
import org.opensingular.internal.lib.commons.xml.MParser;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.commons.ui.Icon;
import org.opensingular.lib.wicket.util.ajax.ActionAjaxButton;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSRow;
import org.opensingular.lib.wicket.util.datatable.BSDataTable;
import org.opensingular.lib.wicket.util.datatable.BSDataTableBuilder;
import org.opensingular.lib.wicket.util.datatable.BaseDataProvider;
import org.opensingular.lib.wicket.util.datatable.column.BSActionPanel;
import org.opensingular.lib.wicket.util.resource.DefaultIcons;
import org.opensingular.lib.wicket.util.scripts.Scripts;
import org.opensingular.lib.wicket.util.util.WicketUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.opensingular.lib.wicket.util.util.Shortcuts.$b;
import static org.opensingular.lib.wicket.util.util.Shortcuts.$m;

public class ListBreadcrumbMapper extends AbstractListMapper {

    @Override
    public void buildView(WicketBuildContext ctx) {

        final IModel<? extends SInstance> model = ctx.getModel();
        final ViewMode viewMode = ctx.getViewMode();

        if (!(ctx.getView() instanceof SViewBreadcrumb)) {
            throw new SingularFormException("Error: Mapper " + ListBreadcrumbMapper.class.getSimpleName()
                + " must be associated with a view  of type" + SViewBreadcrumb.class.getName() + ".", model.getObject());
        }

        final IModel<String> listLabel = newLabelModel(ctx, model);

        BreadCrumbPanel breadcrumbPanel = new BreadCrumbPanel("panel", model, listLabel, ctx, viewMode);

        List<String> breadCrumbs = ctx.getRootContext().getBreadCrumbs();

        if (breadCrumbs.isEmpty()) {
            breadCrumbs.add("Início");
        }

        ctx.getContainer().appendTag("div", breadcrumbPanel);
    }

    /*
     * DATA TABLE
     */

    /**
     * @param ctx
     * @param model
     * @return
     */
    @SuppressWarnings("unchecked")
    private IModel<String> newLabelModel(WicketBuildContext ctx, IModel<? extends SInstance> model) {
        IModel<SIList<SInstance>> listModel = $m.get(() -> (SIList<SInstance>) model.getObject());
        SIList<?> iList = listModel.getObject();
        IModel<String> labelModel = $m.ofValue(trimToEmpty(iList.asAtr().getLabel()));
        ctx.configureContainer(labelModel);
        return labelModel;
    }

    public static class BreadCrumbPanel extends MetronicPanel {

        private IModel<SIList<SInstance>> listModel;
        private IModel<String>            listLabel;
        private WicketBuildContext        ctx;
        private IModel<SInstance>         currentInstance;
        private String                    instanceBackupXml;
        private boolean                   adding;
        private ViewMode                  viewMode;

        @SuppressWarnings("unchecked")
        public BreadCrumbPanel(String id,
            IModel<? extends SInstance> model,
            IModel<String> listLabel,
            WicketBuildContext ctx,
            ViewMode viewMode) {
            super(id);
            this.listModel = $m.get(() -> (SIList<SInstance>) model.getObject());
            this.listLabel = listLabel;
            this.ctx = ctx;
            this.viewMode = viewMode;

            BreadCrumbStatus selectedBreadCrumbStatus = ctx.getSelectedBreadCrumbStatus();
            if (selectedBreadCrumbStatus != null) {
                currentInstance = selectedBreadCrumbStatus.currentInstance;
                instanceBackupXml = selectedBreadCrumbStatus.instanceBackupXml;
                adding = selectedBreadCrumbStatus.adding;
            }
        }

        private void pushStatus() {
            ctx.getBreadCrumbStatus().push(new BreadCrumbStatus(listModel, listLabel, ctx,
                currentInstance, instanceBackupXml, adding, viewMode));
        }

        private void popStatus() {
            BreadCrumbStatus status = ctx.getBreadCrumbStatus().pop();
            this.listModel = status.listModel;
            this.listLabel = status.listLabel;
            this.ctx = status.ctx;
            this.currentInstance = status.currentInstance;
            this.instanceBackupXml = status.instanceBackupXml;
            this.adding = status.adding;
            this.viewMode = status.viewMode;
        }

        private void saveState() {
            SFormXMLUtil.toStringXML(currentInstance.getObject()).ifPresent(x -> instanceBackupXml = x);
        }

        private void rollbackState() {
            try {
                if (adding) {
                    listModel.getObject().remove(listModel.getObject().size() - 1);
                } else {
                    MElement xml = MParser.parse(instanceBackupXml);
                    SInstance i = SFormXMLUtil.fromXML(currentInstance.getObject().getType(), xml);
                    currentInstance.getObject().setValue(i);
                }
            } catch (Exception e) {
                throw SingularException.rethrow(e.getMessage(), e);
            }
        }

        @Override
        protected void buildHeading(BSContainer<?> heading, Form<?> form) {
            heading.appendTag("span", new Label("_title", listLabel));
            heading.add($b.visibleIf($m.get(() -> !Strings.isNullOrEmpty(listLabel.getObject()))));
            if (viewMode.isEdition() && ctx.getViewSupplier(SViewBreadcrumb.class).get().isAddEnabled(listModel.getObject())) {
                appendAddButton(heading, ctx.getModel(), ctx);
            }
        }

        protected void appendAddButton(BSContainer<?> container, IModel<? extends SInstance> m, WicketBuildContext ctx) {
            container
                .newTemplateTag(t -> ""
                    + "<button"
                    + " wicket:id='_add'"
                    + " class='btn btn-sm pull-right'"
                    + " style='" + MapperCommons.BUTTON_STYLE + "'><i style='" + MapperCommons.ICON_STYLE + "' class='" + DefaultIcons.PLUS + "'></i>"
                    + "</button>")
                .add(new AjaxLink<Void>("_add") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        final SInstance si = m.getObject();
                        if (si instanceof SIList) {
                            final SIList sil = (SIList) si;
                            if (sil.getType().getMaximumSize() != null && sil.getType().getMaximumSize() == sil.size()) {
                                target.appendJavaScript(";bootbox.alert('A quantidade máxima de valores foi atingida.');");
                                target.appendJavaScript(Scripts.multipleModalBackDrop());
                            } else {
                                adding = true;
                                pushStatus();
                                SInstance sInstance = sil.addNew();
                                IModel<? extends SInstance> itemModel = new SInstanceFieldModel<>(ctx.getRootContext().getModel(), sInstance.getPathFromRoot());
                                showCrud(ctx, target, itemModel);
                            }
                        }
                    }
                });
        }

        private void showCrud(WicketBuildContext ctx, AjaxRequestTarget target, IModel<? extends SInstance> itemModel) {
            ctx.getRootContext().getBreadCrumbs().add((String) ctx.getCurrentInstance().getType().getAttributeValue(SPackageBasic.ATR_LABEL.getNameFull()));

            BSContainer<?> rootContainer = ctx.getRootContainer();

            target.prependJavaScript(String.format("notify|$('#%s').hide('slide', { direction: 'left' }, 500, notify);", rootContainer.getMarkupId()));
            rootContainer.getItems().removeAll();
            WicketBuildContext childCtx = ctx.createChild(rootContainer, ctx.getExternalContainer(), itemModel);
            childCtx.setShowBreadcrumb(true);
            childCtx.build();
            BSContainer<?> childCtxRootContainer = childCtx.getRootContainer();
            childCtxRootContainer.add(new AttributeAppender("style", Model.of("display: none")) {
                @Override
                public boolean isTemporary(Component component) {
                    return true;
                }
            });
            target.appendJavaScript(String.format("$('#%s').show('slide', { direction: 'right' }, 500);", childCtxRootContainer.getMarkupId()));

            final BSRow buttonsRow = rootContainer.newGrid().newRow();
            appendButtons(ctx, buttonsRow.newCol(11));

            target.add(rootContainer);
        }

        @Override
        protected void buildFooter(BSContainer<?> footer, Form<?> form) {
            footer.setVisible(false);
        }

        @Override
        protected void buildContent(BSContainer<?> content, Form<?> form) {

            content.appendTag("table", true, null, (id) -> {
                BSDataTable<SInstance, ?> bsDataTable = buildTable(id, listModel, ctx, viewMode);
                bsDataTable.add(new Behavior() {
                    @Override
                    public void onConfigure(Component component) {
                        super.onConfigure(component);
                        if (ctx.getCurrentInstance() instanceof SIList) {
                            component.setVisible(!((SIList<?>) ctx.getCurrentInstance()).isEmpty());
                        }
                    }
                });
                return bsDataTable;
            });

        }

        private BSDataTable<SInstance, ?> buildTable(String id, IModel<? extends SInstance> model,
            WicketBuildContext ctx, ViewMode viewMode) {

            SViewBreadcrumb view = (SViewBreadcrumb) ctx.getView();
            BSDataTableBuilder<SInstance, ?, ?> builder = new BSDataTableBuilder<>(newDataProvider(model));
            builder.withNoRecordsToolbar();
            configureColumns(view.getColumns(), builder, model, ctx, viewMode);

            return builder.build(id);
        }

        @SuppressWarnings("unchecked")
        private BaseDataProvider<SInstance, ?> newDataProvider(final IModel<? extends SInstance> model) {
            return new BaseDataProvider<SInstance, Object>() {

                @Override
                public Iterator<SInstance> iterator(int first, int count, Object sortProperty, boolean ascending) {
                    return ((SIList<SInstance>) model.getObject()).iterator();
                }

                @Override
                public long size() {
                    return ((SIList<SInstance>) model.getObject()).size();
                }

                @Override
                public IModel<SInstance> model(SInstance object) {
                    IModel<SIList<SInstance>> listModel = $m.get(() -> (SIList<SInstance>) model.getObject());
                    return new SInstanceListItemModel<>(listModel, listModel.getObject().indexOf(object));
                }
            };
        }

        private void configureColumns(
            List<SViewBreadcrumb.Column> mapColumns,
            BSDataTableBuilder<SInstance, ?, ?> builder,
            IModel<? extends SInstance> model,
            WicketBuildContext ctx,
            ViewMode viewMode) {

            List<ColumnType> columnTypes = new ArrayList<>();

            if (mapColumns.isEmpty()) {
                SType<?> type = ((SIList<?>) model.getObject()).getElementsType();
                if (type instanceof STypeSimple) {
                    columnTypes.add(new ColumnType(type.getName(), null, null));
                } else if (type.isComposite()) {
                    ((STypeComposite<?>) type)
                        .getFields()
                        .stream()
                        .filter(sType -> sType instanceof STypeSimple)
                        .forEach(sType -> columnTypes.add(new ColumnType(sType.getName(), null, null)));

                }
            } else {
                mapColumns.forEach((col) -> {
                    SType<?> type = model.getObject().getDictionary().getType(col.getTypeName());
                    ColumnType columnType = new ColumnType(type.getName(), col.getCustomLabel(), col.getColumnSortName(), col.getDisplayValueFunction());
                    columnTypes.add(columnType);
                });
            }

            for (ColumnType columnType : columnTypes) {

                IModel<String> labelModel;
                String label = columnType.getCustomLabel(model.getObject());

                if (label != null) {
                    labelModel = $m.ofValue(label);
                } else {
                    labelModel = $m.ofValue((String) columnType.getType(model.getObject()).getAttributeValue(SPackageBasic.ATR_LABEL.getNameFull()));
                }
                final String typeName = columnType.getTypeName();
                propertyColumnAppender(builder, labelModel, $m.ofValue(typeName), columnType.getDisplayFunction());
            }

            actionColumnAppender(builder, model, ctx, viewMode);

        }

        private void actionColumnAppender(BSDataTableBuilder<SInstance, ?, ?> builder,
            IModel<? extends SInstance> model,
            WicketBuildContext ctx,
            ViewMode viewMode) {
            ISupplier<SViewBreadcrumb> viewSupplier = ctx.getViewSupplier(SViewBreadcrumb.class);
            builder.appendActionColumn($m.ofValue(""), actionColumn -> {
                if (viewMode.isEdition()) {

                    IFunction<IModel<?>, Boolean> visibleFor = m -> viewSupplier.get().getButtonsConfig().isDeleteEnabled((SInstance) m.getObject());

                    actionColumn.appendAction(new BSActionPanel.ActionConfig().visibleFor(visibleFor)
                        .iconeModel(Model.of(DefaultIcons.MINUS), Model.of(MapperCommons.ICON_STYLE))
                        .styleClasses(Model.of("red"))
                        .style($m.ofValue(MapperCommons.BUTTON_STYLE)),
                        (target, rowModel) -> {
                            SIList<?> sList = ((SIList<?>) model.getObject());
                            sList.remove(sList.indexOf(rowModel.getObject()));
                            target.add(ctx.getContainer());
                        });
                }
                final Icon openModalIcon = viewMode.isEdition() && viewSupplier.get().isEditEnabled() ? DefaultIcons.PENCIL_SQUARE : DefaultIcons.EYE;
                actionColumn.appendAction(
                    new BSActionPanel.ActionConfig()
                        .iconeModel(Model.of(openModalIcon), Model.of(MapperCommons.ICON_STYLE))
                        .styleClasses(Model.of("blue-madison"))
                        .style($m.ofValue(MapperCommons.BUTTON_STYLE)),
                    (target, rowModel) -> {
                        currentInstance = rowModel;
                        saveState();
                        pushStatus();
                        showCrud(ctx, target, rowModel);
                    });
            });
        }

        private void hideCrud(WicketBuildContext ctx, AjaxRequestTarget target) {
            WicketBuildContext originalContext = ctx.getParent();

            while (originalContext.getParent() != null
                && !originalContext.isShowBreadcrumb()) {
                originalContext = originalContext.getParent();
            }

            target.prependJavaScript(String.format("notify|$('#%s').hide('slide', { direction: 'right' }, 500, notify);", ctx.getRootContainer().getMarkupId()));
            originalContext.popBreadCrumb();
            originalContext.getContainer().getItems().removeAll();
            if (!ctx.getBreadCrumbStatus().isEmpty()) {
                originalContext.setSelectedBreadCrumbStatus(ctx.getBreadCrumbStatus().getLast());
            }
            originalContext.build();
            originalContext.getRootContainer().add(new AttributeAppender("style", Model.of("display: none")) {
                @Override
                public boolean isTemporary(Component component) {
                    return true;
                }
            });
            target.appendJavaScript(String.format("$('#%s').show('slide', { direction: 'left' }, 500);", originalContext.getRootContainer().getMarkupId()));

            if (!originalContext.isRootContext()) {
                final BSRow buttonsRow = originalContext.getRootContainer().newGrid().newRow();
                appendButtons(originalContext, buttonsRow.newCol(11));
            }

            target.add(originalContext.getContainer());
        }

        /**
         * property column isolado em outro método para isolar o escopo de
         * serialização do lambda do appendPropertyColumn
         */
        private void propertyColumnAppender(BSDataTableBuilder<SInstance, ?, ?> builder,
            IModel<String> labelModel, IModel<String> sTypeNameModel,
            IFunction<SInstance, String> displayValueFunction) {
            builder.appendPropertyColumn(labelModel, o -> {
                SIComposite composite = (SIComposite) o;
                SType<?> type = composite.getDictionary().getType(sTypeNameModel.getObject());
                SInstance instance = composite.findDescendant(type).get();
                return displayValueFunction.apply(instance);
            });
        }

        private void appendButtons(WicketBuildContext ctx, BSContainer<?> cell) {

            cell.add(WicketUtils.$b.attrAppender("class", "text-center", " "));

            cell
                .newTemplateTag(t -> "" +
                    "<button wicket:id='okButton' class='btn btn-primary'>" +
                    "<wicket:container wicket:id='label'></wicket:container>" +
                    "</button> ")
                .add(new ActionAjaxButton("okButton") {
                    @Override
                    protected void onAction(AjaxRequestTarget target, Form<?> form) {
                        popStatus();
                        hideCrud(ctx, target);
                    }
                }.add(new Label("label", "OK")));

            cell
                .newTemplateTag(t -> "" +
                    "<button wicket:id='cancelButton' class='btn'>" +
                    "<wicket:container wicket:id='label'></wicket:container>" +
                    "</button> ")
                .add(new ActionAjaxButton("cancelButton") {
                    @Override
                    protected void onAction(AjaxRequestTarget target, Form<?> form) {
                        popStatus();
                        rollbackState();
                        hideCrud(ctx, target);
                    }
                }.add(new Label("label", "Cancelar")));

        }

        public static class BreadCrumbStatus implements Serializable {
            final IModel<SIList<SInstance>> listModel;
            final IModel<String>            listLabel;
            final WicketBuildContext        ctx;
            final IModel<SInstance>         currentInstance;
            final String                    instanceBackupXml;
            final boolean                   adding;
            final ViewMode                  viewMode;

            public BreadCrumbStatus(IModel<SIList<SInstance>> listModel, IModel<String> listLabel,
                WicketBuildContext ctx, IModel<SInstance> currentInstance,
                String instanceBackupXml, boolean adding, ViewMode viewMode) {
                this.listModel = listModel;
                this.listLabel = listLabel;
                this.ctx = ctx;
                this.currentInstance = currentInstance;
                this.instanceBackupXml = instanceBackupXml;
                this.adding = adding;
                this.viewMode = viewMode;
            }
        }

    }

}
