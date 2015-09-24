package br.net.mirante.singular.form.wicket.mapper;

import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.model.MInstanciaCampoModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.IBSComponentFactory;
import br.net.mirante.singular.util.wicket.bootstrap.layout.IBSGridCol.BSGridSize;
import br.net.mirante.singular.util.wicket.bootstrap.layout.table.BSTRow;

public class TableRowCompostoMapper implements IWicketComponentMapper {
    @Override
    @SuppressWarnings("unchecked")
    public void buildView(WicketBuildContext ctx, MView view, IModel<? extends MInstancia> model) {
        MInstancia instancia = model.getObject();
        MIComposto composto = (MIComposto) instancia;
        MTipoComposto<MIComposto> tComposto = (MTipoComposto<MIComposto>) composto.getMTipo();

        BSContainer<?> parentCol = ctx.getContainer();
        BSTRow tr = parentCol.newTag("tr", true, "",
            (IBSComponentFactory<BSTRow>) id -> new BSTRow(id, BSGridSize.XS));

        for (String nomeCampo : tComposto.getCampos()) {
            final MTipo<?> tCampo = tComposto.getCampo(nomeCampo);
            final MInstanciaCampoModel<MInstancia> mCampo = new MInstanciaCampoModel<>(model, tCampo.getNomeSimples());
            UIBuilderWicket.buildForEdit(ctx.createChild(tr.newCol(), true), mCampo);
        }
    }
}