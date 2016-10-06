package org.opensingular.singular.form.type.core;

import org.opensingular.singular.form.SISimple;

import java.util.Date;

public class SITime extends SISimple<Date> implements SIComparable<Date> {
    public SITime() {
    }

    public Date getDate() {
        return (Date) getValue();
    }
}
