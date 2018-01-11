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
import org.opensingular.form.document.SDocument;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * Representa um identificador que é único para um formulário dentro de um mesmo ambiente de persitência. As instâncias
 * devem ser inmutáveis.
 *
 * @author Daniel C.Bordin
 */

public interface FormKey extends Serializable {

    /**
     * Gera uma representação string da chave. Deve ser evitado converter em String, mas é interessante quando, por
     * exemplo, estiver montando URLSs.
     */
    @Nonnull
    public String toStringPersistence();


    /**
     * Retorna a {@link FormKey} do {@link SDocument} da instância, se o identificado
     * existir, ou dispara exception senão exisitr.
     */
    @Nonnull
    public static FormKey from(@Nonnull SInstance instance) {
        return from(Objects.requireNonNull(instance).getDocument());
    }

    /**
     * Retorna a {@link FormKey} do {@link SDocument}, se o identificado existir, ou dispara exception senão exisitr.
     */
    @Nonnull
    public static FormKey from(@Nonnull SDocument document) {
        return fromOpt(document).orElseThrow(() -> new SingularNoFormKeyException(document.getRoot()));
    }

    /** Retorna a {@link FormKey} do {@link SDocument} da instância, se o identificado existir. */
    @Nonnull
    public static Optional<FormKey> fromOpt(@Nonnull SInstance instance) {
        return fromOpt(Objects.requireNonNull(instance).getDocument());
    }

    /** Retorna a {@link FormKey} do {@link SDocument}, se o identificado existir. */
    @Nonnull
    static Optional<FormKey> fromOpt(SDocument document) {
        Objects.requireNonNull(document);
        return Optional.ofNullable((FormKey) document.getRoot().getAttributeValue(SPackageFormPersistence.ATR_FORM_KEY));
    }

	/**
	 * Returns the persistence key assigned to a given SInstance. This key is
	 * assigned by the persistence mechanism after loading/saving an instance.
	 */
    @Nonnull
    public static FormKey fromInstance(@Nonnull SInstance instance) {
        Objects.requireNonNull(instance);
        return instance.getAttributeValue(SPackageFormPersistence.ATR_FORM_KEY);
    }

	/**
	 * Assigns a persistence key to the root of a given SInstance. Advised caution
	 * in calling this method as the persistence mechanism will be affected
	 * accordingly.
	 *
	 * This method is deprecated. Use instead one of the following: setOnRoot or
	 * setOnInstance.
	 */
    @Deprecated
    static void set(@Nonnull SInstance instance, @Nullable FormKey formKey) {
        setOnRoot(instance, formKey);
    }

	/**
	 * Assigns a persistence key to a given SInstance. Advised caution in calling
	 * this method as the persistence mechanism will be affected accordingly.
	 */
    static void setOnInstance(@Nonnull SInstance instance, @Nullable FormKey formKey) {
        Objects.requireNonNull(instance);
        instance.setAttributeValue(SPackageFormPersistence.ATR_FORM_KEY, formKey);
    }

	/**
	 * Assigns a persistence key to the root of a given SInstance. Advised caution
	 * in calling this method as the persistence mechanism will be affected
	 * accordingly.
	 */
    static void setOnRoot(@Nonnull SInstance instance, @Nullable FormKey formKey) {
        Objects.requireNonNull(instance);
        setOnInstance(instance.getRoot(), formKey);
    }

    static boolean containsKey(@Nonnull SInstance instance) {
        Objects.requireNonNull(instance);
        return instance.getAttributeValue(SPackageFormPersistence.ATR_FORM_KEY) != null;
    }

}
