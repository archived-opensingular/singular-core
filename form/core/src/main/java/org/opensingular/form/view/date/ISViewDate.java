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

package org.opensingular.form.view.date;

import java.util.Date;
import java.util.List;

import org.opensingular.form.SInstance;
import org.opensingular.lib.commons.lambda.IFunction;

/**
 * This view will configure the Timer component.
 * For more information: <code>http://bootstrap-datepicker.readthedocs.io/en/latest/</code>
 */
public interface ISViewDate {

    IFunction<SInstance, List<Date>> getEnabledDatesFunction();

    /**
     * Function to retrieve the enabled dates that can be selected
     */
    SViewDate setEnabledDatesFunction(IFunction<SInstance, List<Date>> enabledDatesFunction);

    /**
     * Whether or not to close the datepicker immediately when a date is selected.
     * <p>
     * Default: false
     */
    SViewDate setAutoclose(boolean autoclose);

    /**
     * If true, displays a “Clear” button at the bottom of the datepicker to clear the input value.
     * If “autoclose” is also set to true, this button will also close the datepicker.
     * <p>
     * Default: false
     */
    SViewDate setClearBtn(boolean clearBtn);

    /**
     * Initial date that can be selected
     * <p>
     * Default: null
     */
    SViewDate setStartDate(Date startDate);

    /**
     * Initial date that can be selected, this function will override the static value that had been set on setStartDate
     * <p>
     * Default: null
     */
    SViewDate setStartDateFunction(IFunction<SInstance, Date> startDateFunction);

    /**
     * Shows a today button on calendar
     * <p>
     * Default: false
     */
    SViewDate setTodayBtn(boolean todayBtn);

    /**
     * If true, highlights the current date.
     * <p>
     * Default: false
     * <p> if the Today button is enabled, the todayHighlight will be forced to true.
     */
    SViewDate setTodayHighlight(boolean todayHighlight);

    /**
     * If false, the datepicker will be prevented from showing when the input field associated with it receives focus.
     * <p>
     * Default: true
     */
    SViewDate setShowOnFocus(boolean showOnFocus);

    /**
     * This method should be used for hide the modal of date picker.
     * @param hide True will hide the date picker.
     *             False will show the date.
     * @return <code>this</code>
     */
    SViewDate hideModalDatePicker(Boolean hide);

    boolean isAutoclose();

    boolean isClearBtn();

    Date getStartDate();

    boolean isTodayBtn();

    boolean isTodayHighlight();

    boolean isShowOnFocus();

    IFunction<SInstance, Date> getStartDateFunction();

    /**
     * This method should return the value of hide date picker.
     * @return True will hide the picker.
     */
    boolean isModalHide();
}
