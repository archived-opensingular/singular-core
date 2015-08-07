package br.net.mirante.singular.ui.mform.util.comuns;

import br.net.mirante.singular.ui.mform.MFormTipo;
import br.net.mirante.singular.ui.mform.TipoBuilder;
import br.net.mirante.singular.ui.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.ui.mform.core.MTipoString;

@MFormTipo(nome = "CPF", pacote = MPacoteUtil.class)
public class MTipoCPF extends MTipoString {

    @Override
    protected void onCargaTipo(TipoBuilder tb) {
        super.onCargaTipo(tb);
        as(AtrBasic.class).label("CPF").tamanhoMaximo(11);

    }

}
