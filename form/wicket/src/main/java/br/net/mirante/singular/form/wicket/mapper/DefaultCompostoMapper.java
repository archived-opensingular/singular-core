package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.wicket.AtrWicket;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.behavior.DisabledClassBehavior;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.model.MInstanciaCampoModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSCol;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSRow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import java.util.HashMap;
import java.util.Map;

import static br.net.mirante.singular.util.wicket.util.Shortcuts.$m;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

@SuppressWarnings("serial")
public class DefaultCompostoMapper implements IWicketComponentMapper {

    static final HintKey<HashMap<String, Integer>> COL_WIDTHS = HashMap::new;
    static final HintKey<Boolean> INLINE = () -> false;

    @Override
    public void buildForEdit(WicketBuildContext ctx, MView view, IModel<? extends MInstancia> model){
        buildView(ctx, view, model, ViewMode.EDITION);
    }

    @Override
    public void buildForView(WicketBuildContext ctx, MView view, IModel<? extends MInstancia> model){
        buildView(ctx, view, model, ViewMode.VISUALIZATION);
    }

    @SuppressWarnings("unchecked")
    public void buildView(WicketBuildContext ctx, MView view, IModel<? extends MInstancia> model, ViewMode viewMode) {
        final MIComposto instance = (MIComposto) model.getObject();
        final MTipoComposto<MIComposto> tComposto = (MTipoComposto<MIComposto>) instance.getMTipo();

        final BSContainer<?> parentCol = ctx.getContainer();
        final BSGrid grid = parentCol.newGrid();

        addLabelIfNeeded(instance, grid);

        final BSRow row = grid.newRow();

        grid.add(DisabledClassBehavior.getInstance());
        grid.setDefaultModel(model);

        for (MTipo<?> tCampo : tComposto.getFields()) {
            buildField(ctx.getUiBuilderWicket(), ctx, row, new MInstanciaCampoModel<>(model, tCampo.getNomeSimples()), viewMode);
        }
    }

    private void addLabelIfNeeded(final MInstancia instancia, final BSGrid grid) {
        IModel<String> label = $m.ofValue(trimToEmpty(instancia.as(AtrBasic::new).getLabel()));
        if (isNotBlank(label.getObject())) {
            BSCol column = grid.newColInRow();
            column.appendTag("h3", new Label("_title", label));
        }
    }

    private void buildField(UIBuilderWicket wicketBuilder, WicketBuildContext ctx, final BSRow row, final MInstanciaCampoModel<MInstancia> mCampo,
                            ViewMode viewMode) {
        MTipo<?> type = mCampo.getMInstancia().getMTipo();
        final MInstancia iCampo = mCampo.getObject();
        if (iCampo instanceof MIComposto) {
            final BSCol col = configureColspan(ctx, type, iCampo, row.newCol());
            wicketBuilder.build(ctx.createChild(col.newGrid().newColInRow(), true), mCampo, viewMode);
        } else {
            wicketBuilder.build(ctx.createChild(configureColspan(ctx, type, iCampo, row.newCol()), true), mCampo, viewMode);
        }
    }

    private BSCol configureColspan(WicketBuildContext ctx, MTipo<?> tCampo, final MInstancia iCampo, BSCol col) {
        final Map<String, Integer> hintColWidths = ctx.getHint(COL_WIDTHS);
        /*
        * Heuristica de distribuicao de tamanho das colunas, futuramente pode ser
        * parametrizado ou transoformado em uma configuracao
        */
        final int colspanLG = (hintColWidths.containsKey(tCampo.getNome()))
                ? hintColWidths.get(tCampo.getNome())
                : iCampo.as(AtrWicket::new).getLarguraPref(BSCol.MAX_COLS);
        final int colspanMD = Integer.min(colspanLG * 2, BSCol.MAX_COLS);
        final int colspanSM = Integer.min(colspanLG * 3, BSCol.MAX_COLS);

        col.lg(colspanLG);
        col.md(colspanMD);
        col.sm(colspanSM);

        return col;
    }
}