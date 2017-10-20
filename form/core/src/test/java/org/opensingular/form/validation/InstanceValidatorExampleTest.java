/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.validation;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.opensingular.form.SDictionary;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeString;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class InstanceValidatorExampleTest {

    private SIString sistring;
    private InstanceValidator<SIString> identificadorValidator;

    @Before
    public void setUp() throws Exception {
        sistring = SDictionary.create().newInstance(STypeString.class);
        identificadorValidator = (stringInstanceValidatable) -> {
            if (!stringInstanceValidatable.getInstance().matches("^[a-z][\\dA-Za-z]+$")) {
                stringInstanceValidatable.error("Não é um identificador valido");
            }
        };
    }

    @Test
    public void verificarSeNaoExisteErros1() throws Exception {
        verify(preencherValorEValidar("nomeProprio123"), never()).error(anyString());
    }

    @Test
    public void verificarSeNaoExisteErros2() throws Exception {
        verify(preencherValorEValidar("nomeProprio"), never()).error(anyString());
    }

    @Test
    public void verificarSeExisteErros1() throws Exception {
        verify(preencherValorEValidar("123nomeProprio")).error(anyString());
    }

    @Test
    public void verificarSeExisteErros2() throws Exception {
        verify(preencherValorEValidar("#nomeProprio")).error(anyString());
    }

    private InstanceValidatable<SIString> preencherValorEValidar(String nomeProprio123) {
        InstanceValidatable<SIString> instanceValidatable = Mockito.mock(InstanceValidatable.class);
        Mockito.when(instanceValidatable.getInstance()).thenReturn(sistring);
        sistring.setValue(nomeProprio123);
        identificadorValidator.validate(instanceValidatable);
        return instanceValidatable;
    }

}