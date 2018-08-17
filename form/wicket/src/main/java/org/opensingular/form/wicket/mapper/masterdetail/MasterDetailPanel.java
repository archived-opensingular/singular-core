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
import org.opensingular.form.view.list.ButtonAction;
import org.opensingular.form.view.list.SViewListByMasterDetail;
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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
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
        final ISupplier<SViewListByMasterDetail> viewSupplier = ctx.getViewSupplier(SViewListByMasterDetail.class);
        final BSDataTableBuilder<SInstance, String, ?> builder = new MasterDetailBSDataTableBuilder<>(newDataProvider(viewSupplier)).withNoRecordsToolbar();
        final BSDataTable<SInstance, ?> dataTable;

        configureColumns(viewSupplier.get().getColumns(), builder, modal, ctx, ctx.getViewMode(), viewSupplier);
        dataTable = builder.build(id);

        dataTable.setOnNewRowItem((IConsumer<Item<SInstance>>) rowItem -> {
            rowItem.setOutputMarkupId(true);
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
                final SInstance si = MasterDetailPanel.this.ctx.getModel().getObject();
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

    /**
     * This method verify if the user has permission to create a new element in the list.
     * Note: If can create a new element, we will be always enabled to edit.
     *
     * @param viewSupplier The view.
     * @return True if can create new element.
     */
    private boolean canCreateNewElement(ISupplier<SViewListByMasterDetail> viewSupplier) {
        return viewSupplier.get().isNewEnabled(list.getObject());
    }

    private void configureColumns(
            List<SViewListByMasterDetail.Column> mapColumns,
            BSDataTableBuilder<SInstance, String, ?> builder,
            MasterDetailModal modal,
            WicketBuildContext ctx,
            ViewMode viewMode,
            ISupplier<SViewListByMasterDetail> viewSupplier) {

        final List<ColumnType> columnTypes = new ArrayList<>();

        if (mapColumns.isEmpty()) {
            final SType<?> type = list.getObject().getElementsType();
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
            mapColumns.forEach(col -> columnTypes.add(
                    new ColumnType(
                            Optional.ofNullable(col.getTypeName())
                                    .orElse(null),
                            col.getCustomLabel(), col.getColumnSortName(), col.getDisplayValueFunction())));
        }

        final boolean disabledSort = viewSupplier.get().isDisableSort();
        for (ColumnType columnType : columnTypes) {
            final String label = columnType.getCustomLabel(list.getObject());
            final String typeName = columnType.getTypeName();
            final String columnSort = disabledSort ? null : columnType.getColumnSortName();
            final IModel<String> labelModel = $m.ofValue(label);
            propertyColumnAppender(builder, labelModel, $m.get(() -> typeName), columnSort, columnType.getDisplayFunction());
        }

        actionColumnAppender(builder, modal, ctx, viewMode, viewSupplier);
    }

    /**
     * Adiciona as ações a coluna de ações de mestre detalhe.
     */
    private void actionColumnAppender(BSDataTableBuilder<SInstance, ?, ?> builder,
                                      MasterDetailModal modal,
                                      WicketBuildContext ctx,
                                      ViewMode vm,
                                      ISupplier<SViewListByMasterDetail> viewSupplier) {
        if (canCreateNewElement(viewSupplier) || viewSupplier.get().haveAnyActionButton(list.getObject())) {
            //If user can create new element must have at last one action, probably edit.
            builder.appendActionColumn($m.ofValue(viewSupplier.get().getActionColumnLabel()), ac -> {
                if (vm.isEdition()) {
                    ac.appendAction(buildEditActionConfig(viewSupplier), buildViewOrEditAction(modal, ctx, null));
                    ac.appendAction(buildRemoveActionConfig(viewSupplier), buildRemoveAction(ctx));
                }
                ac.appendAction(buildViewActionConfig(vm, viewSupplier), buildViewOrEditAction(modal, ctx, ViewMode.READ_ONLY));
                ac.appendAction(buildShowErrorsActionConfig(), new ShowErrorsAction());
                if (ctx.getAnnotationMode().enabled()) {
                    ac.appendAction(buildShowAnnotationsActionConfig(), buildViewOrEditAction(modal, ctx, null));
                }
            });
        }
    }

    private BSActionPanel.ActionConfig<SInstance> buildRemoveActionConfig(ISupplier<SViewListByMasterDetail> viewSupplier) {
        ButtonAction buttonDelete = viewSupplier.get().getButtonsConfig().getDeleteButton();

        final Icon actionIcon = buttonDelete.getIcon() != null ? buttonDelete.getIcon() : DefaultIcons.REMOVE;
        return new BSActionPanel.ActionConfig<SInstance>()
                .styleClasses(Model.of("list-detail-remove"))
                .iconeModel(Model.of(actionIcon))
                .titleFunction(rowModel -> buttonDelete.getHint())
                .labelModel($m.ofValue("Remover"))
                .visibleFor(m -> buttonDelete.isEnabled(m.getObject()));
    }

    private IBSAction<SInstance> buildRemoveAction(WicketBuildContext ctx) {
        return (target, rowModel) -> {
            IConsumer<AjaxRequestTarget> confirmationAction = t -> {
                list.getObject().remove(list.getObject().indexOf(rowModel.getObject()));
                t.add(ctx.getContainer());
                WicketFormProcessing.onFieldProcess(MasterDetailPanel.this.form, t, list);
            };
            confirmationModal.show(target, confirmationAction);
        };
    }

    private BSActionPanel.ActionConfig<SInstance> buildEditActionConfig(ISupplier<SViewListByMasterDetail> viewSupplier) {
        ButtonAction buttonEdit = viewSupplier.get().getButtonsConfig().getEditButton();

        final Icon actionIcon = buttonEdit.getIcon() != null ? buttonEdit.getIcon() : DefaultIcons.PENCIL;
        return new BSActionPanel.ActionConfig<SInstance>()
                .iconeModel(Model.of(actionIcon))
                .styleClasses(Model.of("list-detail-edit"))
                .visibleFor(instance -> buttonEdit.isEnabled(instance.getObject()))
                .titleFunction(rowModel -> buttonEdit.getHint());
    }

    private BSActionPanel.ActionConfig<SInstance> buildViewActionConfig(ViewMode vm, ISupplier<SViewListByMasterDetail> viewSupplier) {
        ButtonAction buttonView = viewSupplier.get().getButtonsConfig().getViewButtonInEdition();

        final Icon actionIcon = buttonView.getIcon() != null ? buttonView.getIcon() : DefaultIcons.EYE;
        return new BSActionPanel.ActionConfig<SInstance>()
                .iconeModel(Model.of(actionIcon))
                .styleClasses(Model.of("list-detail-edit"))
                .visibleFor(modelInstance -> vm.isVisualization() || buttonView.isEnabled(modelInstance.getObject()))
                .titleFunction(rowModel -> buttonView.getHint());
    }

    /**
     * Method to create a action to show modal for the rowModel.
     *
     * @param modal    The modal to be showing.
     * @param ctx      The context.
     * @param viewMode The viewMode, this is useful for force READ_ONLY case.
     *                 If it's null, it will use a rule of view to get the viewMode.
     * @return Instance of BiConsumer action.
     */
    private IBSAction<SInstance> buildViewOrEditAction(MasterDetailModal modal, WicketBuildContext ctx, @Nullable ViewMode viewMode) {
        return (target, rowModel) -> modal.showExisting(target, rowModel, ctx, viewMode);
    }

    private BSActionPanel.ActionConfig<SInstance> buildShowErrorsActionConfig() {
        return new BSActionPanel.ActionConfig<SInstance>()
                .iconeModel(IReadOnlyModel.of(() -> DefaultIcons.EXCLAMATION_TRIANGLE))
                .styleClasses(Model.of("red"))
                .titleFunction(rowModel -> IMappingModel.of(rowModel).map(it -> it.getNestedValidationErrors().size() + " erro(s) encontrado(s)").getObject())
                .visibleFor(rowModel -> !rowModel.getObject().getNestedValidationErrors().isEmpty())
                .style($m.ofValue(MapperCommons.BUTTON_STYLE));
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
        IFunction<SInstance, Object> propertyFunction = o -> o instanceof SIComposite ? displayValueFunction.apply(toInstance.apply((SIComposite) o)) : o;

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
                        return rowModel.getObject() instanceof SIComposite ? toInstance.apply((SIComposite) rowModel.getObject()) : (SInstance) rowModel.getObject();
                    }
                };
            }
        });
    }

    private BaseDataProvider<SInstance, String> newDataProvider(ISupplier<SViewListByMasterDetail> viewSupplier) {
        return new MasterDetailDataProvider(list, viewSupplier);
    }

    private class ShowErrorsAction implements IBSAction<SInstance> {
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
    }

}