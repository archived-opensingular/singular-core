package br.net.mirante.singular.showcase.view.page.form.examples.canabidiol;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.TipoBuilder;

@MInfoTipo(nome = "MTipoDescricaoProduto", pacote = MPacotePeticaoCanabidiol.class)
public class MTipoDescricaoProduto extends MTipoComposto<MIComposto> implements CanabidiolUtil {


    @Override
    protected void onCargaTipo(TipoBuilder tb) {
        super.onCargaTipo(tb);


    }


}
