/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.bamclient.builder.amchart;

import org.opensingular.singular.bamclient.builder.JSONBuilderContext;

public class AmSerialChartBuilder extends AmChartBuilder<AmSerialChartBuilder> {

    public AmSerialChartBuilder(JSONBuilderContext JSONBuilderContext) {
        super(JSONBuilderContext);
        context.getJsonWriter().key("type").value("serial");
    }

    @Override
    public AmSerialChartBuilder self() {
        return this;
    }
}
