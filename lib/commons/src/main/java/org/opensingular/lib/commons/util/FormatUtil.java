/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.lib.commons.util;

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
    
    public static String booleanDescription(Boolean value, String trueDescription, String falseDescription) {
        return booleanDescription(value, trueDescription, falseDescription, "");
    }
    public static String booleanDescription(Boolean value, String trueDescription, String falseDescription, String nullDescription) {
        if (value == null)
            return nullDescription;
        return (value.booleanValue()) ? trueDescription : falseDescription;
    }
}
