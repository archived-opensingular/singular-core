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

package org.opensingular.lib.commons.internal.function;

import java.lang.ref.SoftReference;
import java.util.function.Supplier;

import static org.apache.commons.lang3.ObjectUtils.NULL;

/**
 * CLASSE APENAS PARA USO INTERNO DO SINGULAR, método utilitários para a classe
 * {@link java.util.function.Supplier}.
 *
 * @author Daniel C. Bordin
 */
public final class SupplierUtil {

    private SupplierUtil() {
    }

    /**
     * Retorna uma supplier baseado em {@link SoftReference} que faz cache do
     * valor criado pelo supplier informado, mas que permite a liberação da
     * memória se necessário.
     */
    public static <T> Supplier<T> cached(Supplier<T> delegate) {
        return new SoftReferenceCacheSupplier<>(delegate);
    }

    private static final class SoftReferenceCacheSupplier<T> implements Supplier<T> {

        private final Supplier<T> delegate;

        private SoftReference<T> reference;

        public SoftReferenceCacheSupplier(Supplier<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public synchronized T get() {
            T value = null;
            if (reference != null) {
                value = reference.get();
            }
            if (value == null) {
                if (delegate != null) {
                    value = delegate.get();
                }
                if (value == null) {
                    value = (T) NULL;
                }
                reference = new SoftReference<>(value);
            }
            if (NULL.equals(value)) {
                return null;
            }
            return value;
        }
    }
}
