/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.spring;

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