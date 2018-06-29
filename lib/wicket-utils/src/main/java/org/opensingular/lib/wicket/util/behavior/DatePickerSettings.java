package org.opensingular.lib.wicket.util.behavior;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Options to configure the bootstrap datepicker
 * @see <a href="http://bootstrap-datepicker.readthedocs.io/en/latest/options.html">Bootstrap DatePicker Settings</a>
 */
public interface DatePickerSettings extends Serializable {
    /**
     * Initial date that can be selected
     */
    Optional<Date> getStartDate();

    /**
     * enabled dates that can be selected
     */
    Optional<List<Date>> getEnabledDates();

    /**
     * Whether or not to close the datepicker immediately when a date is selected.
     */
    Optional<Boolean> isAutoclose();

    /**
     * If true, displays a “Clear” button at the bottom of the datepicker to clear the input value.
     * If “autoclose” is also set to true, this button will also close the datepicker.
     */
    Optional<Boolean> isClearBtn();

    /**
     * If false, the datepicker will be prevented from showing when the input field associated with it receives focus.
     */
    Optional<Boolean> isShowOnFocus();

    /**
     * Shows a today button on calendar
     */
    Optional<Boolean> isTodayBtn();

    /**
     * If true, highlights the current date.
     */
    Optional<Boolean> isTodayHighlight();
}