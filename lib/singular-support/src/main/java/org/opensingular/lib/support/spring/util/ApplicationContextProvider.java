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

package org.opensingular.lib.support.spring.util;

import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.context.SingularContext;
import org.opensingular.lib.commons.context.SingularSingletonStrategy;
import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

import javax.annotation.Nonnull;
import javax.inject.Named;
import java.util.Optional;

/**
 * Métodos para localização e retorno do {@link ApplicationContext} atual.
 */
@Named
@Order(0)
@Lazy(false)
public class ApplicationContextProvider implements ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContextProvider.class);

    private static final ISupplier<ApplicationContext> SUPPLIER = () -> get();

    public static ApplicationContext getApplicationContext() {
        return ((SingularSingletonStrategy) SingularContext.get()).get(ApplicationContext.class);
    }

    @Override
    public synchronized void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ((SingularSingletonStrategy) SingularContext.get()).singletonize(ApplicationContext.class, () -> applicationContext);
    }

    /**
     * Retorna o contexto de aplicação atual ou dispara exception se ainda não estiver configurado.
     */
    public static ApplicationContext get() {
        if (!isConfigured()) {
            throw SingularException.rethrow(
                    "O getApplicationContext() ainda não foi configurado em " + ApplicationContextProvider.class.getName());
        }
        return getApplicationContext();
    }

    /**
     * Indica se o contexto do sping já foi configurado e se pode ser chamado {@link #get()}.
     */
    public static boolean isConfigured() {
        return ((SingularSingletonStrategy) SingularContext.get()).exists(ApplicationContext.class);
    }

    /**
     * Retorna um supplier do aplication context que faz chamar {@link #get()}.
     */
    public static ISupplier<ApplicationContext> supplier() {
        return SUPPLIER;
    }

    /**
     * Retorna um supplier que recuperará dinamicamente o bean mediante chamada da função informada passando o contexto
     * atual da aplicação para a mesma.
     */
    public static <T> ISupplier<T> supplierOf(IFunction<ApplicationContext, T> factory) {
        return () -> factory.apply(ApplicationContextProvider.get());
    }

    /**
     * Retorna um supplier que recuperará dinamicamente o bean a partir do contexto de aplicação atual.
     */
    public static <T> ISupplier<T> supplierOf(Class<T> beanClass) {
        return () -> ApplicationContextProvider.get().getBean(beanClass);
    }

    /**
     * Retorna um supplier que recuperará dinamicamente o bean optional a partir do contexto de aplicação atual.
     */
    public static <T> ISupplier<Optional<T>> supplierOfOptional(Class<T> beanClass) {
        return () -> {
            try {
                Optional.of(ApplicationContextProvider.get().getBean(beanClass));
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
            return Optional.empty();
        };

    }

    @Nonnull
    public static <T> Optional<T> getBeanOpt(@Nonnull String name, @Nonnull Class<T> targetClass) {
        try {
            return Optional.ofNullable(get().getBean(name, targetClass));
        } catch (NoSuchBeanDefinitionException ex) {
            LOGGER.debug(null, ex);
            return Optional.empty();
        }
    }

    @Nonnull
    public static <T> Optional<T> getBeanOpt(@Nonnull Class<T> targetClass) {
        try {
            return Optional.ofNullable(get().getBean(targetClass));
        } catch (NoSuchBeanDefinitionException ex) {
            LOGGER.debug(null, ex);
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T> Optional<T> getBeanOpt(@Nonnull String name) {
        try {
            return Optional.ofNullable((T) get().getBean(name));
        } catch (NoSuchBeanDefinitionException ex) {
            LOGGER.debug(null, ex);
            return Optional.empty();
        }
    }

    @EventListener
    public void handleContextRefresh(ContextStartedEvent event) {
        setApplicationContext(event.getApplicationContext());
    }

}