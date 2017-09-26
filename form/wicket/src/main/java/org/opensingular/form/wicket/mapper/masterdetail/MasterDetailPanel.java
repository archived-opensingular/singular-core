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

import static org.apache.commons.lang3.StringUtils.*;
import static org.opensingular.lib.wicket.util.util.Shortcuts.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.opensingular.form.SFormUtil;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.type.basic.AtrBasic;
import org.opensingular.form.validation.ValidationError;
import org.opensingular.form.validation.ValidationErrorLevel;
import org.opensingular.form.view.SViewListByMasterDetail;
import org.opensingular.form.wicket.ISValidationFeedbackHandlerListener;
import org.opensingular.form.wicket.SValidationFeedbackHandler;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.component.SingularFormWicket;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.feedback.FeedbackFence;
import org.opensingular.form.wicket.feedback.SValidationFeedbackCompactPanel;
import org.opensingular.form.wicket.mapper.AbstractListMapper;
import org.opensingular.form.wicket.mapper.MapperCommons;
import org.opensingular.form.wicket.mapper.behavior.RequiredListLabelClassAppender;
import org.opensingular.form.wicket.mapper.common.util.ColumnType;
import org.opensingular.form.wicket.mapper.decorator.SInstanceActionsPanel;
import org.opensingular.form.wicket.mapper.decorator.SInstanceActionsProviders;
import org.opensingular.form.wicket.model.ISInstanceAwareModel;
import org.opensingular.form.wicket.model.SInstanceListItemModel;
import org.opensingular.form.wicket.util.WicketFormProcessing;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.lib.commons.lambda.IPredicate;
import org.opensingular.lib.commons.ui.Icon;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.datatable.BSDataTable;
import org.opensingular.lib.wicket.util.datatable.BSDataTableBuilder;
import org.opensingular.lib.wicket.util.datatable.BaseDataProvider;
import org.opensingular.lib.wicket.util.datatable.IBSAction;
import org.opensingular.lib.wicket.util.datatable.column.BSActionPanel;
import org.opensingular.lib.wicket.util.datatable.column.BSPropertyColumn;
import org.opensingular.lib.wicket.util.model.IMappingModel;
import org.opensingular.lib.wicket.util.model.IReadOnlyModel;
import org.opensingular.lib.wicket.util.resource.DefaultIcons;
import org.opensingular.lib.wicket.util.scripts.Scripts;
import org.opensingular.lib.wicket.util.util.JavaScriptUtils;
import org.opensingular.lib.wicket.util.util.WicketUtils;

public class MasterDetailPanel extends Panel {

    private final WicketBuildContext        ctx;
    private final IModel<SIList<SInstance>> lista;
    private final MasterDetailModal         modal;
    private final SViewListByMasterDetail   view;
    private final SInstanceActionsProviders instanceActionsProviders;

    private SingularFormWicket<?>           form;
    private WebMarkupContainer              head;
    private Label                           headLabel;
    private BSContainer<?>                  actionsContainer;
    private WebMarkupContainer              body;
    private Component                       table;
    private WebMarkupContainer              footer;
    private AjaxLink<?>                     addButton;
    private Label                           addButtonLabel;
    private SValidationFeedbackCompactPanel feedback;

    public MasterDetailPanel(String id, WicketBuildContext ctx, IModel<SIList<SInstance>> lista, MasterDetailModal modal, SViewListByMasterDetail view, SInstanceActionsProviders instanceActionsProviders) {
        super(id);
        this.ctx = ctx;
        this.lista = lista;
        this.modal = modal;
        this.view = view;
        this.instanceActionsProviders = instanceActionsProviders;

        createComponents();
        addComponents();
        addBehaviours();
    }

    private void addBehaviours() {
        footer.add($b.visibleIf(() -> AbstractListMapper.canAddItems(ctx)));
        addButton.add(WicketUtils.$b.attr("title", addButtonLabel.getDefaultModel()));
        addButton.setEscapeModelStrings(false);
    }

    private void addComponents() {
        add(form
            .add(head
                .add(headLabel)
                .add(actionsContainer))
            .add(body
                .add(table))
            .add(footer
                .add(addButton
                    .add(addButtonLabel)))
            .add(feedback));

        IFunction<AjaxRequestTarget, List<?>> internalContextListProvider = target -> Arrays.asList(
            this,
            RequestCycle.get().find(AjaxRequestTarget.class),
            ctx.getModel(),
            ctx.getModel().getObject(),
            ctx,
            ctx.getContainer());

        SInstanceActionsPanel.addPrimarySecondaryPanelsTo(
            actionsContainer,
            this.instanceActionsProviders,
            ctx.getModel(),
            true,
            internalContextListProvider);

    }

