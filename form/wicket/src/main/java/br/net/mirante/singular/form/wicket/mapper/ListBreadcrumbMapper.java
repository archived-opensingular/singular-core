/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.commons.lambda.IFunction;
import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.internal.xml.MElement;
import br.net.mirante.singular.form.internal.xml.MParser;
import br.net.mirante.singular.form.io.MformPersistenciaXML;
import br.net.mirante.singular.form.type.basic.SPackageBasic;
import br.net.mirante.singular.form.view.SViewBreadcrumb;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.mapper.components.MetronicPanel;
import br.net.mirante.singular.form.wicket.model.STypeModel;
import br.net.mirante.singular.form.wicket.model.SInstanceFieldModel;
import br.net.mirante.singular.form.wicket.model.SInstanceListItemModel;
import br.net.mirante.singular.util.wicket.ajax.ActionAjaxButton;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSRow;
import br.net.mirante.singular.util.wicket.datatable.BSDataTable;
import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import br.net.mirante.singular.util.wicket.datatable.BaseDataProvider;
import br.net.mirante.singular.util.wicket.datatable.column.BSActionPanel;
import br.net.mirante.singular.util.wicket.resource.Icone;
import br.net.mirante.singular.util.wicket.util.WicketUtils;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static br.net.mirante.singular.util.wicket.util.Shortcuts.$b;
import static br.net.mirante.singular.util.wicket.util.Shortcuts.$m;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

public class ListBreadcrumbMapper extends AbstractListaMapper {

    @Override
    public void buildView(WicketBuildContext ctx) {

        final IModel<? extends SInstance> model = ctx.getModel();
        final ViewMode viewMode = ctx.getViewMode();

        if (!(ctx.getView() instanceof SViewBreadcrumb)) {
            throw new SingularFormException("Error: Mapper " + ListBreadcrumbMapper.class.getSimpleName()
                    + " must be associated with a view  of type" + SViewBreadcrumb.class.getName() + ".", model.getObject());
        }

        final SViewBreadcrumb view = (SViewBreadcrumb) ctx.getView();

        final IModel<String> listaLabel = newLabelModel(ctx, model);

        BreadCrumbPanel breadcrumbPanel = new BreadCrumbPanel("panel", model, listaLabel, ctx, viewMode, view);

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
        IModel<SIList<SInstance>> listaModel = $m.get(() -> (SIList<SInstance>) model.getObject());
        SIList<?> iLista = listaModel.getObject();
        IModel<String> labelModel = $m.ofValue(trimToEmpty(iLista.asAtr().getLabel()));
        ctx.configureContainer(labelModel);
        return labelModel;
    }

    public static class ColumnType {

        private SType<?> type;
        private String customLabel;
        private IFunction<SInstance, String> displayValueFunction = SInstance::toStringDisplay;

        public ColumnType() {
        }

        public ColumnType(SType<?> type, String customLabel, IFunction<SInstance, String> displayValueFunction) {
            this.type = type;
            this.customLabel = customLabel;
            if (displayValueFunction != null) {
                this.displayValueFunction = displayValueFunction;
            }
        }

        public ColumnType(SType<?> type, String customLabel) {
            this.type = type;
            this.customLabel = customLabel;
        }

        public SType<?> getType() {
            return type;
        }

        public String getCustomLabel() {
            return customLabel;
        }

        public IFunction<SInstance, String> getDisplayValueFunction() {
            return displayValueFunction;
        }
    }

    public static class BreadCrumbPanel extends MetronicPanel {

        private IModel<SIList<SInstance>> listModel;
        private IModel<String> listaLabel;
        private WicketBuildContext ctx;
        private SViewBreadcrumb view;
        private IModel<SInstance> currentInstance;
        private String instanceBackupXml;
        private boolean adding;
        private ViewMode viewMode;

        @SuppressWarnings("unchecked")
        public BreadCrumbPanel(String id,
                               IModel<? extends SInstance> model,
                               IModel<String> listaLabel,
                               WicketBuildContext ctx,
                               ViewMode viewMode,
                               SViewBreadcrumb view) {
            super(id);
            this.listModel = $m.get(() -> (SIList<SInstance>) model.getObject());
            this.listaLabel = listaLabel;
            this.ctx = ctx;
            this.viewMode = viewMode;
            this.view = view;

            BreadCrumbStatus selectedBreadCrumbStatus = ctx.getSelectedBreadCrumbStatus();
            if (selectedBreadCrumbStatus != null) {
                currentInstance = selectedBreadCrumbStatus.currentInstance;
                instanceBackupXml = selectedBreadCrumbStatus.instanceBackupXml;
                adding = selectedBreadCrumbStatus.adding;
            }
        }

