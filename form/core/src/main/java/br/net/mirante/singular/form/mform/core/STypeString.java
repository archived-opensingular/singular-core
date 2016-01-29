package br.net.mirante.singular.form.mform.core;

import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.STypeSimples;
import br.net.mirante.singular.form.mform.basic.view.MTextAreaView;

@MInfoTipo(nome = "String", pacote = SPackageCore.class)
public class STypeString extends STypeSimples<SIString, String> {

    public STypeString() {
        super(SIString.class, String.class);
    }

    protected STypeString(Class<? extends SIString> classeInstancia) {
        super(classeInstancia, String.class);
    }

    public boolean getValorAtributoTrim() {
        return getValorAtributo(SPackageCore.ATR_TRIM);
    }

    public boolean getValorAtributoEmptyToNull() {
        return getValorAtributo(SPackageCore.ATR_EMPTY_TO_NULL);
    }

    public STypeString withValorAtributoTrim(boolean valor) {
        return (STypeString) with(SPackageCore.ATR_TRIM, valor);
    }

    public <T extends Enum<T>> STypeString withSelectionOf(Class<T> enumType) {
        T[] ops = enumType.getEnumConstants();
        String[] nomes = new String[ops.length];
        for (int i = 0; i < ops.length; i++) {
            nomes[i] = ops[i].toString();
        }
        return (STypeString) super.withSelectionOf(nomes);
    }

    /**
     * Configura o tipo para utilizar a view {@link MTextAreaView} e invoca o initializer 
     */
    @SafeVarargs
    public final STypeString withTextAreaView(Consumer<MTextAreaView>...initializers) {
        withView(new MTextAreaView(), initializers);
        return this;
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
