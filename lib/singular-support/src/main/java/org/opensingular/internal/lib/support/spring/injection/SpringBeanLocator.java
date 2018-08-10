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

package org.opensingular.internal.lib.support.spring.injection;

import org.opensingular.internal.lib.commons.injection.SingularInjectionException;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.ResolvableType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Implementation of {@link IProxyTargetLocator} that can locate beans within a spring application
 * context. Beans are looked up by the combination of name and type, if name is omitted only type is
 * used.
 *
 * @author Igor Vaynberg (ivaynberg)
 * @author Istvan Devai
 * @author Tobias Soloschenko
 * @author Daniel C. Bordin on 16/05/2017.
 */
class SpringBeanLocator implements IProxyTargetLocator {
    private static final long serialVersionUID = 1L;

    // Weak reference so we don't hold up WebApp classloader garbage collection.
    private transient WeakReference<Class<?>> beanTypeCache;

    private final String beanTypeName;

    private String beanName;

    private ISupplier<ApplicationContext> springContextLocator;

    private Boolean singletonCache = null;

    /**
     * Resolvable type for field to inject
     */
    private ResolvableType fieldResolvableType;

    /**
     * If the field to inject is a list this is the resolvable type of its elements
     */
    private ResolvableType fieldElementsResolvableType;

    /**
     * Constructor
     *
     * @param beanName bean name
     * @param beanType bean class
     * @param locator  spring context locator
     */
    public SpringBeanLocator(@Nullable String beanName, @Nonnull Class<?> beanType,
            @Nonnull ISupplier<ApplicationContext> locator) {
        Objects.requireNonNull(locator, "locator");
        Objects.requireNonNull(beanType, "beanType");

        this.beanName = beanName;
        beanTypeCache = new WeakReference<Class<?>>(beanType);
        beanTypeName = beanType.getName();
        springContextLocator = locator;

        fieldResolvableType = ResolvableType.forType(beanType);
        fieldElementsResolvableType = extractElementGeneric(fieldResolvableType);
    }

    /**
     * If the field type is a collection (Map, Set or List) extracts type
     * information about its elements.
     *
     * @param fieldResolvableType the resolvable type of the field
     * @return the resolvable type of elements of the field, if any.
     */
    private static ResolvableType extractElementGeneric(ResolvableType fieldResolvableType) {
        Class<?> clazz = fieldResolvableType.resolve();

        if (Set.class.isAssignableFrom(clazz) || List.class.isAssignableFrom(clazz)) {
            return fieldResolvableType.getGeneric();
        } else if (Map.class.isAssignableFrom(clazz)) {
            return fieldResolvableType.getGeneric(1);
        }

        return null;
    }

    /**
     * @return returns whether the bean (the locator is supposed to istantiate) is a singleton or
     * not
     */
    public boolean isSingletonBean() {
        if (singletonCache == null) {
            singletonCache = getBeanName() != null && getSpringContext().isSingleton(getBeanName());
        }
        return singletonCache;
    }

    /**
     * @return bean class this locator is configured with
     */
    public Class<?> getBeanType() {
        Class<?> clazz = beanTypeCache == null ? null : beanTypeCache.get();
        if (clazz == null) {
            try {
                clazz = Class.forName(beanTypeName, false, Thread.currentThread().getContextClassLoader());
            } catch (Exception e) {
                throw new SingularInjectionException(
                        "SpringBeanLocator could not find class [" + beanTypeName + "] needed to locate the [" +
                                ((beanName != null) ? (beanName) : ("bean name not specified")) + "] bean", e);
            }
            beanTypeCache = new WeakReference<Class<?>>(clazz);
        }
        return clazz;
    }

    @Override
    public Object locateProxyTarget() {
        return lookupSpringBean(getSpringContext(), beanName, getBeanType());
    }

    @Nonnull
    private ApplicationContext getSpringContext() {
        ApplicationContext context = springContextLocator.get();
        if (context == null) {
            throw new SingularInjectionException("spring application context locator returned null");
        }
        return context;
    }

    /**
     * @return bean name this locator is configured with
     */
    public final String getBeanName() {
        return beanName;
    }