        private void pushStatus() {
            ctx.getBreadCrumbStatus().push(new BreadCrumbStatus(listModel, listaLabel, ctx, view,
                    currentInstance, instanceBackupXml, adding, viewMode));
        }

        private void popStatus() {
            BreadCrumbStatus status = ctx.getBreadCrumbStatus().pop();
            this.listModel = status.listModel;
            this.listaLabel = status.listaLabel;
            this.ctx = status.ctx;
            this.view = status.view;
            this.currentInstance = status.currentInstance;
            this.instanceBackupXml = status.instanceBackupXml;
            this.adding = status.adding;
            this.viewMode = status.viewMode;
        }

        private void saveState() {
            MElement xml = MformPersistenciaXML.toXML(currentInstance.getObject());
            if (xml != null) instanceBackupXml = xml.toString();
        }

        private void rollbackState() {
            try {
                if (adding) {
                    listModel.getObject().remove(listModel.getObject().size() - 1);
                } else {
                    MElement xml = MParser.parse(instanceBackupXml);
                    SInstance i = MformPersistenciaXML.fromXML(currentInstance.getObject().getType(), xml);
                    currentInstance.getObject().setValue(i);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void buildHeading(BSContainer<?> heading, Form<?> form) {
            heading.appendTag("span", new Label("_title", listaLabel));
            heading.add($b.visibleIf($m.get(() -> !Strings.isNullOrEmpty(listaLabel.getObject()))));
            if (viewMode.isEdition() && view.isNewEnabled()) {
                appendAddButton(heading, ctx.getModel(), ctx);
            }
        }

        protected void appendAddButton(BSContainer<?> container, IModel<? extends SInstance> m, WicketBuildContext ctx) {
            container
                    .newTemplateTag(t -> ""
                            + "<button"
                            + " wicket:id='_add'"
                            + " class='btn btn-sm pull-right'"
                            + " style='" + MapperCommons.BUTTON_STYLE + "'><i style='" + MapperCommons.ICON_STYLE + "' class='" + Icone.PLUS + "'></i>"
                            + "</button>")
                    .add(new AjaxLink<Void>("_add") {
                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            final SInstance si = m.getObject();
                            if (si instanceof SIList) {
                                final SIList sil = (SIList) si;
                                if (sil.getType().getMaximumSize() != null && sil.getType().getMaximumSize() == sil.size()) {
                                    target.appendJavaScript(";bootbox.alert('A Quantidade máxima de valores foi atingida.');");
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

            target.prependJavaScript(String.format("notify|$('#%s').hide('slide', { direction: 'left' }, 500, notify);", ctx.getRootContainer().getMarkupId()));
            ctx.getRootContainer().getItems().removeAll();
            WicketBuildContext childCtx = ctx.createChild(ctx.getRootContainer(), true, itemModel);
            childCtx.setShowBreadcrumb(true);
            ctx.getUiBuilderWicket().build(childCtx, ctx.getViewMode());
            childCtx.getRootContainer().add(new AttributeAppender("style", Model.of("display: none")) {
                @Override
                public boolean isTemporary(Component component) {
                    return true;
                }
            });
            target.appendJavaScript(String.format("$('#%s').show('slide', { direction: 'right' }, 500);", childCtx.getRootContainer().getMarkupId()));

            final BSRow buttonsRow = ctx.getRootContainer().newGrid().newRow();
            appendButtons(ctx, buttonsRow.newCol(11));

            target.add(ctx.getRootContainer());
        }

        @Override
        protected void buildFooter(BSContainer<?> footer, Form<?> form) {
            footer.setVisible(false);
        }

        @Override
        protected void buildContent(BSContainer<?> content, Form<?> form) {

            content.appendTag("table", true, null, (id) -> {
                BSDataTable<SInstance, ?> bsDataTable = buildTable(id, listModel, view, ctx, viewMode);
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
                                                     SViewBreadcrumb view, WicketBuildContext ctx, ViewMode viewMode) {

            BSDataTableBuilder<SInstance, ?, ?> builder = new BSDataTableBuilder<>(newDataProvider(model));
            builder.withNoRecordsToolbar();
            configureColumns(view.getColumns(), builder, model, ctx, viewMode, view);

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
                    IModel<SIList<SInstance>> listaModel = $m.get(() -> (SIList<SInstance>) model.getObject());
                    return new SInstanceListItemModel<>(listaModel, listaModel.getObject().indexOf(object));
                }
            };
        }

        private void configureColumns(
                List<SViewBreadcrumb.Column> mapColumns,
                BSDataTableBuilder<SInstance, ?, ?> builder,
                IModel<? extends SInstance> model,
                WicketBuildContext ctx,
                ViewMode viewMode,
                SViewBreadcrumb view) {


            List<ColumnType> columnTypes = new ArrayList<>();

            if (mapColumns.isEmpty()) {
                SType<?> tipo = ((SIList<?>) model.getObject()).getElementsType();
                if (tipo instanceof STypeSimple) {
                    columnTypes.add(new ColumnType(tipo, null));
                }
                if (tipo instanceof STypeComposite) {
                    ((STypeComposite<?>) tipo)
                            .getFields()
                            .stream()
                            .filter(mtipo -> mtipo instanceof STypeSimple)
                            .forEach(mtipo -> columnTypes.add(new ColumnType(mtipo, null)));

                }
            } else {
                mapColumns.forEach((col) -> {
                    SType<?> type = model.getObject().getDictionary().getType(col.getTypeName());
                    ColumnType columnType = new ColumnType(type, col.getCustomLabel(), col.getDisplayValueFunction());
                    columnTypes.add(columnType);
                });
            }

            for (ColumnType columnType : columnTypes) {

                IModel<String> labelModel;
                String label = columnType.getCustomLabel();

                if (label != null) {
                    labelModel = $m.ofValue(label);
                } else {
                    labelModel = $m.ofValue((String) columnType.getType().getAttributeValue(SPackageBasic.ATR_LABEL.getNameFull()));
                }

                propertyColumnAppender(builder, labelModel, new STypeModel(columnType.getType()), columnType.getDisplayValueFunction());
            }

            actionColumnAppender(builder, model, ctx, viewMode, view);

        }

        private void actionColumnAppender(BSDataTableBuilder<SInstance, ?, ?> builder,
                                          IModel<? extends SInstance> model,
                                          WicketBuildContext ctx,
                                          ViewMode viewMode,
                                          SViewBreadcrumb view) {

            builder.appendActionColumn($m.ofValue(""), actionColumn -> {
                if (viewMode.isEdition() && view.isDeleteEnabled()) {
                    actionColumn.appendAction(new BSActionPanel.ActionConfig()
                                    .iconeModel(Model.of(Icone.MINUS), Model.of(MapperCommons.ICON_STYLE))
                                    .styleClasses(Model.of("red"))
                                    .style($m.ofValue(MapperCommons.BUTTON_STYLE)),
                            (target, rowModel) -> {
                                SIList<?> sList = ((SIList<?>) model.getObject());
                                sList.remove(sList.indexOf(rowModel.getObject()));
                                target.add(ctx.getContainer());
                            });
                }
                final Icone openModalIcon = viewMode.isEdition() && view.isEditEnabled() ? Icone.PENCIL_SQUARE : Icone.EYE;
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
            ctx.getUiBuilderWicket().build(originalContext, originalContext.getViewMode());
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
                                            IModel<String> labelModel, IModel<SType<?>> mTipoModel,
                                            IFunction<SInstance, String> displayValueFunction) {
            builder.appendPropertyColumn(labelModel, o -> {
                SIComposite composto = (SIComposite) o;
                SType<?> mtipo = mTipoModel.getObject();
                SInstance instancia = composto.findDescendant(mtipo).get();
                return displayValueFunction.apply(instancia);
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
            final IModel<String> listaLabel;
            final WicketBuildContext ctx;
            final SViewBreadcrumb view;
            final IModel<SInstance> currentInstance;
            final String instanceBackupXml;
            final boolean adding;
            final ViewMode viewMode;

            public BreadCrumbStatus(IModel<SIList<SInstance>> listModel, IModel<String> listaLabel,
                                    WicketBuildContext ctx, SViewBreadcrumb view, IModel<SInstance> currentInstance,
                                    String instanceBackupXml, boolean adding, ViewMode viewMode) {
                this.listModel = listModel;
                this.listaLabel = listaLabel;
                this.ctx = ctx;
                this.view = view;
                this.currentInstance = currentInstance;
                this.instanceBackupXml = instanceBackupXml;
                this.adding = adding;
                this.viewMode = viewMode;
            }
        }

    }

}
