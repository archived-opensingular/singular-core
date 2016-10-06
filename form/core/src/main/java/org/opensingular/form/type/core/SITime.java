package org.opensingular.form.type.core;

import org.opensingular.form.SISimple;

import java.util.Date;

public class SITime extends SISimple<Date> implements SIComparable<Date> {
    public SITime() {
    }

    public Date getDate() {
        return (Date) getValue();
    }
}
