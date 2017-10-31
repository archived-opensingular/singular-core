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
import org.opensingular.lib.commons.base.SingularException;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TestConversorToolkit {

    @Test
    public void testGetCalendarFromString(){
        final String date = "01/01/2017";

        Calendar calendar = ConversorToolkit.getCalendar(date);

        Assert.assertTrue(calendar instanceof Calendar);

        Assert.assertEquals(calendar.get(Calendar.YEAR), 2017);
        Assert.assertEquals(calendar.get(Calendar.MONTH), Calendar.JANUARY);
        Assert.assertEquals(calendar.get(Calendar.DATE), 1);
    }

    @Test
    public void testGetCalendarFromDate() throws ParseException {
        final String pattern = "dd/MM/yyyy";

        DateFormat format = new SimpleDateFormat(pattern);
        Date date = format.parse("01/01/2017");

        Calendar calendar = ConversorToolkit.getCalendar(date);
        Assert.assertTrue(calendar instanceof Calendar);

        Assert.assertEquals(calendar.get(Calendar.YEAR), 2017);
        Assert.assertEquals(calendar.get(Calendar.MONTH), Calendar.JANUARY);
        Assert.assertEquals(calendar.get(Calendar.DATE), 1);
    }

    @Test
    public void testGetDouble() {
        final String numberValue = "500,00";
        final String anotherNumberValue = "500.00";

        Double doubleValue = ConversorToolkit.getDouble(numberValue);
        Double doubleValue2 = ConversorToolkit.getDouble(anotherNumberValue);

        Assert.assertTrue(doubleValue instanceof Double);
        Assert.assertTrue(doubleValue2 instanceof Double);

        Assert.assertEquals(doubleValue, doubleValue2);

        Assert.assertEquals(0, ConversorToolkit.getDouble("-"), 0);
    }

    @Test(expected = NumberFormatException.class)
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

        Assert.assertEquals(value, 123456);
    }

    @Test
    public void testIntFromObject() {
        Double doubleVal = 123.12;
        Integer value = ConversorToolkit.getInt(doubleVal);

        Assert.assertEquals(value, (Integer) 123);

        Object test = "123";
        value = ConversorToolkit.getInt(test);
        Assert.assertEquals(value, (Integer) 123);

        test = null;
        value = ConversorToolkit.getInt(test);
        Assert.assertNull(value);
    }

    @Test(expected = NumberFormatException.class)
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

        Assert.assertEquals(receivedLongValue, resultExpectedLong);
        Assert.assertEquals(receivedFullValue, resultExpectedFull);
    }

    @Test
    public void testPrintDateTime(){
        Calendar calendar = ConversorToolkit.getCalendar("01/01/2017");
        Date date = calendar.getTime();

        String printDataHora = ConversorToolkit.printDateTime(date);
        String printDataHoraShort = ConversorToolkit.printDateTimeShort(date);
        String printDataHoraShortAbreviada = ConversorToolkit.printDateTimeShortAbbreviated(date);

        String printDate = ConversorToolkit.printDate(date);
        String printDateShort = ConversorToolkit.printDateShort(date);
        String printDateShortNull = ConversorToolkit.printDateShort(null);

        String printDateNull = ConversorToolkit.printDateNotNull(null);
        String printDateNotNull = ConversorToolkit.printDateNotNull(date);

        String printDateNullWithFormat = ConversorToolkit.printDateNotNull(null, "short");
        String printDateNotNullWithFormat = ConversorToolkit.printDateNotNull(date, "short");

        Assert.assertEquals(printDataHora, "01/01/2017 00:00:00");
        Assert.assertEquals(printDataHoraShort, "01/01/17 00:00");
        Assert.assertEquals(printDataHoraShortAbreviada, "01/01/17");

        Assert.assertEquals(printDate, "01/01/2017");
        Assert.assertEquals(printDateShort, "01/01/17");
        Assert.assertNull(printDateShortNull);

        Assert.assertEquals(printDateNull, "");
        Assert.assertEquals(printDateNotNull, "01/01/2017");

        Assert.assertEquals(printDateNullWithFormat, "");
        Assert.assertEquals(printDateNotNullWithFormat, "01/01/17");

        Assert.assertNull(ConversorToolkit.printDateTimeShort(null));
        Assert.assertNull(ConversorToolkit.printDate(null));

        Calendar calendar2 = ConversorToolkit.getCalendar("01/01/2017");
        calendar2.set(Calendar.HOUR_OF_DAY, 10);
        Assert.assertEquals("01/01/17 10:00", ConversorToolkit.printDateTimeShortAbbreviated(calendar2.getTime()));


    }

    @Test
    public void testPrintNumber(){
        BigDecimal decimal = new BigDecimal("123.450");
        String result = ConversorToolkit.printNumber(decimal, 2);

        Assert.assertEquals(result, "123,45");

        double doubleValue = 123.450;

        Assert.assertEquals(ConversorToolkit.printNumber(doubleValue), "123,45");
        Assert.assertEquals(ConversorToolkit.printNumber(doubleValue, 2), "123,45");
        Assert.assertEquals(ConversorToolkit.printNumber(null, 2), "");
        Assert.assertEquals(ConversorToolkit.printNumber(new Double(123.45)), "123,45");
        Assert.assertEquals(ConversorToolkit.printNumber(new Double(123.45), 2), "123,45");
        Assert.assertNull(ConversorToolkit.printNumber(null));
    }

    @Test
    public void testBreakHtmlLines(){
        final String msgToHtml = "Bom dia,\n estou aqui testando a quebra de linha.\n Muito obrigado pelo apoio.\n Até.";

        String stringConvertida = ConversorToolkit.breakHtmlLines(msgToHtml);

        Assert.assertFalse(stringConvertida.contains("\n"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetDateFromDataError(){
        String iso8601 = "1999-05-31T13:20:00.000-05:00";
        ConversorToolkit.getDateFromData(iso8601);
    }

    @Test(expected = SingularException.class)
    public void testChecarNull(){
        ConversorToolkit.getDateFromData(null);
    }

    @Test(expected = SingularException.class)
    public void testMethods(){
        Date dateFromData = ConversorToolkit.getDateFromData("01 01 17");

        Calendar calendar = ConversorToolkit.getCalendar("01/01/2017");
        Calendar calendarObtained = Calendar.getInstance();
        calendarObtained.setTime(dateFromData);

        Assert.assertEquals(0, calendarObtained.compareTo(calendar));

        ConversorToolkit.getDateFromData("01?01?17");
    }

}
