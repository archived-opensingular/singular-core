package br.net.mirante.singular.form.wicket.mapper;

import static br.net.mirante.singular.util.wicket.util.Shortcuts.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.model.MInstanciaCampoModel;
import br.net.mirante.singular.form.wicket.model.MInstanciaItemListaModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.IBSGridCol.BSGridSize;
import br.net.mirante.singular.util.wicket.bootstrap.layout.TemplatePanel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.table.BSTRow;
import br.net.mirante.singular.util.wicket.bootstrap.layout.table.BSTSection;

public class ListaTableMapper implements IWicketComponentMapper {
    @Override
    @SuppressWarnings("unchecked")
    public void buildView(WicketBuildContext ctx, MView view, IModel<? extends MInstancia> model) {
        final IModel<MILista<MInstancia>> mLista = $m.get(() -> (MILista<MInstancia>) model.getObject());
        final MILista<?> iLista = mLista.getObject();
        final IModel<String> label = $m.ofValue(trimToEmpty(iLista.as(MPacoteBasic.aspect()).getLabel()));

        ctx.setHint(ControlsFieldComponentMapper.NO_DECORATION, true);

        final BSContainer<?> parentCol = ctx.getContainer();
        if (isNotBlank(label.getObject()))
            parentCol.appendTag("h3", new Label("_title", label));

        final TemplatePanel template = parentCol.newTag("div", new TemplatePanel("t", () -> ""
            + "<table wicket:id='table' class='table table-condensed table-unstyled'>"
            + "<thead wicket:id='head'></thead>"
            + "<tbody>"
            + "<wicket:container wicket:id='items'><tr wicket:id='row'></tr></wicket:container>"
            + "</tbody>"
            + "</table>"));
        final WebMarkupContainer table = new WebMarkupContainer("table");
        final ListaTableMapper.TRsView trView = new TRsView("items", mLista, ctx);
        final BSTSection thead = new BSTSection("head").setTagName("thead");

        final MTipo<?> tElementos = iLista.getTipoElementos();
        if (iLista.getTipoElementos() instanceof MTipoComposto<?>) {
            MTipoComposto<MIComposto> tComposto = (MTipoComposto<MIComposto>) tElementos;
            BSTRow tr = thead.newRow();
            for (String nomeCampo : tComposto.getCampos()) {
                final MTipo<?> tCampo = tComposto.getCampo(nomeCampo);
                tr.newTHeaderCell($m.ofValue(tCampo.as(AtrBasic::new).getLabel()));
            }
        } else {
            thead.setVisible(false);
        }

        template
            .add(table
                .add(thead)
                .add(trView));
    }
    private static final class TRsView extends RefreshingView<MInstancia> {
        private WicketBuildContext ctx;
        private TRsView(String id, IModel<MILista<MInstancia>> model, WicketBuildContext ctx) {
            super(id, model);
            setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance());
            this.ctx = ctx;
        }
        @Override
        protected Iterator<IModel<MInstancia>> getItemModels() {
            List<IModel<MInstancia>> list = new ArrayList<>();
            MILista<?> miLista = (MILista<?>) getDefaultModelObject();
            for (int i = 0; i < miLista.size(); i++)
                list.add(new MInstanciaItemListaModel<>(getDefaultModel(), i));
            return list.iterator();
        }
        @Override
        @SuppressWarnings("unchecked")
        protected void populateItem(Item<MInstancia> item) {
            final IModel<MInstancia> itemModel = item.getModel();
            final BSTRow tr = new BSTRow("row", BSGridSize.MD);
            item.add(tr);

            MInstancia instancia = itemModel.getObject();
            if (instancia instanceof MIComposto) {
                MIComposto composto = (MIComposto) instancia;
                MTipoComposto<MIComposto> tComposto = (MTipoComposto<MIComposto>) composto.getMTipo();
                for (String nomeCampo : tComposto.getCampos()) {
                    final MTipo<?> tCampo = tComposto.getCampo(nomeCampo);
                    final MInstanciaCampoModel<MInstancia> mCampo = new MInstanciaCampoModel<>(itemModel, tCampo.getNomeSimples());
                    UIBuilderWicket.buildForEdit(ctx.createChild(tr.newCol(), true), mCampo);
                }
            } else {
                UIBuilderWicket.buildForEdit(ctx.createChild(tr.newCol(), true), itemModel);
            }
        }
    }
}