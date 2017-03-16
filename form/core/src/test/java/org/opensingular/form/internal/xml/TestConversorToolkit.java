package org.opensingular.form.internal.xml;

import org.junit.Assert;
import org.junit.Test;

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
    public void testPrintDataHora(){
        final String stringDate = "01/01/2017";
        Calendar calendar = ConversorToolkit.getCalendar(stringDate);
        Date date = calendar.getTime();

        String printDataHora = ConversorToolkit.printDataHora(date);
        String printDataHoraShort = ConversorToolkit.printDataHoraShort(date);
        String printDataHoraShortAbreviada = ConversorToolkit.printDataHoraShortAbreviada(date);

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

    }

    @Test
    public void testPrintNumber(){
        BigDecimal decimal = new BigDecimal("123.450");
        String result = ConversorToolkit.printNumber(decimal, 2);

        Assert.assertEquals(result, "123,45");

        double doubleValue = 123.450;
        String numberDoublePrimitive = ConversorToolkit.printNumber(doubleValue);
        String numberDoublePrimitiveWithDecimal = ConversorToolkit.printNumber(doubleValue, 2);
        String numberDoublePrimitiveWithDecimalNull = ConversorToolkit.printNumber(null, 2);
        String numberDouble = ConversorToolkit.printNumber(new Double(123.45));
        String numberDoubleNull = ConversorToolkit.printNumber(null);

        Assert.assertEquals(numberDoublePrimitive, "123,45");
        Assert.assertEquals(numberDoublePrimitiveWithDecimal, "123,45");
        Assert.assertEquals(numberDoublePrimitiveWithDecimalNull, "");
        Assert.assertEquals(numberDouble, "123,45");
        Assert.assertNull(numberDoubleNull);
    }

    @Test
    public void testQuebrarLinhasHtml(){
        final String msgToHtml = "Bom dia,\n estou aqui testando a quebra de linha.\n Muito obrigado pelo apoio.\n Até.";

        String stringConvertida = ConversorToolkit.quebrarLinhasHTML(msgToHtml);

        Assert.assertFalse(stringConvertida.contains("\n"));
    }

}
