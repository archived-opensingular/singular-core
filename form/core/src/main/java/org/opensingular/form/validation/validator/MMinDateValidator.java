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

package org.opensingular.form.validation.validator;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.type.core.SIDate;
import org.opensingular.form.validation.InstanceValidatable;

public enum MMinDateValidator implements InstanceValueValidator<SIDate, Date> {

    INSTANCE;

    @Override
    public void validate(InstanceValidatable<SIDate> validatable, Date val) {
        final SIDate ins = validatable.getInstance();
        final Date min = ins.getAttributeValue(SPackageBasic.ATR_MIN_DATE);
        Date dateEndOfDay = val;
        if (val != null) {
            dateEndOfDay = Date.from(LocalDateTime.ofInstant(val.toInstant(), ZoneId.systemDefault())
                    .withHour(23)
                    .withMinute(59)
                    .withSecond(59)
                    .atZone(ZoneId.systemDefault()).toInstant());
        }

        if (min != null && dateEndOfDay != null && dateEndOfDay.compareTo(min) < 0) {
            validatable.error(getErrorMessage(min));
        }
    }

    private String getErrorMessage(Date max) {
        return String.format("A data deve ser maior ou igual à %s", format(max));
    }

    private String format(Date max) {
        return new SimpleDateFormat("dd/MM/yyyy").format(max);
    }

}