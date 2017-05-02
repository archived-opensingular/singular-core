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

package org.opensingular.flow.core.variable.type;

import org.opensingular.flow.core.variable.SingularFlowConvertingValueException;
import org.opensingular.flow.core.variable.VarInstance;
import org.opensingular.flow.core.variable.VarType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Classe de suporte na criação de {@link VarType}.
 *
 * @author Daniel C. Bordin on 19/03/2017.
 */
public abstract class VarTypeBase<TYPE> implements VarType<TYPE> {

    private final Class<TYPE> classTypeContent;

    protected VarTypeBase(Class<TYPE> classTypeContent) {this.classTypeContent = classTypeContent;}

    public final Class<TYPE> getClassTypeContent() {
        return classTypeContent;
    }

    @Override
    public final String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public final String toDisplayString(VarInstance varInstance) {
        return toDisplayString(varInstance.getValue(), varInstance.getDefinition());
    }

    /** Convert o valor informado para o tipo nativo do tipo. */
    public final TYPE convert(Object original) {
        if (original == null) {
            return null;
        } else if (getClassTypeContent().isInstance(original)) {
            return getClassTypeContent().cast(original);
        } else if (original instanceof String) {
            return fromPersistenceString((String) original);
        }
        try {
            TYPE result = convertNotDirectCompatible(original);
            if (result != null) {
                return result;
            }
        } catch(Exception e) {
            throw rethrow(e, original);
        }
        throw rethrow(null, original);
    }

    public final TYPE fromPersistenceString(String persistenceValue) throws SingularFlowConvertingValueException {
        try {
            return fromPersistenceStringImpl(persistenceValue);
        } catch(Exception e) {
            throw rethrow(e, persistenceValue);
        }
    }

    private SingularFlowConvertingValueException rethrow(Exception e, Object originalValue) {
        return SingularFlowConvertingValueException.rethrow(e, this, originalValue)
                .addValueBeingConverted(originalValue);
    }

    /** Deve ser implementando pela subclasse com a lógica específica de conversão. */
    protected abstract TYPE fromPersistenceStringImpl(String persistenceValue) throws
            SingularFlowConvertingValueException;

    /** Chamado para converte um objeto não nativo do tipo. Se retornar null, significa que não conseguiu converte.*/
    @Nullable
    protected abstract TYPE convertNotDirectCompatible(@Nonnull Object original);

}
