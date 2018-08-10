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
     * This method should be used for hide the modal of time picker.
     * @param hide True will hide the timer picker.
     *             False will show the timer.
     * @return <code>this</code>
     */
    ISViewTime hideModalTimePicker(Boolean hide);

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


    /**
     * This method should return the value of hide timer picker.
     * @return True will hide the picker.
     */
    boolean isModalTimerHide();
}
