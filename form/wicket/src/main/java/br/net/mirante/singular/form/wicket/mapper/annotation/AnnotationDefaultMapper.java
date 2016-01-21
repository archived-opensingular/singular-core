package br.net.mirante.singular.form.wicket.mapper.annotation;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.basic.view.MAnnotationView;
import br.net.mirante.singular.form.mform.core.MIString;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.core.annotation.AtrAnnotation;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.behavior.DisabledClassBehavior;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.mapper.DefaultCompostoMapper;
import br.net.mirante.singular.form.wicket.model.MInstanceRootModel;
import br.net.mirante.singular.form.wicket.model.MInstanciaCampoModel;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSCol;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSRow;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * This mapper extends a DefaultCompostoMapper for the case where a MAnnotationView is
 * selected. Its responsibility is to render a grid that a allows a AnnotationComponent
 * to be proper rendered on screen.
 *
 * @author Fabricio Buzeto
 */
public class AnnotationDefaultMapper extends DefaultCompostoMapper {
    private static final Logger LOGGER = Logger.getLogger(AnnotationDefaultMapper.class.getName());

    public void buildView(WicketBuildContext ctx) {
        new AnnotationBuilder(ctx).buildView();
    }

    public static class AnnotationBuilder extends CompostoViewBuilder{

        public AnnotationBuilder(WicketBuildContext ctx) {
            super(ctx);
        }

        protected BSGrid createCompositeGrid(WicketBuildContext ctx) {
            if(ctx.getViewMode().isEdition()){
                LOGGER.warning("AnnotationView only works during readonly mode, otherwise it renders the same as CompositeView");
                return super.createCompositeGrid(ctx);
            }

            final BSContainer<?> parentCol = ctx.getContainer();
            BSRow superRow = parentCol.newGrid().newRow();

            final BSGrid formGrid = superRow.newCol(9).setCssClass("col-sm-9").newGrid();

            addCommentColumn(superRow);
            configureLabel(formGrid);

            formGrid.add(DisabledClassBehavior.getInstance());
            formGrid.setDefaultModel(model);
            return formGrid;
        }

        private void configureLabel(BSGrid formGrid) {
            BSCol titleColumn = addLabelIfNeeded(formGrid);
//            if(titleColumn == null) {
//                titleColumn = grid.newColInRow();
//            }
//            titleColumn.appendTag("div", true, "style=\"float: right;\"",
//                    (id) -> new AnnotationComponent(id, model, new MInstanceRootModel(model.getObject())));
        }

        private void addCommentColumn(BSRow superRow) {
            final BSGrid ngrid = superRow.newCol(3).setCssClass("col-sm-3 .hidden-xs").newGrid();
            ngrid.newRow().appendTag("div", true, "style=\"float: right;\"",
                    (id) -> {
                        AnnotationComponent components =
                                new AnnotationComponent(id, (MAnnotationView) ctx.getView(),model);
                        return components;
                    });
            ;
        }

        /*protected void buildField(UIBuilderWicket wicketBuilder, final BSRow row,
                                  final MInstanciaCampoModel<MInstancia> mCampo) {

            final MTipo<?> type = mCampo.getMInstancia().getMTipo();
            final MInstancia iCampo = mCampo.getObject();
            final ViewMode viewMode = ctx.getViewMode();

            if (iCampo instanceof MIComposto) {
                final BSCol col = configureColspan(ctx, type, iCampo, row.newCol());
                WicketBuildContext childCtx = ctx.createChild(col.newGrid().newColInRow(), true, mCampo);
                wicketBuilder.build(childCtx, viewMode);
            } else {
                BSCol col = row.newCol();
                if(!ctx.getViewMode().isEdition()) {
                    col.appendTag("div", true, "style=\"float: right;\"", (id) -> new AnnotationComponent(id, iCampo, iCampo));
                }
                WicketBuildContext childCtx = ctx.createChild(configureColspan(ctx, type, iCampo, col), true, mCampo);
                wicketBuilder.build(childCtx, viewMode);
            }
        }*/
    }
}
