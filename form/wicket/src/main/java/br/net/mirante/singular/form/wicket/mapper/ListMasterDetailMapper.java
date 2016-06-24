/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper;

import static br.net.mirante.singular.form.wicket.mapper.SingularEventsHandlers.FUNCTION.ADD_MOUSEDOWN_HANDLERS;
import static br.net.mirante.singular.util.wicket.util.Shortcuts.$b;
import static br.net.mirante.singular.util.wicket.util.Shortcuts.$m;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import br.net.mirante.singular.util.wicket.jquery.JQuery;
import br.net.mirante.singular.util.wicket.util.Shortcuts;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.google.common.base.Strings;

import br.net.mirante.singular.commons.lambda.IConsumer;
import br.net.mirante.singular.commons.lambda.IFunction;
import br.net.mirante.singular.form.SFormUtil;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SIList;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.STypeSimple;
import br.net.mirante.singular.form.SingularFormException;
import br.net.mirante.singular.form.document.SDocument;
import br.net.mirante.singular.form.type.basic.AtrBasic;
import br.net.mirante.singular.form.type.basic.SPackageBasic;
import br.net.mirante.singular.form.validation.IValidationError;
import br.net.mirante.singular.form.validation.ValidationErrorLevel;
import br.net.mirante.singular.form.view.SView;
import br.net.mirante.singular.form.view.SViewListByMasterDetail;
import br.net.mirante.singular.form.wicket.ISValidationFeedbackHandlerListener;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper;
import br.net.mirante.singular.form.wicket.SValidationFeedbackHandler;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.component.BFModalWindow;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.mapper.components.MetronicPanel;
import br.net.mirante.singular.form.wicket.model.MTipoModel;
import br.net.mirante.singular.form.wicket.model.SInstanceItemListaModel;
import br.net.mirante.singular.form.wicket.util.FormStateUtil;
import br.net.mirante.singular.form.wicket.util.WicketFormProcessing;
import br.net.mirante.singular.util.wicket.ajax.ActionAjaxButton;
import br.net.mirante.singular.util.wicket.ajax.ActionAjaxLink;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.TemplatePanel;
import br.net.mirante.singular.util.wicket.datatable.BSDataTable;
import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import br.net.mirante.singular.util.wicket.datatable.BaseDataProvider;
import br.net.mirante.singular.util.wicket.datatable.IBSAction;
import br.net.mirante.singular.util.wicket.datatable.column.BSActionPanel.ActionConfig;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder;
import br.net.mirante.singular.util.wicket.model.IMappingModel;
import br.net.mirante.singular.util.wicket.model.IReadOnlyModel;
import br.net.mirante.singular.util.wicket.resource.Icone;
import br.net.mirante.singular.util.wicket.util.JavaScriptUtils;
import br.net.mirante.singular.util.wicket.util.WicketUtils;

@SuppressWarnings("serial")
public class ListMasterDetailMapper implements IWicketComponentMapper {

