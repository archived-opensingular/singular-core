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
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Daniel C. Bordin
 */
public abstract class AbstractBasicFormPersistence<INSTANCE extends SInstance, KEY extends FormKey>
        implements BasicFormPersistence<INSTANCE> {

    private static final String CONVERTER_METHOD_NAME = "convertToKey";
    private final Class<KEY> keyClass;
    private final Constructor<KEY> keyConstructor;
    private final Method convertMethod;

    public AbstractBasicFormPersistence(@Nonnull Class<KEY> keyClass) {
        this.keyClass = Objects.requireNonNull(keyClass);
        this.keyConstructor = findConstructorString(keyClass);
        this.convertMethod = findConvertMethod(keyClass);
    }

    private Method findConvertMethod(Class<KEY> keyClass) {
        Method method;
        try {
            method = keyClass.getMethod(CONVERTER_METHOD_NAME, Object.class);
        } catch (Exception e) {
            throw addInfo(new SingularFormPersistenceException(
                    "Erro tentando obter o metodo " + CONVERTER_METHOD_NAME + "(Object) na classe " +
                            keyClass.getName(), e));
        }
        if (!Modifier.isStatic(method.getModifiers()) || !Modifier.isPublic(method.getModifiers()) ||
                !keyClass.isAssignableFrom(method.getReturnType())) {
            throw addInfo(new SingularFormPersistenceException(
                    "O metodo " + CONVERTER_METHOD_NAME + "(Object) encontrado na classe " + keyClass.getName() +
                            " não é compatível com a assintura de método esperado, que seria 'public static " +
                            keyClass.getSimpleName() + " " + CONVERTER_METHOD_NAME + "(Object)'"));
        }
        return method;
    }

    private Constructor<KEY> findConstructorString(Class<KEY> keyClass) {
        try {
            return keyClass.getConstructor(String.class);
        } catch (Exception e) {
            throw addInfo(new SingularFormPersistenceException(
                    "Erro tentando obter o construtor " + keyClass.getSimpleName() + "(String) na classe " +
                            keyClass.getName(), e));
        }
    }

    @Override
    public KEY keyFromString(String persistenceString) {
        try {
            return keyConstructor.newInstance(persistenceString);
        } catch (Exception e) {
            throw addInfo(new SingularFormPersistenceException(
                    "Erro criando FormKey para o valor string da chave '" + persistenceString + "'", e));
        }
    }

    @Override
    public KEY keyFromObject(Object objectValueToBeConverted) {
        if (objectValueToBeConverted == null) {
            return null;
        } else if (keyClass.isInstance(objectValueToBeConverted)) {
            return keyClass.cast(objectValueToBeConverted);
        }
        Object result;
        try {
            result = convertMethod.invoke(null, objectValueToBeConverted);
        } catch (Exception e) {
            throw addInfo(new SingularFormPersistenceException("Erro chamado método " + CONVERTER_METHOD_NAME).add(
                    "value", objectValueToBeConverted));
        }
        return keyClass.cast(result);
    }

    @Override
    public void delete(@Nonnull FormKey key) {
        deleteInternal(checkKeyOrException(key, null));
    }

    @Override
    public void update(@Nonnull INSTANCE instance, Integer inclusionActor) {
        KEY key = readKeyAttributeOrException(instance);
        updateInternal(key, instance, inclusionActor);
    }

    @Override
    @Nonnull
    public FormKey insertOrUpdate(@Nonnull INSTANCE instance, Integer inclusionActor) {
        Optional<KEY> key = readKeyAttributeOptional(instance);
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
        checkKeyOrException(key, instance, " o insert interno gerasse uma FormKey, mas retornou null");
        instance.setAttributeValue(SPackageFormPersistence.ATR_FORM_KEY, key);
        return key;
    }

    protected abstract void updateInternal(@Nonnull KEY key, @Nonnull INSTANCE instance, Integer inclusionActor);

    protected abstract void deleteInternal(@Nonnull KEY key);

    @Nonnull
    protected abstract KEY insertInternal(@Nonnull INSTANCE instance, Integer inclusionActor);

    //-------------------------------------------------
    // Métodos de apoio
    //-------------------------------------------------

    /**
     * Lê obrigatoriamente o {@link FormKey} de uma instância e verifica se é da classe esperada. Se for diferente de
     * null e não for da classe espera, então dispara uma Exception. Se for null, dispara exception.
     */
    @Nonnull
    protected KEY readKeyAttributeOrException(@Nonnull INSTANCE instance) {
        Optional<KEY> key = readKeyAttributeOptional(instance);
        if (! key.isPresent()) {
            throw addInfo(new SingularFormPersistenceException(
                    "Era esperado que a instância tivesse o atributo FormKey preenchido")).add("key", null).add(
                    instance);
        }
        return key.get();
    }

    /**
     * Lê opcionalmente o {@link FormKey} de uma instância e verifica se é da classe esperada. Se for diferente de
     * null e não for da classe espera, então dispara uma Exception.
     */
    @Nonnull
    private Optional<KEY> readKeyAttributeOptional(@Nonnull INSTANCE instance) {
        if (instance == null) {
            throw addInfo(new SingularFormPersistenceException("O parâmetro instance está null"));
        }
        Optional<FormKey> key = Optional.ofNullable(instance.getAttributeValue(SPackageFormPersistence.ATR_FORM_KEY));
        if (key.isPresent()) {
            return Optional.of(checkKeyOrException(key.get(), instance));
        }
        return Optional.empty();
    }

    /**
     * Verifica se a chave não e nula e é da classe esperada, fazendo o cast para o tipo certo. Caso contrario dispara
     * uma exception.
     */
    @Nonnull
    protected final KEY checkKeyOrException(FormKey key, INSTANCE instance) {
        return checkKeyOrException(key, instance, " o parâmetro key não fosse null");
    }

    /**
     * Verifica se a chave não e nula e é da classe esperada, fazendo o cast para o tipo certo. Caso contrario dispara
     * uma exception.
     */
    @Nonnull
    protected final KEY checkKeyOrException(FormKey key, INSTANCE instance, String msgRequired) {
        if (key == null) {
            throw addInfo(new SingularFormPersistenceException("Era esperado que " + msgRequired)).add("key", null)
                    .add(instance);
        } else if (!keyClass.isInstance(key)) {
            throw addInfo(new SingularFormPersistenceException(
                    "A chave encontrada incompatível: (key= " + key + ") é da classe " + key.getClass().getName() +
                            " mas era esperado que fosse da classe " + keyClass.getName())).add(instance);
        }
        return (KEY) key;
    }

    @Override
    public final boolean isPersistent(@Nonnull INSTANCE instance) {
        return readKeyAttributeOptional(instance).isPresent();
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


