/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper;

import static br.net.mirante.singular.util.wicket.util.Shortcuts.$b;
import static br.net.mirante.singular.util.wicket.util.Shortcuts.$m;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.net.mirante.singular.form.wicket.util.FormStateUtil;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.google.common.base.Strings;

import br.net.mirante.singular.commons.lambda.IConsumer;
import br.net.mirante.singular.commons.lambda.IFunction;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;
import br.net.mirante.singular.form.mform.basic.view.SView;
import br.net.mirante.singular.form.mform.basic.view.SViewListByMasterDetail;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.component.BFModalWindow;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.mapper.components.MetronicPanel;
import br.net.mirante.singular.form.wicket.model.MTipoModel;
import br.net.mirante.singular.form.wicket.model.SInstanceItemListaModel;
import br.net.mirante.singular.form.wicket.util.WicketFormProcessing;
import br.net.mirante.singular.util.wicket.ajax.ActionAjaxButton;
import br.net.mirante.singular.util.wicket.ajax.ActionAjaxLink;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.datatable.BSDataTable;
import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import br.net.mirante.singular.util.wicket.datatable.BaseDataProvider;
import br.net.mirante.singular.util.wicket.datatable.column.BSActionPanel.ActionConfig;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder;
import br.net.mirante.singular.util.wicket.resource.Icone;

@SuppressWarnings("serial")
public class ListMasterDetailMapper implements IWicketComponentMapper {

    @Override
    public void buildView(WicketBuildContext ctx) {

        final IModel<? extends SInstance> model = ctx.getModel();
        final ViewMode viewMode = ctx.getViewMode();
        final SView view = ctx.getView();

        if (!(view instanceof SViewListByMasterDetail)) {
            throw new SingularFormException("Error: Mapper " + ListMasterDetailMapper.class.getSimpleName()
                    + " must be associated with a view  of type" + SViewListByMasterDetail.class.getName() + ".", model.getObject());
        }

        final IModel<String> listaLabel = newLabelModel(ctx, model);

        BSContainer<?> externalAtual = new BSContainer<>("externalContainerAtual");
        BSContainer<?> externalIrmao = new BSContainer<>("externalContainerIrmao");

        ctx.getExternalContainer().appendTag("div", true, null, externalAtual);
        ctx.getExternalContainer().appendTag("div", true, null, externalIrmao);

        final MasterDetailModal modal = new MasterDetailModal("mods", model, listaLabel, ctx, viewMode, (SViewListByMasterDetail) view, externalIrmao, ctx.getUiBuilderWicket());

        externalAtual.appendTag("div", true, null, modal);

        ctx.getContainer().appendTag("div", true, null, new MetronicPanel("panel") {

            @Override
            protected void buildHeading(BSContainer<?> heading, Form<?> form) {
                heading.appendTag("span", new Label("_title", listaLabel));
                heading.add($b.visibleIf($m.get(() -> !Strings.isNullOrEmpty(listaLabel.getObject()))));
                if (viewMode.isEdition() && ((SViewListByMasterDetail) view).isNewEnabled()) {
                    appendAddButton(heading, modal, ctx.getModel());
                }
            }

            @Override
            protected void buildFooter(BSContainer<?> footer, Form<?> form) {
                footer.setVisible(false);
            }

            @Override
            protected void buildContent(BSContainer<?> content, Form<?> form) {
                content.appendTag("table", true, null, (id) -> {
                    BSDataTable<SInstance, ?> bsDataTable = buildTable(id, model, (SViewListByMasterDetail) view, modal, ctx, viewMode);
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
        });
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
        IModel<String> labelModel = $m.ofValue(trimToEmpty(iLista.as(SPackageBasic.aspect()).getLabel()));
        ctx.configureContainer(labelModel);
        return labelModel;
    }

    private BSDataTable<SInstance, ?> buildTable(String id, IModel<? extends SInstance> model, SViewListByMasterDetail view, MasterDetailModal modal, WicketBuildContext ctx, ViewMode viewMode) {

        BSDataTableBuilder<SInstance, ?, ?> builder = new BSDataTableBuilder<>(newDataProvider(model)).withNoRecordsToolbar();

        configureColumns(view.getColumns(), builder, model, modal, ctx, viewMode, view);

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
                return new SInstanceItemListaModel<>(listaModel, listaModel.getObject().indexOf(object));
            }
        };
    }

    private void configureColumns(
            List<SViewListByMasterDetail.Column> mapColumns,
            BSDataTableBuilder<SInstance, ?, ?> builder,
            IModel<? extends SInstance> model,
            MasterDetailModal modal,
            WicketBuildContext ctx,
            ViewMode viewMode,
            SViewListByMasterDetail view) {


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
            mapColumns.forEach((col) -> columnTypes.add(new ColumnType(model.getObject().getDictionary().getType(col.getTypeName()), col.getCustomLabel(), col.getDisplayValueFunction())));
        }

        for (ColumnType columnType : columnTypes) {

            String label = columnType.getCustomLabel();
            IModel<String> labelModel = $m.ofValue(label);

            propertyColumnAppender(builder, labelModel, new MTipoModel(columnType.getType()), columnType.getDisplayValueFunction());
        }

        actionColumnAppender(builder, model, modal, ctx, viewMode, view);

    }

