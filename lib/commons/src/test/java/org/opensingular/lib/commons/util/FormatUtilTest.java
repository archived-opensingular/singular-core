package org.opensingular.lib.commons.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;

public class FormatUtilTest {

    @Test
    public void dateToDefaultTimestampStringTest(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR, 13);
        calendar.set(Calendar.MINUTE, 10);

        Assert.assertEquals("01/01/2017 13:10", FormatUtil.dateToDefaultTimestampString(calendar.getTime()));
    }

    @Test
    public void appendSecondsTest(){
        StringBuilder time = new StringBuilder();
        FormatUtil.appendSeconds(time, 0);
        Assert.assertEquals("", time.toString());

        time = new StringBuilder();
        FormatUtil.appendSeconds(time, 10);
        Assert.assertEquals("10 s ", time.toString());

        time = new StringBuilder();
        FormatUtil.appendSeconds(time, 60);
    }

    @Test
    public void appendMinutesTest(){
        StringBuilder time = new StringBuilder();
        FormatUtil.appendMinutes(time, 0);
        Assert.assertEquals("", time.toString());

        time = new StringBuilder();
        FormatUtil.appendMinutes(time, 10);
        Assert.assertEquals("10 min ", time.toString());

        time = new StringBuilder();
        FormatUtil.appendMinutes(time, 60);
        Assert.assertEquals("1 h 0 min ", time.toString());
    }

    @Test
    public void appendHoursTest(){
        StringBuilder time = new StringBuilder();
        FormatUtil.appendHours(time, 0);
        Assert.assertEquals("", time.toString());

        time = new StringBuilder();
        FormatUtil.appendHours(time, 10);
        Assert.assertEquals("10 h ", time.toString());

        time = new StringBuilder();
        FormatUtil.appendHours(time, 24);
        Assert.assertEquals("1 d 0 h ", time.toString());
    }

    @Test
    public void appendDaysTest(){
        StringBuilder time = new StringBuilder();
        FormatUtil.appendDays(time, 0);
        Assert.assertEquals("", time.toString());

        time = new StringBuilder();
        FormatUtil.appendDays(time, 50);
        Assert.assertEquals("50 d ", time.toString());
    }

    @Test
    public void booleanDescriptionTest(){
        String resultado = FormatUtil.booleanDescription(null, "Verdadeiro", "Falso");

        Assert.assertEquals("", resultado);

        resultado = FormatUtil.booleanDescription(true, "Verdadeiro", "Falso");
        Assert.assertEquals("Verdadeiro", resultado);

        resultado = FormatUtil.booleanDescription(false, "Verdadeiro", "Falso");
        Assert.assertEquals("Falso", resultado);
    }
}
