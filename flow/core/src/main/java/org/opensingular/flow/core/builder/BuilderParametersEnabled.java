/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

import java.util.function.Consumer;

/**
 *
 * @author Daniel C. Bordin on 20/03/2017.
 */
public interface BuilderParametersEnabled<SELF extends BuilderParametersEnabled<SELF>> {

    public default SELF self() {
        return (SELF) this;
    }

    default SELF with(Consumer<SELF> consumer) {
        SELF self = self();
        consumer.accept(self);
        return self;
    }

    public abstract SParametersEnabled getParametersEnabled();

    /**
     * Adiciona um parâmetro que automaticamente atualiza a variável do processo. O parâmetro têm as mesmas
     * definições da variável.
     */
    public default SELF addParamBindedToProcessVariable(String ref, boolean obrigatorio) {
        getParametersEnabled().addParamBindedToProcessVariable(ref, obrigatorio);
        return self();
    }

    public default SELF addParamString(String ref, boolean obrigatorio, Integer tamanho) {
        return addParamString(ref, ref, obrigatorio, tamanho);
    }

    public default SELF addParamString(String ref, boolean obrigatorio) {
        return addParamString(ref, ref, obrigatorio, null);
    }

    public default SELF addParamString(String ref, String nome, boolean obrigatorio) {
        return addParamString(ref, nome, obrigatorio, null);
    }

    public default SELF addParamString(String ref, String nome, boolean obrigatorio, Integer tamanho) {
        getParametersEnabled().getParameters().addVariableString(ref, nome, tamanho).setRequired(obrigatorio);
        return self();
    }

    public default SELF addParamStringMultipleLines(String ref, String nome, boolean obrigatorio) {
        return addParamStringMultipleLines(ref, nome, obrigatorio, null);
    }

    public default SELF addParamStringMultipleLines(String ref, String nome, boolean obrigatorio, Integer tamanho) {
        getParametersEnabled().getParameters().addVariableStringMultipleLines(ref, nome, tamanho).setRequired(obrigatorio);
        return self();
    }

    public default SELF addParamInteger(String ref, boolean obrigatorio) {
        return addParamInteger(ref, ref, obrigatorio);
    }

    public default SELF addParamInteger(String ref, String nome, boolean obrigatorio) {
        getParametersEnabled().getParameters().addVariableInteger(ref, nome).setRequired(obrigatorio);
        return self();
    }

    public default SELF addParamDouble(String ref, boolean obrigatorio) {
        return addParamDouble(ref, ref, obrigatorio);
    }

    public default SELF addParamDouble(String ref, String nome, boolean obrigatorio) {
        getParametersEnabled().getParameters().addVariableDouble(ref, nome).setRequired(obrigatorio);
        return self();
    }

    public default SELF addParamDate(String ref, boolean obrigatorio) {
        return addParamDate(ref, ref, obrigatorio);
    }

    public default SELF addParamDate(String ref, String nome, boolean obrigatorio) {
        getParametersEnabled().getParameters().addVariableDate(ref, nome).setRequired(obrigatorio);
        return self();
    }

    public default SELF addParam(String ref, VarType tipo, boolean obrigatorio) {
        return addParam(ref, ref, tipo, obrigatorio);
    }

    public default SELF addParam(String ref, String nome, VarType varType, boolean obrigatorio) {
        getParametersEnabled().getParameters().addVariable(ref, nome, varType).setRequired(obrigatorio);
        return self();
    }
}
