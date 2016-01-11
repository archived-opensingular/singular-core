package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.wicket.AtrBootstrap;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.behavior.DisabledClassBehavior;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.mapper.comment.CommentComponent;
import br.net.mirante.singular.form.wicket.model.MInstanciaCampoModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSCol;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSRow;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import static br.net.mirante.singular.util.wicket.util.Shortcuts.$m;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

@SuppressWarnings("serial")
public class DefaultCompostoMapper implements IWicketComponentMapper {

    static final HintKey<HashMap<String, Integer>> COL_WIDTHS = HashMap::new;
    static final HintKey<Boolean> INLINE = () -> false;


    @SuppressWarnings("unchecked")
    public void buildView(WicketBuildContext ctx) {

        final IModel<? extends MInstancia> model = ctx.getModel();
        final MIComposto instance =  ctx.getCurrenttInstance();
        final MTipoComposto<MIComposto> tComposto = (MTipoComposto<MIComposto>) instance.getMTipo();

        final BSContainer<?> parentCol = ctx.getContainer();

        BSRow superRow = parentCol.newGrid().newRow();

        {
//            final BSGrid grid = parentCol.newGrid();
            final BSGrid grid = superRow.newCol(9).setCssClass("col-sm-9").newGrid();

            addLabelIfNeeded(instance, grid);

            final BSRow row = grid.newRow();

            grid.add(DisabledClassBehavior.getInstance());
            grid.setDefaultModel(model);

            for (MTipo<?> tCampo : tComposto.getFields()) {
                buildField(ctx.getUiBuilderWicket(), ctx, row, new MInstanciaCampoModel<>(model, tCampo.getNomeSimples()));
            }
        }
        {
            final BSGrid grid = superRow.newCol(3).setCssClass("col-sm-3 .hidden-xs").newGrid();
            final BSRow row = grid.newRow();
        }
    }

    private void addLabelIfNeeded(final MInstancia instancia, final BSGrid grid) {
        IModel<String> label = $m.ofValue(trimToEmpty(instancia.as(AtrBasic::new).getLabel()));
        if (isNotBlank(label.getObject())) {
            BSCol column = grid.newColInRow();
            column.appendTag("h3", new Label("_title", label));
        }
    }

    private void buildField(UIBuilderWicket wicketBuilder, WicketBuildContext ctx, final BSRow row,
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
            col.appendTag("div",true, "style=\"float: right;\"", (id) -> new CommentComponent(id, iCampo));
            WicketBuildContext childCtx = ctx.createChild(configureColspan(ctx, type, iCampo, col), true, mCampo);
            wicketBuilder.build(childCtx, viewMode);
        }
    }

    private BSCol configureColspan(WicketBuildContext ctx, MTipo<?> tCampo, final MInstancia iCampo, BSCol col) {
        final Map<String, Integer> hintColWidths = ctx.getHint(COL_WIDTHS);
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