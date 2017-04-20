package org.opensingular.internal.lib.commons.xml;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;

public class TestConversorDataISO8601 {

    @Test
    public void testFormatDate(){
        Calendar calendar = ConversorToolkit.getCalendar("01/01/2017");
        calendar.set(Calendar.HOUR, 10);
        calendar.set(Calendar.MINUTE, 10);
        calendar.set(Calendar.MILLISECOND, 10);

        String resultado = ConversorDataISO8601.format(calendar);
        Assert.assertTrue(ConversorDataISO8601.isISO8601(resultado));

        Assert.assertFalse(ConversorDataISO8601.isISO8601(""));
    }

    @Test
    public void testFormatYear(){
        Calendar calendar = ConversorToolkit.getCalendar("01/01/2017");
        calendar.set(Calendar.YEAR, 07);

        String dateWithYearChanging = ConversorDataISO8601.format(calendar);
        Assert.assertEquals("0007-01-01", dateWithYearChanging);

        calendar.set(Calendar.YEAR, 70);
        dateWithYearChanging = ConversorDataISO8601.format(calendar);
        Assert.assertEquals("0070-01-01", dateWithYearChanging);

        calendar.set(Calendar.YEAR, 700);
        dateWithYearChanging = ConversorDataISO8601.format(calendar);
        Assert.assertEquals("0700-01-01", dateWithYearChanging);
    }

    @Test
    public void testFormatMili(){
        Calendar calendar = ConversorToolkit.getCalendar("01/01/2017");
        calendar.set(Calendar.MILLISECOND, 1);

        String dateWithMiliChanging = ConversorDataISO8601.format(calendar);
        Assert.assertTrue(ConversorDataISO8601.isISO8601(dateWithMiliChanging));

        calendar.set(Calendar.MILLISECOND, 10);
        dateWithMiliChanging = ConversorDataISO8601.format(calendar);
        Assert.assertTrue(ConversorDataISO8601.isISO8601(dateWithMiliChanging));

    }

    @Test(expected = IllegalArgumentException.class)
    public void testFormatMiliArgumentNegative(){
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