    @Override
    public void buildView(WicketBuildContext ctx) {

        @SuppressWarnings("unchecked")
        final IModel<SIList<SInstance>> model = $m.get(() -> (SIList<SInstance>) ctx.getModel().getObject());
        final ViewMode viewMode = ctx.getViewMode();
        final SView view = ctx.getView();

        if (!(view instanceof SViewListByMasterDetail)) {
            throw new SingularFormException("Error: Mapper " + ListMasterDetailMapper.class.getSimpleName()
                    + " must be associated with a view  of type" + SViewListByMasterDetail.class.getName() + ".", model.getObject());
        }

        BSContainer<?> externalAtual = new BSContainer<>("externalContainerAtual");
        BSContainer<?> externalIrmao = new BSContainer<>("externalContainerIrmao");

        ctx.getExternalContainer().appendTag("div", true, null, externalAtual);
        ctx.getExternalContainer().appendTag("div", true, null, externalIrmao);

        final MasterDetailModal modal = new MasterDetailModal("mods", model, newItemLabelModel(ctx, model), ctx, viewMode, (SViewListByMasterDetail) view, externalIrmao, ctx.getUiBuilderWicket());

        externalAtual.appendTag("div", true, null, modal);

        final IModel<String> listaLabel = newLabelModel(ctx, model);

        ctx.getContainer().appendTag("div", true, null, new MetronicPanel("panel") {

            protected String getPanelWrapperClass() {
                return "list-detail-input";
            }

            protected String getPanelHeadingClass() {
                return "list-table-heading";
            }

            protected String getPanelBodyClass() {
                return "list-detail-body";
            }

            protected String getPanelFooterClass() {
                return "list-detail-footer";
            }

            @Override
            protected void buildHeading(BSContainer<?> heading, Form<?> form) {
                heading.appendTag("span", new Label("_title", listaLabel));
                heading.add($b.visibleIf($m.get(() -> !Strings.isNullOrEmpty(listaLabel.getObject()))));
            }

            @Override
            protected void buildFooter(BSContainer<?> footer, Form<?> form) {
                AbstractListaMapper.buildFooter(footer, ctx,  () -> newAddAjaxLink(modal, ctx));
            }

            @Override
            protected void buildContent(BSContainer<?> content, Form<?> form) {

                final String emptyContentTemplate = ""
                        + " <div class='list-detail-empty'>                                          "
                        + "    <p class='list-detail-empty-message'>Nenhum item foi adicionado.</p>  "
                        + " </div>                                                                   ";
                final TemplatePanel emptyContent = new TemplatePanel("emptyContent", emptyContentTemplate);
                emptyContent.add(new Behavior() {
                    @Override
                    public void onConfigure(Component component) {
                        super.onConfigure(component);
                        if (ctx.getCurrentInstance() instanceof SIList) {
                            component.setVisible(((SIList<?>) ctx.getCurrentInstance()).isEmpty());
                        }
                    }
                });
                content.appendTag("div", emptyContent);

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

        modal.add($b.onEnterDelegate(modal.addButton));

    }

    /*
     * DATA TABLE
     */

    private IModel<String> newLabelModel(WicketBuildContext ctx, IModel<SIList<SInstance>> listaModel) {
        AtrBasic       iLista     = listaModel.getObject().asAtr();
        IModel<String> labelModel = $m.ofValue(trimToEmpty(iLista.asAtr().getLabel()));
        ctx.configureContainer(labelModel);
        return labelModel;
    }

    private IModel<String> newItemLabelModel(WicketBuildContext ctx, IModel<SIList<SInstance>> listaModel) {
        AtrBasic iLista = listaModel.getObject().asAtr();
        return $m.ofValue(trimToEmpty(iLista.getItemLabel() != null ? iLista.getItemLabel() : iLista.asAtr().getLabel()));
    }

    private BSDataTable<SInstance, ?> buildTable(String id, IModel<SIList<SInstance>> model, SViewListByMasterDetail view, MasterDetailModal modal, WicketBuildContext ctx, ViewMode viewMode) {

        BSDataTableBuilder<SInstance, ?, ?> builder = new BSDataTableBuilder<>(newDataProvider(model)).withNoRecordsToolbar();

        configureColumns(view.getColumns(), builder, model, modal, ctx, viewMode, view);

        BSDataTable<SInstance, ?> dataTable = builder.build(id);

        dataTable.setOnNewRowItem(new IConsumer<Item<SInstance>>() {
            @Override
            public void accept(Item<SInstance> rowItem) {
                SValidationFeedbackHandler feedbackHandler = SValidationFeedbackHandler.bindTo(rowItem)
                        .addInstanceModel(rowItem.getModel())
                        .addListener(ISValidationFeedbackHandlerListener.withTarget(t -> t.add(rowItem)));
                rowItem.add($b.classAppender("singular-form-table-row can-have-error"));
                rowItem.add($b.classAppender("has-errors", $m.ofValue(feedbackHandler).map(it -> it.containsNestedErrors())));
            }
        });

        return dataTable;
    }

    private BaseDataProvider<SInstance, ?> newDataProvider(final IModel<SIList<SInstance>> model) {
        return new BaseDataProvider<SInstance, Object>() {

            @Override
            public Iterator<SInstance> iterator(int first, int count, Object sortProperty, boolean ascending) {
                return model.getObject().iterator();
            }

            @Override
            public long size() {
                return model.getObject().size();
            }

            @Override
            public IModel<SInstance> model(SInstance object) {
                return new SInstanceItemListaModel<>(model, model.getObject().indexOf(object));
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

            String         label      = columnType.getCustomLabel();
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
                                .title(Model.of("Remover"))
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
                            .title(viewMode.isEdition() && view.isEditEnabled() ? Model.of("Editar") : Model.of("Visualizar"))
                            .style($m.ofValue(MapperCommons.BUTTON_STYLE)),
                    (target, rowModel) -> {
                        modal.showExisting(target, rowModel, ctx);
                    });
            actionColumn.appendAction(
                    new ActionConfig<>()
                            .iconeModel(IReadOnlyModel.of(() -> Icone.EXCLAMATION_TRIANGLE))
                            .buttonModel(Model.of("red"))
                            .title(IMappingModel.of(model).map(it -> it.getNestedValidationErrors().size() + " erro(s) encontrado(s)"))
                            .style($m.ofValue(MapperCommons.BUTTON_STYLE)),
                    new IBSAction<SInstance>() {
                        @Override
                        public void execute(AjaxRequestTarget target, IModel<SInstance> model) {
                            SInstance                    baseInstance = model.getObject();
                            SDocument                    doc          = baseInstance.getDocument();
                            Collection<IValidationError> errors       = baseInstance.getNestedValidationErrors();
                            if ((errors != null) && !errors.isEmpty()) {
                                String alertLevel = errors.stream()
                                        .map(it -> it.getErrorLevel())
                                        .collect(Collectors.maxBy(Comparator.naturalOrder()))
                                        .map(it -> it.le(ValidationErrorLevel.WARNING) ? "alert-warning" : "alert-danger")
                                        .get();

                                final StringBuilder sb = new StringBuilder("<div><ul class='list-unstyled alert " + alertLevel + "'>");
                                for (IValidationError error : errors) {
                                    Optional<SInstance> inst = doc.findInstanceById(error.getInstanceId());
                                    if (inst.isPresent()) {
                                        sb.append("<li>")
                                                .append(SFormUtil.generateUserFriendlyPath(inst.get(), baseInstance))
                                                .append(": ")
                                                .append(error.getMessage())
                                                .append("</li>");
                                    }
                                }
                                sb.append("</ul></div>");

                                target.appendJavaScript(""
                                        + ";bootbox.alert('" + JavaScriptUtils.javaScriptEscape(sb.toString()) + "');");
                            }
                        }

                        @Override
                        public boolean isVisible(IModel<SInstance> model) {
                            return model.getObject().hasNestedValidationErrors();
                        }
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
            SType<?>    mtipo    = mTipoModel.getObject();
            if (mtipo == null) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Não foi especificado o valor da coluna para " + o);
                return null;
            }
            SInstance instancia = composto.findDescendant(mtipo).get();
            return displayValueFunction.apply(instancia);
        });
    }

    private AjaxLink<String> newAddAjaxLink(final MasterDetailModal modal, final WicketBuildContext ctx) {
        return new AjaxLink<String>("_add") {
            @Override
            protected void onInitialize() {
                super.onInitialize();
                add(WicketUtils.$b.attr("title", "Adicionar"));
                add(new SingularEventsHandlers(SingularEventsHandlers.FUNCTION.ADD_MOUSEDOWN_HANDLERS));
                setBody($m.ofValue("<i class=\"fa fa-plus\"></i>"+AbstractListaMapper.definirLabel(ctx)));
                setEscapeModelStrings(false);
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                final SInstance si = ctx.getModel().getObject();
                if (si instanceof SIList) {
                    final SIList<?> sil = (SIList<?>) si;
                    if (sil.getType().getMaximumSize() != null && sil.getType().getMaximumSize() == sil.size()) {
                        target.appendJavaScript(";bootbox.alert('A Quantidade máxima de valores foi atingida.');");
                    } else {
                        modal.showNew(target);
                    }
                }
            }
        };
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
        private       IModel<String>               actionLabel;
        private       ActionAjaxButton             addButton;

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

            actionLabel = $m.ofValue("");
            this.addButton(BSModalBorder.ButtonStyle.EMPTY, actionLabel, addButton = new ActionAjaxButton("btn") {
                @Override
                protected void onAction(AjaxRequestTarget target, Form<?> form) {
                    target.add(table);
                    MasterDetailModal.this.hide(target);
                }

                @Override
                protected void onInitialize() {
                    super.onInitialize();
                    add(new SingularEventsHandlers(ADD_MOUSEDOWN_HANDLERS));
                }
            });

            if (viewMode.isEdition()) {
                this.addLink(BSModalBorder.ButtonStyle.CANCEl, $m.ofValue("Cancelar"), new ActionAjaxLink<Void>("btn-cancelar") {
                    @Override
                    protected void onAction(AjaxRequestTarget target) {
                        if (closeCallback != null) {
                            closeCallback.accept(target);
                        }
                        rollbackState();
                        target.add(table);
                        MasterDetailModal.this.hide(target);
                    }

                    @Override
                    protected void onInitialize() {
                        super.onInitialize();
                        add(new SingularEventsHandlers(ADD_MOUSEDOWN_HANDLERS));
                    }
                });
            }

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
            actionLabel.setObject(view.getNewActionLabel());
            MasterDetailModal.this.configureNewContent(actionLabel.getObject(), target);
        }

        void showExisting(AjaxRequestTarget target, IModel<SInstance> forEdit, WicketBuildContext ctx) {
            closeCallback = null;
            currentInstance = forEdit;
            String prefix;
            if (ctx.getViewMode().isEdition()) {
                prefix = view.getEditActionLabel();
                actionLabel.setObject(prefix);
            } else {
                prefix = "";
                actionLabel.setObject("Fechar");
            }
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
        private String   customLabel;
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
