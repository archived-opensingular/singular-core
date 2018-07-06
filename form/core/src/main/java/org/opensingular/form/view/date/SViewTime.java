package org.opensingular.form.view.date;

import org.opensingular.form.view.SView;

public class SViewTime extends SView implements ISViewTime {

    private boolean mode24hs = false;
    private Integer minuteStep;

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

}
