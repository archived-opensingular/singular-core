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

package org.opensingular.lib.support.spring.test;

import org.opensingular.lib.commons.context.spring.SpringServiceRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.io.Resource;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ApplicationContextMock extends AnnotationConfigWebApplicationContext {

    private static final long serialVersionUID = 1L;

    private final Map<String, Object> beans = new HashMap<>();
    private Date date = new Date();

    public ApplicationContextMock() {
        super();
        configureSpringServiceRegistry();
        refresh();
    }

    public void configureSpringServiceRegistry(){
        SpringServiceRegistry springServiceRegistry = new SpringServiceRegistry();
        springServiceRegistry.init();
        putOrReplaceBean(springServiceRegistry);
    }

    /**
     * puts bean with the given name into the context
     *
     * @param name
     * @param bean
     */
    public void putOrReplaceBean(final String name, final Object bean) {
        beans.put(name, bean);
    }

    /**
     * puts bean with into the context. bean object's class name will be used as the bean name.
     *
     * @param bean
     */
    public void putOrReplaceBean(final Object bean) {
        putOrReplaceBean(bean.getClass().getName(), bean);
    }

    @Override
    public Object getBean(final String name) throws BeansException {
        Object bean = beans.get(name);
        if (bean == null) {
            throw new NoSuchBeanDefinitionException(name);
        }
        return bean;
    }

    @Override
    public Object getBean(final String name, final Object... args) throws BeansException {
        return getBean(name);
    }

    /**
     * @see org.springframework.beans.factory.BeanFactory#getBean(java.lang.String, java.lang.Class)
     */
    @Override
    @SuppressWarnings({"unchecked"})
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        Object bean = getBean(name);
        if (!(requiredType.isAssignableFrom(bean.getClass()))) {
            throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
        }
        return (T) bean;
    }

    /**
     * @see org.springframework.beans.factory.ListableBeanFactory#getBeansOfType(java.lang.Class)
     */
    @Override
    @SuppressWarnings({"unchecked"})
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        final Map<String, T> found = new HashMap<>();

        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            if (type.isAssignableFrom(entry.getValue().getClass())) {
                found.put(entry.getKey(), (T) entry.getValue());
            }
        }

        return found;
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        Iterator<T> beans = getBeansOfType(requiredType).values().iterator();

        if (beans.hasNext() == false) {
            throw new NoSuchBeanDefinitionException("bean of required type " + requiredType +
                    " not found");
        }
        final T bean = beans.next();

        if (beans.hasNext() != false) {
            throw new NoSuchBeanDefinitionException("more than one bean of required type " +
                    requiredType + " found");
        }
        return bean;
    }

    @Override
    public <T> T getBean(Class<T> requiredType, Object... objects) throws BeansException {
        return getBean(requiredType);
    }

    @Override
    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType)
            throws BeansException {
        final Map<String, Object> found = new HashMap<>();

        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            if (entry.getValue().getClass().isAnnotationPresent(annotationType)) {
                found.put(entry.getKey(), entry.getValue());
            }
        }
        return found;
    }

    @Override
    public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) {
        return findAnnotationOnClass(getBean(beanName).getClass(), annotationType);
    }

    private <A extends Annotation> A findAnnotationOnClass(Class<?> cls, Class<A> annotationType) {
        // lookup annotation type on class
        A annotation = cls.getAnnotation(annotationType);

        // lookup annotation type on superclass
        if (annotation == null && cls.getSuperclass() != null) {
            annotation = findAnnotationOnClass(cls.getSuperclass(), annotationType);
        }

        // lookup annotation type on interfaces
        if (annotation == null) {
            for (Class<?> intfClass : cls.getInterfaces()) {
                annotation = findAnnotationOnClass(intfClass, annotationType);

                if (annotation != null) {
                    break;
                }
            }
        }

        return annotation;
    }

    @Override
    public ApplicationContext getParent() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return getClass().getName();
    }

    @Override
    public long getStartupDate() {
        return date.getTime();
    }

    @Override
    public void publishEvent(final ApplicationEvent event) {

    }

    @Override
    public boolean containsBeanDefinition(final String beanName) {
        return containsBean(beanName);
    }

    @Override
    public int getBeanDefinitionCount() {
        return beans.size();
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return beans.keySet().toArray(new String[beans.size()]);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public String[] getBeanNamesForType(final Class type) {
        ArrayList<String> names = new ArrayList<>();
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Object bean = entry.getValue();

            if (type.isAssignableFrom(bean.getClass())) {
                names.add(entry.getKey());
            }
        }
        return names.toArray(new String[names.size()]);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public String[] getBeanNamesForType(Class type, boolean includeNonSingletons,
                                        boolean allowEagerInit) {
        return new String[]{type.getName()};
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type, boolean includeNonSingletons,
                                             boolean allowEagerInit) throws BeansException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getBeanNamesForAnnotation(Class<? extends Annotation> aClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsBean(final String name) {
        return beans.containsKey(name);
    }

    @Override
    public boolean isSingleton(final String name) throws NoSuchBeanDefinitionException {
        return true;
    }

    @Override
    public Class<?> getType(final String name) throws NoSuchBeanDefinitionException {
        Object bean = beans.get(name);
        if (bean == null) {
            throw new NoSuchBeanDefinitionException("No bean with name '" + name + "'");
        }
        return bean.getClass();
    }

    @Override
    public String[] getAliases(final String name) throws NoSuchBeanDefinitionException {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.springframework.beans.factory.HierarchicalBeanFactory# getParentBeanFactory()
     */
    @Override
    public BeanFactory getParentBeanFactory() {
        return null;
    }

    @Override
    public String getMessage(final String code, final Object[] args, final String defaultMessage,
                             final Locale locale) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getMessage(final String code, final Object[] args, final Locale locale)
            throws NoSuchMessageException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getMessage(final MessageSourceResolvable resolvable, final Locale locale)
            throws NoSuchMessageException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Resource[] getResources(final String locationPattern) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Resource getResource(final String location) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsLocalBean(final String arg0) {
        return containsBean(arg0);
    }

    @Override
    public ClassLoader getClassLoader() {
       return Thread.currentThread().getContextClassLoader();
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getApplicationName() {
        return "";
    }

    @Override
    public boolean isPrototype(final String name) throws NoSuchBeanDefinitionException {
        return !isSingleton(name);
    }


    @Override
    @SuppressWarnings({"unchecked"})
    public boolean isTypeMatch(final String name, final Class targetType)
            throws NoSuchBeanDefinitionException {
        throw new UnsupportedOperationException();
    }

    public Set<String> listAllBeans() {
        return beans.keySet();
    }
}
