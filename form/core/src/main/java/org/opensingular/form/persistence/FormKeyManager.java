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

package org.opensingular.form.persistence;

import org.opensingular.form.SInstance;
import org.opensingular.lib.commons.base.SingularException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Fornece as operações básicas de leitura e conversão para manipular um tipo específico de {@link FormKey}.
 *
 * @author Daniel C. Bordin on 05/04/2017.
 */
public class FormKeyManager<KEY extends FormKey> {

    private static final String CONVERTER_METHOD_NAME = "convertToKey";
    private final Class<KEY> keyClass;
    private final Constructor<KEY> keyConstructor;
    private final Method convertMethod;
    private final Consumer<SingularFormPersistenceException> exceptionDecorator;

    public FormKeyManager(@Nonnull Class<KEY> keyClass,
            @Nullable Consumer<SingularFormPersistenceException> exceptionDecorator) {
        this.keyClass = Objects.requireNonNull(keyClass);
        this.keyConstructor = findConstructorString(keyClass);
        this.convertMethod = findConvertMethod(keyClass);
        this.exceptionDecorator = exceptionDecorator;
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

    /**
     * Tenta converter a string de persistência da chave para o FormKey específica. Senão for possível, dispara
     * exception. <p>Esse metodo seria tipicamente usado para converter chave passadas por parâmetro (por exemplo na
     * URL) de volta a FormKey.</p>
     */
    @Nonnull
    public KEY keyFromString(@Nonnull String persistenceString) {
        if (persistenceString == null) {
            throw addInfo(new SingularFormPersistenceException("Erro criando FormKey. String recebida null"));
        }
        try {
            return keyConstructor.newInstance(persistenceString);
        } catch (Exception e) {
            throw addInfo(new SingularFormPersistenceException(
                    "Erro criando FormKey para o valor string da chave '" + persistenceString + "'", e));
        }
    }

    /**
     * Converte um tipo primitivo na FormKey específica, se possível. Se o valor não for possível de ser convertido
     * para o tipo específico, então dispara exception.
     */
    @Nonnull
    public KEY keyFromObject(@Nonnull Object objectValueToBeConverted) {
        if (objectValueToBeConverted == null) {
            throw addInfo(new SingularFormPersistenceException("O objeto a ser convertido não pode ser null")
                    .add("valor sendo convertido", objectValueToBeConverted));
        } else if (keyClass.isInstance(objectValueToBeConverted)) {
            return keyClass.cast(objectValueToBeConverted);
        }
        Object result;
        try {
            result = convertMethod.invoke(null, objectValueToBeConverted);
        } catch (Exception e) {
            throw addInfo(new SingularFormPersistenceException(
                    "Erro convertendo valor usando o método " + keyClass.getSimpleName() + "." + CONVERTER_METHOD_NAME +
                            "()", e).add("valor sendo convertido", objectValueToBeConverted));
        }
        if (result == null) {
            throw addInfo(new SingularFormPersistenceException(
                    "Método " + keyClass.getSimpleName() + "." + CONVERTER_METHOD_NAME +
                            "() retornou null para um valor não null")
                    .add("valor sendo convertido", objectValueToBeConverted));
        }
        return keyClass.cast(result);
    }

    /**
     * Verifica se a chave não e nula e é da classe esperada, fazendo o cast para o tipo certo. Caso contrario dispara
     * uma exception.
     */
    @Nonnull
    public final KEY validKeyOrException(@Nullable FormKey key) {
        return validKeyOrException(key, null, null);
    }

    /**
     * Verifica se a chave não e nula e é da classe esperada, fazendo o cast para o tipo certo. Caso contrario dispara
     * uma exception.
     */
    @Nonnull
    public final KEY validKeyOrException(@Nullable FormKey key, @Nullable SInstance keyOwner,
            @Nullable String complementMsgIfKeyIsNull) {
        if (key == null) {
            SingularException e = addInfo(new SingularNoFormKeyException(keyOwner)).add("key", null);
            if (complementMsgIfKeyIsNull != null) {
                e.add("complement", complementMsgIfKeyIsNull);
            }
            throw e;
        } else if (!keyClass.isInstance(key)) {
            throw addInfo(new SingularFormPersistenceException(
                    "A chave encontrada incompatível: (key= " + key + ") é da classe " + key.getClass().getName() +
                            " mas era esperado que fosse da classe " + keyClass.getName())).add(keyOwner);
        }
        return keyClass.cast(key);
    }

    /**
     * Lê obrigatoriamente o {@link FormKey} de uma instância e verifica se é da classe esperada. Se for diferente de
     * null e não for da classe espera, então dispara uma Exception. Se for null, dispara exception.
     */
    @Nonnull
    public KEY readFormKeyOrException(@Nonnull SInstance instance) {
        Optional<KEY> key = readFormKeyOptional(instance);
        if (!key.isPresent()) {
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
    public Optional<KEY> readFormKeyOptional(@Nonnull SInstance instance) {
        if (instance == null) {
            throw addInfo(new SingularFormPersistenceException("O parâmetro instance está null"));
        }
        Optional<FormKey> key = FormKey.fromOpt(instance);
        if (key.isPresent()) {
            return Optional.of(validKeyOrException(key.get(), instance, null));
        }
        return Optional.empty();
    }

    public final boolean isPersistent(@Nonnull SInstance instance) {
        return readFormKeyOptional(instance).isPresent();
    }

    /** Método chamado para adicionar informção do serviço de persistência à exception. */
    @Nonnull
    private SingularFormPersistenceException addInfo(@Nonnull SingularFormPersistenceException exception) {
        if (exceptionDecorator != null) {
            exceptionDecorator.accept(exception);
        }
        return exception;
    }
}
