/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.wicket.mapper.masterdetail;

import static org.opensingular.singular.util.wicket.util.Shortcuts.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.google.common.base.Strings;

import org.opensingular.singular.commons.lambda.IConsumer;
import org.opensingular.singular.commons.lambda.IFunction;
import org.opensingular.form.SFormUtil;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.type.basic.AtrBasic;
import org.opensingular.form.validation.IValidationError;
import org.opensingular.form.validation.ValidationErrorLevel;
import org.opensingular.form.view.SView;
import org.opensingular.form.view.SViewListByMasterDetail;
import org.opensingular.singular.form.wicket.ISValidationFeedbackHandlerListener;
import org.opensingular.singular.form.wicket.IWicketComponentMapper;
import org.opensingular.singular.form.wicket.SValidationFeedbackHandler;
import org.opensingular.singular.form.wicket.WicketBuildContext;
import org.opensingular.singular.form.wicket.enums.ViewMode;
import org.opensingular.singular.form.wicket.mapper.AbstractListaMapper;
import org.opensingular.singular.form.wicket.mapper.MapperCommons;
import org.opensingular.singular.form.wicket.mapper.SingularEventsHandlers;
import org.opensingular.singular.form.wicket.model.ISInstanceAwareModel;
import org.opensingular.singular.form.wicket.model.SInstanceListItemModel;
import org.opensingular.singular.form.wicket.model.STypeModel;
import org.opensingular.singular.util.wicket.bootstrap.layout.BSContainer;
import org.opensingular.singular.util.wicket.datatable.BSDataTable;
import org.opensingular.singular.util.wicket.datatable.BSDataTableBuilder;
import org.opensingular.singular.util.wicket.datatable.BaseDataProvider;
import org.opensingular.singular.util.wicket.datatable.IBSAction;
import org.opensingular.singular.util.wicket.datatable.column.BSPropertyColumn;
import org.opensingular.singular.util.wicket.datatable.column.BSActionPanel.ActionConfig;
import org.opensingular.singular.util.wicket.model.IMappingModel;
import org.opensingular.singular.util.wicket.model.IReadOnlyModel;
import org.opensingular.singular.util.wicket.resource.Icone;
import org.opensingular.singular.util.wicket.util.JavaScriptUtils;
import org.opensingular.singular.util.wicket.util.WicketUtils;

@SuppressWarnings("serial")
public class ListMasterDetailMapper implements IWicketComponentMapper {

