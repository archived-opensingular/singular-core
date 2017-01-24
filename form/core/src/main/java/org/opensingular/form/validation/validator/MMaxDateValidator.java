package org.opensingular.form.validation.validator;

import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.type.core.SIDate;
import org.opensingular.form.validation.IInstanceValidatable;

import java.text.SimpleDateFormat;
import java.util.Date;

public enum MMaxDateValidator implements IInstanceValueValidator<SIDate, Date> {

    INSTANCE;

    @Override
    public void validate(IInstanceValidatable<SIDate> validatable, Date val) {
        final SIDate ins = validatable.getInstance();
        final Date   max = ins.getAttributeValue(SPackageBasic.ATR_MAX_DATE);
        if (max != null && val != null && val.compareTo(max) > 0) {
            validatable.error(getErrorMessage(max));
        }
    }

    private String getErrorMessage(Date max) {
        return String.format("A data deve ser menor ou igual à %s", format(max));
    }

    private String format(Date max) {
        return new SimpleDateFormat("dd/MM/yyyy").format(max);
    }

}