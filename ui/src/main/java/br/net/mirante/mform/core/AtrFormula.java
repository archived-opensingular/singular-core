package br.net.mirante.mform.core;

import java.util.function.Supplier;

import br.net.mirante.mform.MTranslatorParaAtributo;

public class AtrFormula extends MTranslatorParaAtributo {

    public AtrFormula set(Supplier<Object> supplier) {
        getTipo().setValorAtributo(MPacoteCore.ATR_FORMULA, null, null);
        return this;
    }

}
