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

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.function.Function;
import java.util.function.Supplier;

public class ApplicationContextProvider implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private static synchronized void setup(ApplicationContext applicationContext){
        ApplicationContextProvider.applicationContext = applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextProvider.setup(applicationContext);
    }

    public static ApplicationContext get() {
        return applicationContext;
    }

    public static <T> Supplier<T> supplierOf(Function<ApplicationContext, T> factory) {
        return () -> factory.apply(ApplicationContextProvider.get());
    }

}