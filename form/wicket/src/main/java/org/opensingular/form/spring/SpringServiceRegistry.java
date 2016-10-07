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

package org.opensingular.form.spring;

import org.opensingular.form.document.ServiceRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Collections;
import java.util.Map;

/**
 * This class provides a {@link ServiceRegistry} that relays service lookup
 * to the spring context.
 *
 * @author Fabricio Buzeto
 */
public class SpringServiceRegistry implements ServiceRegistry,
        ApplicationContextAware {

    private ApplicationContext applicationContext;

    public SpringServiceRegistry() {
    }

    public SpringServiceRegistry(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Map<String, Pair> services() {
        return Collections.emptyMap();
    }

    @Override
    public <T> T lookupService(String name, Class<T> targetClass) {
        try {
            return applicationContext.getBean(name, targetClass);
        } catch (NoSuchBeanDefinitionException ex) {
            return null;
        }
    }

    @Override
    public <T> T lookupService(Class<T> targetClass) {
        try {
            return applicationContext.getBean(targetClass);
        } catch (NoSuchBeanDefinitionException ex) {
            return null;
        }
    }

    @Override
    public Object lookupService(String name) {
        try {
            return applicationContext.getBean(name);
        } catch (NoSuchBeanDefinitionException ex) {
            return null;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        SpringFormUtil.setApplicationContext(applicationContext);
    }

}