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

import org.opensingular.form.view.SView;

public class SViewTime extends SView implements ISViewTime {

    private boolean mode24hs = false;
    private Integer minuteStep;
    private boolean hideModalTimer = false;


    public SViewTime setMode24hs(boolean value) {
        mode24hs = value;
        return this;
    }

    public SViewTime setMinuteStep(Integer value) {
        minuteStep = value;
        return this;
    }

    public boolean isMode24hs() {
        return mode24hs;
    }

    public Integer getMinuteStep() {
        return minuteStep;
    }

    /**
     * Default: False (show modal picker).
     *
     * @param hide True will hide the timer picker.
     *             False will show the timer.
     * @return <code>this</code>
     */
    @Override
    public SViewTime hideModalTimePicker(Boolean hide) {
        hideModalTimer = hide;
        return this;
    }

    @Override
    public boolean isModalTimerHide() {
        return hideModalTimer;
    }


}
