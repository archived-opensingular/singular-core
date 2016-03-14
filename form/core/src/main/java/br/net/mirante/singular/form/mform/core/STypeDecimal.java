/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.core;

import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.STypeSimple;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

@SInfoType(name = "Decimal", spackage = SPackageCore.class)
public class STypeDecimal extends STypeSimple<SIBigDecimal, BigDecimal> {

    public STypeDecimal() {
        super(SIBigDecimal.class, BigDecimal.class);
    }

    protected STypeDecimal(Class<? extends SIBigDecimal> classeInstancia) {
        super(classeInstancia, BigDecimal.class);
    }

    @Override
    protected BigDecimal convertNotNativeNotString(Object valor) {
        if (valor instanceof Number) {
            return new BigDecimal(valor.toString());
        }
        throw createConversionError(valor);
    }

    @Override
    public BigDecimal fromString(String valor) {
        valor = StringUtils.trimToNull(valor);
        if (valor == null) {
            return null;
        }

        try {
            return new BigDecimal(valor.replaceAll("\\.", "").replaceAll(",", "."));

        } catch (Exception e) {
            throw createConversionError(valor, BigDecimal.class, null, e);
        }
    }

    @Override
    public BigDecimal fromStringPersistence(String valor) {
        valor = StringUtils.trimToNull(valor);
        if (valor == null) {
            return null;
        }

        try {
            return new BigDecimal(valor);

        } catch (Exception e) {
            throw createConversionError(valor, BigDecimal.class, null, e);
        }
    }
}
