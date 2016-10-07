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