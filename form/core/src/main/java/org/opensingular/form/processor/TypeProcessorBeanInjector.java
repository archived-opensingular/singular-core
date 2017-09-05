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

package org.opensingular.form.processor;

import org.opensingular.form.SType;
import org.opensingular.form.context.ServiceRegistry;
import org.opensingular.form.context.ServiceRegistryLocator;
import org.opensingular.internal.lib.commons.injection.SingularInjector;

import javax.annotation.Nonnull;

/**
 * Faz a injeção de bean nos campos marcados como @{@link javax.inject.Inject} conforme a lógica de {@link
 * SingularInjector}, se o mesmo
 * estiver configurado para o tipo em questão.
 *
 * @author Daniel C. Bordin on 21/05/2017.
 */
public class TypeProcessorBeanInjector {

    /**
     * Instância única do processador.
     */
    public final static TypeProcessorBeanInjector INSTANCE = new TypeProcessorBeanInjector();

    /**
     * Método chamado logo após o registro do tipo. Nesse caso verificará se precisa injetar algum bean.
     */
    public <T extends SType<?>> void onRegisterTypeByClass(@Nonnull T type, @Nonnull Class<T> typeClass) {
        //TODO (by Daniel) Potential point of optimization by avoiding looking for the Injector for classes that
        // don't have a injection, which are vast majority of cases.
        findInjectorFor(type).inject(type);
    }

    /**
     * Localiza o injetor para o tipo informado.
     */
    @Nonnull
    private SingularInjector findInjectorFor(@Nonnull SType<?> type) {
        ServiceRegistry registry = ServiceRegistryLocator.locate();
        if (registry != null) {
            return registry.lookupSingularInjector();
        }
        return SingularInjector.getEmptyInjector();
    }
}
