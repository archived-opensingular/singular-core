package br.net.mirante.singular.form.mform.core;

import org.apache.commons.lang3.StringUtils;

import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.STypeSimple;

@SInfoType(name = "Integer", spackage = SPackageCore.class)
public class STypeInteger extends STypeSimple<SIInteger, Integer> {

    public STypeInteger() {
        super(SIInteger.class, Integer.class);
    }

    protected STypeInteger(Class<? extends SIInteger> classeInstancia) {
        super(classeInstancia, Integer.class);
    }

    @Override
    protected Integer convertNotNativeNotString(Object valor) {
        if (valor instanceof Number) {
            long longValue = ((Number) valor).longValue();
            if (longValue > Integer.MAX_VALUE) {
                throw createConversionError(valor, Integer.class, " Valor muito grande.", null);
            }
            if (longValue < Integer.MIN_VALUE) {
                throw createConversionError(valor, Integer.class, " Valor muito pequeno.", null);
            }
            return ((Number) valor).intValue();
        }
        throw createConversionError(valor);
    }

    @Override
    public Integer fromString(String valor) {
        valor = StringUtils.trimToNull(valor);
        if (valor == null) {
            return null;
        }
        try {
            return Integer.parseInt(valor);
        } catch (Exception e) {
            throw createConversionError(valor, Integer.class, null, e);
        }
    }
}
