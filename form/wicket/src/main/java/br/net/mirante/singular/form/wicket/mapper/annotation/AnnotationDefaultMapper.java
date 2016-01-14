package br.net.mirante.singular.form.wicket.mapper.annotation;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
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
 * Created by nuk on 14/01/16.
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

            final BSGrid grid = superRow.newCol(9).setCssClass("col-sm-9").newGrid();

            final BSGrid ngrid = superRow.newCol(3).setCssClass("col-sm-3 .hidden-xs").newGrid();
            ngrid.newRow().appendTag("div", true, "style=\"float: right;\"",
                    (id) -> {
                        /*MTipoString stringType = model.getMInstancia().getDicionario().getTipo(MTipoString.class);

                        MInstanceRootModel target = new MInstanceRootModel(stringType.novaInstancia()){
                            @Override
                            public void setObject(MInstancia object) {
                                super.setObject(object);
                            }
                        };
                        MInstanciaValorModel target1 = new MInstanciaValorModel(target);*/
                        IModel target1 = new Model<String>(){
                            @Override
                            public void setObject(String object) {
                                annotatedInstance().text(object);
                            }

                            @Override
                            public String getObject() {
                                return annotatedInstance().text();
                            }

                            private AtrAnnotation annotatedInstance() {
                                return model.getMInstancia().as(AtrAnnotation::new);
                            }
                        };
                        AnnotationComponent components = new AnnotationComponent(id, model, target1);
                        return components;
                    });;

            BSCol titleColumn = addLabelIfNeeded(grid);
//            if(titleColumn == null) {
//                titleColumn = grid.newColInRow();
//            }
//            titleColumn.appendTag("div", true, "style=\"float: right;\"",
//                    (id) -> new AnnotationComponent(id, model, new MInstanceRootModel(model.getObject())));

            grid.add(DisabledClassBehavior.getInstance());
            grid.setDefaultModel(model);
            return grid;
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
