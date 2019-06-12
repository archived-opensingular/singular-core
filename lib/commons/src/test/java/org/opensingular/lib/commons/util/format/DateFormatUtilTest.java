package org.opensingular.lib.commons.util.format;

import org.junit.Assert;
import org.junit.Test;
import org.opensingular.lib.commons.util.FormatUtil;

import java.util.Calendar;

public class DateFormatUtilTest {

    @Test
    public void appendDateLongFormTest() {
        Calendar calendar = createDate();
        Assert.assertEquals("01 de Janeiro de 2017", DateFormatUtil.formatDataLongForm(calendar.getTime()));
    }

    @Test
    public void dateToDefaultTimestampStringTest() {
        Calendar calendar = createDate();
        Assert.assertEquals("01/01/2017 13:10", FormatUtil.dateToDefaultTimestampString(calendar.getTime()));
    }


    @Test
    public void appendSecondsTest() {
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
    public void appendMinutesTest() {
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
    public void appendHoursTest() {
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
    public void appendDaysTest() {
        StringBuilder time = new StringBuilder();
        FormatUtil.appendDays(time, 0);
        Assert.assertEquals("", time.toString());

        time = new StringBuilder();
        FormatUtil.appendDays(time, 50);
        Assert.assertEquals("50 d ", time.toString());
    }

    @Test
    public void dateMonthYearDescribeTest() {
        Calendar calendar = createDate();
        Assert.assertEquals("Janeiro de 2017", FormatUtil.dateMonthYearDescribe(calendar.getTime()));
    }

    public Calendar createDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 13);
        calendar.set(Calendar.MINUTE, 10);
        return calendar;
    }
}
