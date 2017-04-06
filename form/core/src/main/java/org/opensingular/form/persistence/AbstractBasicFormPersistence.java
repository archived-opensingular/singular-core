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

package org.opensingular.form.persistence;

import org.opensingular.form.SInstance;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author Daniel C. Bordin
 */
public abstract class AbstractBasicFormPersistence<INSTANCE extends SInstance, KEY extends FormKey>
        implements BasicFormPersistence<INSTANCE> {

    private final FormKeyManager<KEY> formKeyManager;

    public AbstractBasicFormPersistence(@Nonnull Class<KEY> keyClass) {
        this.formKeyManager = new FormKeyManager<>(keyClass, e -> addInfo(e));
    }

    /** Retornar o manipulador de chave usado por essa implementação para ler e converte FormKey. */
    @Nonnull
    public final FormKeyManager<KEY> getFormKeyManager() {
        return formKeyManager;
    }

    @Override
    @Nonnull
    public KEY keyFromObject(@Nonnull Object objectValueToBeConverted) {
        return formKeyManager.keyFromObject(objectValueToBeConverted);
    }

    @Override
    public void delete(@Nonnull FormKey key) {
        deleteInternal(getFormKeyManager().validKeyOrException(key));
    }

    @Override
    public void update(@Nonnull INSTANCE instance, Integer inclusionActor) {
        KEY key = getFormKeyManager().readFormKeyOrException(instance);
        updateInternal(key, instance, inclusionActor);
    }

    @Override
    @Nonnull
    public FormKey insertOrUpdate(@Nonnull INSTANCE instance, Integer inclusionActor) {
        Optional<KEY> key = getFormKeyManager().readFormKeyOptional(instance);
        if (key.isPresent()) {
            updateInternal(key.get(), instance, inclusionActor);
            return key.get();
        }
        return insertImpl(instance, inclusionActor);
    }

    @Override
    @Nonnull
    public FormKey insert(@Nonnull INSTANCE instance, Integer inclusionActor) {
        if (instance == null) {
            throw addInfo(new SingularFormPersistenceException("O parâmetro instance está null")).add(this);
        }
        return insertImpl(instance, inclusionActor);
    }

    @Nonnull
    private KEY insertImpl(@Nonnull INSTANCE instance, Integer inclusionActor) {
        KEY key = insertInternal(instance, inclusionActor);
        getFormKeyManager().validKeyOrException(key, instance, "Era esperado que o insert interno gerasse uma FormKey, mas retornou null");
        FormKey.set(instance, key);
        return key;
    }

    protected abstract void updateInternal(@Nonnull KEY key, @Nonnull INSTANCE instance, Integer inclusionActor);

    protected abstract void deleteInternal(@Nonnull KEY key);

    @Nonnull
    protected abstract KEY insertInternal(@Nonnull INSTANCE instance, Integer inclusionActor);

    @Override
    public final boolean isPersistent(@Nonnull INSTANCE instance) {
        return getFormKeyManager().isPersistent(instance);
    }

    /**
     * Método chamado para adicionar informção do serviço de persistência à exception. Pode ser ser sobreescito para
     * acrescimo de maiores informações.
     */
    @Nonnull
    protected SingularFormPersistenceException addInfo(@Nonnull SingularFormPersistenceException exception) {
        exception.add("persitence", toString());
        return exception;
    }
}


