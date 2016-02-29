package br.net.mirante.singular.flow.schedule;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;

/**
 * Builder for {@link IScheduleData}.
 *
 * @author lucas.lopes
 */
public class ScheduleDataBuilder {

    private ScheduleDataBuilder() {
        /* CONSTRUTOR VAZIO */
    }

    public static IScheduleData buildHourly(int hours) {
        String description = "Repetido a cada " + hours + "h";
        String cronExpression = generateCronExpression("0", "0", "0/" + Integer.toString(hours), "*", "*", "?", "");

        return new ScheduleDataImpl(cronExpression, description);
    }

    /**
     * @param hours mandatory = yes. allowed values = {@code 0-23}
     * @param minutes mandatory = yes. allowed values = {@code 0-59}
     * @return {@link IScheduleData}
     */
    public static IScheduleData buildDaily(int hours, int minutes) {
        String description = "Diário às " + hours + ':' + (minutes < 10 ? "0" : "") + minutes + "h";
        String cronExpression = generateCronExpression("0", Integer.toString(minutes),
                Integer.toString(hours), "*", "*", "?", "");

        return new ScheduleDataImpl(cronExpression, description);
    }

    /**
     * @param hours mandatory = yes. allowed values = {@code 0-23}
     * @param minutes mandatory = yes. allowed values = {@code 0-59}
     * @param dayOfWeek mandatory = yes. allowed values = {@code 0-6  (0=SUN)}
     * @return {@link IScheduleData}
     */
    public static IScheduleData buildWeekly(int hours, int minutes, Integer... dayOfWeek) {
        Preconditions.checkArgument(dayOfWeek.length > 0, "any dayOfWeek provided");

        String[] weekDaysNames = new String[]{"domingo", "segunda", "terça", "quarta", "quinta", "sexta", "sábado"};

        try (Stream<Integer> dayOfWeekStream = Arrays.stream(dayOfWeek)) {

            String daysDescription = dayOfWeekStream.map((day) -> weekDaysNames[day]).collect(Collectors.joining(","));
            String description = "Semanal: " + daysDescription
                    + " às " + hours + ':' + (minutes < 10 ? "0" : "") + minutes + "h";

            try (Stream<Integer> dayOfWeekStream2 = Arrays.stream(dayOfWeek)) {

                String cronExpression = generateCronExpression("0", Integer.toString(minutes),
                        Integer.toString(hours), "*", "*", dayOfWeekStream2.map(Object::toString)
                                .collect(Collectors.joining(",")), "");
                return new ScheduleDataImpl(cronExpression, description);
            }
        }
    }

    /**
     * @param dayOfMonth mandatory = yes. allowed values = {@code 1-31}
     * @param hours mandatory = yes. allowed values = {@code 0-23}
     * @param minutes mandatory = yes. allowed values = {@code 0-59}
     * @param months mandatory = no. allowed values = {@code 1-12}
     * @return {@link IScheduleData}
     */
    public static IScheduleData buildMonthly(int dayOfMonth, int hours, int minutes, Integer... months) {
        if (months.length == 0) {
            String description = "Mensal: todo dia " + dayOfMonth
                    + " às " + hours + ':' + (minutes < 10 ? "0" : "") + minutes + "h";
            String cronExpression = generateCronExpression("0", Integer.toString(minutes), Integer.toString(hours),
                    Integer.toString(dayOfMonth), "*", "?", "");
            return new ScheduleDataImpl(cronExpression, description);
        } else {

            try (Stream<Integer> stream = Arrays.stream(months)) {

                String monthsDescription = stream.map(Object::toString).collect(Collectors.joining(","));
                String description = "Mensal: todo dia " + dayOfMonth
                        + " às " + hours + ':' + (minutes < 10 ? "0" : "") + minutes + "h"
                        + " nos meses: " + monthsDescription;

                String cronExpression = generateCronExpression("0", Integer.toString(minutes),
                        Integer.toString(hours), Integer.toString(dayOfMonth), monthsDescription, "?", "");

                return new ScheduleDataImpl(cronExpression, description);
            }
        }
    }

    /**
     * Generate a CRON expression is a string comprising 6 or 7 fields separated by white space.
     *
     * @param seconds mandatory = yes. allowed values = {@code  0-59    * / , -}
     * @param minutes mandatory = yes. allowed values = {@code  0-59    * / , -}
     * @param hours mandatory = yes. allowed values = {@code 0-23   * / , -}
     * @param dayOfMonth mandatory = yes. allowed values = {@code 1-31  * / , - ? L W}
     * @param month mandatory = yes. allowed values = {@code 1-12 or JAN-DEC    * / , -}
     * @param dayOfWeek mandatory = yes. allowed values = {@code 0-6 or SUN-SAT * / , - ? L #}
     * @param year mandatory = no. allowed values = {@code 19702099    * / , -}
     * @return a CRON Formatted String.
     */
    private static String generateCronExpression(final String seconds, final String minutes, final String hours,
            final String dayOfMonth, final String month, final String dayOfWeek, final String year) {
        return String.format("%1$s %2$s %3$s %4$s %5$s %6$s %7$s", seconds, minutes, hours, dayOfMonth,
                month, dayOfWeek, year).trim();
    }
}
