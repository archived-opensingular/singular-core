package org.opensingular.singular.form.type.core;

import org.opensingular.singular.commons.base.SingularUtil;
import org.opensingular.singular.form.SInfoType;
import org.opensingular.singular.form.STypeSimple;
import com.google.common.base.Strings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@SInfoType(name = "Time", spackage = SPackageCore.class)
public class STypeTime extends STypeSimple<SITime, Date> {

    private static final Logger LOGGER = Logger.getLogger(STypeTime.class.getName());

    public static final String FORMAT = "HH:mm";

    public STypeTime() {
        super(SITime.class, Date.class);
    }

    protected STypeTime(Class<? extends SITime> classeInstancia) {
        super(classeInstancia, Date.class);
    }

    @Override
    public Date fromString(String value) {
        try {
            if (Strings.isNullOrEmpty(value)) return null;
            return (new SimpleDateFormat(FORMAT)).parse(value);
        } catch (ParseException e) {
            String msg = String.format("Can't parse value '%s' with format '%s'.", value, FORMAT);
            LOGGER.log(Level.WARNING, msg, e);
            throw SingularUtil.propagate(e);
        }
    }

    @Override
    protected String toStringPersistence(Date originalValue) {
        return (new SimpleDateFormat(FORMAT)).format(originalValue);
    }

}