    private void createComponents() {
        form = new SingularFormWicket<>("form");
        head = new WebMarkupContainer("head");
        headLabel = newHeadLabel();
        actionsContainer = new BSContainer<>("actionsContainer");
        body = new WebMarkupContainer("body");
        footer = new WebMarkupContainer("footer");
        addButton = newAddAjaxLink();
        addButtonLabel = new Label("addButtonLabel", Model.of(AbstractListMapper.defineLabel(ctx)));
        table = newTable("table");
        feedback = ctx.createFeedbackCompactPanel("feedback");
    }

    private BSDataTable<SInstance, ?> newTable(String id) {

        final BSDataTableBuilder<SInstance, ?, ?> builder = new MasterDetailBSDataTableBuilder<>(newDataProvider()).withNoRecordsToolbar();
        final BSDataTable<SInstance, ?> dataTable;

        configureColumns(view.getColumns(), builder, lista, modal, ctx, ctx.getViewMode(), view);
        dataTable = builder.build(id);

        dataTable.setOnNewRowItem((IConsumer<Item<SInstance>>) rowItem -> {
            SValidationFeedbackHandler feedbackHandler = SValidationFeedbackHandler.bindTo(new FeedbackFence(rowItem))
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

        final AtrBasic attr = lista.getObject().asAtr();
        final IModel<String> labelModel = $m.ofValue(trimToEmpty(attr.getLabel()));

        ctx.configureContainer(labelModel);

        Label label = new Label("headLabel", labelModel);

        if (ctx.getViewMode() != null && ctx.getViewMode().isEdition()) {
            label.add(new RequiredListLabelClassAppender(ctx.getModel()));
        }
        return label;
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
                columnTypes.add(new ColumnType(tipo.getName(), null));
            } else if (tipo.isComposite()) {
                ((STypeComposite<?>) tipo)
                    .getFields()
                    .stream()
                    .filter(mtipo -> mtipo instanceof STypeSimple)
                    .forEach(mtipo -> columnTypes.add(new ColumnType(mtipo.getName(), null)));
            }
        } else {
            mapColumns.forEach((col) -> columnTypes.add(
                new ColumnType(
                    Optional.ofNullable(col.getTypeName())
                        .orElse(null),
                    col.getCustomLabel(), col.getDisplayValueFunction())));
        }

        for (ColumnType columnType : columnTypes) {
            final String label = columnType.getCustomLabel(model.getObject());
            final String typeName = columnType.getTypeName();
            final IModel<String> labelModel = $m.ofValue(label);
            propertyColumnAppender(builder, labelModel, $m.get(() -> typeName), columnType.getDisplayFunction());
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

            if (ctx.getAnnotationMode().enabled())
                ac.appendAction(buildShowAnnotationsActionConfig(model), buildViewOrEditAction(modal, ctx));
        });
    }

    private BSActionPanel.ActionConfig<SInstance> buildRemoveActionConfig() {
        return new BSActionPanel.ActionConfig<SInstance>()
            .styleClasses(Model.of("list-detail-remove"))
            .iconeModel(Model.of(DefaultIcons.REMOVE))
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
        final Icon openModalIcon = viewMode.isEdition() && view.isEditEnabled() ? DefaultIcons.PENCIL : DefaultIcons.EYE;
        return new BSActionPanel.ActionConfig<SInstance>()
            .iconeModel(Model.of(openModalIcon))
            .styleClasses(Model.of("list-detail-edit"))
            .titleFunction(rowModel -> viewMode.isEdition() && view.isEditEnabled() ? "Editar" : "Visualizar");
    }

    private IBSAction<SInstance> buildViewOrEditAction(MasterDetailModal modal, WicketBuildContext ctx) {
        return (target, rowModel) -> modal.showExisting(target, rowModel, ctx);
    }

    private BSActionPanel.ActionConfig<SInstance> buildShowErrorsActionConfig(IModel<? extends SInstance> model) {
        return new BSActionPanel.ActionConfig<SInstance>()
            .iconeModel(IReadOnlyModel.of(() -> DefaultIcons.EXCLAMATION_TRIANGLE))
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
                Collection<ValidationError> errors = baseInstance.getNestedValidationErrors();
                if ((errors != null) && !errors.isEmpty()) {
                    String alertLevel = errors.stream()
                        .map(ValidationError::getErrorLevel).max(Comparator.naturalOrder())
                        .map(it -> it.le(ValidationErrorLevel.WARNING) ? "alert-warning" : "alert-danger")
                        .orElse(null);

                    final StringBuilder sb = new StringBuilder("<div><ul class='list-unstyled alert ").append(alertLevel).append("'>");
                    for (ValidationError error : errors) {
                        Optional<SInstance> inst = doc.findInstanceById(error.getInstanceId());
                        inst.ifPresent(sInstance -> sb.append("<li>")
                            .append(SFormUtil.generateUserFriendlyPath(sInstance, baseInstance))
                            .append(": ")
                            .append(error.getMessage())
                            .append("</li>"));
                    }
                    sb.append("</ul></div>");

                    target.appendJavaScript(";bootbox.alert('" + JavaScriptUtils.javaScriptEscape(sb.toString()) + "');");
                    target.appendJavaScript(Scripts.multipleModalBackDrop());
                }
            }

            @Override
            public boolean isVisible(IModel<SInstance> model) {
                return model != null && model.getObject() != null && model.getObject().hasNestedValidationErrors();
            }
        };
    }

    private BSActionPanel.ActionConfig<SInstance> buildShowAnnotationsActionConfig(IModel<? extends SInstance> model) {
        IPredicate<SInstance> hasAnyRefusal = it -> it.asAtrAnnotation().hasAnyRefusal();
        IPredicate<SInstance> hasAnyAnnotable = it -> it.asAtrAnnotation().hasAnyAnnotable();
        IPredicate<SInstance> hasAnyAnnotation = it -> it.asAtrAnnotation().hasAnyAnnotationOnTree();
        //@formatter:off
        IFunction<SInstance, String> titleFunc = it -> 
            hasAnyRefusal   .test(it) ? "possui anotação rejeitada"
          : hasAnyAnnotation.test(it) ? "possui anotação"
          : hasAnyAnnotable .test(it) ? "possui anotável"
          : null;

        IModel<Icon> iconModel = IMappingModel.of(model).map(it -> 
            hasAnyRefusal   .test(it) ? Icon.of("annotation-icon annotation-icon-rejected")
          : hasAnyAnnotation.test(it) ? Icon.of("annotation-icon annotation-icon-approved")
          : hasAnyAnnotable .test(it) ? Icon.of("annotation-icon annotation-icon-empty")
          : null);
        //@formatter:on

        return new BSActionPanel.ActionConfig<SInstance>()
            .iconeModel(iconModel)
            .titleFunction(rowModel -> titleFunc.apply(rowModel.getObject()))
            .style($m.ofValue("line-height:1em; font-size: 1em;"));
    }

    /**
     * property column isolado em outro método para isolar o escopo de
     * serialização do lambda do appendPropertyColumn
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void propertyColumnAppender(BSDataTableBuilder<SInstance, ?, ?> builder,
        IModel<String> labelModel, IModel<String> sTypeNameModel,
        IFunction<SInstance, String> displayValueFunction) {
        IFunction<SIComposite, SInstance> toInstance = composto -> {
            String sTypeName = sTypeNameModel.getObject();
            if (sTypeName == null || composto == null) {
                return composto;
            }
            SType<?> sType = composto.getDictionary().getType(sTypeName);
            return (SInstance) composto.findDescendant(sType).orElse(null);
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
                    public SInstance getSInstance() {
                        return toInstance.apply((SIComposite) rowModel.getObject());
                    }
                };
            }
        });
    }

    private BaseDataProvider<SInstance, ?> newDataProvider() {
        return new SIListDataProvider(lista);
    }

    static class SIListDataProvider extends BaseDataProvider<SInstance, Object> {
        private final IModel<SIList<SInstance>> lista;
        public SIListDataProvider(IModel<SIList<SInstance>> lista) {
            this.lista = lista;
        }

        @Override
        public Iterator<SInstance> iterator(int first, int count, Object sortProperty, boolean ascending) {
            final SIList<SInstance> siList = lista.getObject();
            final List<SInstance> list = new ArrayList<>();
            for (int i = 0; (i < count) && (i + first < siList.size()); i++)
                list.add(siList.get(i + first));
            return list.iterator();
        }

        @Override
        public long size() {
            return lista.getObject().size();
        }

        @Override
        public IModel<SInstance> model(SInstance object) {
            return new SInstanceListItemModel<>(lista, lista.getObject().indexOf(object));
        }
    }
}