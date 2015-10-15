package br.net.mirante.singular.form.mform.core;

import br.net.mirante.singular.form.mform.MAtributoEnabled;
import br.net.mirante.singular.form.mform.MTranslatorParaAtributo;

public class AtrCore extends MTranslatorParaAtributo {

    public AtrCore() {}
    public AtrCore(MAtributoEnabled alvo) {
        super(alvo);
    }

    public AtrCore obrigatorio(Boolean valor) {
        getAlvo().setValorAtributo(MPacoteCore.ATR_OBRIGATORIO, valor);
        return this;
    }

    public Boolean isObrigatorio() {
        return getAlvo().getValorAtributo(MPacoteCore.ATR_OBRIGATORIO);
    }
}
