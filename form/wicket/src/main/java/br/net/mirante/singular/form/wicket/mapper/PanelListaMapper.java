/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper;

import static br.net.mirante.singular.form.wicket.mapper.components.MetronicPanel.dependsOnModifier;
import static br.net.mirante.singular.util.wicket.util.Shortcuts.$b;
import static br.net.mirante.singular.util.wicket.util.Shortcuts.$m;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

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

import br.net.mirante.singular.form.SIList;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.util.transformer.Value;
import br.net.mirante.singular.form.view.SViewListByForm;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.mapper.components.MetronicPanel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSCol;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSRow;
import br.net.mirante.singular.util.wicket.bootstrap.layout.TemplatePanel;
import br.net.mirante.singular.util.wicket.resource.Icone;

public class PanelListaMapper extends AbstractListaMapper {

    public void buildView(WicketBuildContext ctx) {
        final BSContainer<?> parentCol = ctx.getContainer();
        parentCol.appendComponent((id) -> this.newpanel(id, ctx));
    }

    public MetronicPanel newpanel(String id, WicketBuildContext ctx) {
        final IModel<SIList<SInstance>> listaModel = $m.get(ctx::getCurrentInstance);
        final SIList<?>                 iLista     = listaModel.getObject();
        final IModel<String>            label      = $m.ofValue(trimToEmpty(iLista.asAtr().getLabel()));
        final SViewListByForm           view       = (SViewListByForm) ctx.getView();

        final ViewMode viewMode    = ctx.getViewMode();
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
                        return "";
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