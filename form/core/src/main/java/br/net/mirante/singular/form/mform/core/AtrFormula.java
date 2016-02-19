package br.net.mirante.singular.form.mform.core;

import java.util.function.Supplier;

import br.net.mirante.singular.form.mform.MTranslatorParaAtributo;

public class AtrFormula extends MTranslatorParaAtributo {

    public AtrFormula set(Supplier<Object> supplier) {
        getTipo().setValorAtributo(SPackageCore.ATR_FORMULA, null, null);
        return this;
    }

}