    /**
     * Looks up a bean by its name and class. Throws IllegalState exception if bean not found.
     *
     * @param ctx   spring application context
     * @param name  bean name
     * @param clazz bean class
     * @return found bean
     */
    private Object lookupSpringBean(ApplicationContext ctx, String name, Class<?> clazz) {
        try {
            // If the name is set the lookup is clear
            if (name != null) {
                return ctx.getBean(name, clazz);
            }

            // If the given class is a list try to get the generic of the list
            Class<?> lookupClass = fieldElementsResolvableType != null ? fieldElementsResolvableType.resolve() : clazz;

            // Else the lookup is done via Generic
            List<String> names = loadBeanNames(ctx, lookupClass);

            return getBeansByName(ctx, names);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    /**
     * Returns a list of candidate names for the given class.
     *
     * @param ctx         spring application context
     * @param lookupClass the class to lookup
     * @return a list of candidate names
     */
    private List<String> loadBeanNames(ApplicationContext ctx, Class<?> lookupClass) {
        List<String> beanNames = new ArrayList<>();
        Class<?> fieldType = getBeanType();
        String[] beanNamesArr = ctx.getBeanNamesForType(fieldType);

        //add names for field class
        beanNames.addAll(Arrays.asList(beanNamesArr));

        //add names for lookup class
        if (lookupClass != fieldType) {
            beanNamesArr = ctx.getBeanNamesForType(lookupClass);
            beanNames.addAll(Arrays.asList(beanNamesArr));
        }

        //filter those beans who don't have a definition (used internally by Spring)
        beanNames.removeIf(s -> !ctx.containsBeanDefinition(s));

        return beanNames;
    }

    /**
     * Retrieves a list of beans or a single bean for the given list of names and assignable to the
     * current field to inject.
     *
     * @param ctx   spring application context.
     * @param names the list of candidate names
     * @return a list of matching beans or a single one.
     */
    private Object getBeansByName(ApplicationContext ctx, List<String> names) {
        FieldBeansCollector beansCollector = new FieldBeansCollector(fieldResolvableType);

        for (String beanName : names) {
            RootBeanDefinition beanDef = getBeanDefinition(ctx, beanName);

            if (beanDef == null) {
                continue;
            }

            ResolvableType candidateResolvableType = null;

            //check if we have the class of the bean or the factory method.
            //Usually if use XML as config file we have the class while we
            //have the factory method if we use Java-based configuration.
            if (beanDef.hasBeanClass()) {
                candidateResolvableType = ResolvableType.forClass(beanDef.getBeanClass());
            } else if (beanDef.getResolvedFactoryMethod() != null) {
                candidateResolvableType = ResolvableType.forMethodReturnType(beanDef.getResolvedFactoryMethod());
            }

            if (candidateResolvableType == null) {
                continue;
            }

            boolean exactMatch = fieldResolvableType.isAssignableFrom(candidateResolvableType);
            boolean elementMatch = fieldElementsResolvableType != null && fieldElementsResolvableType.isAssignableFrom(
                    candidateResolvableType);

            if (exactMatch) {
                this.beanName = beanName;
                return ctx.getBean(beanName);
            }

            if (elementMatch) {
                beansCollector.addBean(beanName, ctx.getBean(beanName));
            }

        }

        return beansCollector.getBeansToInject();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof SpringBeanLocator) {
            SpringBeanLocator other = (SpringBeanLocator) obj;
            return beanTypeName.equals(other.beanTypeName) && Objects.equals(beanName, other.beanName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hashcode = beanTypeName.hashCode();
        if (getBeanName() != null) {
            hashcode = hashcode + (127 * beanName.hashCode());
        }
        return hashcode;
    }

    /**
     * Gets the root bean definition for the given name.
     *
     * @param ctx  spring application context.
     * @param name bean name
     * @return bean definition for the current name, null if such a definition is not found.
     */
    public RootBeanDefinition getBeanDefinition(final ApplicationContext ctx, final String name) {
        ConfigurableListableBeanFactory beanFactory = ((AbstractApplicationContext) ctx).getBeanFactory();

        BeanDefinition beanDef = beanFactory.containsBean(name) ? beanFactory.getMergedBeanDefinition(name) : null;

        if (beanDef instanceof RootBeanDefinition) {
            return (RootBeanDefinition) beanDef;
        }

        return null;
    }
}
