package br.net.mirante.singular.form.mform.util.comuns;

import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.TipoBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.MTipoString;

@MInfoTipo(nome = "CPF", pacote = MPacoteUtil.class)
public class MTipoCPF extends MTipoString {

    @Override
    protected void onCargaTipo(TipoBuilder tb) {
        super.onCargaTipo(tb);
        as(AtrBasic.class).label("CPF").basicMask("CPF");
    }
}
