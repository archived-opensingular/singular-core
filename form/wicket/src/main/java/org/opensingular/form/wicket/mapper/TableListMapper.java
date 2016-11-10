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

import static org.opensingular.lib.wicket.util.util.Shortcuts.*;

import java.util.Set;

import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

import org.opensingular.form.wicket.feedback.FeedbackFence;
import org.opensingular.form.wicket.model.SInstanceFieldModel;
import org.opensingular.lib.commons.lambda.IBiConsumer;
import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.view.SView;
import org.opensingular.form.view.SViewListByTable;
import org.opensingular.form.wicket.ISValidationFeedbackHandlerListener;
import org.opensingular.form.wicket.SValidationFeedbackHandler;
import org.opensingular.form.wicket.UIBuilderWicket;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.mapper.components.MetronicPanel;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.bootstrap.layout.IBSGridCol.BSGridSize;
import org.opensingular.lib.wicket.util.bootstrap.layout.TemplatePanel;
import org.opensingular.lib.wicket.util.bootstrap.layout.table.BSTDataCell;
import org.opensingular.lib.wicket.util.bootstrap.layout.table.BSTRow;
import org.opensingular.lib.wicket.util.bootstrap.layout.table.BSTSection;
import static org.opensingular.form.wicket.mapper.components.MetronicPanel.dependsOnModifier;

public class TableListMapper extends AbstractListaMapper {

    @Override
    public void buildView(WicketBuildContext ctx) {

        if (!(ctx.getView() instanceof SViewListByTable)) {
            throw new SingularFormException("TableListMapper deve ser utilizado com SViewListByTable", ctx.getCurrentInstance());
        }

        if (!(ctx.getCurrentInstance() instanceof SIList)) {
            return;
        }

        ctx.setHint(AbstractControlsFieldComponentMapper.NO_DECORATION, true);
        ctx.getContainer().appendComponent((String id) -> buildPannel(ctx, id));
    }

    private TableListPanel buildPannel(WicketBuildContext ctx, String id) {
        final IModel<SIList<SInstance>> list        = $m.get(ctx::getCurrentInstance);
        final SViewListByTable          view        = (SViewListByTable) ctx.getView();
        final Boolean                   isEdition   = ctx.getViewMode() == null || ctx.getViewMode().isEdition();
        final SIList<SInstance>         iLista      = list.getObject();
        final SType<?>                  currentType = ctx.getCurrentInstance().getType();

        addMinimumSize(currentType, iLista);

        TableListPanel panel = TableListPanel.TableListPanelBuilder.build(id,
                (h, form) -> buildHeader(h, form, list, ctx, view, isEdition),
                (c, form) -> builContent(c, form, list, ctx, view, isEdition),
                (f, form) -> buildFooter(f, form, ctx));
        return panel;
    }

    private void buildHeader(BSContainer<?> header, Form<?> form, IModel<SIList<SInstance>> list,
                             WicketBuildContext ctx, SViewListByTable view, boolean isEdition) {

        final IModel<String> label = $m.ofValue(ctx.getCurrentInstance().getType().asAtr().getLabel());
        final Label          title = new Label("_title", label);

        ctx.configureContainer(label);
        header.appendTag("span", title);
//        header.add($b.visibleIf($m.get(() -> !Strings.isNullOrEmpty(label.getObject()))));

        final SType<SInstance> elementsType = list.getObject().getElementsType();

        if (!(elementsType instanceof STypeComposite) && elementsType.asAtr().isRequired()) {
            title.add($b.classAppender("singular-form-required"));
        }

    }

    private void builContent(BSContainer<?> content, Form<?> form, IModel<SIList<SInstance>> list,
                             WicketBuildContext ctx, SViewListByTable view, boolean isEdition) {

        final String markup = ""
                + " <div class='list-table-empty' wicket:id='empty-content'>                                             "
                + "     <p class='list-table-empty-message'>Nenhum item foi adicionado. </p>                             "
                + " </div>                                                                                               "
                + " <div wicket:id='not-empty-content'>                                                                  "
                + "     <table class='table table-condensed table-unstyled' style='margin-bottom:0px'>                   "
                + "          <thead wicket:id='_h'></thead>                                                              "
                + "          <tbody><wicket:container wicket:id='_e'><tr wicket:id='_r'></tr></wicket:container></tbody> "
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

        final BSTSection         tableHeader  = new BSTSection("_h").setTagName("thead");
        final ElementsView       tableRows    = new TableElementsView("_e", list, ctx, form);
        final WebMarkupContainer tableFooter  = new WebMarkupContainer("_ft");
        final BSContainer<?>     footerBody   = new BSContainer<>("_fb");
        final SType<SInstance>   elementsType = list.getObject().getElementsType();

        notEmptyContent.add($b.onConfigure(c -> c.setVisible(!list.getObject().isEmpty())));

//        content.add($b.attrAppender("style", "padding: 15px 15px 10px 15px", ";"));

        if (elementsType instanceof STypeComposite) {

            final STypeComposite<?> compositeElementsType = (STypeComposite<?>) elementsType;
            final BSTRow            row                   = tableHeader.newRow();

            if (view.isInsertEnabled()) {
                row.newTHeaderCell($m.ofValue(""));
            }

            int sumWidthPref = compositeElementsType.getFields().stream().mapToInt((x) -> x.asAtrBootstrap().getColPreference(1)).sum();

            for (SType<?> tCampo : compositeElementsType.getFields()) {

                final Integer        preferentialWidth  = tCampo.asAtrBootstrap().getColPreference(1);
                final IModel<String> headerModel        = $m.ofValue(tCampo.asAtr().getLabel());
                final BSTDataCell    cell               = row.newTHeaderCell(headerModel);
                final String         width              = String.format("width:%.0f%%;", (100.0 * preferentialWidth) / sumWidthPref);
                final boolean        isCampoObrigatorio = tCampo.asAtr().isRequired();

                ctx.configureContainer(headerModel);

                cell.setInnerStyle(width);
                cell.add(new ClassAttributeModifier() {
                    @Override
                    protected Set<String> update(Set<String> oldClasses) {
                        if (isCampoObrigatorio && isEdition) {
                            oldClasses.add("singular-form-required");
                        } else {
                            oldClasses.remove("singular-form-required");
                        }
                        return oldClasses;
                    }
                });
            }
        }

        tableFooter.setVisible(!(view.isNewEnabled() && isEdition));

        template.add(
                notEmptyContent.add(tableHeader)
                        .add(tableRows)
                        .add(tableFooter.add(footerBody))
        );

        content.getParent().add(dependsOnModifier(list));
    }

