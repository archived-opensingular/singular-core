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

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.decorator.action.ISInstanceActionCapable;
import org.opensingular.form.decorator.action.ISInstanceActionsProvider;
import org.opensingular.form.view.list.SViewListByTable;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.mapper.behavior.RequiredLabelClassAppender;
import org.opensingular.form.wicket.mapper.buttons.ElementsView;
import org.opensingular.form.wicket.mapper.components.ConfirmationModal;
import org.opensingular.form.wicket.mapper.decorator.SInstanceActionsPanel;
import org.opensingular.form.wicket.mapper.decorator.SInstanceActionsProviders;
import org.opensingular.form.wicket.mapper.tablelist.TableElementsView;
import org.opensingular.form.wicket.mapper.tablelist.TableListPanel;
import org.opensingular.form.wicket.model.ReadOnlyCurrentInstanceModel;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.bootstrap.layout.TemplatePanel;
import org.opensingular.lib.wicket.util.bootstrap.layout.table.BSTDataCell;
import org.opensingular.lib.wicket.util.bootstrap.layout.table.BSTRow;
import org.opensingular.lib.wicket.util.bootstrap.layout.table.BSTSection;
import org.opensingular.lib.wicket.util.util.Shortcuts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.opensingular.form.wicket.mapper.components.MetronicPanel.dependsOnModifier;
import static org.opensingular.lib.wicket.util.util.Shortcuts.$b;
import static org.opensingular.lib.wicket.util.util.Shortcuts.$m;

public class TableListMapper extends AbstractListMapper implements ISInstanceActionCapable {

    private SInstanceActionsProviders instanceActionsProviders = new SInstanceActionsProviders(this);
    public ConfirmationModal          confirmationModal;

    @Override
    public void addSInstanceActionsProvider(int sortPosition, ISInstanceActionsProvider provider) {
        this.instanceActionsProviders.addSInstanceActionsProvider(sortPosition, provider);
    }

    @Override
    public void buildView(WicketBuildContext ctx) {

        if (!(ctx.getView() instanceof SViewListByTable)) { throw new SingularFormException("TableListMapper deve ser utilizado com SViewListByTable", ctx.getCurrentInstance()); }

        if (!(ctx.getCurrentInstance() instanceof SIList)) { return; }

        ctx.setHint(AbstractControlsFieldComponentMapper.NO_DECORATION, Boolean.TRUE);
        confirmationModal = ctx.getExternalContainer().newComponent(ConfirmationModal::new);
        ctx.getContainer().appendComponent((String id) -> buildPanel(ctx, id));
    }

    protected TableListPanel buildPanel(WicketBuildContext ctx, String id) {

        final IModel<SIList<SInstance>> listModel = new ReadOnlyCurrentInstanceModel<>(ctx);
        final ViewMode viewMode = ctx.getViewMode();
        final Boolean isEdition = viewMode == null || viewMode.isEdition();
        final SIList<SInstance> iList = listModel.getObject();
        final SType<?> currentType = ctx.getCurrentInstance().getType();

        addInitialNumberOfLines(currentType, iList, ctx.getViewSupplier(SViewListByTable.class));
        return TableListPanel.TableListPanelBuilder.build(id,
            (h, form) -> buildHeader(h, listModel, ctx),
            (c, form) -> buildContent(c, form, listModel, ctx, isEdition, confirmationModal),
            (f, form) -> buildFooter(f, form, listModel, ctx));
    }

    private void buildHeader(BSContainer<?> header, IModel<SIList<SInstance>> list,
        WicketBuildContext ctx) {

        final IModel<String> label = $m.ofValue(ctx.getCurrentInstance().getType().asAtr().getLabel());
        final Label title = new Label("_title", label);
        title.add($b.visibleIfModelObject(StringUtils::isNotBlank));

        ctx.configureContainer(label);

        header.appendTag("span", title);

        IFunction<AjaxRequestTarget, List<?>> internalContextListProvider = target -> Arrays.asList(
            this,
            RequestCycle.get().find(AjaxRequestTarget.class),
            list,
            list.getObject(),
            ctx,
            ctx.getContainer());

        SInstanceActionsPanel.addPrimarySecondaryPanelsTo(
            header,
            this.instanceActionsProviders,
            list,
            false,
            internalContextListProvider, ctx.getActionClassifier());

        title.add(new RequiredLabelClassAppender(list));

    }

