package org.opensingular.internal.lib.commons.test;

import org.opensingular.internal.lib.commons.xml.ConversorToolkit;

import javax.annotation.Nonnull;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

/**
 * Helper class for tests that need to generate a sequence of dates and times separated by specific amount of time.
 *
 * @author Daniel C. Bordin
 * @since 2018-08-17
 */
public final class TimeMaker {

    private final Calendar cal;

    /** Creates a new TimeMaker with the current date and time. */
    public TimeMaker() {
        cal = new GregorianCalendar();
    }

    /** Create a new TimeMaker with the specified date in ISO format (yyyy-mm-dd or yyyy-mm-ddThh:mm:ss). */
    public TimeMaker(@Nonnull String dateTime) {
        cal = ConversorToolkit.getCalendar(Objects.requireNonNull(dateTime));
    }

    /** Returns the current date internally contained. */
    @Nonnull
    public Date get() {
        return cal.getTime();
    }

    /** Increments the internal time in X minutes and returns the new value. */
    @Nonnull
    public Date incMinutes(int minutes) {
        cal.add(Calendar.MINUTE, minutes);
        return cal.getTime();
    }
}