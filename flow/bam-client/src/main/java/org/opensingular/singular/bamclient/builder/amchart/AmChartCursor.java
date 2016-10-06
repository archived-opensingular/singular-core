/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.bamclient.builder.amchart;

import org.opensingular.singular.bamclient.builder.JSONObjectMappper;

public class AmChartCursor extends JSONObjectMappper<AmChartCursor> {

    public AmChartCursor categoryBalloonEnabled(boolean value) {
        return put("categoryBalloonEnabled", value);
    }

    public AmChartCursor cursorAlpha(Number value) {
        return put("cursorAlpha", value);
    }

    public AmChartCursor zoomable(boolean value) {
        return put("zoomable", value);
    }

    @Override
    public AmChartCursor self() {
        return this;
    }

}
