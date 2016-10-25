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

package org.opensingular.form.wicket.mapper.masterdetail;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.opensingular.form.*;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.type.basic.AtrBasic;
import org.opensingular.form.validation.IValidationError;
import org.opensingular.form.validation.ValidationErrorLevel;
import org.opensingular.form.view.SViewListByMasterDetail;
import org.opensingular.form.wicket.ISValidationFeedbackHandlerListener;
import org.opensingular.form.wicket.SValidationFeedbackHandler;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.component.SingularForm;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.feedback.SValidationFeedbackCompactPanel;
import org.opensingular.form.wicket.mapper.AbstractListaMapper;
import org.opensingular.form.wicket.mapper.MapperCommons;
import org.opensingular.form.wicket.model.ISInstanceAwareModel;
import org.opensingular.form.wicket.model.SInstanceListItemModel;
import org.opensingular.form.wicket.model.STypeModel;
import org.opensingular.form.wicket.util.WicketFormProcessing;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.lib.wicket.util.datatable.BSDataTable;
import org.opensingular.lib.wicket.util.datatable.BSDataTableBuilder;
import org.opensingular.lib.wicket.util.datatable.BaseDataProvider;
import org.opensingular.lib.wicket.util.datatable.IBSAction;
import org.opensingular.lib.wicket.util.datatable.column.BSActionPanel;
import org.opensingular.lib.wicket.util.datatable.column.BSPropertyColumn;
import org.opensingular.lib.wicket.util.model.IMappingModel;
import org.opensingular.lib.wicket.util.model.IReadOnlyModel;
import org.opensingular.lib.wicket.util.resource.Icone;
import org.opensingular.lib.wicket.util.scripts.Scripts;
import org.opensingular.lib.wicket.util.util.JavaScriptUtils;
import org.opensingular.lib.wicket.util.util.WicketUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.opensingular.lib.wicket.util.util.Shortcuts.$b;
import static org.opensingular.lib.wicket.util.util.Shortcuts.$m;


public class MasterDetailPanel extends Panel {

    private final WicketBuildContext        ctx;
    private final IModel<SIList<SInstance>> lista;
    private final MasterDetailModal         modal;
    private final SViewListByMasterDetail   view;

    private SingularForm<?>                 form;
    private WebMarkupContainer              head;
    private Label                           headLabel;
    private WebMarkupContainer              body;
    private Component                       table;
    private WebMarkupContainer              footer;
    private AjaxLink                        addButton;
    private Label                           addButtonLabel;
    private SValidationFeedbackCompactPanel feedback;

    public MasterDetailPanel(String id, WicketBuildContext ctx, IModel<SIList<SInstance>> lista, MasterDetailModal modal, SViewListByMasterDetail view) {
        super(id);
        this.ctx = ctx;
        this.lista = lista;
        this.modal = modal;
        this.view = view;
        createComponents();
        addComponents();
        addBehaviours();
    }

    private void addBehaviours() {
        footer.add($b.visibleIf(() -> AbstractListaMapper.canAddItems(ctx)));
        addButton.add(WicketUtils.$b.attr("title", addButtonLabel.getDefaultModel()));
        addButton.setEscapeModelStrings(false);
    }

    private void addComponents() {
        add(form);
        form.add(head.add(headLabel));
        form.add(body.add(table));
        form.add(footer.add(addButton.add(addButtonLabel)));
        form.add(feedback);
    }

    private void createComponents() {
        form = new SingularForm<>("form");
        head = new WebMarkupContainer("head");
        headLabel = newHeadLabel();
        body = new WebMarkupContainer("body");
        footer = new WebMarkupContainer("footer");
        addButton = newAddAjaxLink();
        addButtonLabel = new Label("addButtonLabel", Model.of(AbstractListaMapper.defineLabel(ctx)));
        table = newTable("table");
        feedback = ctx.createFeedbackCompactPanel("feedback");
    }

    private BSDataTable<SInstance, ?> newTable(String id) {

        final BSDataTableBuilder<SInstance, ?, ?> builder = new MasterDetailBSDataTableBuilder<>(newDataProvider()).withNoRecordsToolbar();
        final BSDataTable<SInstance, ?>           dataTable;

        configureColumns(view.getColumns(), builder, lista, modal, ctx, ctx.getViewMode(), view);
        dataTable = builder.build(id);

        dataTable.setOnNewRowItem((IConsumer<Item<SInstance>>) rowItem -> {
            SValidationFeedbackHandler feedbackHandler = SValidationFeedbackHandler.bindTo(rowItem)
                    .addInstanceModel(rowItem.getModel())
                    .addListener(ISValidationFeedbackHandlerListener.withTarget(t -> t.add(rowItem)));
            rowItem.add($b.classAppender("singular-form-table-row can-have-error"));
            rowItem.add($b.classAppender("has-errors", $m.ofValue(feedbackHandler).map(SValidationFeedbackHandler::containsNestedErrors)));
        });

        dataTable.setStripedRows(false);
        dataTable.setHoverRows(false);
        dataTable.setBorderedTable(false);

        return dataTable;
    }

