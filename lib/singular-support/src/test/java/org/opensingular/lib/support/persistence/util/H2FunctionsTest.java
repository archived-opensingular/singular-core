package org.opensingular.lib.support.persistence.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;

public class H2FunctionsTest {

    @Test
    public void diffTest(){
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.YEAR, 2017);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Calendar calendarToCompare = (Calendar) calendar.clone();
        calendarToCompare.set(Calendar.DAY_OF_MONTH, 1);
        calendarToCompare.set(Calendar.MONTH, Calendar.FEBRUARY);

        Double diff = H2Functions.dateDiffInDays(calendarToCompare.getTime(), calendar.getTime());

        Assert.assertEquals(31, diff.intValue());
    }
}
