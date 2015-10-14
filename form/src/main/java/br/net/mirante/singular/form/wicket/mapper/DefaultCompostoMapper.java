package br.net.mirante.singular.form.wicket.mapper;

import static br.net.mirante.singular.util.wicket.util.Shortcuts.*;
import static org.apache.commons.lang3.StringUtils.*;

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
import br.net.mirante.singular.form.wicket.model.instancia.MInstanciaCampoModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSCol;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSRow;

public class DefaultCompostoMapper implements IWicketComponentMapper {
    static final HintKey<HashMap<String, Integer>> COL_WIDTHS = () -> new HashMap<>();
    static final HintKey<Boolean>                  INLINE     = () -> false;

    @Override
    @SuppressWarnings("unchecked")
    public void buildView(WicketBuildContext ctx, MView view, IModel<? extends MInstancia> model) {
        final Map<String, Integer> hintColWidths = ctx.getHint(COL_WIDTHS);

        final MInstancia instancia = model.getObject();
        final MIComposto composto = (MIComposto) instancia;
        final MTipoComposto<MIComposto> tComposto = (MTipoComposto<MIComposto>) composto.getMTipo();

        final BSContainer<?> parentCol = ctx.getContainer();
        final BSGrid grid = parentCol.newGrid();
        final BSRow row = grid.newRow();

        grid.add(DisabledClassBehavior.getInstance());

        for (String nomeCampo : tComposto.getCampos()) {
            final MTipo<?> tCampo = tComposto.getCampo(nomeCampo);
            final MInstanciaCampoModel<MInstancia> mCampo = new MInstanciaCampoModel<>(model, tCampo.getNomeSimples());
            final MInstancia iCampo = mCampo.getObject();
            final IModel<String> label = $m.ofValue(trimToEmpty(iCampo.as(AtrBasic::new).getLabel()));
            final int colspan = (hintColWidths.containsKey(nomeCampo))
                ? hintColWidths.get(nomeCampo)
                : iCampo.as(AtrWicket::new).getLarguraPref(BSCol.MAX_COLS);
            if (iCampo instanceof MIComposto) {
                final BSCol col = row.newCol().md(colspan);
                if (isNotBlank(label.getObject()))
                    col.appendTag("h3", new Label("_title", label));
                UIBuilderWicket.buildForEdit(ctx.createChild(col.newGrid().newColInRow(), true), mCampo);
            } else {
                UIBuilderWicket.buildForEdit(ctx.createChild(row.newCol().md(colspan), true), mCampo);
            }
        }
    }
}