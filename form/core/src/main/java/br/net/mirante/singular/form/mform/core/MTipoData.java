package br.net.mirante.singular.form.mform.core;

import java.util.Date;

import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.MTipoSimples;

@MInfoTipo(nome = "Data", pacote = MPacoteCore.class)
public class MTipoData extends MTipoSimples<MIData, Date> {

    public MTipoData() {
        super(MIData.class, Date.class);
    }

    protected MTipoData(Class<? extends MIData> classeInstancia) {
        super(classeInstancia, Date.class);
    }

//    public static ValidadorDataBuilder validadorBuilder() {
//        return new ValidadorDataBuilder();
//    }
}
