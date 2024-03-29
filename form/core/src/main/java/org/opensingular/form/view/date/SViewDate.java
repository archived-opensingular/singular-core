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
import org.opensingular.form.view.SView;
import org.opensingular.lib.commons.lambda.IFunction;

public class SViewDate extends SView implements ISViewDate {

    private boolean autoclose = true;
    private boolean clearBtn = false;
    private Date startDate;
    private boolean todayBtn = false;
    private boolean todayHighlight = false;
    private boolean showOnFocus = true;
    private boolean hideModal = false;
    private IFunction<SInstance, List<Date>> enabledDatesFunction;
    private IFunction<SInstance, Date> startDateFunction;

    public IFunction<SInstance, List<Date>> getEnabledDatesFunction() {
        return enabledDatesFunction;
    }

    /**
     * Function to retrieve the enabled dates that can be selected
     */
    @Override
    public SViewDate setEnabledDatesFunction(IFunction<SInstance, List<Date>> enabledDatesFunction) {
        this.enabledDatesFunction = enabledDatesFunction;
        return this;
    }

    /**
     * Whether or not to close the datepicker immediately when a date is selected.
     * <p>
     * Default: false
     */
    @Override
    public SViewDate setAutoclose(boolean autoclose) {
        this.autoclose = autoclose;
        return this;
    }

    /**
     * If true, displays a “Clear” button at the bottom of the datepicker to clear the input value.
     * If “autoclose” is also set to true, this button will also close the datepicker.
     * <p>
     * Default: false
     */
    @Override
    public SViewDate setClearBtn(boolean clearBtn) {
        this.clearBtn = clearBtn;
        return this;
    }

    /**
     * Initial date that can be selected
     * <p>
     * Default: null
     */
    @Override
    public SViewDate setStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    /**
     * Initial date that can be selected, this function will override the static value that had been set on setStartDate
     * <p>
     * Default: null
     */
    @Override
    public SViewDate setStartDateFunction(IFunction<SInstance, Date> startDateFunction) {
        this.startDateFunction = startDateFunction;
        return this;
    }

    /**
     * Shows a today button on calendar
     * <p>
     * Default: false
     */
    @Override
    public SViewDate setTodayBtn(boolean todayBtn) {
        this.todayBtn = todayBtn;
        return this;
    }

    /**
     * If true, highlights the current date.
     * <p>
     * Default: false
     */
    @Override
    public SViewDate setTodayHighlight(boolean todayHighlight) {
        this.todayHighlight = todayHighlight;
        return this;
    }

    /**
     * If false, the datepicker will be prevented from showing when the input field associated with it receives focus.
     * <p>
     * Default: true
     */
    @Override
    public SViewDate setShowOnFocus(boolean showOnFocus) {
        this.showOnFocus = showOnFocus;
        return this;
    }

    /**
     * Default: False (show modal picker).
     *
     * @param hide True will hide the date picker.
     *             False will show the date.
     * @return <code>this</code>
     */
    @Override
    public SViewDate hideModalDatePicker(Boolean hide) {
        this.hideModal = hide;
        return this;
    }

    @Override
    public boolean isAutoclose() {
        return autoclose;
    }

    @Override
    public boolean isClearBtn() {
        return clearBtn;
    }

    @Override
    public Date getStartDate() {
        return startDate;
    }

    @Override
    public boolean isTodayBtn() {
        return todayBtn;
    }

    @Override
    public boolean isTodayHighlight() {
        return todayHighlight;
    }

    @Override
    public boolean isShowOnFocus() {
        return showOnFocus;
    }

    @Override
    public IFunction<SInstance, Date> getStartDateFunction() {
        return startDateFunction;
    }

    @Override
    public boolean isModalHide() {
        return hideModal;
    }


}