    private static final class TableElementsView extends ElementsView {

        private final WicketBuildContext ctx;
        private final SView              view;
        private final Form<?>            form;
        private final ViewMode           viewMode;
        private final UIBuilderWicket    wicketBuilder;

        private TableElementsView(String id, IModel<SIList<SInstance>> model, WicketBuildContext ctx, Form<?> form) {
            super(id, model);
            this.wicketBuilder = ctx.getUiBuilderWicket();
            this.ctx = ctx;
            this.view = ctx.getView();
            this.form = form;
            this.viewMode = ctx.getViewMode();
        }

        @Override
        protected void populateItem(Item<SInstance> item) {

            final BSTRow            row = new BSTRow("_r", BSGridSize.MD);
            final IModel<SInstance> im  = item.getModel();
            final SInstance         ins = im.getObject();

            SValidationFeedbackHandler feedbackHandler = SValidationFeedbackHandler.bindTo(new FeedbackFence(row))
                    .addInstanceModel(im)
                    .addListener(ISValidationFeedbackHandlerListener.withTarget(t -> t.add(row)));
            row.add($b.classAppender("singular-form-table-row can-have-error"));
            row.add($b.classAppender("has-errors", $m.ofValue(feedbackHandler).map(SValidationFeedbackHandler::containsNestedErrors)));

            if (!(view instanceof SViewListByTable)) {
                return;
            }

            final SViewListByTable viewListByTable = (SViewListByTable) view;

            if (viewListByTable.isInsertEnabled()) {
                final BSTDataCell actionColumn = row.newCol();
                actionColumn.add($b.attrAppender("style", "width:20px", ";"));
                appendInserirButton(this, form, item, actionColumn);
            }

            if (ins instanceof SIComposite) {
                final SIComposite       ci = (SIComposite) ins;
                final STypeComposite<?> ct = ci.getType();

                for (SType<?> ft : ct.getFields()) {
                    final IModel<SInstance> fm = new SInstanceFieldModel<>(item.getModel(), ft.getNameSimple());
                    wicketBuilder.build(ctx.createChild(row.newCol(), true, fm), viewMode);
                }
            } else {
                wicketBuilder.build(ctx.createChild(row.newCol(), true, im), viewMode);
            }

            if (viewListByTable.isDeleteEnabled() && viewMode.isEdition()) {
                final BSTDataCell actionColumn = row.newCol();
                actionColumn.add($b.attrAppender("style", "width:20px", ";"));
                appendRemoverButton(this, form, item, actionColumn);
            }

            item.add(row);
        }
    }

    private static abstract class TableListPanel extends MetronicPanel {

        public TableListPanel(String id) {
            super(id);
        }

        public TableListPanel(String id, boolean withForm) {
            super(id, withForm);
        }
        
        @Override
        public IFunction<TemplatePanel, String> getTemplateFunction() {
            String wrapper = withForm ? "<form wicket:id='_fo'>%s</form>" : "%s";
            return (tp) -> String.format(wrapper, ""
                + "  <div class='list-table-input'>"
                + "    <div wicket:id='_hd' class='list-table-heading'></div>"
                + "    <div class='list-table-body' wicket:id='_co' >"
                + "    </div>"
                + "    <div wicket:id='_ft' class='list-table-footer'></div>"
                + "  </div>"
                + "");
        }
        
        public static final class TableListPanelBuilder {

            private TableListPanelBuilder() {
            }

            public static TableListPanel build(String id,
                                               IBiConsumer<BSContainer<?>, Form<?>> buildHeading,
                                               IBiConsumer<BSContainer<?>, Form<?>> buildContent,
                                               IBiConsumer<BSContainer<?>, Form<?>> buildFooter) {
                return build(id, true, buildHeading, buildContent, buildFooter);
            }

            public static TableListPanel build(String id,
                                               boolean withForm,
                                               IBiConsumer<BSContainer<?>, Form<?>> buildHeading,
                                               IBiConsumer<BSContainer<?>, Form<?>> buildContent,
                                               IBiConsumer<BSContainer<?>, Form<?>> buildFooter) {

                return new TableListPanel(id, withForm) {
                    @Override
                    protected void buildHeading(BSContainer<?> heading, Form<?> form) {
                        buildHeading.accept(heading, form);
                    }

                    @Override
                    protected void buildFooter(BSContainer<?> footer, Form<?> form) {
                        buildFooter.accept(footer, form);
                    }

                    @Override
                    protected void buildContent(BSContainer<?> content, Form<?> form) {
                        buildContent.accept(content, form);
                    }
                };
            }

        }
    }
}
