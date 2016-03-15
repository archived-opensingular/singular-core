/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;

import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.STypeSimple;

@SInfoType(name = "Date", spackage = SPackageCore.class)
public class STypeDate extends STypeSimple<SIDate, Date> {
    private static final Logger LOGGER = Logger.getLogger(SIDate.class.getName());

    public static final String FORMAT = "dd/MM/yyyy";

    public STypeDate() {
        super(SIDate.class, Date.class);
    }

    protected STypeDate(Class<? extends SIDate> instanceClass) {
        super(instanceClass, Date.class);
    }

    public Date fromString(String value) {
        try {
            if (Strings.isNullOrEmpty(value)) return null;
            return (new SimpleDateFormat(STypeDate.FORMAT)).parse(value);
        } catch (ParseException e) {
            String msg = String.format("Can't parse value '%s' with format '%s'.", value, STypeDate.FORMAT);
            LOGGER.log(Level.WARNING, msg, e);
            throw Throwables.propagate(e);
        }
    }

    @Override
    protected String toStringPersistence(Date originalValue) {
        if (originalValue != null) {
            return (new SimpleDateFormat(STypeDate.FORMAT)).format(originalValue);
        } else {
            return null;
        }
    }
}
