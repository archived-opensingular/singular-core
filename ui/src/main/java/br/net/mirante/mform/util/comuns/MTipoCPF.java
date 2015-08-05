package br.net.mirante.mform.util.comuns;

import br.net.mirante.mform.MFormTipo;
import br.net.mirante.mform.TipoBuilder;
import br.net.mirante.mform.basic.ui.AtrBasic;
import br.net.mirante.mform.core.MTipoString;

@MFormTipo(nome = "CPF", pacote = MPacoteUtil.class)
public class MTipoCPF extends MTipoString {

    @Override
    protected void onCargaTipo(TipoBuilder tb) {
        super.onCargaTipo(tb);
        as(AtrBasic.class).label("CPF").tamanhoMaximo(11);

    }

}
