/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.internal.lib.commons.xml;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestConversorDataISO8601 {

    @Test
    public void testFormatDate(){
        Calendar calendar = ConversorToolkit.getCalendar("01/01/2017");
        calendar.set(Calendar.HOUR, 10);
        calendar.set(Calendar.MINUTE, 10);
        calendar.set(Calendar.MILLISECOND, 10);

        String resultado = ConversorDataISO8601.format(calendar);
        Assert.assertTrue(ConversorDataISO8601.isISO8601(resultado));

        assertFalse(ConversorDataISO8601.isISO8601(""));
    }

    @Test
    public void isISO8611() {
        assertTrue(ConversorDataISO8601.isISO8601("2011-01-06T19:18:09"));
        assertTrue(ConversorDataISO8601.isISO8601("2011-01-06T19:18:09.100"));
        assertTrue(ConversorDataISO8601.isISO8601("2012-02-16T10:11:12.200-01:00"));
        assertTrue(ConversorDataISO8601.isISO8601("2012-02-16T10:11:12.200+01:00"));
        assertTrue(ConversorDataISO8601.isISO8601("2012-02-16 10:11:12.200+01:00"));
        assertFalse(ConversorDataISO8601.isISO8601("2012-02-16R10:11:12.200+01:00"));
        assertFalse(ConversorDataISO8601.isISO8601("2012-02-16T10:11:12.200#01:00"));
    }

    @Test
    public void testParse() {
        assertDate("2011-01-06T19:18:09", 2011, 1, 6, 19, 18, 9, 0);
        assertDate("2011-01-06T19:18:09.100", 2011, 1, 6, 19, 18, 9, 100);
        assertDate("2012-02-16T10:11:12.200+01:00", 2012, 2, 16, 10, 11, 12, 200);
        assertDate("2012-02-16T10:11:12.200-01:00", 2012, 2, 16, 10, 11, 12, 200);

        Assertions.assertThatThrownBy(() -> ConversorDataISO8601.getCalendar("2012-02-16T10:11:12.200#01:00"))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        Assertions.assertThatThrownBy(() -> ConversorDataISO8601.getCalendar("2012-02-16T10:11:12.200#01:00"))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    private void assertDate(String s, int year, int month, int day, int hour, int minute, int second, int millis) {
        GregorianCalendar cal = ConversorDataISO8601.getCalendar(s);
        assertEquals(year, cal.get(Calendar.YEAR));
        assertEquals(month, cal.get(Calendar.MONTH) + 1);
        assertEquals(day, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(hour, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(minute, cal.get(Calendar.MINUTE));
        assertEquals(second, cal.get(Calendar.SECOND));
        assertEquals(millis, cal.get(Calendar.MILLISECOND));
    }

    @Test
    public void testGoAndCameBack() {
        testGoAndCameBack("2011-01-06");
        testGoAndCameBack("2011-01-06T19:18", "2011-01-06T19:18:00");
        testGoAndCameBack("2011-01-06T19:18:12");
        testGoAndCameBack("2011-01-06T19:18:12.200");
    }

    private void testGoAndCameBack(@Nonnull String text) {
        testGoAndCameBack(text, text);
    }

    private void testGoAndCameBack(@Nonnull String text, String expected) {
        Date dt = ConversorDataISO8601.getDate(text);
        String text2 = ConversorDataISO8601.format(dt);
        assertThat(text2).isEqualTo(expected);

        Calendar cal = ConversorDataISO8601.getCalendar(text);
        String text3 = ConversorDataISO8601.format(cal);
        assertThat(text3).isEqualTo(expected);
    }

    @Test
    public void testFormatYear(){
        Calendar calendar = ConversorToolkit.getCalendar("01/01/2017");
        calendar.set(Calendar.YEAR, 7);

        String dateWithYearChanging = ConversorDataISO8601.format(calendar);
        assertEquals("0007-01-01", dateWithYearChanging);

        calendar.set(Calendar.YEAR, 70);
        dateWithYearChanging = ConversorDataISO8601.format(calendar);
        assertEquals("0070-01-01", dateWithYearChanging);

        calendar.set(Calendar.YEAR, 700);
        dateWithYearChanging = ConversorDataISO8601.format(calendar);
        assertEquals("0700-01-01", dateWithYearChanging);
    }

    @Test
    public void testFormatMilli(){
        Calendar calendar = ConversorToolkit.getCalendar("01/01/2017");
        calendar.set(Calendar.MILLISECOND, 1);

        String dateWithMilliChanging = ConversorDataISO8601.format(calendar);
        Assert.assertTrue(ConversorDataISO8601.isISO8601(dateWithMilliChanging));

        calendar.set(Calendar.MILLISECOND, 10);
        dateWithMilliChanging = ConversorDataISO8601.format(calendar);
        Assert.assertTrue(ConversorDataISO8601.isISO8601(dateWithMilliChanging));

    }

    @Test(expected = IllegalArgumentException.class)
    public void testFormatMilliArgumentNegative(){
        Calendar calendar = ConversorToolkit.getCalendar("01/01/2017");
        calendar.set(Calendar.MILLISECOND, -1);
        ConversorDataISO8601.format(calendar);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFormatMiliArgumentBreaksUpperLimit(){
        Calendar calendar = ConversorToolkit.getCalendar("01/01/2017");
        calendar.set(Calendar.MILLISECOND, 1000);
        ConversorDataISO8601.format(calendar);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionGerCalendar(){
        ConversorDataISO8601.getCalendar("11*17)2017");
    }
}
