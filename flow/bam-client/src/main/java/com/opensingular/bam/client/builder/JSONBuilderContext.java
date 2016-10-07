/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.client.builder;

import java.io.StringWriter;

import org.json.JSONWriter;


/**
 * Contexto utilizado durante a criação de JSON com AbstractJSONBuilder
 */
public class JSONBuilderContext {

    private final StringWriter writer = new StringWriter();
    private final JSONWriter jsonWriter = new JSONWriter(writer);

    public JSONWriter getJsonWriter() {
        return jsonWriter;
    }

    public StringWriter getWriter() {
        return writer;
    }
}
