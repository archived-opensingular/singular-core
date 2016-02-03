package br.net.mirante.singular.form.wicket.mapper;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

import com.google.common.base.Strings;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SList;
import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;
import br.net.mirante.singular.form.mform.basic.view.MPanelListaView;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.mapper.components.MetronicPanel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSRow;
import br.net.mirante.singular.util.wicket.bootstrap.layout.TemplatePanel;
import static br.net.mirante.singular.util.wicket.util.Shortcuts.$b;
import static br.net.mirante.singular.util.wicket.util.Shortcuts.$m;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

public class PanelListaMapper extends AbstractListaMapper {

    @SuppressWarnings("unchecked")
    public void buildView(WicketBuildContext ctx) {

        final IModel<SList<SInstance>> listaModel = $m.get(() -> (ctx.getCurrentInstance()));
        final SList<?> iLista = listaModel.getObject();
        final IModel<String> label = $m.ofValue(trimToEmpty(iLista.as(SPackageBasic.aspect()).getLabel()));
        final MView view = ctx.getView();
        final BSContainer<?> parentCol = ctx.getContainer();
        final ViewMode viewMode = ctx.getViewMode();

        parentCol.appendComponent(id -> MetronicPanel.MetronicPanelBuilder.build(id,
                        (heading, form) -> {

                            heading.appendTag("span", new Label("_title", label));
                            heading.add($b.visibleIf($m.get(() -> !Strings.isNullOrEmpty(label.getObject()))));

                            if ((view instanceof MPanelListaView)
                                    && ((MPanelListaView) view).isPermiteAdicaoDeLinha()
                                    && viewMode.isEdition()) {
                                appendAddButton(listaModel, form, heading, false)
                                        .add($b.onConfigure(c -> c.setVisible(listaModel.getObject().isEmpty())));
                            }
                        },
                        (content, form) -> {

                            TemplatePanel list = content.newTemplateTag(t -> ""
                                    + "    <ul class='list-group'>"
                                    + "      <li wicket:id='_e' class='list-group-item'>"
                                    + "        <div wicket:id='_r'></div>"
                                    + "      </li>"
                                    + "    </ul>");
                            list.add(new PanelElementsView("_e", listaModel, ctx.getUiBuilderWicket(), ctx, view, form));

                        },
                        (footer, form) -> {
                            footer.add($b.onConfigure(c -> c.setVisible(!listaModel.getObject().isEmpty())));
                            footer.setVisible(false);
                            if ((view instanceof MPanelListaView)
                                    && ((MPanelListaView) view).isPermiteAdicaoDeLinha()
                                    && viewMode.isEdition()) {
                                appendAddButton(listaModel, form, footer, true);
                            }
                        })
        );
    }

    private static final class PanelElementsView extends ElementsView {

        private final MView view;
        private final Form<?> form;
        private final WicketBuildContext ctx;
        private final UIBuilderWicket wicketBuilder;

        private PanelElementsView(String id,
                                  IModel<SList<SInstance>> model,
                                  UIBuilderWicket wicketBuilder,
                                  WicketBuildContext ctx,
                                  MView view,
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

            if ((view instanceof MPanelListaView) && (((MPanelListaView) view).isPermiteInsercaoDeLinha())
                    && viewMode.isEdition()) {
                appendInserirButton(this, form, item, btnGrid.newColInRow())
                        .add($b.classAppender("pull-right"));
            }

            if ((view instanceof MPanelListaView) && ((MPanelListaView) view).isPermiteExclusaoDeLinha()
                    && viewMode.isEdition()) {
                appendRemoverButton(this, form, item, btnGrid.newColInRow())
                        .add($b.classAppender("pull-right"));
            }

            item.add(grid);
        }
    }
}
