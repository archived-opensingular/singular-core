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

import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.opensingular.form.wicket.mapper.components.MetronicPanel.dependsOnModifier;
import static org.opensingular.lib.wicket.util.util.Shortcuts.$b;
import static org.opensingular.lib.wicket.util.util.Shortcuts.$m;

import java.io.Serializable;
import java.util.Optional;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.util.transformer.Value;
import org.opensingular.form.view.SViewListByForm;
import org.opensingular.form.wicket.UIBuilderWicket;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.mapper.components.MetronicPanel;
import org.opensingular.form.wicket.model.ReadOnlyCurrentInstanceModel;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSCol;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSGrid;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSRow;
import org.opensingular.lib.wicket.util.bootstrap.layout.TemplatePanel;
import org.opensingular.lib.wicket.util.resource.Icone;

public class PanelListMapper extends AbstractListMapper {

    public void buildView(WicketBuildContext ctx) {
        final BSContainer<?> parentCol = ctx.getContainer();
        parentCol.appendComponent((id) -> this.newpanel(id, ctx));
    }

    public MetronicPanel newpanel(String id, WicketBuildContext ctx) {
        final IModel<SIList<SInstance>> listaModel = new ReadOnlyCurrentInstanceModel<>(ctx);
        final SIList<?>                 iLista     = listaModel.getObject();
        final IModel<String>            label      = $m.ofValue(trimToEmpty(iLista.asAtr().getLabel()));
        final SViewListByForm           view       = (SViewListByForm) ctx.getView();

        final SType<?> currentType = ctx.getCurrentInstance().getType();

        addMinimumSize(currentType, iLista);

        ctx.configureContainer(label);

        MetronicPanel panel = MetronicPanel.MetronicPanelBuilder.build(id,
                (heading, form) -> {
                    heading.appendTag("span", new Label("_title", label));
                    heading.setVisible(ctx.isTitleInBlock());
                },
                (content, form) -> {

                    TemplatePanel list = content.newTemplateTag(t -> ""
                            + "    <ul class='list-group list-by-form'>"
                            + "      <li wicket:id='_e' class='list-group-item' style='margin-bottom:15px'>"
                            + "         <div wicket:id='_r'></div>"
                            + "      </li>"
                            + "      <div wicket:id='_empty' class='list-by-form-empty-state'>"
                            + "         <span>Nenhum item foi adicionado</span>"
                            + "      </div>"
                            + "    </ul>"
                    );
                    final PanelElementsView elements = new PanelElementsView("_e", listaModel, ctx.getUiBuilderWicket(), ctx, view, form);
                    elements.add($b.onConfigure(c -> c.setVisible(!listaModel.getObject().isEmpty())));
                    list.add(elements);
                    final WebMarkupContainer empty = new WebMarkupContainer("_empty");
                    empty.add($b.onConfigure(c -> c.setVisible(listaModel.getObject().isEmpty())));
                    list.add(empty);
                    content.add($b.attrAppender("style", "padding: 15px 15px 10px 15px", ";"));
                    content.getParent().add(dependsOnModifier(listaModel));
                },
                (f, form) -> buildFooter(f, form, ctx)
        );

        return panel;
    }

    private static final class PanelElementsView extends ElementsView {

        private final SViewListByForm    view;
        private final Form<?>            form;
        private final WicketBuildContext ctx;
        private final UIBuilderWicket    wicketBuilder;

        private PanelElementsView(String id,
                                  IModel<SIList<SInstance>> model,
                                  UIBuilderWicket wicketBuilder,
                                  WicketBuildContext ctx,
                                  SViewListByForm view,
                                  Form<?> form) {
            super(id, model);
            this.wicketBuilder = wicketBuilder;
            this.ctx = ctx;
            this.view = view;
            this.form = form;
        }

        @Override
        public void renderHead(IHeaderResponse response) {
            super.renderHead(response);
            PackageResourceReference cssFile =
                    new PackageResourceReference(this.getClass(), "PanelElementsView.js");
            JavaScriptHeaderItem javascriptItem = JavaScriptHeaderItem.forReference(cssFile);

            response.render(javascriptItem);
            response.render(OnDomReadyHeaderItem.forScript("appendListItemEvent();"));
        }

        @Override
        protected void populateItem(Item<SInstance> item) {
            final BSGrid   grid     = new BSGrid("_r");
            final ViewMode viewMode = ctx.getViewMode();

            buildHeader(item, grid, viewMode);
            buildBody(item, grid, viewMode);

            item.add(grid);
        }

        private void buildHeader(Item<SInstance> item, BSGrid grid, ViewMode viewMode) {
            final BSRow header = grid.newRow();
            header.add($b.classAppender("list-item-header"));
            final BSCol title = header.newCol(11).newGrid().newColInRow();
            Model model = new Model() {
                @Override
                public Serializable getObject() {
                    if (view.getHeaderPath() != null) {
                        return Optional.ofNullable(Value.of(item.getModelObject(), view.getHeaderPath())).orElse("").toString();
                    } else {
                        return item.getModelObject().toStringDisplay();
                    }
                }
            };
            title.newTemplateTag(tp -> "<span wicket:id='_title' ></span>")
                    .add(new Label("_title", model));

            final BSGrid btnGrid = header.newCol(1).newGrid();

            header.add($b.classAppender("list-icons"));

            if ((view != null) && (view.isInsertEnabled()) && viewMode.isEdition()) {
                appendInserirButton(this, form, item, btnGrid.newColInRow()).add($b.classAppender("pull-right"));
            }

            final BSCol btnCell = btnGrid.newColInRow();

            if ((view != null) && view.isDeleteEnabled() && viewMode.isEdition()) {
                appendRemoverIconButton(this, form, item, btnCell).add($b.classAppender("pull-right"));
            }

            if (viewMode == ViewMode.EDIT) {
                btnCell
                        .newTemplateTag(tp -> ""
                                + "<i"
                                + " style='" + MapperCommons.ICON_STYLE + " 'class='" + Icone.PENCIL + " pull-right' />");
            } else {
                btnCell
                        .newTemplateTag(tp -> ""
                                + "<i"
                                + " style='" + MapperCommons.ICON_STYLE + " 'class='" + Icone.EYE + " pull-right' />");
            }
        }

        private void buildBody(Item<SInstance> item, BSGrid grid, ViewMode viewMode) {
            final BSRow body = grid.newRow();
            body.add($b.classAppender("list-item-body"));
            wicketBuilder.build(ctx.createChild(body.newCol(12), true, item.getModel()), viewMode);
        }
    }

    protected static RemoverButton appendRemoverIconButton(ElementsView elementsView, Form<?> form, Item<SInstance> item, BSContainer<?> cell) {
        final RemoverButton btn = new RemoverButton("_remover_", form, elementsView, item);
        cell
                .newTemplateTag(tp -> "<i  wicket:id='_remover_' class='singular-remove-btn " + Icone.REMOVE + "' />")
                .add(btn);
        return btn;
    }

}