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

import java.math.BigInteger;

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
            BigInteger bigIntegerValue = new BigInteger(String.valueOf(valor));
            if (bigIntegerValue.compareTo(new BigInteger(String.valueOf(Long.MAX_VALUE))) > 0) {
                throw createConversionError(valor, Long.class, " Valor muito grande.", null);
            }
            if (bigIntegerValue.compareTo(new BigInteger(String.valueOf(Long.MIN_VALUE))) < 0) {
                throw createConversionError(valor, Long.class, " Valor muito pequeno.", null);
            }
            return bigIntegerValue.longValue();
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
            return Long.valueOf(valor);
        } catch (Exception e) {
            throw createConversionError(valor, Long.class, null, e);
        }
    }
}
