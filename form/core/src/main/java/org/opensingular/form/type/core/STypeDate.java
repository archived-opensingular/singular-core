/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.type.core;

import org.opensingular.form.STypeSimple;
import org.opensingular.singular.commons.base.SingularUtil;
import org.opensingular.form.SInfoType;
import com.google.common.base.Strings;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@SInfoType(name = "Date", spackage = SPackageCore.class)
public class STypeDate extends STypeSimple<SIDate, Date> {
    private static final Logger LOGGER = Logger.getLogger(SIDate.class.getName());

    public STypeDate() {
        super(SIDate.class, Date.class);
    }

    protected STypeDate(Class<? extends SIDate> instanceClass) {
        super(instanceClass, Date.class);
    }

    public Date fromString(String value) {
        if (Strings.isNullOrEmpty(value)) return null;
        try {
            return isoFormarter().parseDateTime(value).toDate();
        } catch (Exception e) {
            try{
                return latinFormatter().parse(value);
            } catch (Exception ex) {
                return handleError(value, ex);
            }
        }
    }

    private Date handleError(String value, Exception e) {
        String msg = String.format("Can't parse value '%s' with format '%s'.", value, "dd/MM/yyyy");
        LOGGER.log(Level.WARNING, msg, e);
        throw SingularUtil.propagate(e);
    }

    @Override
    protected String toStringPersistence(Date originalValue) {
        if (originalValue != null) {
            return isoFormarter().print(new DateTime(originalValue));
        } else {
            return null;
        }
    }

    @Override
    public String toStringDisplayDefault(Date date) {
        if(date != null) {
            return latinFormatter().format(date);
        } else {
            return null;
        }
    }

    private static DateTimeFormatter isoFormarter() {
        return ISODateTimeFormat.date();
    }

    private static SimpleDateFormat latinFormatter() {
        return new SimpleDateFormat("dd/MM/yyyy");
    }

}
