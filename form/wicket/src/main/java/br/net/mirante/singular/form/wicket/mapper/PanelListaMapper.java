/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper;

import static br.net.mirante.singular.form.wicket.mapper.components.MetronicPanel.dependsOnModifier;
import static br.net.mirante.singular.util.wicket.util.Shortcuts.$b;
import static br.net.mirante.singular.util.wicket.util.Shortcuts.$m;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.SIList;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.view.SView;
import br.net.mirante.singular.form.view.SViewListByForm;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.mapper.components.MetronicPanel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSRow;
import br.net.mirante.singular.util.wicket.bootstrap.layout.TemplatePanel;

public class PanelListaMapper extends AbstractListaMapper {

    public void buildView(WicketBuildContext ctx) {
        final BSContainer<?> parentCol = ctx.getContainer();
        parentCol.appendComponent((id) -> this.newpanel(id, ctx));
    }

    public MetronicPanel newpanel(String id, WicketBuildContext ctx) {
        final IModel<SIList<SInstance>> listaModel = $m.get(ctx::getCurrentInstance);
        final SIList<?> iLista = listaModel.getObject();
        final IModel<String> label = $m.ofValue(trimToEmpty(iLista.asAtr().getLabel()));
        final SView view = ctx.getView();

        final ViewMode viewMode = ctx.getViewMode();
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

        private final SView view;
        private final Form<?> form;
        private final WicketBuildContext ctx;
        private final UIBuilderWicket wicketBuilder;

        private PanelElementsView(String id,
                                  IModel<SIList<SInstance>> model,
                                  UIBuilderWicket wicketBuilder,
                                  WicketBuildContext ctx,
                                  SView view,
                                  Form<?> form) {
            super(id, model);
            this.wicketBuilder = wicketBuilder;
            this.ctx = ctx;
            this.view = view;
            this.form = form;
        }

        @Override
        protected void populateItem(Item<SInstance> item) {
            final BSGrid grid = new BSGrid("_r");
            final BSRow row = grid.newRow();
            final ViewMode viewMode = ctx.getViewMode();

            wicketBuilder.build(ctx.createChild(row.newCol(11), true, item.getModel()), viewMode);

            final BSGrid btnGrid = row.newCol(1).newGrid();

            if ((view instanceof SViewListByForm) && (((SViewListByForm) view).isInsertEnabled())
                    && viewMode.isEdition()) {
                appendInserirButton(this, form, item, btnGrid.newColInRow())
                        .add($b.classAppender("pull-right"));
            }

            if ((view instanceof SViewListByForm) && ((SViewListByForm) view).isDeleteEnabled()
                    && viewMode.isEdition()) {
                appendRemoverButton(this, form, item, btnGrid.newColInRow())
                        .add($b.classAppender("pull-right"));
            }

            item.add(grid);
        }
    }
}
