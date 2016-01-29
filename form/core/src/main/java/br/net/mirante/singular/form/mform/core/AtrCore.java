package br.net.mirante.singular.form.mform.core;

import java.util.function.Predicate;

import br.net.mirante.singular.form.mform.MAtributoEnabled;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.MTranslatorParaAtributo;

public class AtrCore extends MTranslatorParaAtributo {

    public AtrCore() {}
    public AtrCore(MAtributoEnabled alvo) {
        super(alvo);
    }

    public AtrCore obrigatorio() {
        return obrigatorio(true);
    }


    public AtrCore obrigatorio(Boolean value) {
        getAlvo().setValorAtributo(SPackageCore.ATR_OBRIGATORIO, value);
        return this;
    }

    public AtrCore obrigatorio(Predicate<SInstance> valor) {
        getAlvo().setValorAtributo(SPackageCore.ATR_OBRIGATORIO_FUNCTION, valor);
        return this;
    }
    
    public Boolean isObrigatorio() {
        return !Boolean.FALSE.equals(getAlvo().getValorAtributo(SPackageCore.ATR_OBRIGATORIO));
    }

    public AtrCore exists(Boolean valor) {
        getAlvo().setValorAtributo(SPackageCore.ATR_OBRIGATORIO, valor);
        return this;
    }
    
    public AtrCore exists(Predicate<SInstance> valor) {
        getAlvo().setValorAtributo(SPackageCore.ATR_OBRIGATORIO_FUNCTION, valor);
        return this;
    }
    
    public boolean exists() {
        return !Boolean.FALSE.equals(getAlvo().getValorAtributo(SPackageCore.ATR_OBRIGATORIO));
    }
}
