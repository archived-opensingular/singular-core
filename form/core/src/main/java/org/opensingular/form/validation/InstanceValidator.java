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

package org.opensingular.form.validation;

import org.opensingular.form.SInstance;

/**
 * <h1>Validação</h1>
 *
 * <p>
 * Validações são essenciais em uma aplicação, pôs garantem que o dado informado pelo usuário
 * é uma informação que contempla os requisitos esperados. O Singular Form possibilita a validação de dados através
 * da interface InstanceValidator.
 * </p>
 * <p>
 * O Exemplo abaixo, apresenta uma possível implementação desta interface, no qual irá verificar se a string
 * informada inicia com letra minúscula e possui somente letras e números.
 * <pre>
 *  InstanceValidator<SIString> identificadorValidator = (instanceValidatable) -> {
 *      if (!instanceValidatable.getInstance().getValue().matches("^[a-z][\\dA-Za-z]+$")) {
 *          instanceValidatable.error("Não é um identificador valido");
 *      }
 *  };
 * </pre>
 * </p>
 *
 * @param <I> o tipo da instancia a ser validada
 */
public interface InstanceValidator<I extends SInstance> {

    /**
     *  Executa a validação do dado, adicionando os erros encontrados.
     *
     * @param instanceValidatable Os dados de validação da instancia
     */
    void validate(InstanceValidatable<I> instanceValidatable);
    
    /**
     * Caso retorne <code>true</code>, este validador só será executado caso a instância correspondente não
     * possua nenhum erro em seus descendentes. Caso retorne <code>false</code>, será executado independentemente da
     * validade de seus descendentes (campos obrigatórios poderão estar nulos neste caso).
     * @return se este validador deve ou não ser executado caso seus descendentes contenham erros. Por padrão, returna <code>true</code>. 
     */
    default boolean executeOnlyIfChildrenValid() {
        return true;
    }

}