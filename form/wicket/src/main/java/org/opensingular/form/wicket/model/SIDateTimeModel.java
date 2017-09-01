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

package org.opensingular.form.wicket.model;

import org.opensingular.form.SInstance;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public interface SIDateTimeModel {

    abstract class AbstractDateTimeModel implements ISInstanceAwareModel<String> {

        final ISInstanceAwareModel<Date> model;

        AbstractDateTimeModel(ISInstanceAwareModel<Date> model) {
            this.model = model;
        }

        @Override
        public void detach() {
            model.detach();
        }

        protected Date getDate() {
            return model.getObject();
        }

        @Override
        public SInstance getSInstance() {
            return model.getSInstance();
        }
    }

    class DateModel extends AbstractDateTimeModel {

        public DateModel(ISInstanceAwareModel<Date> model) {
            super(model);
        }

        @Override
        public String getObject() {
            if (model.getObject() != null) {
                final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                return format.format(getDate());
            }
            return null;
        }

        @Override
        public void setObject(String rawTime) {
            if (rawTime != null && rawTime.matches("[0-9]{2}/[0-9]{2}/[0-9]{4}")) {

                final Calendar c = new GregorianCalendar();
                c.clear();
                final String[] date = rawTime.split("/");
                final int day = Integer.parseInt(date[0]);
                final int month = Integer.parseInt(date[1]);
                final int year = Integer.parseInt(date[2]);

                if (getDate() != null) {
                    c.setTime(getDate());
                }

                c.set(Calendar.DAY_OF_MONTH, day);
                c.set(Calendar.MONTH, month - 1);
                c.set(Calendar.YEAR, year);

                model.setObject(c.getTime());
            } else {
                model.setObject(null);
            }
        }
    }

    class TimeModel extends AbstractDateTimeModel {

        public TimeModel(ISInstanceAwareModel<Date> model) {
            super(model);
        }

        @Override
        public String getObject() {
            if (getDate() != null) {
                final SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                return format.format(getDate());
            }
            return null;
        }

        @Override
        public void setObject(String rawTime) {
            if (rawTime != null && rawTime.matches("[0-9]{1,2}:[0-9]{1,2}")) {

                final Calendar c = new GregorianCalendar();
                c.clear();
                final String[] hourMinute = rawTime.split(":");
                final int hour = Integer.parseInt(hourMinute[0]);
                final int minute = Integer.parseInt(hourMinute[1]);

                if (getDate() != null) {
                    c.setTime(getDate());
                }

                c.set(Calendar.HOUR_OF_DAY, hour);
                c.set(Calendar.MINUTE, minute);

                model.setObject(c.getTime());
            } else {
                model.setObject(null);
            }
        }
    }

}