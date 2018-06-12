package org.opensingular.lib.wicket.util.behavior;

import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.opensingular.lib.wicket.util.bootstrap.datepicker.BSDatepickerConstants;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DatePickerInitScriptBuilder {
    private final static PackageTextTemplate INIT_SCRIPT = new PackageTextTemplate(DatePickerInitBehaviour.class, "DatePickerInitScriptTemplate.js");
    private final static SimpleDateFormat ISO_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private final static SimpleDateFormat DATAPICKER_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

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
        changeEvent(BSDatepickerConstants.JS_CHANGE_EVENT);
        configureBeforeShowDay(Boolean.FALSE);
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
        return INIT_SCRIPT.asString(variables);
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

    private void configureBeforeShowDay(Boolean configureBeforeShowDate) {
        variables.put("configureBeforeShowDay", configureBeforeShowDate);
    }

    private void setAutoclose(Boolean autoclose) {
        variables.put("autoclose", autoclose);
    }

    private void setClearBtn(Boolean clearBtn) {
        variables.put("clearBtn", clearBtn);
    }

    private void setShowOnFocus(Boolean showOnFocus) {
        variables.put("showOnFocus", showOnFocus);
    }

    private void setTodayBtn(Boolean todayBtn) {
        variables.put("todayBtn", todayBtn);
    }

    private void setTodayHighlight(Boolean todayHighlight) {
        variables.put("todayHighlight", todayHighlight);
    }

    private void setStartDate(Date startDate) {
        String val = "false";
        if (startDate != null) {
            val = DATAPICKER_FORMAT.format(startDate);
        }
        variables.put("startDate", val);
    }

    private DatePickerInitScriptBuilder setEnabledDates(List<Date> enabledDates) {
        final JSONArray jsonArray = new JSONArray();

        for (Date enabledDate : enabledDates) {
            jsonArray.put(ISO_FORMAT.format(enabledDate));
        }
        configureBeforeShowDay(!enabledDates.isEmpty());
        variables.put("enabledDates", jsonArray.toString());
        return this;
    }

}