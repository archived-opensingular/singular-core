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

package org.opensingular.lib.wicket.util.behavior;

import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.util.template.PackageTextTemplate;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DatePickerInitScriptBuilder {

    public static final String JS_CHANGE_EVENT = "singularChangeDate";

    private final PackageTextTemplate initScript = new PackageTextTemplate(DatePickerInitScriptBuilder.class, "DatePickerInitScriptTemplate.js");
    private final SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat datapickerFormat = new SimpleDateFormat("dd/MM/yyyy");

    private final Map<String, Object> variables;
    private final DatePickerSettings datePickerSettings;

    DatePickerInitScriptBuilder(Map<String, Object> variables,
                                String datePickerMarkupId,
                                String inputMarkupId,
                                DatePickerSettings datePickerSettings) {
        this.variables = variables;
        this.datePickerSettings = datePickerSettings;
        //Default Values
        datePickerMarkupId(datePickerMarkupId);
        inputMarkupId(inputMarkupId);
        setAutoclose(true);
        setClearBtn(false);
        setTodayBtn(false);
        setTodayHighlight(false);
        setShowOnFocus(true);
        changeEvent(JS_CHANGE_EVENT);
        configureBeforeShowDay(false);
        setEnabledDates(Collections.emptyList());
        setStartDate(null);
    }

    public String generateScript() {
        if (datePickerSettings != null) {
            datePickerSettings.getEnabledDates().ifPresent(this::setEnabledDates);
            datePickerSettings.isAutoclose().ifPresent(this::setAutoclose);
            datePickerSettings.isClearBtn().ifPresent(this::setClearBtn);
            datePickerSettings.isShowOnFocus().ifPresent(this::setShowOnFocus);
            datePickerSettings.isTodayBtn().ifPresent(this::setTodayBtn);
            datePickerSettings.isTodayHighlight().ifPresent(this::setTodayHighlight);
            datePickerSettings.getStartDate().ifPresent(this::setStartDate);
        }
        return initScript.asString(variables);
    }


    private void datePickerMarkupId(String datePickerMarkupId) {
        variables.put("datePickerMarkupId", datePickerMarkupId);
    }

    private void inputMarkupId(String inputMarkupId) {
        variables.put("inputMarkupId", inputMarkupId);
    }

    private void changeEvent(String changeEvent) {
        variables.put("changeEvent", changeEvent);
    }

    private void configureBeforeShowDay(boolean configureBeforeShowDate) {
        variables.put("configureBeforeShowDay", configureBeforeShowDate);
    }

    private void setAutoclose(boolean autoclose) {
        variables.put("autoclose", autoclose);
    }

    private void setClearBtn(boolean clearBtn) {
        variables.put("clearBtn", clearBtn);
    }

    private void setShowOnFocus(boolean showOnFocus) {
        variables.put("showOnFocus", showOnFocus);
    }

    private void setTodayBtn(boolean todayBtn) {
        variables.put("todayBtn", todayBtn);
    }

    private void setTodayHighlight(boolean todayHighlight) {
        variables.put("todayHighlight", todayHighlight);
    }

    private void setStartDate(Date startDate) {
        String val = "false";
        if (startDate != null) {
            val = datapickerFormat.format(startDate);
        }
        variables.put("startDate", val);
    }

    private DatePickerInitScriptBuilder setEnabledDates(List<Date> enabledDates) {
        final JSONArray jsonArray = new JSONArray();

        for (Date enabledDate : enabledDates) {
            jsonArray.put(isoFormat.format(enabledDate));
        }
        configureBeforeShowDay(!enabledDates.isEmpty());
        variables.put("enabledDates", jsonArray.toString());
        return this;
    }

}