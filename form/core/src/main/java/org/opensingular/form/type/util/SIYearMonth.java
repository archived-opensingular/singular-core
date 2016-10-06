/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.type.util;

import org.opensingular.form.type.core.SIComparable;
import org.opensingular.form.SISimple;

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
