package org.opensingular.form.view.date;

public interface ISViewTime {

    ISViewTime setMode24hs(boolean value);

    ISViewTime setMinuteStep(Integer value);

    boolean isMode24hs();

    Integer getMinuteStep();
}
