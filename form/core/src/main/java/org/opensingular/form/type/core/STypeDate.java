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

import org.opensingular.form.STypeSimple;
import org.opensingular.lib.commons.base.SingularUtil;
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
