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

import org.apache.commons.lang3.StringUtils;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeSimple;

import java.math.BigDecimal;

@SInfoType(name = "Decimal", spackage = SPackageCore.class)
public class STypeDecimal extends STypeSimple<SIBigDecimal, BigDecimal> {

    public STypeDecimal() {
        super(SIBigDecimal.class, BigDecimal.class);
    }

    protected STypeDecimal(Class<? extends SIBigDecimal> instanceClass) {
        super(instanceClass, BigDecimal.class);
    }

    @Override
    protected BigDecimal convertNotNativeNotString(Object value) {
        if (value instanceof Number) {
            return new BigDecimal(value.toString());
        }
        throw createConversionError(value);
    }

    @Override
    public BigDecimal fromString(String value) {
        String v = StringUtils.trimToNull(value);
        if (v == null) {
            return null;
        }

        try {
            return new BigDecimal(v.replaceAll("\\.", "").replaceAll(",", "."));
        } catch (Exception e) {
            throw createConversionError(value, BigDecimal.class, null, e);
        }
    }

    @Override
    public BigDecimal fromStringPersistence(String value) {
        String v = StringUtils.trimToNull(value);
        if (v == null) {
            return null;
        }

        try {
            return new BigDecimal(v);
        } catch (Exception e) {
            throw createConversionError(value, BigDecimal.class, null, e);
        }
    }
}
