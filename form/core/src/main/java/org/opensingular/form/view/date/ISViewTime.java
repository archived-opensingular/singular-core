package org.opensingular.form.view.date;

/**
 * This interface will configure the Time component.
 * <code> https://github.com/jdewit/bootstrap-timepicker </code>
 */
public interface ISViewTime {

    /**
     * Configure the mode of the timer.
     * By default the value will be False.
     *
     * @param value True will able the 12 AM/PM mode.
     *              False wil able the 24 mode.
     * @return <code>this</code>
     */
    ISViewTime setMode24hs(boolean value);

    /**
     * Change the minute step of the timer modal.
     *
     * @param value The number of the step.
     * @return <code>this</code>
     */
    ISViewTime setMinuteStep(Integer value);

    /**
     * True if is 12 AM/PM mode.
     * False if showMeridian is 24hr.
     *
     * @return return boolean that represents the mode 24 hrs.
     */
    boolean isMode24hs();

    /**
     * The value of the step minutes in the modal of the timer configurate.
     * Example: 20 will pass 20 minute by click.
     *
     * @return The number of the step minute.
     */
    Integer getMinuteStep();
}
