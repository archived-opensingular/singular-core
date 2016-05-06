/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.type.util;

import br.net.mirante.singular.form.SISimple;
import br.net.mirante.singular.form.type.core.SIComparable;

import java.time.YearMonth;

public class SIYearMonth extends SISimple<YearMonth> implements SIComparable<YearMonth> {

    public SIYearMonth() {
    }

    public YearMonth getJavaYearMonth() {
        if (isEmptyOfData()) {
            return null;
        }

        return YearMonth.of(getAno(), getMes());
    }

    public Integer getAno() {
        if (isEmptyOfData()) {
            return null;
        }
        return getValue().getYear();
    }

    public Integer getMes() {
        if (isEmptyOfData()) {
            return null;
        }
        return getValue().getMonthValue();
    }

    @Override
    public STypeYearMonth getType() {
        return (STypeYearMonth) super.getType();
    }
}
