package org.opensingular.lib.commons.util.format;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class DateFormatUtil {

    public static final SimpleDateFormat DATE_HOUR_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    private DateFormatUtil() {
    }

    public static String dateToDefaultTimestampString(Date date) {
        if(date != null) {
            return DATE_HOUR_FORMAT.format(date);
        }
        return null;
    }

    public static String dateToDefaultDateString(Date date) {
        if(date != null) {
            return DATE_FORMAT.format(date);
        }
        return null;
    }

    public static StringBuilder appendSeconds(StringBuilder time, long seconds) {
        if (seconds > 0) {
            if (seconds < 60) {
                time.append(seconds);
            } else {
                appendMinutes(time, seconds / 60);
                time.append(seconds % 60);
            }
            time.append(" s ");
        }
        return time;
    }

    public static StringBuilder appendMinutes(StringBuilder time, long minutes) {
        if (minutes > 0) {
            if (minutes < 60) {
                time.append(minutes);
            } else {
                appendHours(time, minutes / 60);
                time.append(minutes % 60);
            }
            time.append(" min ");
        }
        return time;
    }

    public static StringBuilder appendHours(StringBuilder time, long hours) {
        if (hours > 0) {
            if (hours < 24) {
                time.append(hours);
            } else {
                appendDays(time, hours / 24);
                time.append(hours % 24);
            }
            time.append(" h ");
        }
        return time;
    }

    public static StringBuilder appendDays(StringBuilder time, long days) {
        if (days > 0) {
            time.append(days).append(" d ");
        }
        return time;
    }

    public static String dateMonthYearDescribe(Date date) {
        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
            String format = dateFormat.format(date);
            return format.replaceFirst(" ", " de ");
        }
        return null;
    }

    public static String formatDataLongForm(Date data) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.getDefault());
        return Objects.nonNull(data) ? sdf.format(data) : null;
    }

}
