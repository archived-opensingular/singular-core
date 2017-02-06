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

package org.opensingular.form.view;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Representa um valor (que pode ser null) que está associado a um peso. Facilita a seleção de valores de acordo com
 * o peso.
 * @author Daniel C. Bordin 2017-01-21
 */
final class PrioritizedResult<K> {
    private final K result;
    private final int priority;

    private PrioritizedResult(@Nullable K result, int priority) {
        this.result = result;
        this.priority = priority;
    }

    /** Criar uma seleção com conteúdo vazio e a mais baixa prioridade possível. */
    public static <T> PrioritizedResult<T> empty() {
        return new PrioritizedResult(null, -1);
    }

    /** Retorna o valor associado a essa seleção. Pode ser null. */
    @Nullable
    public K get() {
        return result;
    }

    /** Retorna o valor default informado se o resultado for nulo, senão retorna o resultado. */
    public @Nonnull
    K orElse(@Nonnull K defaultValueIfNull) {
        return result == null ? defaultValueIfNull : result;
    }

    /**
     * Deve o resultado com o maior peso entre o atual e o informado. Se for o atual, retorna a própria instância.
     * Se for o novo para valor-prioridade, então cria um novo resultado.
     */
    public @Nonnull
    PrioritizedResult<K> selectHigherPriority(int newPriority, @Nonnull Supplier<K> valueSupplier) {
        if (result == null || priority < newPriority) {
            K newValue = valueSupplier.get();
            if (newValue != null) {
                return new PrioritizedResult<>(newValue, newPriority);
            }
        }
        return this;
    }

    /**
     * Deve o resultado com o maior peso entre o atual e o informado. Se for o atual, retorna a própria instância.
     * Se for o novo para valor-prioridade, então cria um novo resultado.
     */
    public @Nonnull
    PrioritizedResult<K> selectHigherPriority(int newPriority, K newValue) {
        if (result == null || priority < newPriority) {
            if (newValue != null) {
                return new PrioritizedResult<>(newValue, newPriority);
            }
        }
        return this;
    }
}
