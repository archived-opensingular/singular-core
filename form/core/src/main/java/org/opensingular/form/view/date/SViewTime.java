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
