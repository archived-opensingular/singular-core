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

package org.opensingular.flow.core.variable;

import org.opensingular.lib.commons.base.SingularException;

import java.util.Objects;

public class DefaultVarInstance extends AbstractVarInstance {

    private boolean historySaved = false;

    private Object valor;

    public DefaultVarInstance(VarDefinition definition) {
        super(definition);
    }

    @Override
    public VarInstance setValor(Object valor) {
        try {
            Object antes = this.valor;
            this.valor = valor;
            if (needToNotifyAboutValueChanged() && !Objects.equals(antes, this.valor)) {
                notifyValueChanged();
            }
            return this;
        } catch (RuntimeException e) {
            throw new SingularException("Erro setando valor '" + valor + "' em " + getRef() + " (" + getNome() + ")", e);
        }
    }

    @Override
    public Object getValor() {
        return valor;
    }
}
