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

package org.opensingular.flow.core;

import org.opensingular.flow.core.variable.ValidationResult;

import javax.annotation.Nonnull;

/**
 * Exception referente a passagem incorreta de parâmentros, em geral em um start de processo ou execução de transição.
 *
 * @author Daniel C. Bordin on 21/03/2017.
 */
public class SingularFlowInvalidParametersException extends SingularFlowException {

    SingularFlowInvalidParametersException(StartCall<?> startCall, ValidationResult result) {
        this(startCall.getFlowDefinition(), result, "Erro nos parâmetros passados para inicialização do processo '" +
                startCall.getFlowDefinition().getName() + "'");
    }

    SingularFlowInvalidParametersException(FlowDefinition<?> flowDefinition, ValidationResult result) {
        this(flowDefinition, result, "Erro ao iniciar processo '" + flowDefinition.getName());
    }

    SingularFlowInvalidParametersException(@Nonnull TaskInstance taskInstance, @Nonnull STransition transition, ValidationResult result) {
        this(taskInstance.getFlowInstance().getFlowDefinition(), result,
                "Erro ao validar os parametros da transição '" + transition.getName() + "' a partir da tarefa '" +
                        taskInstance.getName() + "'");
        add(taskInstance);
    }

    private SingularFlowInvalidParametersException(FlowDefinition<?> flowDefinition, ValidationResult result, String msg) {
        super(msg + ": variáveis inválidas" + appendIfOneErro(result));
        add(flowDefinition);
        if (result.errors().size() > 1) {
            add(result);
        }
    }

    private static String appendIfOneErro(ValidationResult result) {
        if (result.errors().size() == 1) {
            return ": " + result.errors().get(0);
        }
        return "";
    }

    private void add(ValidationResult result) {
        int i = 1;
        for (String msg : result.errors()) {
            add("Erro " + i++, msg);
        }
    }
}
