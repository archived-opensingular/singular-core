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

import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ApplicationContextProvider implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private static synchronized void setup(ApplicationContext applicationContext) {
        ApplicationContextProvider.applicationContext = applicationContext;
    }

    /**
     * Retorna o contexto de aplicação atual ou dispara exception se ainda não estiver configurado.
     */
    public static ApplicationContext get() {
        if (applicationContext == null) {
            throw SingularException.rethrow(
                    "O applicationContext ainda não foi configurado em " + ApplicationContextProvider.class.getName());
        }
        return applicationContext;
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
                LoggerFactory.logger(ApplicationContextProvider.class).error(e.getMessage(), e);
            }
            return Optional.empty();
        };

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextProvider.setup(applicationContext);
    }

}