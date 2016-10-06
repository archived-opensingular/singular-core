package org.opensingular.form.wicket.mapper.composite;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.type.core.SPackageBootstrap;
import org.opensingular.form.view.Block;
import org.opensingular.form.view.SViewByBlock;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.model.SInstanceFieldModel;
import org.opensingular.form.wicket.util.WicketFormProcessing;
import org.opensingular.singular.util.wicket.bootstrap.layout.BSGrid;
import org.opensingular.singular.util.wicket.bootstrap.layout.BSRow;
import org.opensingular.singular.util.wicket.bootstrap.layout.IBSComponentFactory;
import org.opensingular.singular.util.wicket.bootstrap.layout.TemplatePanel;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.StyleAttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

import java.util.*;


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

            final List<String> remainingTypes = new ArrayList<>();
            final List<String> addedTypes     = new ArrayList<>();
            final SViewByBlock view           = (SViewByBlock) ctx.getView();

            final WicketBuildContext             rootContext = ctx.getRootContext();
            final IBSComponentFactory<Component> factory     = rootContext.getPreFormPanelFactory();

            if (factory != null) {
                grid.newComponent(factory);
                grid = grid.newGrid();
                rootContext.setPreFormPanelFactory(null);
            }

            for (int i = 0; i < view.getBlocks().size(); i++) {
                final Block block = view.getBlocks().get(i);
                if (StringUtils.isEmpty(block.getName()) && block.getTypes().size() == 1 && ctx.getCurrentInstance() instanceof SIComposite) {
                    final SIComposite sic        = ctx.getCurrentInstance();
                    final SInstance   firstChild = sic.getField(block.getTypes().get(0));
                    block.setName(firstChild.asAtr().getLabel());
                    ctx.setTitleInBlock(true);
                }
                final PortletPanel portlet = new PortletPanel("_portlet" + i, block, ctx);
                addedTypes.addAll(block.getTypes());
                appendBlock(grid, block, portlet);
            }

            for (SType<?> f : getInstanceType().getFields()) {
                if (!addedTypes.contains(f.getNameSimple())) {
                    remainingTypes.add(f.getNameSimple());
                }
            }

            if (!remainingTypes.isEmpty()) {
                final Block        block   = new Block();
                final PortletPanel portlet = new PortletPanel("_portletForRemaining", block, ctx);
                block.setTypes(remainingTypes);
                appendBlock(grid, block, portlet);
            }

        }


        private void appendBlock(BSGrid grid, Block block, PortletPanel portlet) {

            final BSGrid newGrid = portlet.getNewGrid();
            BSRow        row     = newGrid.newRow();

            grid.appendTag("div", portlet);

            for (String typeName : block.getTypes()) {
                row = buildBlockAndGetCurrentRow(getInstanceType().getField(typeName), newGrid, row);
            }

        }

        private BSRow buildBlockAndGetCurrentRow(SType<?> field, BSGrid grid, BSRow row) {
            final Boolean                        newRow = field.getAttributeValue(SPackageBootstrap.ATR_COL_ON_NEW_ROW);
            final SInstanceFieldModel<SInstance> im     = fieldModel(field);
            if (newRow != null && newRow) {
                row = grid.newRow();
            }
            buildField(ctx.getUiBuilderWicket(), row, im);
            return row;
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

        private final Block              block;
        private final BSGrid             newGrid;
        private final WicketBuildContext ctx;

        private boolean visible;

        PortletPanel(String id, Block block, WicketBuildContext ctx) {
            super(id, PORTLET_MARKUP);
            this.block = block;
            this.ctx = ctx;
            this.newGrid = new BSGrid(GRID_ID);
            add(newGrid, buildPortletTitle(block));

        }

        @Override
        protected void onInitialize() {
            super.onInitialize();
            add(new StyleAttributeModifier() {
                @Override
                protected Map<String, String> update(Map<String, String> oldStyles) {
                    final Map<String, String> newStyles = new HashMap<>(oldStyles);
                    if (isAnyChildrenVisible()) {
                        newStyles.put("display", "block");
                        visible = true;
                    } else {
                        newStyles.put("display", "none");
                        visible = false;
                    }
                    return newStyles;
                }
            });
        }

        private boolean isAnyChildrenVisible() {
            if (ctx.getCurrentInstance().asAtr().exists() && ctx.getCurrentInstance().asAtr().isVisible()) {
                for (String typeName : block.getTypes()) {
                    if (ctx.getCurrentInstance() instanceof SIComposite) {
                        final SIComposite ci    = ctx.getCurrentInstance();
                        final SInstance   field = ci.getField(typeName);
                        if (field.asAtr().exists() && field.asAtr().isVisible()) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        @Override
        public void onEvent(IEvent<?> event) {
            super.onEvent(event);
            if (AjaxRequestTarget.class.isAssignableFrom(event.getPayload().getClass())) {
                final Boolean isAnyFieldUpdated = getRequestCycle().getMetaData(WicketFormProcessing.MDK_FIELD_UPDATED);
                if (isAnyFieldUpdated != null && isAnyFieldUpdated) {
                    final AjaxRequestTarget payload = (AjaxRequestTarget) event.getPayload();
                    if (isAnyChildrenVisible() != visible) {
                        if (isAnyChildrenVisible()) {
                            payload.appendJavaScript("$('#" + this.getMarkupId() + "').css('display', 'block');");
                            visible = true;
                        } else {
                            payload.appendJavaScript("$('#" + this.getMarkupId() + "').css('display', 'none');");
                            visible = false;
                        }
                    }
                }
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
            final Label         titleLabel   = new Label(name, Model.of(block.getName()));

            portletTitle.setVisible(StringUtils.isNotEmpty(block.getName()));
            portletTitle.add(titleLabel);

            titleLabel.add(new ClassAttributeModifier() {
                @Override
                protected Set<String> update(Set<String> oldClasses) {
                    if (block.getTypes().size() == 1) {
                        final SIComposite sic        = ctx.getCurrentInstance();
                        final SInstance   firstChild = sic.getField(block.getTypes().get(0));
                        if (firstChild.isRequired()) {
                            oldClasses.add("singular-form-required");
                        } else {
                            oldClasses.remove("singular-form-required");
                        }
                    }
                    return oldClasses;
                }
            });

            return portletTitle;
        }

        BSGrid getNewGrid() {
            return newGrid;
        }
    }


}