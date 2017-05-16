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

import org.opensingular.flow.core.SingularFlowException;
import org.opensingular.lib.commons.base.SingularException;

import java.io.Serializable;
import java.util.Objects;

public class DefaultVarInstance extends AbstractVarInstance {

    private Serializable valor;

    public DefaultVarInstance(VarDefinition definition) {
        super(definition);
    }

    @Override
    public VarInstance setValue(Object valor) {
        try {
            Object antes = this.valor;
            Object v = getDefinition().convert(valor);
            if (v != null && !(v instanceof Serializable)) {
                throw new SingularFlowException("O valor atribuido não é serializável")
                        .add("varName", getName())
                        .add("varType", getType())
                        .add("value", v)
                        .add("valueClass", v.getClass().getName());
            }
            this.valor = (Serializable) v;
            if (needToNotifyAboutValueChanged() && !Objects.equals(antes, this.valor)) {
                notifyValueChanged();
            }
            return this;
        } catch (RuntimeException e) {
            throw SingularException.rethrow("Erro setando valor '" + valor + "' em " + getRef() + " (" + getName() + ")", e);
        }
    }

    @Override
    public Object getValue() {
        return valor;
    }
}
