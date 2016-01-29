package br.net.mirante.singular.showcase.view.page.form.examples.canabidiol;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.STypeComposto;
import br.net.mirante.singular.form.mform.STypeLista;
import br.net.mirante.singular.form.mform.TipoBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MListMasterDetailView;

@MInfoTipo(nome = "MTipoProdutos", pacote = SPackagePeticaoCanabidiol.class)
public class STypeProdutos extends STypeComposto<SIComposite> {


    @Override
    protected void onLoadType(TipoBuilder tb) {
        super.onLoadType(tb);

        final STypeLista<STypeDescricaoProduto, SIComposite> experiencias = this.addCampoListaOf("produtos", STypeDescricaoProduto.class);

        STypeDescricaoProduto desc = experiencias.getTipoElementos();

        experiencias
                .withView(new MListMasterDetailView()
                        .col(desc.getNomeComercial())
                        .col(desc.getComposicao())
                        .col(desc.getDescricaoQuantidade(), "Quantidade Solicitada"))
                .as(AtrBasic::new)
                .label("Descrição do Produto");

    }


}