    private void actionColumnAppender(BSDataTableBuilder<SInstance, ?, ?> builder,
                                      IModel<? extends SInstance> model,
                                      MasterDetailModal modal,
                                      WicketBuildContext ctx,
                                      ViewMode viewMode,
                                      SViewListByMasterDetail view) {

        builder.appendActionColumn($m.ofValue(""), actionColumn -> {
            if (viewMode.isEdition() && view.isDeleteEnabled()) {
                actionColumn.appendAction(new ActionConfig<>()
                                .iconeModel(Model.of(Icone.MINUS), Model.of(MapperCommons.ICON_STYLE))
                                .buttonModel(Model.of("red"))
                                .style($m.ofValue(MapperCommons.BUTTON_STYLE)),
                        (target, rowModel) -> {
                            SIList<?> sList = ((SIList<?>) model.getObject());
                            sList.remove(sList.indexOf(rowModel.getObject()));
                            target.add(ctx.getContainer());
                        });
            }
            final Icone openModalIcon = viewMode.isEdition() && view.isEditEnabled() ? Icone.PENCIL_SQUARE : Icone.EYE;
            actionColumn.appendAction(
                    new ActionConfig<>()
                            .iconeModel(Model.of(openModalIcon), Model.of(MapperCommons.ICON_STYLE))
                            .buttonModel(Model.of("blue-madison"))
                            .style($m.ofValue(MapperCommons.BUTTON_STYLE)),
                    (target, rowModel) -> {
                        modal.showExisting(target, rowModel, ctx);
                    });
        });
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

    private void appendAddButton(BSContainer<?> container, MasterDetailModal modal, IModel<? extends SInstance> m) {
        container
                .newTemplateTag(t -> ""
                        + "<button"
                        + " wicket:id='_add'"
                        + " class='btn blue btn-sm pull-right'"
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
                                modal.showNew(target);
                            }
                        }
                    }
                });
    }

    private static class MasterDetailModal extends BFModalWindow {

        private final IModel<SIList<SInstance>>    listModel;
        private final IModel<String>               listaLabel;
        private final WicketBuildContext           ctx;
        private final UIBuilderWicket              wicketBuilder;
        private final Component                    table;
        private final ViewMode                     viewMode;
        private       IModel<SInstance>            currentInstance;
        private       IConsumer<AjaxRequestTarget> closeCallback;
        private       SViewListByMasterDetail      view;
        private       BSContainer<?>               containerExterno;
        private       FormStateUtil.FormState      formState;

        @SuppressWarnings("unchecked")
        MasterDetailModal(String id,
                          IModel<? extends SInstance> model,
                          IModel<String> listaLabel,
                          WicketBuildContext ctx,
                          ViewMode viewMode,
                          SViewListByMasterDetail view,
                          BSContainer<?> containerExterno,
                          UIBuilderWicket wicketBuilder) {
            super(id, true, false);

            this.wicketBuilder = wicketBuilder;
            this.listaLabel = listaLabel;
            this.ctx = ctx;
            this.table = ctx.getContainer();
            this.viewMode = viewMode;
            this.view = view;
            this.listModel = $m.get(() -> (SIList<SInstance>) model.getObject());
            this.containerExterno = containerExterno;

            setSize(BSModalBorder.Size.NORMAL);

            this.addButton(BSModalBorder.ButtonStyle.PRIMARY, $m.ofValue("OK"), new ActionAjaxButton("btn") {
                @Override
                protected void onAction(AjaxRequestTarget target, Form<?> form) {
                    target.add(table);
                    MasterDetailModal.this.hide(target);
                }
            });

            this.addLink(BSModalBorder.ButtonStyle.EMPTY, $m.ofValue("Cancelar"), new ActionAjaxLink<Void>("btn-cancelar") {
                @Override
                protected void onAction(AjaxRequestTarget target) {
                    if (closeCallback != null) {
                        closeCallback.accept(target);
                    }
                    rollbackState();
                    target.add(table);
                    MasterDetailModal.this.hide(target);
                }
            });

        }

        private void saveState() {
            formState = FormStateUtil.keepState(currentInstance.getObject());
        }

        private void rollbackState() {
            try {
                if (formState != null && currentInstance.getObject() != null) {
                    FormStateUtil.restoreState(currentInstance.getObject(), formState);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }


        void showNew(AjaxRequestTarget target) {
            closeCallback = this::revert;
            currentInstance = new SInstanceItemListaModel<>(listModel, listModel.getObject().indexOf(listModel.getObject().addNew()));
            MasterDetailModal.this.configureNewContent("Adicionar", target);
        }

        void showExisting(AjaxRequestTarget target, IModel<SInstance> forEdit, WicketBuildContext ctx) {
            String prefix = ctx.getViewMode().isEdition() ? "Editar" : "";
            closeCallback = null;
            currentInstance = forEdit;
            saveState();
            configureNewContent(prefix, target);
        }

        private void revert(AjaxRequestTarget target) {
            listModel.getObject().remove(listModel.getObject().size() - 1);
        }

        private void configureNewContent(String prefix, AjaxRequestTarget target) {
            setTitleText($m.ofValue((prefix + " " + listaLabel.getObject()).trim()));
            BSContainer<?> modalBody = new BSContainer<>("bogoMips");
            setBody(modalBody);

            ViewMode viewModeModal = viewMode;
            if (!view.isEditEnabled()) {
                viewModeModal = ViewMode.VISUALIZATION;
            }

            final WicketBuildContext context = new WicketBuildContext(ctx, modalBody, containerExterno, true, currentInstance);

            wicketBuilder.build(context, viewModeModal);
            WicketFormProcessing.onFormPrepare(modalBody, currentInstance, false);
            context.initContainerBehavior();

            target.add(ctx.getExternalContainer().setOutputMarkupId(true));
            target.add(containerExterno.setOutputMarkupId(true));

            show(target);
        }

        @Override
        public void show(AjaxRequestTarget target) {
            super.show(target);
            target.appendJavaScript(getConfigureBackdropScript());
        }

        private String getConfigureBackdropScript() {
            String js = "";
            js += " (function (zindex){ ";
            js += "     $('.modal-backdrop').each(function(index) { ";
            js += "         var zIndex = $(this).css('z-index'); ";
            js += "         $(this).css('z-index', zindex-1+index); ";
            js += "     }); ";
            js += "     $('.modal').each(function(index) { ";
            js += "         var zIndex = $(this).css('z-index'); ";
            js += "         $(this).css('z-index', zindex+index); ";
            js += "     }); ";
            js += " })(10050); ";
            return js;
        }

    }


    private static class ColumnType {

        private SType<?> type;
        private String customLabel;
        private IFunction<SInstance, String> displayValueFunction = SInstance::toStringDisplay;

        ColumnType(SType<?> type, String customLabel, IFunction<SInstance, String> displayValueFunction) {
            this.type = type;
            this.customLabel = customLabel;
            if (displayValueFunction != null) {
                this.displayValueFunction = displayValueFunction;
            }
        }

        ColumnType(SType<?> type, String customLabel) {
            this.type = type;
            this.customLabel = customLabel;
        }

        public SType<?> getType() {
            return type;
        }

        String getCustomLabel() {
            if (customLabel == null && type != null) {
                return type.getAttributeValue(SPackageBasic.ATR_LABEL);
            }
            return customLabel;
        }

        IFunction<SInstance, String> getDisplayValueFunction() {
            return displayValueFunction;
        }
    }
}
