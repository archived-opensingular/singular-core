package br.net.mirante.singular.form.wicket.mapper.composite;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.type.core.SPackageBootstrap;
import br.net.mirante.singular.form.view.Block;
import br.net.mirante.singular.form.view.SViewByBlock;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.model.SInstanceCampoModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSRow;
import br.net.mirante.singular.util.wicket.bootstrap.layout.TemplatePanel;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

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

                final Block block = view.getBlocks().get(i);

                String templatePortlet = "";
                templatePortlet += " <div class='portlet light'>                         ";
                templatePortlet += "     <div class='portlet-title' wicket:id='title' /> ";
                templatePortlet += "     <div class='portlet-body'>                      ";
                templatePortlet += " 		<div wicket:id='grid' />                     ";
                templatePortlet += "     </div>                                          ";
                templatePortlet += " </div>                                              ";

                final TemplatePanel portlet = new TemplatePanel("_portlet" + i, templatePortlet);

                String templateTitle = "";
                templateTitle += "  <div class='caption'>                               ";
                templateTitle += " 	    <span wicket:id='name'                          ";
                templateTitle += " 		      class='caption-subject bold uppercase' /> ";
                templateTitle += "  </div>                                              ";

                final TemplatePanel portletTitle = new TemplatePanel("title", templateTitle);

                portletTitle.add($b.onConfigure(c -> c.setVisible(StringUtils.isNotEmpty(block.getName()))));
                portletTitle.add(new Label("name", Model.of(block.getName())));

                final BSGrid newGrid = new BSGrid("grid");

                portlet.add(newGrid, portletTitle);

                grid.appendTag("div", portlet);

                BSRow row = newGrid.newRow();

                for (String typeName : block.getTypes()) {

                    final SType<?>                       field  = type.getField(typeName);
                    final Boolean                        newRow = field.getAttributeValue(SPackageBootstrap.ATR_COL_ON_NEW_ROW);
                    final SInstanceCampoModel<SInstance> im     = fieldModel(field);

                    if (newRow != null && newRow) {
                        row = grid.newRow();
                    }

                    buildField(ctx.getUiBuilderWicket(), row, im);
                }

            }
        }
    }

}
