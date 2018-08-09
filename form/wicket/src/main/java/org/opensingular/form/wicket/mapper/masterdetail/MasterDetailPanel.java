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

import org.apache.commons.lang3.StringUtils;
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
import org.opensingular.form.type.core.SIComparable;
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
import org.opensingular.form.wicket.mapper.components.ConfirmationModal;
import org.opensingular.form.wicket.mapper.decorator.SInstanceActionsPanel;
import org.opensingular.form.wicket.mapper.decorator.SInstanceActionsProviders;
import org.opensingular.form.wicket.model.ISInstanceAwareModel;
import org.opensingular.form.wicket.model.SInstanceListItemModel;
import org.opensingular.form.wicket.util.WicketFormProcessing;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.lib.commons.lambda.IPredicate;
import org.opensingular.lib.commons.lambda.ISupplier;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.opensingular.form.wicket.IWicketComponentMapper.HIDE_LABEL;
import static org.opensingular.lib.wicket.util.util.Shortcuts.$b;
import static org.opensingular.lib.wicket.util.util.Shortcuts.$m;

public class MasterDetailPanel extends Panel {

    private final WicketBuildContext ctx;
    private final IModel<SIList<SInstance>> list;
    private final MasterDetailModal modal;
    private final SInstanceActionsProviders instanceActionsProviders;

    private SingularFormWicket<?> form;
    private WebMarkupContainer head;
    private Label headLabel;
    private BSContainer<?> actionsContainer;
    private WebMarkupContainer body;
    private Component table;
    private WebMarkupContainer footer;
    private AjaxLink<?> addButton;
    private Label addButtonLabel;
    private SValidationFeedbackCompactPanel feedback;
    private ConfirmationModal confirmationModal;

