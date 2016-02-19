package br.net.mirante.singular.form.mform.basic.view;


import java.util.Map;
import java.util.TreeMap;

public class MDateTimerView extends MView {

    private Map<String, Object> params = new TreeMap<>();

    public MDateTimerView set24hsMode(boolean value) {
        params.put("showMeridian", value);
        return this;
    }

    public MDateTimerView setMinuteStep(Integer value) {
        params.put("minuteStep", value);
        return this;
    }

    public Map<String, Object> getParams() {
        return params;
    }
}
