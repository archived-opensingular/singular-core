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

package org.opensingular.flow.core.builder;

import org.opensingular.flow.core.SParametersEnabled;
import org.opensingular.flow.core.variable.VarType;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 *
 * @author Daniel C. Bordin on 20/03/2017.
 */
public interface BuilderParametersEnabled<SELF extends BuilderParametersEnabled<SELF>> {

    @Nonnull
    public default SELF self() {
        return (SELF) this;
    }

    @Nonnull
    default SELF with(Consumer<SELF> consumer) {
        SELF self = self();
        consumer.accept(self);
        return self;
    }

    public abstract SParametersEnabled getParametersEnabled();

    /**
     * Adiciona um parâmetro que automaticamente atualiza a variável do fluxo. O parâmetro têm as mesmas
     * definições da variável.
     */
    public default SELF addParamBindedToFlowVariable(String ref, boolean required) {
        getParametersEnabled().addParamBindedToFlowVariable(ref, required);
        return self();
    }

    public default SELF addParamString(String ref, boolean required, Integer size) {
        return addParamString(ref, ref, required, size);
    }

    public default SELF addParamString(String ref, boolean required) {
        return addParamString(ref, ref, required, null);
    }

    public default SELF addParamString(String ref, String name, boolean required) {
        return addParamString(ref, name, required, null);
    }

    public default SELF addParamString(String ref, String name, boolean required, Integer size) {
        getParametersEnabled().getParameters().addVariableString(ref, name, size).setRequired(required);
        return self();
    }

    public default SELF addParamStringMultipleLines(String ref, String name, boolean required) {
        return addParamStringMultipleLines(ref, name, required, null);
    }

    public default SELF addParamStringMultipleLines(String ref, String name, boolean required, Integer size) {
        getParametersEnabled().getParameters().addVariableStringMultipleLines(ref, name, size).setRequired(required);
        return self();
    }

    public default SELF addParamInteger(String ref, boolean required) {
        return addParamInteger(ref, ref, required);
    }

    public default SELF addParamInteger(String ref, String name, boolean required) {
        getParametersEnabled().getParameters().addVariableInteger(ref, name).setRequired(required);
        return self();
    }

    public default SELF addParamBigDecimal(String ref, boolean required) {
        return addParamBigDecimal(ref, ref, required);
    }

    public default SELF addParamBigDecimal(String ref, String name, boolean required) {
        getParametersEnabled().getParameters().addVariableBigDecimal(ref, name).setRequired(required);
        return self();
    }

    public default SELF addParamDouble(String ref, boolean required) {
        return addParamDouble(ref, ref, required);
    }

    public default SELF addParamDouble(String ref, String name, boolean required) {
        getParametersEnabled().getParameters().addVariableDouble(ref, name).setRequired(required);
        return self();
    }

    public default SELF addParamDate(String ref, boolean required) {
        return addParamDate(ref, ref, required);
    }

    public default SELF addParamDate(String ref, String name, boolean required) {
        getParametersEnabled().getParameters().addVariableDate(ref, name).setRequired(required);
        return self();
    }

    /**
     * Cria um parâmetro de uma classe específica. Use preferencialmente os métodos addParamXXXX com um tipo ja
     * previsto.
     */
    public default SELF addParamCustom(@Nonnull String ref, @Nonnull Class<?> paramClass, boolean required) {
        return addParamCustom(ref, ref, paramClass, required);
    }

    /**
     * Cria um parâmetro de uma classe específica. Use preferencialmente os métodos addParamXXXX com um tipo ja
     * previsto.
     */
    public default SELF addParamCustom(@Nonnull String ref, @Nonnull String name, @Nonnull Class<?> paramClass,
            boolean required) {
        getParametersEnabled().getParameters().addVariableCustom(ref, name, paramClass).setRequired(required);
        return self();
    }

    public default SELF addParam(String ref, VarType varType, boolean required) {
        return addParam(ref, ref, varType, required);
    }

    public default SELF addParam(String ref, String name, VarType varType, boolean required) {
        getParametersEnabled().getParameters().addVariable(ref, name, varType).setRequired(required);
        return self();
    }
}