    public MasterDetailPanel(String id, WicketBuildContext ctx, IModel<SIList<SInstance>> list, MasterDetailModal modal,
                             SInstanceActionsProviders instanceActionsProviders) {
        super(id);
        this.ctx = ctx;
        this.list = list;
        this.modal = modal;
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
                false,
                internalContextListProvider);

    }

    private void createComponents() {
        form = new SingularFormWicket<>("form");
        head = newHead("head");
        headLabel = newHeadLabel();
        actionsContainer = new BSContainer<>("actionsContainer");
        body = new WebMarkupContainer("body");
        footer = new WebMarkupContainer("footer");
        addButton = newAddAjaxLink();
        addButtonLabel = new Label("addButtonLabel", Model.of(AbstractListMapper.defineLabel(ctx)));
        table = newTable("table");
        feedback = ctx.createFeedbackCompactPanel("feedback");
        confirmationModal = ctx.getExternalContainer().newComponent(ConfirmationModal::new);

    }

    private WebMarkupContainer newHead(String id) {
        WebMarkupContainer thisHead = new WebMarkupContainer(id);
        thisHead.add($b.visibleIf(() -> !ctx.getHint(HIDE_LABEL)
                || !this.instanceActionsProviders.actionList(this.list).isEmpty()));
        return thisHead;
    }

    private BSDataTable<SInstance, ?> newTable(String id) {

        final BSDataTableBuilder<SInstance, String, ?> builder = new MasterDetailBSDataTableBuilder<>(newDataProvider()).withNoRecordsToolbar();
        final BSDataTable<SInstance, ?> dataTable;

        ISupplier<SViewListByMasterDetail> viewSupplier = ctx.getViewSupplier(SViewListByMasterDetail.class);
        configureColumns(viewSupplier.get().getColumns(), builder, list, modal, ctx, ctx.getViewMode(), viewSupplier);
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

        final AtrBasic attr = list.getObject().asAtr();
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
                        target.appendJavaScript(";bootbox.alert('A quantidade máxima de valores foi atingida.');");
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
            BSDataTableBuilder<SInstance, String, ?> builder,
            IModel<? extends SInstance> model,
            MasterDetailModal modal,
            WicketBuildContext ctx,
            ViewMode viewMode,
            ISupplier<SViewListByMasterDetail> viewSupplier) {

        final List<ColumnType> columnTypes = new ArrayList<>();

        if (mapColumns.isEmpty()) {
            final SType<?> type = ((SIList<?>) model.getObject()).getElementsType();
            if (type instanceof STypeSimple) {
                columnTypes.add(new ColumnType(type.getName(), null, type.getNameSimple()));
            } else if (type.isComposite()) {
                ((STypeComposite<?>) type)
                        .getFields()
                        .stream()
                        .filter(sType -> sType instanceof STypeSimple)
                        .forEach(sType -> columnTypes.add(new ColumnType(sType.getName(), null, sType.getNameSimple())));
            }
        } else {
            mapColumns.forEach((col) -> columnTypes.add(
                    new ColumnType(
                            Optional.ofNullable(col.getTypeName())
                                    .orElse(null),
                            col.getCustomLabel(), col.getColumnSortName(), col.getDisplayValueFunction())));
        }

        for (ColumnType columnType : columnTypes) {
            final String label = columnType.getCustomLabel(model.getObject());
            final String typeName = columnType.getTypeName();
            final String columnSort = columnType.getColumnSortName();
            final IModel<String> labelModel = $m.ofValue(label);
            propertyColumnAppender(builder, labelModel, $m.get(() -> typeName), columnSort, columnType.getDisplayFunction());
        }

        actionColumnAppender(builder, model, modal, ctx, viewMode, viewSupplier);
    }

    /**
     * Adiciona as ações a coluna de ações de mestre detalhe.
     */
    private void actionColumnAppender(BSDataTableBuilder<SInstance, ?, ?> builder,
                                      IModel<? extends SInstance> model,
                                      MasterDetailModal modal,
                                      WicketBuildContext ctx,
                                      ViewMode vm,
                                      ISupplier<SViewListByMasterDetail> viewSupplier) {
        builder.appendActionColumn($m.ofValue(viewSupplier.get().getActionColumnLabel()), ac -> {
            ac.appendAction(buildViewOrEditActionConfig(vm, viewSupplier), buildViewOrEditAction(modal, ctx));
            if (vm.isEdition()) {
                ac.appendAction(buildRemoveActionConfig(viewSupplier), buildRemoveAction(model, ctx));
            }
            ac.appendAction(buildShowErrorsActionConfig(model), buildShowErrorsAction());
            if (ctx.getAnnotationMode().enabled())
                ac.appendAction(buildShowAnnotationsActionConfig(), buildViewOrEditAction(modal, ctx));
        });
    }

    private BSActionPanel.ActionConfig<SInstance> buildRemoveActionConfig(ISupplier<SViewListByMasterDetail> viewSupplier) {
        return new BSActionPanel.ActionConfig<SInstance>()
                .styleClasses(Model.of("list-detail-remove"))
                .iconeModel(Model.of(DefaultIcons.REMOVE))
                .titleFunction(rowModel -> "Remover")
                .labelModel($m.ofValue("Remover"))
                .visibleFor(m -> viewSupplier.get().isDeleteEnabled(m.getObject()));
    }

    private IBSAction<SInstance> buildRemoveAction(IModel<? extends SInstance> model, WicketBuildContext ctx) {
        return (target, rowModel) -> {
            IConsumer<AjaxRequestTarget> confirmationAction = t -> {
                final SIList<?> list = ((SIList<?>) model.getObject());
                list.remove(list.indexOf(rowModel.getObject()));
                t.add(ctx.getContainer());
                WicketFormProcessing.onFieldProcess(MasterDetailPanel.this.form, t, model);
            };
            //            target.add(confirmationModal);
            confirmationModal.show(target, confirmationAction);
        };
    }

    private BSActionPanel.ActionConfig<SInstance> buildViewOrEditActionConfig(ViewMode viewMode, ISupplier<SViewListByMasterDetail> viewSupplier) {
        final Icon openModalIcon = viewMode.isEdition() && viewSupplier.get().isEditEnabled() ? DefaultIcons.PENCIL : DefaultIcons.EYE;
        return new BSActionPanel.ActionConfig<SInstance>()
                .iconeModel(Model.of(openModalIcon))
                .styleClasses(Model.of("list-detail-edit"))
                .titleFunction(rowModel -> viewMode.isEdition() && viewSupplier.get().isEditEnabled() ? "Editar" : "Visualizar");
    }

    private IBSAction<SInstance> buildViewOrEditAction(MasterDetailModal modal, WicketBuildContext ctx) {
        return (target, rowModel) -> modal.showExisting(target, rowModel, ctx);
    }

    private BSActionPanel.ActionConfig<SInstance> buildShowErrorsActionConfig(IModel<? extends SInstance> model) {
        return new BSActionPanel.ActionConfig<SInstance>()
                .iconeModel(IReadOnlyModel.of(() -> DefaultIcons.EXCLAMATION_TRIANGLE))
                .styleClasses(Model.of("red"))
                .titleFunction(rowModel -> IMappingModel.of(rowModel).map(it -> (it.getNestedValidationErrors().size() + " erro(s) encontrado(s)")).getObject())
                .visibleFor(rowModel -> !rowModel.getObject().getNestedValidationErrors().isEmpty())
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

    private BSActionPanel.ActionConfig<SInstance> buildShowAnnotationsActionConfig() {
        IPredicate<SInstance> hasAnyRefusal = it -> it.asAtrAnnotation().hasAnyRefusal();
        IPredicate<SInstance> hasAnyAnnotable = it -> it.asAtrAnnotation().hasAnyAnnotable();
        IPredicate<SInstance> hasAnyAnnotation = it -> it.asAtrAnnotation().hasAnyAnnotationOnTree();
        //@formatter:off
        IFunction<SInstance, String> titleFunc = it ->
                hasAnyRefusal.test(it) ? "Possui anotação rejeitada"
                        : hasAnyAnnotation.test(it) ? "Possui anotação"
                        : hasAnyAnnotable.test(it) ? "Possui anotável"
                        : null;

        IFunction<SInstance, IModel<Icon>> iconFunc = it ->
                $m.ofValue(
                        hasAnyRefusal.test(it) ? Icon.of("annotation-icon annotation-icon-rejected")
                                : hasAnyAnnotation.test(it) ? Icon.of("annotation-icon annotation-icon-approved")
                                : hasAnyAnnotable.test(it) ? Icon.of("annotation-icon annotation-icon-empty")
                                : null
                );
        //@formatter:on

        return new BSActionPanel.ActionConfig<SInstance>()
                .iconFunction(rowModel -> iconFunc.apply(rowModel.getObject()))
                .titleFunction(rowModel -> titleFunc.apply(rowModel.getObject()))
                .style($m.ofValue("line-height:1em; font-size: 1em;"));
    }

    /**
     * property column isolado em outro método para isolar o escopo de
     * serialização do lambda do appendPropertyColumn
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void propertyColumnAppender(BSDataTableBuilder<SInstance, String, ?> builder,
                                        IModel<String> labelModel, IModel<String> sTypeNameModel, String columnSortName,
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
        builder.appendColumn(new BSPropertyColumn<SInstance, String>(labelModel, columnSortName, propertyFunction) {
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

    private BaseDataProvider<SInstance, String> newDataProvider() {
        return new SIListDataProvider(list);
    }

    static class SIListDataProvider extends BaseDataProvider<SInstance, String> {
        private final IModel<SIList<SInstance>> list;

        public SIListDataProvider(IModel<SIList<SInstance>> list) {
            this.list = list;
        }

        @Override
        public Iterator<SInstance> iterator(int first, int count, String sortProperty, boolean ascending) {
            final SIList<SInstance> siList = list.getObject();
            final List<SInstance> list = new ArrayList<>();

            if (StringUtils.isNotEmpty(sortProperty)) {
                siList.getValues().sort(new Comparator<SInstance>() {
                    @Override
                    public int compare(SInstance instanceList1, SInstance instanceList2) {
                        Optional<SInstance> obj1 = getObjectBySortProperty(instanceList1);
                        Optional<SInstance> obj2 = getObjectBySortProperty(instanceList2);

                        if (obj1.isPresent() && obj2.isPresent()
                                && obj1.get() instanceof SIComparable
                                && obj2.get() instanceof SIComparable) {
                            if (ascending) {
                                return ((SIComparable) obj1.get()).compareTo((SIComparable) obj2.get());
                            } else {
                                return ((SIComparable) obj2.get()).compareTo((SIComparable) obj1.get());
                            }
                        }
                        return 0;
                    }

                    private Optional<SInstance> getObjectBySortProperty(SInstance instance) {
                        if (instance != null && instance.getValue() instanceof ArrayList) {
                            return (Optional<SInstance>) ((ArrayList) instance.getValue())
                                    .parallelStream()
                                    .filter(i -> ((SInstance) i).getType().getNameSimple().equals(sortProperty))
                                    .findFirst();
                        }
                        return Optional.empty();
                    }
                });
            }

            for (int i = 0; (i < count) && (i + first < siList.size()); i++) {
                list.add(siList.get(i + first));
            }

            return list.iterator();
        }

        @Override
        public long size() {
            return list.getObject().size();
        }

        @Override
        public IModel<SInstance> model(SInstance object) {
            return new SInstanceListItemModel<>(list, list.getObject().indexOf(object));
        }
    }
}