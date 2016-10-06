/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.model;

import org.opensingular.singular.form.SInstance;

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
        public SInstance getMInstancia() {
            return model.getMInstancia();
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
                final String[] date = rawTime.split("/");
                final int day = Integer.valueOf(date[0]);
                final int month = Integer.valueOf(date[1]);
                final int year = Integer.valueOf(date[2]);

                if (getDate() != null) {
                    c.setTime(getDate());
                }

                c.set(Calendar.DAY_OF_MONTH, day);
                c.set(Calendar.MONTH, month - 1);
                c.set(Calendar.YEAR, year);

                model.setObject(c.getTime());
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
                final String[] hourMinute = rawTime.split(":");
                final int hour = Integer.valueOf(hourMinute[0]);
                final int minute = Integer.valueOf(hourMinute[1]);

                if (getDate() != null) {
                    c.setTime(getDate());
                }

                c.set(Calendar.HOUR_OF_DAY, hour);
                c.set(Calendar.MINUTE, minute);

                model.setObject(c.getTime());
            }
        }
    }

}