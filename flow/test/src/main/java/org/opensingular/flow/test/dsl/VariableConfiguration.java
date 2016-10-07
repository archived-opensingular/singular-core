/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.test.dsl;

import org.opensingular.flow.core.variable.VarType;

public class VariableConfiguration {

    public  VariableConfiguration addVariable(String ref, VarType tipo) {
        return this;
    }

    public  VariableConfiguration addVariableBoolean(String ref, String name) {
        return this;
    }

    public  VariableConfiguration addVariableDouble(String ref, String name) {
        return this;
    }

    public  VariableConfiguration addVariableInteger(String ref, String name) {
        return this;
    }

    public  VariableConfiguration addVariableInteger(String ref) {
        return this;
    }

    public  VariableConfiguration addVariableDate(String ref, String name) {
        return this;
    }

    public  VariableConfiguration addVariableDate(String ref) {
        return this;
    }

    public  VariableConfiguration addVariableStringMultipleLines(String ref, String name, Integer tamanhoMaximo) {
        return this;
    }

    public  VariableConfiguration addVariableStringMultipleLines(String ref, String name) {
        return this;
    }

    public  VariableConfiguration addVariableString(String ref, String name, Integer tamanhoMaximo) {
        return this;
    }

    public  VariableConfiguration addVariableString(String ref, String name) {
        return this;
    }

    public  VariableConfiguration addVariableString(String ref) {
        return addVariableString(ref, ref, null);
    }
}