    private void buildContent(BSContainer<?> content, Form<?> form, IModel<SIList<SInstance>> list,
        WicketBuildContext ctx, boolean isEdition, ConfirmationModal confirmationModal) {

        final String markup = ""
            + " <div class='list-table-empty' wicket:id='empty-content'>                                             "
            + "     <p class='list-table-empty-message'>Nenhum item foi adicionado. </p>                             "
            + " </div>                                                                                               "
            + " <div wicket:id='not-empty-content'>                                                                  "
            + "     <table class='table table-condensed table-unstyled' style='margin-bottom:0px'>                   "
            + "          <thead wicket:id='_h'></thead>                                                              "
            + "          <tbody wicket:id='_b'><wicket:container wicket:id='_e'><tr wicket:id='_r'></tr></wicket:container></tbody> "
            + "          <tfoot wicket:id='_ft'>                                                                     "
            + "              <tr><td colspan='99' wicket:id='_fb'></td></tr>                                         "
            + "          </tfoot>                                                                                    "
            + "     </table>                                                                                         "
            + " </div>                                                                                               ";

        final TemplatePanel template = content.newTemplateTag(tp -> markup);

        final WebMarkupContainer emptyContent = new WebMarkupContainer("empty-content");
        emptyContent.add(new Behavior() {
            @Override
            public void onConfigure(Component component) {
                super.onConfigure(component);
                component.setVisible(list.getObject().isEmpty());
            }
        });
        template.add(emptyContent);

        final WebMarkupContainer notEmptyContent = new WebMarkupContainer("not-empty-content");
        final BSTSection tableHeader = new BSTSection("_h").setTagName("thead");
        final WebMarkupContainer tableBody = new WebMarkupContainer("_b");
        final ElementsView tableRows = new TableElementsView("_e", list, ctx, form, tableBody, confirmationModal);
        final WebMarkupContainer tableFooter = new WebMarkupContainer("_ft");
        final BSContainer<?> footerBody = new BSContainer<>("_fb");
        final IModel<SType<?>> elementTypeModel = Shortcuts.$m.map(list, SIList::getElementsType);
        final ISupplier<SViewListByTable> viewSupplier = ctx.getViewSupplier(SViewListByTable.class);

        notEmptyContent.add($b.onConfigure(c -> c.setVisible(!list.getObject().isEmpty())));

        if (elementTypeModel.getObject().isComposite()) {

            final BSTRow rowHeader = tableHeader.newRow();
            if (isEdition) {
                rowHeader.newTHeaderCell($m.ofValue("")).add(new Behavior() {
                    @Override
                    public void onConfigure(Component component) {
                        super.onConfigure(component);
                        component.setVisible(list.getObject().stream().anyMatch((i) -> viewSupplier.get().getButtonsConfig().isInsertEnabled(i)));
                    }
                });
            }

            final List<IModel<SType<?>>> columns = collectFieldsThatWillBeRendered(elementTypeModel, viewSupplier);
            final int sumWidthPref = columns.stream().mapToInt((x) -> x.getObject().asAtrBootstrap().getColPreference(1)).sum();

            final IConsumer<IModel<SType<?>>> columnConfigurer = fieldModel -> {
                final IModel<String> headerModel = $m.map(fieldModel, i -> i.asAtr().getLabel());

                ctx.configureContainer(headerModel);

                final Integer preferentialWidth = fieldModel.getObject().asAtrBootstrap().getColPreference(1);
                final BSTDataCell cell = rowHeader.newTHeaderCell(headerModel);
                cell.setInnerStyle(String.format("width:%.0f%%;", (100.0 * preferentialWidth) / sumWidthPref));

                if (isEdition) {
                    cell.add(new ClassAttributeModifier() {
                        @Override
                        protected Set<String> update(Set<String> oldClasses) {
                            boolean required = list.getObject()
                                    .stream()
                                    .map(i -> i
                                            .getFieldOpt(fieldModel.getObject().getNameSimple())
                                            .orElse(i))
                                    .anyMatch(SInstance::isRequired);

                            if (required) {
                                oldClasses.add("singular-form-required");
                            } else {
                                oldClasses.remove("singular-form-required");
                            }
                            return oldClasses;
                        }
                    });

                    ctx.addRenderedAsListField(fieldModel.getObject().getPathFull());

                    SInstanceActionsPanel.addPrimarySecondaryPanelsTo(
                        cell,
                        list,
                        false,
                        t -> Arrays.asList(cell, list, t),
                        filter -> instanceActionsProviders.listFieldActionList(list, filter, fieldModel.getObject().getNameSimple(), null));
                }
            };

            if (viewSupplier.get().isRenderCompositeFieldsAsColumns()) {
                for (IModel<SType<?>> field : columns) {
                    columnConfigurer.accept(field);
                }
            } else {
                columnConfigurer.accept(elementTypeModel);
            }
        }

        tableFooter.add($b.onConfigure(c -> c.setVisible(!(viewSupplier.get().isAddEnabled(list.getObject()) && isEdition))));

        template
            .add(notEmptyContent
                .add(tableHeader)
                .add(tableBody
                    .add(tableRows))
                .add(tableFooter
                    .add(footerBody)));

        content.getParent().add(dependsOnModifier(list));
    }

    private List<IModel<SType<?>>> collectFieldsThatWillBeRendered(IModel<SType<?>> elementTypeModel, ISupplier<SViewListByTable> viewSupplier) {
        final List<IModel<SType<?>>> compositeFieldsModel = new ArrayList<>();
        final STypeComposite<?> compositeElementType = (STypeComposite<?>) elementTypeModel.getObject();
        for (int aux = 0; aux < compositeElementType.getFields().size(); aux++) {
            if (shouldRenderHeaderForSType(compositeElementType.getField(aux), viewSupplier)) {
                final int index = aux;
                compositeFieldsModel.add($m.map(elementTypeModel, type -> ((STypeComposite<?>) type).getField(index)));
            }
        }
        return compositeFieldsModel;
    }

    private static boolean shouldRenderHeaderForSType(SType<?> type, ISupplier<SViewListByTable> viewSupplier) {
        return !viewSupplier.get().isRenderCompositeFieldsAsColumns() || (type.asAtr().isExists() && type.asAtr().isVisible());
    }
}