    private Label newHeadLabel() {
        AtrBasic       attr       = lista.getObject().asAtr();
        IModel<String> labelModel = $m.ofValue(trimToEmpty(attr.getLabel()));
        ctx.configureContainer(labelModel);
        return new Label("headLabel", labelModel);
    }

    private AjaxLink<String> newAddAjaxLink() {
        return new AjaxLink<String>("addButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                final SInstance si = ctx.getModel().getObject();
                if (si instanceof SIList) {
                    final SIList<?> sil = (SIList<?>) si;
                    if (sil.getType().getMaximumSize() != null && sil.getType().getMaximumSize() == sil.size()) {
                        target.appendJavaScript(";bootbox.alert('A Quantidade máxima de valores foi atingida.');");
                        target.appendJavaScript(Scripts.multipleModalBackDrop());
                    } else {
                        modal.setOnHideCallback(t -> t.focusComponent(this));
                        modal.showNew(target);
                    }
                }
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
            final String         label      = columnType.getCustomLabel();
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

    private BSActionPanel.ActionConfig<SInstance> buildRemoveActionConfig() {
        return new BSActionPanel.ActionConfig<SInstance>()
                .styleClasses(Model.of("list-detail-remove"))
                .iconeModel(Model.of(Icone.REMOVE))
                .titleFunction(rowModel -> "Remover");
    }

    private IBSAction<SInstance> buildRemoveAction(IModel<? extends SInstance> model, WicketBuildContext ctx) {
        return (target, rowModel) -> {
            final SIList<?> list = ((SIList<?>) model.getObject());
            list.remove(list.indexOf(rowModel.getObject()));
            target.add(ctx.getContainer());
            WicketFormProcessing.onFieldProcess(form, target, model);
        };
    }

    private BSActionPanel.ActionConfig<SInstance> buildViewOrEditActionConfig(ViewMode viewMode, SViewListByMasterDetail view) {
        final Icone openModalIcon = viewMode.isEdition() && view.isEditEnabled() ? Icone.PENCIL : Icone.EYE;
        return new BSActionPanel.ActionConfig<SInstance>()
                .iconeModel(Model.of(openModalIcon))
                .styleClasses(Model.of("list-detail-edit"))
                .titleFunction(rowModel -> viewMode.isEdition() && view.isEditEnabled() ? "Editar" : "Visualizar");
    }

    private IBSAction<SInstance> buildViewOrEditAction(MasterDetailModal modal, WicketBuildContext ctx) {
        return (target, rowModel) -> modal.showExisting(target, rowModel, ctx);
    }

    private BSActionPanel.ActionConfig<SInstance> buildShowErrorsActionConfig(IModel<? extends SInstance> model) {
        Integer count = IMappingModel.of(model).map(it -> it.getNestedValidationErrors().size()).getObject();
        if (count > 0)
            System.out.println(count);
        return new BSActionPanel.ActionConfig<SInstance>()
                .iconeModel(IReadOnlyModel.of(() -> Icone.EXCLAMATION_TRIANGLE))
                .styleClasses(Model.of("red"))
                .titleFunction(rowModel -> IMappingModel.of(rowModel).map(it -> (it.getNestedValidationErrors().size() + " erro(s) encontrado(s)")).getObject())
                .style($m.ofValue(MapperCommons.BUTTON_STYLE));
    }

    private IBSAction<SInstance> buildShowErrorsAction() {
        return new IBSAction<SInstance>() {
            @Override
            public void execute(AjaxRequestTarget target, IModel<SInstance> model) {
                SInstance                    baseInstance = model.getObject();
                SDocument                    doc          = baseInstance.getDocument();
                Collection<IValidationError> errors       = baseInstance.getNestedValidationErrors();
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

                    target.appendJavaScript(";bootbox.alert('" + JavaScriptUtils.javaScriptEscape(sb.toString()) + "');");
                    target.appendJavaScript(Scripts.multipleModalBackDrop());
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
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void propertyColumnAppender(BSDataTableBuilder<SInstance, ?, ?> builder,
                                        IModel<String> labelModel, IModel<SType<?>> mTipoModel,
                                        IFunction<SInstance, String> displayValueFunction) {
        IFunction<SIComposite, SInstance> toInstance = composto -> {
            SType<?> mtipo = mTipoModel.getObject();
            if (mtipo == null) {
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
    }


    private BaseDataProvider<SInstance, ?> newDataProvider() {
        return new BaseDataProvider<SInstance, Object>() {
            @Override
            public Iterator<SInstance> iterator(int first, int count, Object sortProperty, boolean ascending) {
                return lista.getObject().iterator();
            }

            @Override
            public long size() {
                return lista.getObject().size();
            }

            @Override
            public IModel<SInstance> model(SInstance object) {
                return new SInstanceListItemModel<>(lista, lista.getObject().indexOf(object));
            }
        };
    }

}