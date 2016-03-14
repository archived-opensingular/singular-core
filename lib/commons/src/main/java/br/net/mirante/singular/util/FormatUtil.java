/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class FormatUtil {

    private FormatUtil() {
        /* CONSTRUTOR VAZIO */
    }

    public static String dateToDefaultTimestampString(Date date) {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(date);
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
}