    private void checkView(SView view, IModel<SIList<SInstance>> model) {
        if (!(view instanceof SViewListByMasterDetail)) {
            throw new SingularFormException("Error: Mapper " + ListMasterDetailMapper.class.getSimpleName()
                + " must be associated with a view  of type" + SViewListByMasterDetail.class.getName() + ".", model.getObject());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void buildView(WicketBuildContext ctx) {

        final IModel<SIList<SInstance>> model = $m.get(() -> (SIList<SInstance>) ctx.getModel().getObject());

        checkView(ctx.getView(), model);

        final SViewListByMasterDetail view = (SViewListByMasterDetail) ctx.getView();
        final ViewMode viewMode = ctx.getViewMode();
        final BSContainer<?> externalAtual = new BSContainer<>("externalContainerAtual");
        final BSContainer<?> externalIrmao = new BSContainer<>("externalContainerIrmao");

        ctx.getExternalContainer().appendTag("div", true, null, externalAtual);
        ctx.getExternalContainer().appendTag("div", true, null, externalIrmao);

        final MasterDetailModal modal = new MasterDetailModal("mods", model, newItemLabelModel(model), ctx, viewMode, view, externalIrmao);
        final IModel<String> listaLabel = newLabelModel(ctx, model);

        externalAtual.appendTag("div", true, null, modal);

        ctx.getContainer().appendTag("div", true, null, new MasterDetailMetronicPanel("panel") {
            @Override
            protected void buildHeading(BSContainer<?> heading, Form<?> form) {
                heading.appendTag("span", new Label("_title", listaLabel));
                heading.add($b.visibleIf($m.get(() -> !Strings.isNullOrEmpty(listaLabel.getObject()))));
            }

            @Override
            protected void buildFooter(BSContainer<?> footer, Form<?> form) {
                AbstractListaMapper.buildFooter(footer, ctx, () -> newAddAjaxLink(modal, ctx));
            }

            @Override
            protected void buildContent(BSContainer<?> content, Form<?> form) {
                content.appendTag("table", true, null, (id) -> {
                    final BSDataTable<SInstance, ?> bsDataTable = buildTable(id, model, view, modal, ctx, viewMode);
                    bsDataTable.setStripedRows(false);
                    bsDataTable.setHoverRows(false);
                    bsDataTable.setBorderedTable(false);
                    return bsDataTable;
                });
            }
        });

        modal.add($b.onEnterDelegate(modal.addButton));

    }

    private IModel<String> newLabelModel(WicketBuildContext ctx, IModel<SIList<SInstance>> listaModel) {
        AtrBasic iLista = listaModel.getObject().asAtr();
        IModel<String> labelModel = $m.ofValue(trimToEmpty(iLista.asAtr().getLabel()));
        ctx.configureContainer(labelModel);
        return labelModel;
    }

    private IModel<String> newItemLabelModel(IModel<SIList<SInstance>> listaModel) {
        AtrBasic iLista = listaModel.getObject().asAtr();
        return $m.ofValue(trimToEmpty(iLista.getItemLabel() != null ? iLista.getItemLabel() : iLista.asAtr().getLabel()));
    }

    private BSDataTable<SInstance, ?> buildTable(String id,
                                                 IModel<SIList<SInstance>> model,
                                                 SViewListByMasterDetail view,
                                                 MasterDetailModal modal,
                                                 WicketBuildContext ctx,
                                                 ViewMode viewMode) {

        final BSDataTableBuilder<SInstance, ?, ?> builder = new MasterDetailBSDataTableBuilder<>(newDataProvider(model)).withNoRecordsToolbar();
        final BSDataTable<SInstance, ?> dataTable;

        configureColumns(view.getColumns(), builder, model, modal, ctx, viewMode, view);
        dataTable = builder.build(id);

        dataTable.setOnNewRowItem((IConsumer<Item<SInstance>>) rowItem -> {
            SValidationFeedbackHandler feedbackHandler = SValidationFeedbackHandler.bindTo(rowItem)
                .addInstanceModel(rowItem.getModel())
                .addListener(ISValidationFeedbackHandlerListener.withTarget(t -> t.add(rowItem)));
            rowItem.add($b.classAppender("singular-form-table-row can-have-error"));
            rowItem.add($b.classAppender("has-errors", $m.ofValue(feedbackHandler).map(SValidationFeedbackHandler::containsNestedErrors)));
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
                return new SInstanceListItemModel<>(model, model.getObject().indexOf(object));
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

        final List<ColumnType> columnTypes = new ArrayList<>();

        if (mapColumns.isEmpty()) {
            final SType<?> tipo = ((SIList<?>) model.getObject()).getElementsType();
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
            mapColumns.forEach((col) -> columnTypes.add(
                new ColumnType(
                    Optional.ofNullable(col.getTypeName())
                        .map(typeName -> model.getObject().getDictionary().getType(typeName))
                        .orElse(null),
                    col.getCustomLabel(), col.getDisplayValueFunction())));
        }

        for (ColumnType columnType : columnTypes) {
            final String label = columnType.getCustomLabel();
            final IModel<String> labelModel = $m.ofValue(label);
            propertyColumnAppender(builder, labelModel, new STypeModel(columnType.getType()), columnType.getDisplayFunction());
        }

        actionColumnAppender(builder, model, modal, ctx, viewMode, view);
    }

    /**
     * Adiciona as ações a coluna de ações de mestre detalhe.
     */
    private void actionColumnAppender(BSDataTableBuilder<SInstance, ?, ?> builder,
                                      IModel<? extends SInstance> model,
                                      MasterDetailModal modal,
                                      WicketBuildContext ctx,
                                      ViewMode vm,
                                      SViewListByMasterDetail view) {
        builder.appendActionColumn($m.ofValue("Ações"), ac -> {
            if (vm.isEdition() && view.isDeleteEnabled()) {
                ac.appendAction(buildRemoveActionConfig(), buildRemoveAction(model, ctx));
            }
            ac.appendAction(buildViewOrEditActionConfig(vm, view), buildViewOrEditAction(modal, ctx));
            ac.appendAction(buildShowErrorsActionConfig(model), buildShowErrorsAction());
        });
    }

    private ActionConfig<SInstance> buildRemoveActionConfig() {
        return new ActionConfig<SInstance>()
            .styleClasses(Model.of("list-detail-remove"))
            .iconeModel(Model.of(Icone.REMOVE))
            .titleFunction(rowModel -> "Remover");
    }

    private IBSAction<SInstance> buildRemoveAction(IModel<? extends SInstance> model, WicketBuildContext ctx) {
        return (target, rowModel) -> {
            final SIList<?> list = ((SIList<?>) model.getObject());
            list.remove(list.indexOf(rowModel.getObject()));
            target.add(ctx.getContainer());
        };
    }

    private ActionConfig<SInstance> buildViewOrEditActionConfig(ViewMode viewMode, SViewListByMasterDetail view) {
        final Icone openModalIcon = viewMode.isEdition() && view.isEditEnabled() ? Icone.PENCIL : Icone.EYE;
        return new ActionConfig<SInstance>()
            .iconeModel(Model.of(openModalIcon))
            .styleClasses(Model.of("list-detail-edit"))
            .titleFunction(rowModel -> viewMode.isEdition() && view.isEditEnabled() ? "Editar" : "Visualizar");
    }

    private IBSAction<SInstance> buildViewOrEditAction(MasterDetailModal modal, WicketBuildContext ctx) {
        return (target, rowModel) -> modal.showExisting(target, rowModel, ctx);
    }

    private ActionConfig<SInstance> buildShowErrorsActionConfig(IModel<? extends SInstance> model) {
        Integer count = IMappingModel.of(model).map(it -> it.getNestedValidationErrors().size()).getObject();
        if (count > 0)
            System.out.println(count);
        return new ActionConfig<SInstance>()
            .iconeModel(IReadOnlyModel.of(() -> Icone.EXCLAMATION_TRIANGLE))
            .styleClasses(Model.of("red"))
            .titleFunction(rowModel -> IMappingModel.of(rowModel).map(it -> (it.getNestedValidationErrors().size() + " erro(s) encontrado(s)")).getObject())
            .style($m.ofValue(MapperCommons.BUTTON_STYLE));
    }

    private IBSAction<SInstance> buildShowErrorsAction() {
        return new IBSAction<SInstance>() {
            @Override
            public void execute(AjaxRequestTarget target, IModel<SInstance> model) {
                SInstance baseInstance = model.getObject();
                SDocument doc = baseInstance.getDocument();
                Collection<IValidationError> errors = baseInstance.getNestedValidationErrors();
                if ((errors != null) && !errors.isEmpty()) {
                    String alertLevel = errors.stream()
                        .map(IValidationError::getErrorLevel)
                        .collect(Collectors.maxBy(Comparator.naturalOrder()))
                        .map(it -> it.le(ValidationErrorLevel.WARNING) ? "alert-warning" : "alert-danger")
                        .orElse(null);

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
        };
    }

    /**
     * property column isolado em outro método para isolar o escopo de
     * serialização do lambda do appendPropertyColumn
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void propertyColumnAppender(BSDataTableBuilder<SInstance, ?, ?> builder,
                                        IModel<String> labelModel, IModel<SType<?>> mTipoModel,
                                        IFunction<SInstance, String> displayValueFunction) {
        IFunction<SIComposite, SInstance> toInstance = composto ->{
            SType<?> mtipo = mTipoModel.getObject();
            if (mtipo == null) {
                // TODO (LL) - confirmar
                // Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Não foi especificado o valor da coluna para " + o);
                // return null;
                return composto;
            }
            return (SInstance) composto.findDescendant(mtipo).orElse(null);
        };
        IFunction<SInstance, Object> propertyFunction = o -> displayValueFunction.apply(toInstance.apply((SIComposite) o));
        builder.appendColumn(new BSPropertyColumn(labelModel, propertyFunction) {
            @Override
            public IModel getDataModel(IModel rowModel) {
                return new ISInstanceAwareModel<Object>() {
                    @Override
                    public Object getObject() {
                        return propertyFunction.apply((SInstance) rowModel.getObject());
                    }
                    @Override
                    public void setObject(Object object) {
                        throw new UnsupportedOperationException();
                    }
                    @Override
                    public void detach() {
                        rowModel.detach();
                    }
                    @Override
                    public SInstance getMInstancia() {
                        return toInstance.apply((SIComposite) rowModel.getObject());
                    }
                };
            }
        });
//        builder.appendPropertyColumn(labelModel, propertyFunction);
    }

    private AjaxLink<String> newAddAjaxLink(final MasterDetailModal modal, final WicketBuildContext ctx) {
        return new AjaxLink<String>("_add") {
            @Override
            protected void onInitialize() {
                super.onInitialize();
                add(WicketUtils.$b.attr("title", "Adicionar"));
                add(new SingularEventsHandlers(SingularEventsHandlers.FUNCTION.ADD_MOUSEDOWN_HANDLERS));
                setBody($m.ofValue("<i class=\"fa fa-plus\"></i>" + AbstractListaMapper.definirLabel(ctx)));
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
                        modal.setOnHideCallback(t -> t.focusComponent(this));
                        modal.showNew(target);
                    }
                }
            }
        };
    }

}
