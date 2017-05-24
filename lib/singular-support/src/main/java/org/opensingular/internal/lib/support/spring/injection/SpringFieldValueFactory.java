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

import org.apache.commons.lang3.StringUtils;
import org.opensingular.internal.lib.commons.injection.FieldInjectionInfo;
import org.opensingular.internal.lib.commons.injection.SingularFieldValueFactory;
import org.opensingular.internal.lib.commons.injection.SingularInjectionException;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.support.spring.util.ApplicationContextProvider;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.ResolvableType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * {@link IFieldValueFactory} that uses {@link LazyInitProxyFactory} to create proxies for Spring
 * dependencies based on the {@link SpringBean} annotation applied to a field. This class is usually
 * used by the {@link SpringComponentInjector} to inject objects with lazy init proxies. However,
 * this class can be used on its own to create proxies for any field decorated with a
 * {@link SpringBean} annotation.
 * <p>
 * Example:
 * <p>
 * <pre>
 * IFieldValueFactory factory = new AnnotProxyFieldValueFactory(contextLocator);
 * field = obj.getClass().getDeclaredField(&quot;dependency&quot;);
 * IDependency dependency = (IDependency)factory.getFieldValue(field, obj);
 * </pre>
 * <p>
 * In the example above the
 * <p>
 * <code>dependency</code> object returned is a lazy init proxy that will look up the actual
 * IDependency bean from spring context upon first access to one of the methods.
 * <p>
 * This class will also cache any produced proxies so that the same proxy is always returned for the
 * same spring dependency. This helps cut down on session size beacause proxies for the same
 * dependency will not be serialized twice.
 *
 * @author Igor Vaynberg (ivaynberg)
 * @author Istvan Devai
 * @author Tobias Soloschenko
 * @author Daniel C. Bordin on 16/05/2017.
 * @see LazyInitProxyFactory
 * @see SpringBean
 * @see SpringBeanLocator
 * @see javax.inject.Inject
 */
class SpringFieldValueFactory implements SingularFieldValueFactory {

    /** Indica que o bean referenciado não existe no contexto do Spring. */
    private static final Object NO_BEAN = new Object();

    private final ISupplier<ApplicationContext> contextLocator;

    private final ConcurrentMap<SpringBeanLocator, Object> cache = new ConcurrentHashMap<>();

    private final ConcurrentMap<AbstractMap.SimpleEntry<Class<?>, Class<?>>, String> beanNameCache =
    new ConcurrentHashMap<>();

    private final boolean wrapInProxies;

    public SpringFieldValueFactory() {
        this(ApplicationContextProvider.supplier(), true);
    }

    public SpringFieldValueFactory(@Nonnull ISupplier<ApplicationContext> contextLocator) {
        this(contextLocator, true);
    }

    /**
     * @param contextLocator spring context locator
     * @param wrapInProxies  whether or not wicket should wrap dependencies with specialized proxies that can
     *                       be safely serialized. in most cases this should be set to true.
     */
    public SpringFieldValueFactory(@Nonnull ISupplier<ApplicationContext> contextLocator, boolean wrapInProxies) {
        this.contextLocator = Objects.requireNonNull(contextLocator);
        this.wrapInProxies = wrapInProxies;
    }

    @Override
    @Nonnull
    public FieldInjectionInfo createCachedInfo(@Nonnull Field field) {
        return new FieldInjectionInfoSpring(field);
    }

    @Override
    @Nullable
    public Object getFieldValue(@Nonnull FieldInjectionInfo fieldInfo, @Nonnull Object fieldOwner) {
        FieldInjectionInfoSpring springFieldInfo = (FieldInjectionInfoSpring) fieldInfo;

        Object cachedValue = springFieldInfo.getCachedValue();
        if (cachedValue != null) {
            return cachedValue == NO_BEAN ? null : cachedValue;
        }

        SpringBeanLocator locator = resolveLocator(springFieldInfo);

        // only check the cache if the bean is a singleton
        cachedValue = cache.get(locator);
        if (cachedValue != null) {
            return cachedValue == NO_BEAN ? null : cachedValue;
        }

        Object target = null;
        try {
            // check whether there is a bean with the provided properties
            target = locator.locateProxyTarget();
        } catch (IllegalStateException isx) {
        }
        if (target == null) {
            cache.put(locator, NO_BEAN); //Marca como não existe no Spring
            springFieldInfo.setCachedValue(NO_BEAN);
            return null;
        }

        if (wrapInProxies) {
            target = LazyInitProxyFactory.createProxy(fieldInfo.getType(), locator);
        }

        // only put the proxy into the cache if the bean is a singleton
        if (locator.isSingletonBean()) {
            cache.put(locator, target);
            springFieldInfo.setCachedValue(target);
        }
        return target;
    }

