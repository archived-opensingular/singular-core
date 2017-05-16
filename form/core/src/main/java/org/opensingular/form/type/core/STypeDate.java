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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.validation.ValidationErrorLevel;
import org.opensingular.form.validation.validator.InstanceValidators;
import org.opensingular.lib.commons.base.SingularUtil;

import com.google.common.base.Strings;

import static org.opensingular.form.type.basic.SPackageBasic.ATR_MAX_DATE;

@SInfoType(name = "Date", spackage = SPackageCore.class)
public class STypeDate extends STypeSimple<SIDate, Date> {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        addInstanceValidator(ValidationErrorLevel.ERROR, InstanceValidators.maxDate());
    }

    public STypeDate() {
        super(SIDate.class, Date.class);
    }

    protected STypeDate(Class<? extends SIDate> instanceClass) {
        super(instanceClass, Date.class);
    }

    public Date fromString(String value) {
        if (Strings.isNullOrEmpty(value)) {
            return null;
        }
        try {
            return isoFormarter().parseLocalDate(value).toDate();
        } catch (Exception e) {
            getLogger().debug(null, e);
            try{
                return latinFormatter().parse(value);
            } catch (Exception ex) {
                return handleError(value, ex);
            }
        }
    }

    private Date handleError(String value, Exception e) {
        String msg = String.format("Can't parse value '%s' with format '%s'.", value, "dd/MM/yyyy");
        getLogger().warn(msg, e);
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

    public STypeDate maxDate(Date date) {
        asAtr().setAttributeValue(ATR_MAX_DATE, date);
        return this;
    }

}
