/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.type.core;

import org.opensingular.form.STypeSimple;
import org.opensingular.form.SInfoType;
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
