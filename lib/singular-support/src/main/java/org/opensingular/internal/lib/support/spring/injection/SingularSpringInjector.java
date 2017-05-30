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

package org.opensingular.internal.lib.support.spring.injection;

import org.opensingular.internal.lib.commons.injection.SingularInjectionException;
import org.opensingular.internal.lib.commons.injection.SingularInjector;
import org.opensingular.internal.lib.commons.injection.SingularInjectorImpl;
import org.opensingular.internal.lib.commons.injection.SingularInjectorProxy;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.support.spring.util.ApplicationContextProvider;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Classe utilitária para obter um {@link SingularInjector}.
 *
 * @author Daniel C. Bordin on 18/05/2017.
 */
public class SingularSpringInjector {

    private SingularSpringInjector() {}

    /**
     * Obtêm o {@link SingularInjector} a partir do contexto default do Spring configurado me {@link
     * ApplicationContextProvider}.
     */
    @Nonnull
    public static SingularInjector get() {
        if (ApplicationContextProvider.isConfigured()) {
            return get(ApplicationContextProvider.supplier());
        } else {
            //Retorna um proxy que não reclama da ausência do Spring até encontrar um objeto que precisa efetivamente
            // de um injeção de um bean
            return new SingularSpringInjectorProxy();
        }
    }

    /**
     * Procura o {@link SingularInjector} no contexto de aplicação informado. Não existindo, cria um novo e salva no
     * contexto do Spring para uso em chamadas subsequentes desse método. Dispara {@link SingularInjectionException}
     * senão conseguir localizar.
     */
    @Nonnull
    public static SingularInjector get(@Nonnull ISupplier<ApplicationContext> ctxSupplier) {
        ApplicationContext ctx = ctxSupplier.get();
        SingularInjector injector;
        try {
            injector = ctx.getBean(SingularInjector.class);
        } catch (NoSuchBeanDefinitionException e) {
            injector = null;
        }
        if (injector == null) {
            injector = new SingularInjectorImpl(new SpringFieldValueFactory(ctxSupplier));
            if (ctx instanceof ConfigurableApplicationContext) {
                ConfigurableApplicationContext ctxConfigurable = (ConfigurableApplicationContext) ctx;
                ctxConfigurable.getBeanFactory().registerSingleton(SingularInjector.class.getName(), injector);
            } else {
                throw new SingularInjectionException(
                        "O applicationContext passado [" + ctx.getClass().getName() + "] não implementa " +
                                ConfigurableApplicationContext.class.getName());
            }
        }
        return injector;
    }

    /**
     * É um SingularInjector que funciona sem o contexto do Spring configurado até o momento que encontrar um @Inject.
     * Nesse último caso, se nesse momento estiver disponível o contexto Spring, utilizá-o para injeção, caso contrário
     * dispara uma exception.
     */
    static final class SingularSpringInjectorProxy extends SingularInjectorProxy {

        @Nullable
        @Override
        protected SingularInjector findInjectorIfAvailable() {
            if (ApplicationContextProvider.isConfigured()) {
                return get(ApplicationContextProvider.supplier());
            }
            return null;
        }
    }
}
