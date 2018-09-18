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

import org.junit.Assert;
import org.junit.Test;
import org.opensingular.internal.lib.commons.test.SingularTestUtil;
import org.opensingular.lib.commons.base.SingularException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class TestConversorToolkit {

    @Test
    public void testGetCalendarFromString(){
        final String date = "01/01/2017";

        Calendar calendar = ConversorToolkit.getCalendar(date);

        assertEquals(calendar.get(Calendar.YEAR), 2017);
        assertEquals(calendar.get(Calendar.MONTH), Calendar.JANUARY);
        assertEquals(calendar.get(Calendar.DATE), 1);
    }

    @Test
    public void testGetCalendarFromDate() throws ParseException {
        final String pattern = "dd/MM/yyyy";

        DateFormat format = new SimpleDateFormat(pattern);
        Date date = format.parse("01/01/2017");

        Calendar calendar = ConversorToolkit.getCalendar(date);

        assertEquals(calendar.get(Calendar.YEAR), 2017);
        assertEquals(calendar.get(Calendar.MONTH), Calendar.JANUARY);
        assertEquals(calendar.get(Calendar.DATE), 1);
    }

    @Test
    public void testGetDouble() {
        final String numberValue = "500,00";
        final String anotherNumberValue = "500.00";

        Double doubleValue = ConversorToolkit.getDouble(numberValue);
        Double doubleValue2 = ConversorToolkit.getDouble(anotherNumberValue);

        assertThat(doubleValue).isNotNull().isEqualTo(doubleValue2);

        assertEquals(0, ConversorToolkit.getDouble("-"), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetDoubleErrorNullValue(){
        ConversorToolkit.getDouble(null);
    }

    @Test(expected = SingularException.class)
    public void testGetDoubleErrorInvalidValue(){
        ConversorToolkit.getDouble("123+54");
    }

    @Test
    public void testIntFromString() {
        final String stringValue = "123456";

        int value = ConversorToolkit.getInt(stringValue);

        assertEquals(value, 123456);
    }

    @Test
    public void testIntFromObject() {
        assertEquals((Integer) 123, ConversorToolkit.getInteger(123.12));
        assertEquals((Integer) 123, ConversorToolkit.getInteger("123"));
        Assert.assertNull(ConversorToolkit.getInteger(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetIntWithEmptyValue(){
        ConversorToolkit.getInt(null);
    }

    @Test(expected = SingularException.class)
    public void testGetIntWithInvalidValue(){
        ConversorToolkit.getInt("123-87");
    }

    @Test
    public void testGetDateFormat() {
        final String date = "01/01/2017";
        Calendar calendar = ConversorToolkit.getCalendar(date);

        DateFormat longFormat = ConversorToolkit.getDateFormat("long");
        DateFormat fullFormat = ConversorToolkit.getDateFormat("full");

        String receivedLongValue = longFormat.format(calendar.getTime());
        String receivedFullValue = fullFormat.format(calendar.getTime());

        final String resultExpectedLong = "1 de Janeiro de 2017";
        final String resultExpectedFull = "Domingo, 1 de Janeiro de 2017";

        assertEquals(receivedLongValue, resultExpectedLong);
        assertEquals(receivedFullValue, resultExpectedFull);
    }

    @Test
    public void testGetTime() {
        Calendar cal = ConversorToolkit.getTime("23:10");
        assertEquals(23, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(10, cal.get(Calendar.MINUTE));
        assertNull(ConversorToolkit.getTime(null));
        assertNull(ConversorToolkit.getTime(""));
        SingularTestUtil.assertException(() -> ConversorToolkit.getTime("aa"), SingularException.class,
                "Hora inválida");
    }

    @Test
    public void testPrintDateTime(){
        Calendar calendar = ConversorToolkit.getCalendar("01/01/2017");
        Date date = calendar.getTime();

        String printDataHora = ConversorToolkit.printDateTime(date);
        String printDataHoraShort = ConversorToolkit.printDateTimeShort(date);

        String printDateShort = ConversorToolkit.printDateShort(date);
        String printDateShortNull = ConversorToolkit.printDateShort(null);

        String printDateNull = ConversorToolkit.printDateNotNull(null);
        String printDateNotNull = ConversorToolkit.printDateNotNull(date);

        String printDateNullWithFormat = ConversorToolkit.printDateNotNull(null, "short");
        String printDateNotNullWithFormat = ConversorToolkit.printDateNotNull(date, "short");

        assertEquals(printDataHora, "01/01/2017 00:00:00");
        assertEquals(printDataHoraShort, "01/01/17 00:00");
        assertEquals("01/01/17", ConversorToolkit.printDateTimeShortAbbreviated(date));
        assertNull(ConversorToolkit.printDateTimeShortAbbreviated(null));

        assertEquals("01/01/2017", ConversorToolkit.printDate(date));
        assertEquals("01/01/17", ConversorToolkit.printDate(date, "short"));
        assertEquals(printDateShort, "01/01/17");
        Assert.assertNull(printDateShortNull);

        assertEquals(printDateNull, "");
        assertEquals(printDateNotNull, "01/01/2017");

        assertEquals(printDateNullWithFormat, "");
        assertEquals(printDateNotNullWithFormat, "01/01/17");

        Assert.assertNull(ConversorToolkit.printDateTimeShort(null));
        Assert.assertNull(ConversorToolkit.printDate(null));

        Calendar calendar2 = ConversorToolkit.getCalendar("01/01/2017");
        calendar2.set(Calendar.HOUR_OF_DAY, 10);
        assertEquals("01/01/17 10:00", ConversorToolkit.printDateTimeShortAbbreviated(calendar2.getTime()));


    }

    @Test
    public void testPrintNumber(){
        BigDecimal decimal = new BigDecimal("123.450");
        String result = ConversorToolkit.printNumber(decimal, 2);
        assertEquals("123,45", ConversorToolkit.printNumber(decimal, 2));
        assertEquals(null, ConversorToolkit.printNumber((BigDecimal) null, 2));
        assertEquals("", ConversorToolkit.printNumberNotNull((BigDecimal) null, 2));

        double doubleValue = 123.450;

        assertEquals("123,45", ConversorToolkit.printNumber(doubleValue));
        assertEquals("123,45", ConversorToolkit.printNumber(doubleValue, 2));
        assertEquals("", ConversorToolkit.printNumber(0.0, 3, false));
        assertEquals("0,000", ConversorToolkit.printNumber(0.0, 3, true));
        assertEquals(null, ConversorToolkit.printNumber((Double) null, 2));
        assertEquals("", ConversorToolkit.printNumberNotNull((Double) null, 2));
        assertEquals("123,45", ConversorToolkit.printNumber(new Double(123.45)));
        assertEquals("123,45", ConversorToolkit.printNumber(new Double(123.45), 2));
        assertEquals(null, ConversorToolkit.printNumber(null));
        assertEquals("", ConversorToolkit.printNumberNotNull(null));
        assertEquals("", ConversorToolkit.printNumber(0.0d, 3, false));
        assertEquals("0,000", ConversorToolkit.printNumber(0.0d, 3, true));

        assertEquals("123,4", ConversorToolkit.printNumber((Number) decimal, 1));
        assertEquals("1,500", ConversorToolkit.printNumber((Number) 1.5d, 3));
        assertEquals("1,500", ConversorToolkit.printNumber((Number) 1.5f, 3));
        assertEquals("1,000", ConversorToolkit.printNumber((Number) 1, 3));
        assertEquals(null, ConversorToolkit.printNumber((Number) null, 3));
        assertEquals("", ConversorToolkit.printNumberNotNull((Number) null, 3));
        assertEquals("1,500", ConversorToolkit.printNumberNotNull((Number) 1.5f, 3));
    }

    @Test
    public void testPlainTextToHtml() {
        assertEquals(null, ConversorToolkit.plainTextToHtml(null, false));
        assertEquals("", ConversorToolkit.plainTextToHtml("", false));
        assertEquals("A B", ConversorToolkit.plainTextToHtml("A B", false));
        assertEquals("A\nB", ConversorToolkit.plainTextToHtml("A\nB", false));
        assertEquals("A&gt;B", ConversorToolkit.plainTextToHtml("A>B", false));
        assertEquals("A&amp;B", ConversorToolkit.plainTextToHtml("A&B", false));
        assertEquals("&lt;b&gt;&ccedil;", ConversorToolkit.plainTextToHtml("<b>ç", false));
        assertEquals("X: http://site.com", ConversorToolkit.plainTextToHtml("X: http://site.com", false));
        assertEquals("X: <a href=\"http://site.com\">http://site.com</a>",
                ConversorToolkit.plainTextToHtml("X: http://site.com", true));
        assertEquals("X: <a href=\"http://www.site.com\">www.site.com</a>",
                ConversorToolkit.plainTextToHtml("X: www.site.com", true));
        assertEquals("www", ConversorToolkit.plainTextToHtml("www", true));
    }

    @Test
    public void testAdd() {
        BigDecimal a = new BigDecimal(10.5);
        BigDecimal b = new BigDecimal(3);

        assertEquals(null, ConversorToolkit.add(null, null));
        assertEquals(a, ConversorToolkit.add(a, null));
        assertEquals(b, ConversorToolkit.add(null, b));
        assertEquals(new BigDecimal(13.5d), ConversorToolkit.add(a, b));
        assertEquals(new BigDecimal(13.5d), ConversorToolkit.add(a, 3d));
        assertEquals(new BigDecimal(2.5d), ConversorToolkit.add(1d, 1.5d));
        assertEquals(new Integer(3), ConversorToolkit.add(1, 2));
        assertEquals(new Long(3), ConversorToolkit.add(1L, 2L));
        assertEquals(BigInteger.valueOf(13), ConversorToolkit.add(BigInteger.valueOf(10), BigInteger.valueOf(3)));
    }

    @Test
    public void testMultiply() {
        BigDecimal a = new BigDecimal(10.5);
        BigDecimal b = new BigDecimal(3);

        assertEquals(null, ConversorToolkit.multiply(null, null));
        assertEquals(null, ConversorToolkit.multiply(a, null));
        assertEquals(null, ConversorToolkit.multiply(null, b));
        assertEquals(new BigDecimal(31.5d), ConversorToolkit.multiply(a, b));
        assertTrue((new BigDecimal(4.5d).compareTo((BigDecimal) ConversorToolkit.multiply(3d, 1.5d))) == 0);
        assertEquals(new BigDecimal(4.5d), ConversorToolkit.multiply(b, 1.5d));
        assertEquals(new Integer(2), ConversorToolkit.multiply(1, 2));
        assertEquals(new Long(2), ConversorToolkit.multiply(1L, 2L));
        assertEquals(BigInteger.valueOf(30), ConversorToolkit.multiply(BigInteger.valueOf(10), BigInteger.valueOf(3)));
    }

    @Test
    public void testDivide() {
        BigDecimal a = new BigDecimal(31.5);
        BigDecimal b = new BigDecimal(3);

        assertEquals(null, ConversorToolkit.divide(null, null));
        assertEquals(null, ConversorToolkit.divide(a, null));
        assertEquals(null, ConversorToolkit.divide(null, b));
        assertEquals(null, ConversorToolkit.divide(a, 0.0));
        assertEquals(null, ConversorToolkit.divide(a, new BigDecimal(0.0)));
        assertEquals(new BigDecimal(10.5d), ConversorToolkit.divide(a, b));
        assertTrue((new BigDecimal(4.5d).compareTo((BigDecimal) ConversorToolkit.multiply(3d, 1.5d))) == 0);
        assertEquals(new BigDecimal(2), ConversorToolkit.divide(b, 1.5d));
        assertEquals(new BigDecimal(1.5), ConversorToolkit.divide(3, 2));
        assertEquals(new BigDecimal(15), ConversorToolkit.divide(30L, 2L));
        assertEquals(new BigDecimal(5.5), ConversorToolkit.divide(BigInteger.valueOf(11), BigInteger.valueOf(2)));
    }

    @Test
    public void testIsZero() {
        assertFalse(ConversorToolkit.isZero(null));
        assertFalse(ConversorToolkit.isZero((Integer) 1));
        assertTrue(ConversorToolkit.isZero((Integer) 0));
        assertTrue(ConversorToolkit.isZero((Double) 0.0));
        assertTrue(ConversorToolkit.isZero(new BigDecimal(0.0)));
        assertTrue(ConversorToolkit.isZero(BigInteger.valueOf(0)));

        assertTrue(ConversorToolkit.isZeroOrNull(null));
        assertTrue(ConversorToolkit.isZeroOrNull((Double) 0.0));
        assertFalse(ConversorToolkit.isZeroOrNull(1));
    }

    @Test
    public void round() {
        Integer ii = 10;
        Long ll = 10L;
        BigInteger bi = BigInteger.valueOf(100);
        assertSame(ii, ConversorToolkit.round(ii, 1));
        assertSame(ll, ConversorToolkit.round(ll, 1));
        assertSame(bi, ConversorToolkit.round(bi, 1));

        assertThat(ConversorToolkit.round(16.66d, 1)).isEqualTo(16.7d);
        assertThat(ConversorToolkit.round(16.66d, 0)).isEqualTo(17.0d);
        assertThat(ConversorToolkit.round(16.66d, -1)).isEqualTo(20.0d);
        assertThat(ConversorToolkit.round(11.21d, 1)).isEqualTo(11.2d);
        assertThat(ConversorToolkit.round(11.21d, 0)).isEqualTo(11.0d);
        assertThat(ConversorToolkit.round(11.21d, -1)).isEqualTo(10.0d);

        assertThat(ConversorToolkit.round((Double) null, -1)).isNull();
        assertThat(ConversorToolkit.round((Double) 2.56, 1)).isEqualTo(2.6d);
        assertThat(ConversorToolkit.round((Double) 2.56, -1)).isEqualTo(0.0d);
        assertThat(ConversorToolkit.round((Double) 12.56, -1)).isEqualTo(10.0d);

        assertThat(ConversorToolkit.round((BigDecimal) null, -1)).isNull();
        assertThat(ConversorToolkit.round(new BigDecimal(10.1d), 0)).isEqualTo(new BigDecimal("10"));
        assertThat(ConversorToolkit.round(new BigDecimal(10.17d), 1)).isEqualTo(new BigDecimal("10.2"));
        assertThat(ConversorToolkit.round(new BigDecimal(11.17d), -1)).isEqualTo(new BigDecimal("1E+1"));
    }

    @Test
    public void testTruncate() {
        assertThat(ConversorToolkit.truncate((BigDecimal) null, -1)).isNull();
        assertThat(ConversorToolkit.truncate(new BigDecimal(10.6d), 0)).isEqualTo(new BigDecimal("10"));
        assertThat(ConversorToolkit.truncate(new BigDecimal(10.17d), 1)).isEqualTo(new BigDecimal("10.1"));
        assertThat(ConversorToolkit.truncate(new BigDecimal(11.17d), -1)).isEqualTo(new BigDecimal("1E+1"));

        assertThat(ConversorToolkit.truncate((Double) null, -1)).isNull();
        assertThat(ConversorToolkit.truncate((Double) 10.6d, 0)).isEqualTo(10d);
        assertThat(ConversorToolkit.truncate((Double) 10.17d, 1)).isEqualTo(10.1d);
        assertThat(ConversorToolkit.truncate((Double) 11.17d, -1)).isEqualTo(10.0d);
    }

    @Test
    public void testBreakHtmlLines(){
        final String msgToHtml = "Bom dia,\n estou aqui testando a quebra de linha.\n Muito obrigado pelo apoio.\n Até.";

        String stringConvertida = ConversorToolkit.breakHtmlLines(msgToHtml);

        Assert.assertFalse(stringConvertida.contains("\n"));
    }

    @Test
    public void testCheckNull() {
        Assert.assertNull(ConversorToolkit.getDateFromDate(null));
        Assert.assertNull(ConversorToolkit.getDateFromDate(""));
        Assert.assertNull(ConversorToolkit.getDateFromDate("  "));
    }

    @Test(expected = SingularException.class)
    public void testMethods(){
        Date dateFromData = ConversorToolkit.getDateFromDate("01 01 17");

        Calendar calendar = ConversorToolkit.getCalendar("01/01/2017");
        Calendar calendarObtained = Calendar.getInstance();
        calendarObtained.setTime(dateFromData);

        assertEquals(0, calendarObtained.compareTo(calendar));

        ConversorToolkit.getDateFromDate("01?01?17");
    }

    @Test
    public void testHoursConversions() {
        assertEquals("8:10", ConversorToolkit.toHour(490));
        assertEquals("8:00", ConversorToolkit.toHour(480, false));
        assertEquals("08:00", ConversorToolkit.toHour(480, true));
        assertEquals("12:10", ConversorToolkit.toHour(730));
        assertEquals("12:00", ConversorToolkit.toHour(720, false));
        assertEquals("12:00", ConversorToolkit.toHour(720, true));
        assertEquals(null, ConversorToolkit.toHour(null));
        assertEquals("", ConversorToolkit.toHour((Integer) null, ""));
        assertEquals("8:10", ConversorToolkit.toHour((Integer) 490, ""));
        assertEquals("8:10", ConversorToolkit.toHour(490.1, ""));
        assertEquals("8:11", ConversorToolkit.toHour(490.9, ""));
        assertEquals("XX", ConversorToolkit.toHour((Number) null, "XX"));
        assertEquals(null, ConversorToolkit.toHour((Number) null));
        assertEquals("8:12", ConversorToolkit.toHour((Number) 492));
        assertEquals("-8:12", ConversorToolkit.toHour(-492));
    }
}
