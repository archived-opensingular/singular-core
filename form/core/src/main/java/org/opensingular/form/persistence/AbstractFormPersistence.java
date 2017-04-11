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
import org.opensingular.form.SType;
import org.opensingular.form.document.SDocumentFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * @author Daniel C. Bordin
 */
public abstract class AbstractFormPersistence<TYPE extends SType<INSTANCE>, INSTANCE extends SInstance, KEY extends FormKey>
        implements FormRespository<TYPE, INSTANCE> {

    private final FormKeyManager<KEY> formKeyManager;

    private String name;

    public AbstractFormPersistence(Class<KEY> keyClass) {
        this.formKeyManager = new FormKeyManager<>(keyClass, e -> addInfo(e));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nonnull
    public abstract SDocumentFactory getDocumentFactory();

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

    @Override
    @Nonnull
    public INSTANCE load(@Nonnull FormKey key) {
        INSTANCE instance = loadImpl(key);
        if (instance == null) {
            throw new SingularFormNotFoundException(key);
        }
        return instance;
    }

    @Nonnull
    public Optional<INSTANCE> loadOpt(@Nonnull FormKey key) {
        return Optional.ofNullable(loadImpl(key));
    }

    @Nullable
    private INSTANCE loadImpl(@Nonnull FormKey key) {
        INSTANCE instance = loadInternal(getFormKeyManager().validKeyOrException(key));
        if (instance != null) {
            KEY key2 = getFormKeyManager().readFormKeyOrException(instance);
            if (!key2.equals(key)) {
                throw addInfo(new SingularFormPersistenceException(
                        "FormKey da instância encontrada, não é igual à solicitada")).add("Key Esperado", key).add(
                        "Key Encontado", key2).add(instance);
            }
        }
        return instance;
    }

    @Override
    @Nonnull
    public List<INSTANCE> loadAll(long first, long max) {
        return loadAllInternal(first, max);
    }

    @Override
    @Nonnull
    public List<INSTANCE> loadAll() {
        return loadAllInternal();
    }

    protected abstract INSTANCE loadInternal(KEY key);

    @Nonnull
    protected abstract List<INSTANCE> loadAllInternal();

    @Nonnull
    protected abstract List<INSTANCE> loadAllInternal(long first, long max);


    //-------------------------------------------------
    // Métodos de apoio
    //-------------------------------------------------

    @Override
    public String toString() {
        String s = getClass().getName();
        if (name != null) {
            s += "( name=" + name + ")";
        }
        return s;
    }
}
