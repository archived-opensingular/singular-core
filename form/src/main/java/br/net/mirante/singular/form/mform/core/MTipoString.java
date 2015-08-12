package br.net.mirante.singular.form.mform.core;

import org.apache.commons.lang.StringUtils;

import br.net.mirante.singular.form.mform.MFormTipo;
import br.net.mirante.singular.form.mform.MProviderOpcoes;
import br.net.mirante.singular.form.mform.MTipoSimples;

@MFormTipo(nome = "String", pacote = MPacoteCore.class)
public class MTipoString extends MTipoSimples<MIString, String> {

    public MTipoString() {
        super(MIString.class, String.class);
    }

    protected MTipoString(Class<? extends MIString> classeInstancia) {
        super(classeInstancia, String.class);
    }

    public boolean getValorAtributoTrim() {
        return getValorAtributo(MPacoteCore.ATR_TRIM);
    }

    public boolean getValorAtributoEmptyToNull() {
        return getValorAtributo(MPacoteCore.ATR_EMPTY_TO_NULL);
    }

    public MTipoString withValorAtributoTrim(boolean valor) {
        return (MTipoString) with(MPacoteCore.ATR_TRIM, valor);
    }

    public <T extends Enum<T>> MProviderOpcoes selectionOf(Class<T> enumType) {
        T[] ops = enumType.getEnumConstants();
        String[] nomes = new String[ops.length];
        for (int i = 0; i < ops.length; i++) {
            nomes[i] = ops[i].toString();
        }
        return super.selectionOf(nomes);
    }

    @Override
    public String converter(Object valor) {
        String s = super.converter(valor);
        if (s != null) {
            if (getValorAtributoEmptyToNull()) {
                if (getValorAtributoTrim()) {
                    s = StringUtils.trimToNull(s);
                } else if (StringUtils.isEmpty(s)) {
                    s = null;
                }
            } else if (getValorAtributoTrim()) {
                s = StringUtils.trim(s);
            }
        }
        return s;
    }

    @Override
    public String converterNaoNativoNaoString(Object valor) {
        return valor.toString();
    }
}
