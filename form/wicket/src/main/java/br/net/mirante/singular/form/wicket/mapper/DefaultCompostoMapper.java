package br.net.mirante.singular.form.wicket.mapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.behavior.DisabledClassBehavior;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.model.AbstractMInstanciaModel;
import br.net.mirante.singular.form.wicket.model.MInstanciaCampoModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSCol;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSRow;
import static br.net.mirante.singular.util.wicket.util.Shortcuts.$m;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

@SuppressWarnings("serial")
public class DefaultCompostoMapper implements IWicketComponentMapper {

    static final HintKey<HashMap<String, Integer>> COL_WIDTHS = HashMap::new;
    static final HintKey<Boolean> INLINE = () -> false;

    @SuppressWarnings("unchecked")
    public void buildView(WicketBuildContext ctx) {
        new CompostoViewBuilder(ctx).buildView();
    }

    public static class CompostoViewBuilder {

        protected WicketBuildContext ctx;
        protected AbstractMInstanciaModel<? extends MInstancia> model;
        protected MIComposto instance;
        protected MTipoComposto<MIComposto> type;

        public CompostoViewBuilder(WicketBuildContext ctx){
            this.ctx = ctx;
            model = (AbstractMInstanciaModel<? extends MInstancia>) this.ctx.getModel();
            instance = ctx.getCurrenttInstance();
            type = (MTipoComposto<MIComposto>) instance.getMTipo();
        }

        @SuppressWarnings("unchecked")
        public void buildView() {
            final BSGrid grid = createCompositeGrid(ctx);
            buildFields(ctx, grid.newRow());
        }

        protected BSGrid createCompositeGrid(WicketBuildContext ctx) {
            final BSContainer<?> parentCol = ctx.getContainer();
            final BSGrid grid = parentCol.newGrid();

            addLabelIfNeeded(grid);

            grid.add(DisabledClassBehavior.getInstance());
            grid.setDefaultModel(model);
            return grid;
        }

        protected void buildFields(WicketBuildContext ctx, BSRow row) {
            for (MTipo<?> tCampo : type.getFields()) {
                buildField(ctx.getUiBuilderWicket(), row, fieldModel(tCampo));
            }
        }

        protected MInstanciaCampoModel<MInstancia> fieldModel(MTipo<?> tCampo) {
            return new MInstanciaCampoModel<>(model, tCampo.getNomeSimples());
        }


        protected BSCol addLabelIfNeeded(final BSGrid grid) {
            IModel<String> label = $m.ofValue(trimToEmpty(instance.as(AtrBasic::new).getLabel()));
            if (isNotBlank(label.getObject())) {
                BSCol column = grid.newColInRow();
                column.appendTag("h3", new Label("_title", label));
                return column;
            }
            return null;
        }

        protected void buildField(UIBuilderWicket wicketBuilder, final BSRow row,
                                final MInstanciaCampoModel<MInstancia> mCampo) {

            final MTipo<?> type = mCampo.getMInstancia().getMTipo();
            final MInstancia iCampo = mCampo.getObject();
            final ViewMode viewMode = ctx.getViewMode();

            if (iCampo instanceof MIComposto) {
                final BSCol col = configureColspan(ctx, type, iCampo, row.newCol());
                wicketBuilder.build(ctx.createChild(col.newGrid().newColInRow(), true, mCampo), viewMode);
            } else {
                wicketBuilder.build(ctx.createChild(configureColspan(ctx, type, iCampo, row.newCol()), true, mCampo), viewMode);
            }
        }

        protected BSCol configureColspan(WicketBuildContext ctx, MTipo<?> tCampo, final MInstancia iCampo, BSCol col) {
            final Map<String, Integer> hintColWidths = ctx.getHint(DefaultCompostoMapper.COL_WIDTHS);
        /*
        * Heuristica de distribuicao de tamanho das colunas, futuramente pode ser
        * parametrizado ou transoformado em uma configuracao
        */
            final int colPref;

            if (hintColWidths.containsKey(tCampo.getNome())) {
                colPref = hintColWidths.get(tCampo.getNome());
            } else {
                colPref = iCampo.as(AtrBootstrap::new).getColPreference(BSCol.MAX_COLS);
            }

            final Optional<Integer> colXs = Optional.ofNullable(iCampo.as(AtrBootstrap::new).getColXs());
            final Optional<Integer> colSm = Optional.ofNullable(iCampo.as(AtrBootstrap::new).getColSm());
            final Optional<Integer> colMd = Optional.ofNullable(iCampo.as(AtrBootstrap::new).getColMd());
            final Optional<Integer> colLg = Optional.ofNullable(iCampo.as(AtrBootstrap::new).getColLg());

            col.xs(colXs.orElse(Integer.min(colPref * 4, BSCol.MAX_COLS)));
            col.sm(colSm.orElse(Integer.min(colPref * 3, BSCol.MAX_COLS)));
            col.md(colMd.orElse(Integer.min(colPref * 2, BSCol.MAX_COLS)));
            col.lg(colLg.orElse(Integer.min(colPref, BSCol.MAX_COLS)));

            return col;
        }
    }
}