package br.net.mirante.singular.form.wicket.mapper;

import static br.net.mirante.singular.util.wicket.util.Shortcuts.$m;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

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
import br.net.mirante.singular.form.wicket.model.MInstanciaCampoModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSCol;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSRow;

@SuppressWarnings("serial")
public class DefaultCompostoMapper implements IWicketComponentMapper {

    static final HintKey<HashMap<String, Integer>> COL_WIDTHS = HashMap::new;
    static final HintKey<Boolean>                  INLINE     = () -> false;

    @Override
    @SuppressWarnings("unchecked")
    public void buildView(WicketBuildContext ctx, MView view, IModel<? extends MInstancia> model) {
        final MIComposto instance = (MIComposto) model.getObject();
        final MTipoComposto<MIComposto> tComposto = (MTipoComposto<MIComposto>) instance.getMTipo();

        final BSContainer<?> parentCol = ctx.getContainer();
        final BSGrid grid = parentCol.newGrid();
        
        addLabelIfNeeded(instance, grid);
        
        final BSRow row = grid.newRow();

        grid.add(DisabledClassBehavior.getInstance());
        grid.setDefaultModel(model);

        for (MTipo<?> tCampo : tComposto.getFields()) {
            buildField(ctx, row, new MInstanciaCampoModel<>(model, tCampo.getNomeSimples()));
        }
    }

    private void addLabelIfNeeded(final MInstancia instancia, final BSGrid grid) {
        IModel<String> label = $m.ofValue(trimToEmpty(instancia.as(AtrBasic::new).getLabel()));
        if (isNotBlank(label.getObject())){
            BSCol column = grid.newColInRow();
            column.appendTag("h3", new Label("_title", label));
        }
    }

    private void buildField(WicketBuildContext ctx, final BSRow row, final MInstanciaCampoModel<MInstancia> mCampo) {
        MTipo<?> type = mCampo.getMInstancia().getMTipo();
        final MInstancia iCampo = mCampo.getObject();
        final int colspan = defineColSpan(ctx, type, iCampo);
        if (iCampo instanceof MIComposto) {
            final BSCol col = row.newCol().md(colspan);
            UIBuilderWicket.buildForEdit(ctx.createChild(col.newGrid().newColInRow(), true), mCampo);
        } else {
            UIBuilderWicket.buildForEdit(ctx.createChild(row.newCol().md(colspan), true), mCampo);
        }
    }

    private int defineColSpan(WicketBuildContext ctx, MTipo<?> tCampo, final MInstancia iCampo) {
        final Map<String, Integer> hintColWidths = ctx.getHint(COL_WIDTHS);
        final int colspan = (hintColWidths.containsKey(tCampo.getNome()))
            ? hintColWidths.get(tCampo.getNome())
            : iCampo.as(AtrWicket::new).getLarguraPref(BSCol.MAX_COLS);
        return colspan;
    }
}