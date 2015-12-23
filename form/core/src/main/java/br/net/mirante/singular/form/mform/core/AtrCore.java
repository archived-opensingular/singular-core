package br.net.mirante.singular.form.mform.core;

import java.util.function.Predicate;

import br.net.mirante.singular.form.mform.MAtributoEnabled;
import br.net.mirante.singular.form.mform.MInstancia;
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

    public AtrCore obrigatorio(Predicate<MInstancia> valor) {
        getAlvo().setValorAtributo(MPacoteCore.ATR_OBRIGATORIO_FUNCTION, valor);
        return this;
    }
    
    public Boolean isObrigatorio() {
        return !Boolean.FALSE.equals(getAlvo().getValorAtributo(MPacoteCore.ATR_OBRIGATORIO));
    }

    public AtrCore exists(Boolean valor) {
        getAlvo().setValorAtributo(MPacoteCore.ATR_OBRIGATORIO, valor);
        return this;
    }
    
    public AtrCore exists(Predicate<MInstancia> valor) {
        getAlvo().setValorAtributo(MPacoteCore.ATR_OBRIGATORIO_FUNCTION, valor);
        return this;
    }
    
    public boolean exists() {
        return !Boolean.FALSE.equals(getAlvo().getValorAtributo(MPacoteCore.ATR_OBRIGATORIO));
    }
}
