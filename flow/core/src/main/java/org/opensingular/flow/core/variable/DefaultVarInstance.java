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

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Objects;

public class DefaultVarInstance extends AbstractVarInstance {

    private transient Object value;

    public DefaultVarInstance(VarDefinition definition) {
        super(definition);
    }

    @Override
    public VarInstance setValue(Object value) {
        try {
            Object before = this.value;
            this.value = getDefinition().convert(value);
            if (needToNotifyAboutValueChanged() && !Objects.equals(before, this.value)) {
                notifyValueChanged();
            }
            return this;
        } catch (RuntimeException e) {
            throw SingularException.rethrow(
                    "Erro setando valor '" + value + "' em " + getRef() + " (" + getName() + ")", e);
        }
    }

    @Override
    public Object getValue() {
        return value;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        try {
            out.writeObject(value);
        } catch (NotSerializableException e) {
            throw new SingularFlowException("O valor da variável não é serializável", e)
                    .add("varName", getName())
                    .add("varType", getType())
                    .add("value", value)
                    .add("valueClass", value.getClass().getName());
        }
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        value = in.readObject();
        in.defaultReadObject();
    }
}