    @Nonnull
    private SpringBeanLocator resolveLocator(@Nonnull FieldInjectionInfoSpring fieldInfo) {
        SpringBeanLocator locator = fieldInfo.getLocator();
        if (locator == null) {
            String beanName = getBeanName(fieldInfo);

            locator = new SpringBeanLocator(beanName, fieldInfo.getType(), contextLocator);
            fieldInfo.setLocator(locator);
        }
        return locator;
    }

    /**
     * @return bean name
     */
    private String getBeanName(@Nonnull FieldInjectionInfo fieldInfo) {
        String name = fieldInfo.getBeanName();
        if (StringUtils.isBlank(name)) {
            Class<?> generic = ResolvableType.forType(fieldInfo.getType()).resolveGeneric(0);

            Class<?> fieldType = fieldInfo.getType();
            AbstractMap.SimpleEntry<Class<?>, Class<?>> keyPair = new AbstractMap.SimpleEntry<Class<?>, Class<?>>(
                    fieldType, generic);

            name = beanNameCache.get(keyPair);
            if (name == null) {
                name = getBeanNameOfClass(fieldInfo, fieldType, generic);
                if (name != null) {
                    beanNameCache.put(keyPair, name);
                }
            }
        }

        return name;
    }

    /**
     * Returns the name of the Bean as registered to Spring. Throws IllegalState exception if none
     * or more than one beans are found.
     *
     * @param ctx   spring application context
     * @param clazz bean class
     * @return spring name of the bean
     */
    private String getBeanNameOfClass(FieldInjectionInfo fieldInfo, Class<?> clazz, final Class<?> generic) {
        ApplicationContext ctx = contextLocator.get();
        // get the list of all possible matching beans
        List<String> names = new ArrayList<>(
                Arrays.asList(BeanFactoryUtils.beanNamesForTypeIncludingAncestors(ctx, clazz)));

        // filter out beans that are not candidates for autowiring
        if (ctx instanceof AbstractApplicationContext) {
            Iterator<String> it = names.iterator();
            while (it.hasNext()) {
                final String possibility = it.next();
                BeanDefinition beanDef = getBeanDefinition(((AbstractApplicationContext) ctx).getBeanFactory(),
                        possibility);
                if (BeanFactoryUtils.isFactoryDereference(possibility) || possibility.startsWith("scopedTarget.") ||
                        (beanDef != null && !beanDef.isAutowireCandidate())) {
                    it.remove();
                }
            }
        }

        if (names.size() > 1) {
            if (ctx instanceof AbstractApplicationContext) {
                List<String> primaries = new ArrayList<>();
                for (String name : names) {
                    BeanDefinition beanDef = getBeanDefinition(((AbstractApplicationContext) ctx).getBeanFactory(),
                            name);
                    if (beanDef instanceof AbstractBeanDefinition) {
                        if (beanDef.isPrimary()) {
                            primaries.add(name);
                        }
                    }
                }
                if (primaries.size() == 1) {
                    return primaries.get(0);
                }
            }

            //use field name to find a match
            int nameIndex = names.indexOf(fieldInfo.getFieldName());

            if (nameIndex > -1) {
                return names.get(nameIndex);
            }

            if (generic != null) {
                return null;
            }

            StringBuilder msg = new StringBuilder();
            msg.append("More than one bean of type [");
            msg.append(clazz.getName());
            msg.append("] found, you have to specify the name of the bean ");
            msg.append("(@SpringBean(name=\"foo\")) or (@Named(\"foo\") if using @javax.inject classes) in order to " +
                    "resolve this conflict. ");
            msg.append("Matched beans: ");
            msg.append(names.stream().collect(Collectors.joining(",")));
            throw new SingularInjectionException(fieldInfo, null, msg, null);
        } else if (!names.isEmpty()) {
            return names.get(0);
        }

        return null;
    }

    public BeanDefinition getBeanDefinition(final ConfigurableListableBeanFactory beanFactory, final String name) {
        if (beanFactory.containsBeanDefinition(name)) {
            return beanFactory.getBeanDefinition(name);
        } else {
            BeanFactory parent = beanFactory.getParentBeanFactory();
            if ((parent != null) && (parent instanceof ConfigurableListableBeanFactory)) {
                return getBeanDefinition((ConfigurableListableBeanFactory) parent, name);
            } else {
                return null;
            }
        }
    }

    private static class FieldInjectionInfoSpring extends FieldInjectionInfo {

        private SpringBeanLocator locator;
        private Object cachedValue;

        public FieldInjectionInfoSpring(Field field) {
            super(field);
        }

        public SpringBeanLocator getLocator() {
            return locator;
        }

        public void setLocator(SpringBeanLocator locator) {
            this.locator = locator;
        }

        public void setCachedValue(Object cachedValue) {
            this.cachedValue = cachedValue;
        }

        public Object getCachedValue() {
            return cachedValue;
        }
    }
}
