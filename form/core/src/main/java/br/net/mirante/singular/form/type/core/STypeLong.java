/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.type.core;

import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeSimple;
import org.apache.commons.lang3.StringUtils;

@SInfoType(name = "Long", spackage = SPackageCore.class)
public class STypeLong extends STypeSimple<SILong, Long> {

    public STypeLong() {
        super(SILong.class, Long.class);
    }

    protected STypeLong(Class<? extends SILong> classeInstancia) {
        super(classeInstancia, Long.class);
    }

    @Override
    protected Long convertNotNativeNotString(Object valor) {
        if (valor instanceof Number) {
            long longValue = ((Number) valor).longValue();
            if (longValue > Long.MAX_VALUE) {
                throw createConversionError(valor, Long.class, " Valor muito grande.", null);
            }
            if (longValue < Long.MIN_VALUE) {
                throw createConversionError(valor, Long.class, " Valor muito pequeno.", null);
            }
            return ((Number) valor).longValue();
        }
        throw createConversionError(valor);
    }

    @Override
    public Long fromString(String valor) {
        valor = StringUtils.trimToNull(valor);
        if (valor == null) {
            return null;
        }
        try {
            return Long.parseLong(valor);
        } catch (Exception e) {
            throw createConversionError(valor, Long.class, null, e);
        }
    }
}
