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

import org.opensingular.lib.commons.util.format.BooleanFormatUtil;
import org.opensingular.lib.commons.util.format.DateFormatUtil;
import org.opensingular.lib.commons.util.format.ListFormatUtil;

import java.util.Date;
import java.util.List;

//TODO remover essa classe e criar um utilitario que tenha todas as implementações do formatters.
public final class FormatUtil {

    private FormatUtil() {
    }

    public static String dateToDefaultTimestampString(Date date) {
        return DateFormatUtil.dateToDefaultTimestampString(date);
    }

    public static String dateToDefaultDateString(Date date) {
        return DateFormatUtil.dateToDefaultDateString(date);
    }

    public static StringBuilder appendSeconds(StringBuilder time, long seconds) {
        return DateFormatUtil.appendSeconds(time, seconds);
    }

    public static StringBuilder appendMinutes(StringBuilder time, long minutes) {
        return DateFormatUtil.appendMinutes(time, minutes);
    }

    public static StringBuilder appendHours(StringBuilder time, long hours) {
        return DateFormatUtil.appendHours(time, hours);
    }

    public static StringBuilder appendDays(StringBuilder time, long days) {
        return DateFormatUtil.appendDays(time, days);
    }

    public static String booleanDescription(Boolean value, String trueDescription, String falseDescription) {
        return BooleanFormatUtil.booleanDescription(value, trueDescription, falseDescription);
    }

    public static String booleanDescription(Boolean value, String trueDescription, String falseDescription, String nullDescription) {
        return BooleanFormatUtil.booleanDescription(value, trueDescription, falseDescription, nullDescription);
    }

    public static String dateMonthYearDescribe(Date date) {
        return DateFormatUtil.dateMonthYearDescribe(date);
    }

    public static String formatListToString(List<String> listOfWords, String joiner, String lastJoiner, String lastCharacter) {
        return ListFormatUtil.formatListToString(listOfWords, joiner, lastJoiner, lastCharacter);
    }

    public static String formatListToString(List<String> listOfWords, String joiner, String lastJoiner) {
        return ListFormatUtil.formatListToString(listOfWords, joiner, lastJoiner);
    }
}
