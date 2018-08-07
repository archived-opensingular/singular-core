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

package org.opensingular.internal.lib.commons.injection;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * Injeta em um objeto os beans e valores no campos que forem marcados com @{@link javax.inject.Inject}. Também leva em
 * consideração @{@link javax.inject.Named}.
 *
 * @author Daniel C. Bordin on 16/05/2017.
 */
public interface SingularInjector {

    /**
     * Injects the specified object.
     */
    public void inject(@Nonnull Object object);

    /** Injects in all objects of the list. */
    default void injectAll(@Nonnull Collection<?> objects) {
        objects.forEach(o -> inject(o));
    }

    /**
     * Retorna um injetor que não possui nenhum bean disponível e que dispara uma exception se encontrar um @Inject que
     * seja de injeção obrigatória.
     */
    @Nonnull
    public static SingularInjector getEmptyInjector() {
        return SingularInjectorProxy.getEmptyInjectorImpl();
    }
}
