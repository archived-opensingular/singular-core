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

package org.opensingular.form.validation.validator;

import org.opensingular.form.validation.IInstanceValidator;

/**
 * Coleção de {@link IInstanceValidator}
 */
public final class InstanceValidators {

    private InstanceValidators() {
    }
    
    /**
     * Verifica se todos ou nenhum campo foi preenchido
     */
    public static AllOrNothingInstanceValidator allOrNothing(){
        return AllOrNothingInstanceValidator.INSTANCE;
    }

    /**
     * Verifica se o CEP é válido
     */
    public static MCEPValidator cep(){
        return MCEPValidator.INSTANCE;
    }

    /**
     * Verifica se o CNPJ é válido
     */
    public static MCNPJValidator cnpj(){
        return MCNPJValidator.INSTANCE;
    }

    /**
     * Verifica se o CPF é válido
     */
    public static MCPFValidator cpf(){
        return MCPFValidator.INSTANCE;
    }

    /**
     * Verifica se o email é válido
     */
    public static MEmailValidator email(){
        return MEmailValidator.INSTANCE;
    }

    /**
     * Verifica se o telefone nacional é válido
     */
    public static MTelefoneNacionalValidator telefoneNacional(){
        return MTelefoneNacionalValidator.INSTANCE;
    }

    /**
     * Verifica se o email é válido. Permite informar endereço local.
     */
    public static MEmailValidator emailLocalAddress(){
        return MEmailValidator.INSTANCE_ALLOW_LOCAL_ADDRESS;
    }
}
