package br.net.mirante.singular.form.wicket.mapper.composite;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.type.core.SPackageBootstrap;
import br.net.mirante.singular.form.view.Block;
import br.net.mirante.singular.form.view.SViewByBlock;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;
import br.net.mirante.singular.form.wicket.model.SInstanceCampoModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSRow;
import br.net.mirante.singular.util.wicket.bootstrap.layout.TemplatePanel;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import java.io.Serializable;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

public class BlocksCompositeMapper extends AbstractCompositeMapper {

    @Override
    protected ICompositeViewBuilder getViewBuilder(WicketBuildContext ctx) {
        return new CompositeViewBuilder(ctx);
    }

    private static class CompositeViewBuilder extends AbstractCompositeViewBuilder {

        CompositeViewBuilder(WicketBuildContext ctx) {
            super(ctx);
        }

        @Override
        protected void buildFields(WicketBuildContext ctx, BSGrid grid) {
            final SViewByBlock view = (SViewByBlock) ctx.getView();

            for (int i = 0; i < view.getBlocks().size(); i++) {

                final Block        block   = view.getBlocks().get(i);
                final PortletPanel portlet = new PortletPanel("_portlet" + i, block);
                final BSGrid       newGrid = portlet.getNewGrid();
                BSRow              row     = newGrid.newRow();

                grid.appendTag("div", portlet);

                for (String typeName : block.getTypes()) {
                    final SType<?>                       field  = type.getField(typeName);
                    final Boolean                        newRow = field.getAttributeValue(SPackageBootstrap.ATR_COL_ON_NEW_ROW);
                    final SInstanceCampoModel<SInstance> im     = fieldModel(field);
                    if (newRow != null && newRow) {
                        row = newGrid.newRow();
                    }
                    buildField(ctx.getUiBuilderWicket(), row, im);
                }

                portlet.add(new ConfigurePortletVisibilityBehaviour(block));
            }

        }
    }

    private static class ConfigurePortletVisibilityBehaviour extends Behavior {

        private final Block block;

        private ConfigurePortletVisibilityBehaviour(Block block) {
            this.block = block;
        }

        @Override
        public void onConfigure(Component c) {
            super.onConfigure(c);
            final MarkupContainer container    = (MarkupContainer) c;
            final Boolean         isAnyVisible = container.visitChildren(Component.class, new VisibilityVisitor(block));
            container.setVisible(isAnyVisible != null && isAnyVisible);
        }

    }

    private static class VisibilityVisitor implements IVisitor<Component, Boolean>, Serializable {

        private final Block block;

        private VisibilityVisitor(Block block) {
            this.block = block;
        }

        @Override
        public void component(Component component, IVisit<Boolean> visit) {
            IModel<?> model = component.getDefaultModel();
            if (model != null && IMInstanciaAwareModel.class.isAssignableFrom(model.getClass())) {
                SInstance si = ((IMInstanciaAwareModel) model).getMInstancia();
                if (block.getTypes().contains(si.getType().getNameSimple())) {
                    if (si.asAtr().isVisible()) {
                        visit.stop(true);
                    }
                }
            }
        }
    }

    private static class PortletPanel extends TemplatePanel {

        private static final String TITLE_ID = "title";
        private static final String GRID_ID  = "grid";

        private static final String PORTLET_MARKUP = ""
                + " <div class='portlet light'>                                    "
                + "     <div class='portlet-title' wicket:id='" + TITLE_ID + "' /> "
                + "     <div class='portlet-body'>                                 "
                + "         <div wicket:id='" + GRID_ID + "' />                    "
                + "     </div>                                                     "
                + " </div>                                                         ";

        private final Block  block;
        private final BSGrid newGrid;

        PortletPanel(String id, Block block) {
            super(id, PORTLET_MARKUP);
            this.block = block;
            this.newGrid = new BSGrid(GRID_ID);
            add(newGrid, buildPortletTitle(block));
        }

        @Override
        public void onEvent(IEvent<?> event) {
            super.onEvent(event);
            final Boolean isAnyVisible = visitChildren(Component.class, new VisibilityVisitor(block));
            setVisible(isAnyVisible != null && isAnyVisible);
            if (AjaxRequestTarget.class.isAssignableFrom(event.getPayload().getClass())) {
                ((AjaxRequestTarget) event.getPayload()).add(this);
            }
        }

        private TemplatePanel buildPortletTitle(Block block) {

            final String name = "name";
            final String titleMarkup = ""
                    + "  <div class='caption'>                   "
                    + "         <span wicket:id='" + name + "'   "
                    + "               class='caption-subject' /> "
                    + "  </div>                                  ";

            final TemplatePanel portletTitle = new TemplatePanel(TITLE_ID, titleMarkup);

            portletTitle.add($b.onConfigure(c -> c.setVisible(StringUtils.isNotEmpty(block.getName()))));
            portletTitle.add(new Label(name, Model.of(block.getName())));

            return portletTitle;
        }

        BSGrid getNewGrid() {
            return newGrid;
        }
    }


}