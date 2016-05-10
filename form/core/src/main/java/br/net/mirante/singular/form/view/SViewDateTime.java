/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.view;


import java.util.Map;
import java.util.TreeMap;

public class SViewDateTime extends SView {

    private Map<String, Object> params = new TreeMap<>();

    public SViewDateTime set24hsMode(boolean value) {
        params.put("showMeridian", value);
        return this;
    }

    public SViewDateTime setMinuteStep(Integer value) {
        params.put("minuteStep", value);
        return this;
    }

    public Map<String, Object> getParams() {
        return params;
    }
}
