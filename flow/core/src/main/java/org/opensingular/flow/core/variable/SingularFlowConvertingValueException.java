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

package org.opensingular.flow.core.variable;

import org.opensingular.flow.core.SingularFlowException;

import java.util.Objects;

/**
 * Representa um erro ao converte um valor de variável, provavelmente devido ao fato do valor não ser passível de
 * conversão.
 *
 * @author Daniel C. Bordin on 22/03/2017.
 */
public class SingularFlowConvertingValueException extends SingularFlowException {

    private SingularFlowConvertingValueException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * Transforma a exception em {@link SingularFlowConvertingValueException}, se a mesma já não for desse tipo.
     */
    public static SingularFlowConvertingValueException rethrow(String errorMsg, Exception original) {
        return rethrow(errorMsg, original, null, null, null);
    }

    public static SingularFlowConvertingValueException rethrow(Exception original, VarType varType, Object value) {
        return rethrow(null, original, null, varType, value);
    }

    public static SingularFlowConvertingValueException rethrow(Exception original, VarDefinition varDefinition,
            Object value) {
        return rethrow(null, original, varDefinition, null, value);
    }

    /**
     * Transforma a exception em {@link SingularFlowConvertingValueException}, se a mesma já não for desse tipo. Além
     * disso acrescenta na mensagem de erro as informações da variável que deu erro.
     */
    private static SingularFlowConvertingValueException rethrow(String errorMsg, Exception original,
            VarDefinition varDefinition, VarType<?> varType, Object value) {
        SingularFlowConvertingValueException e2;
        if (original instanceof SingularFlowConvertingValueException) {
            e2 = (SingularFlowConvertingValueException) original;
            e2.add(varDefinition);
        } else {
            VarType<?> type = (varType == null && varDefinition != null ? varDefinition.getType() : varType);
            String m2 = errorMsg;
            if (errorMsg == null) {
                m2 = "Não foi possível converter o valor de variável";
                if (type != null) {
                    m2 += " para o tipo " + type.getClassTypeContent().getName();
                }
            }
            e2 = new SingularFlowConvertingValueException(m2, original);
            e2.add(varDefinition);
            if (type != null) {
                e2.add("varType", type.getName() + " (" + type.getClass() + ")");
            }
            e2.addValueBeingConverted(value);
        }
        return e2;
    }

    private void add(VarDefinition varDefinition) {
        if (varDefinition != null && ! containsEntry("variableName")) {
            if (Objects.equals(varDefinition.getRef(), varDefinition.getName())) {
                add("variableName", varDefinition.getRef());
            } else {
                add("variableName", varDefinition.getRef() + " (" + varDefinition.getName() + ")");
            }
        }
    }

    /** Adiciona informações sobre o valor sendo convertido na mensagem de erro. */
    public SingularFlowConvertingValueException addValueBeingConverted(Object originalValue) {
        if(! containsEntry("valueToBeConverted")) {
            add("valueToBeConverted", originalValue);
            if (originalValue != null) {
                add("valueToBeConverted class", originalValue.getClass().getName());
            }
        }
        return this;
    }
}
