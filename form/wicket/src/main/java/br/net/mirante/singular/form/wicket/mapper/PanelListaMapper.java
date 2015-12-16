package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
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
import com.google.common.base.Strings;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

import static br.net.mirante.singular.util.wicket.util.Shortcuts.$b;
import static br.net.mirante.singular.util.wicket.util.Shortcuts.$m;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

public class PanelListaMapper extends AbstractListaMapper {

    @Override
    @SuppressWarnings("unchecked")
    public void buildView(WicketBuildContext ctx, MView view, IModel<? extends MInstancia> model, ViewMode viewMode) {
        final IModel<MILista<MInstancia>> listaModel = $m.get(() -> (MILista<MInstancia>) model.getObject());
        final MILista<?> iLista = listaModel.getObject();
        final IModel<String> label = $m.ofValue(trimToEmpty(iLista.as(MPacoteBasic.aspect()).getLabel()));

        final BSContainer<?> parentCol = ctx.getContainer();

        parentCol.appendComponent(id -> MetronicPanel.MetronicPanelBuilder.build(id,
                        (heading, form) -> {

                            heading.appendTag("span", new Label("_title", label));
                            heading.add($b.visibleIf($m.get(() -> !Strings.isNullOrEmpty(label.getObject()))));

                        },
                        (content, form) -> {

                            TemplatePanel list = content.newTemplateTag(t -> ""
                                    + "    <ul class='list-group'>"
                                    + "      <li wicket:id='_e' class='list-group-item'>"
                                    + "        <div wicket:id='_r'></div>"
                                    + "      </li>"
                                    + "    </ul>");
                            list.add(new PanelElementsView("_e", listaModel, ctx, view, form, viewMode));

                        },
                        (footer, form) -> {

                            if ((view instanceof MPanelListaView) && ((MPanelListaView) view).isPermiteAdicaoDeLinha()
                                    && viewMode.isEdition()) {
                                appendAdicionarButton(listaModel, form, footer);
                            } else {
                                footer.setVisible(false);
                            }

                        })
        );
    }

    private static final class PanelElementsView extends ElementsView {

        private final MView view;
        private final Form<?> form;
        private final ViewMode viewMode;
        private final WicketBuildContext ctx;

        private PanelElementsView(String id, IModel<MILista<MInstancia>> model, WicketBuildContext ctx, MView view, Form<?> form, ViewMode viewMode) {
            super(id, model);
            this.ctx = ctx;
            this.view = view;
            this.form = form;
            this.viewMode = viewMode;
        }

        @Override
        protected void populateItem(Item<MInstancia> item) {
            final BSGrid grid = new BSGrid("_r");
            final BSRow row = grid.newRow();

            UIBuilderWicket.build(ctx.createChild(row.newCol(11), true), item.getModel(), viewMode);

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
