/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.core;

import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;

public class SIDate extends SIComparable<Date> {
    public SIDate() {
    }

    public Date getDate() {
        return (Date) getValue();
    }

    public YearMonth getJavaYearMonth() {
        return YearMonth.from(getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }

}
