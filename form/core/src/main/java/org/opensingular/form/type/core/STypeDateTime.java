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
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@SInfoType(name = "DateTime", spackage = SPackageCore.class)
public class STypeDateTime extends STypeSimple<SIDateTime, Date> {

    private static final Logger LOGGER = Logger.getLogger(STypeDateTime.class.getName());

    public static final String FORMAT = "dd/MM/yyyy HH:mm";

    public STypeDateTime() {
        super(SIDateTime.class, Date.class);
    }

    protected STypeDateTime(Class<? extends SIDateTime> classeInstancia) {
        super(classeInstancia, Date.class);
    }

    public Date fromString(String value) {
        if (Strings.isNullOrEmpty(value)) return null;
        try {
            return isoFormarter().parseLocalDate(value).toDate();
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
    public String toStringDisplayDefault(Date date) {
        return latinFormatter().format(date);
    }

    @Override
    protected String toStringPersistence(Date originalValue) {
        DateTime instant = new DateTime(originalValue);
        return isoFormarter().print(instant.withZone(DateTimeZone.UTC));
    }

    private static DateTimeFormatter isoFormarter() {
        return DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ").withZone(DateTimeZone.UTC);
    }

    private static SimpleDateFormat latinFormatter() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm");
    }

}