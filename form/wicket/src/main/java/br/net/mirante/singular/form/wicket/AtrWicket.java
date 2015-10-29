package br.net.mirante.singular.form.wicket;

import java.util.function.Function;

import org.apache.commons.lang3.ObjectUtils;

import br.net.mirante.singular.form.mform.MAtributoEnabled;
import br.net.mirante.singular.form.mform.MTranslatorParaAtributo;

public class AtrWicket extends MTranslatorParaAtributo {

    public AtrWicket() {}
    public AtrWicket(MAtributoEnabled alvo) {
        super(alvo);
    }

    public static <A extends MAtributoEnabled> Function<A, AtrWicket> factory() {
        return a -> new AtrWicket(a);
    }

    public AtrWicket larguraPref(Integer valor) {
        getAlvo().setValorAtributo(MPacoteWicket.ATR_LARGURA_PREF, valor);
        return this;
    }

    public Integer getLarguraPref() {
        return getAlvo().getValorAtributo(MPacoteWicket.ATR_LARGURA_PREF);
    }

    public Integer getLarguraPref(Integer defaultValue) {
        return ObjectUtils.defaultIfNull(getLarguraPref(), defaultValue);
    }
}
