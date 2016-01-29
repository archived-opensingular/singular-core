package br.net.mirante.singular.showcase.view.page.form.examples.canabidiol;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoLista;
import br.net.mirante.singular.form.mform.TipoBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MListMasterDetailView;

@MInfoTipo(nome = "MTipoProdutos", pacote = MPacotePeticaoCanabidiol.class)
public class MTipoProdutos extends MTipoComposto<MIComposto>  {


    @Override
    protected void onCargaTipo(TipoBuilder tb) {
        super.onCargaTipo(tb);

        final MTipoLista<MTipoDescricaoProduto, MIComposto> experiencias = this.addCampoListaOf("produtos", MTipoDescricaoProduto.class);

        MTipoDescricaoProduto desc = experiencias.getTipoElementos();

        experiencias
                .withView(new MListMasterDetailView()
                        .col(desc.getNomeComercial())
                        .col(desc.getComposicao())
                        .col(desc.getDescricaoQuantidade(), "Quantidade Solicitada"))
                .as(AtrBasic::new)
                .label("Descrição do Produto");

    }


}
