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

package org.opensingular.lib.commons.base;

import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang3.StringUtils;
import org.opensingular.lib.commons.context.SingularContext;
import org.opensingular.lib.commons.context.SingularSingletonStrategy;
import org.opensingular.lib.commons.lambda.IConsumerEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class SingularPropertiesImpl implements SingularProperties {
    private static final Logger   LOGGER                      = LoggerFactory.getLogger(SingularPropertiesImpl.class);
    private static final String   DEFAULT_PROPERTIES_FILENAME = "singular-defaults.properties";
    private static final String[] PROPERTIES_FILES_NAME       = {"singular.properties"};
    private volatile PropertyMap propertyMap;

    public static SingularPropertiesImpl get() {
        return (SingularPropertiesImpl) ((SingularSingletonStrategy) SingularContext.get()).singletonize(
                SingularProperties.class, SingularPropertiesImpl::new);
    }

    private static File findConfDir() {
        String path = System.getProperty(SYSTEM_PROPERTY_SINGULAR_SERVER_HOME);
        if (path != null) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("   Encontrado a propriedade singular.server.home={}", path);
            }
            File confDir = new File(path, "conf");
            if (confDir.exists()) {
                if (!confDir.isDirectory() && LOGGER.isWarnEnabled()) {
                    LOGGER.warn("   Era esperado que \"[singular.server.home]\\conf\" fosse um diretório");
                }
                return confDir;
            } else if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("      Não exite o diretório {}", confDir);
            }
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println("true".equals(Optional.ofNullable("true").map(String::toLowerCase).orElse(null)));
    }

    /**
     * Limpa as propriedades da memoria e força recarga a partir da memória e classPath.
     */
    public synchronized void reload() {
        LOGGER.info("Carregando configurações do Singular");
        PropertyMap newProperties = readClasspathDefaults();
        newProperties = readPropertiesFilesOverrides(newProperties);
        propertyMap = newProperties.consolidateAndFrozen();
    }

    public void setSingularServerHome(String serverHome) {
        if (serverHome != null)
            System.setProperty(SYSTEM_PROPERTY_SINGULAR_SERVER_HOME, serverHome);
        else
            System.clearProperty(SYSTEM_PROPERTY_SINGULAR_SERVER_HOME);
    }

    /**
     * Looks for the property with the giving key.
     * <p>Never return empty String (in this case they became null) and also trims the resulting String.</p>
     *
     * @throws SingularPropertyException If the search results in a null value.
     */
    @Override
    @Nonnull
    public String getProperty(@Nonnull String key) {
        String value = getInternal(key);
        if (value == null) {
            throw new SingularPropertyException("The property '" + key + "' is not set or it's value is null.");
        }
        return value;
    }

    @Nullable
    private String getInternal(@Nonnull String key) {
        //se contém a chave ainda que esta seja com valor nulo
        PropertyEntry entry = getProperties().getEntry(key);
        if (entry != null) {
            return entry.getValue();
        }
        return StringUtils.trimToNull(System.getProperties().getProperty(key));
    }

    @Override
    @Nonnull
    public Optional<String> getPropertyOpt(@Nonnull String key) {
        return Optional.ofNullable(getInternal(key));
    }


    @Override
    public boolean isTrue(String key) {
        return "true".equalsIgnoreCase(getInternal(key));
    }

    @Override
    public boolean isFalse(String key) {
        return "false".equalsIgnoreCase(getInternal(key));
    }

    @VisibleForTesting
    public void setProperty(@Nonnull String key, @Nonnull String value) {
        if (getProperties().isFrozen()) {
            propertyMap = new PropertyMap(getProperties());
        }
        propertyMap.add(key, value);
    }

    private synchronized PropertyMap getProperties() {
        //Faz leitura lazy das propriedades, pois no construtor da enum, as variáveis estáticas não estão disponíveis
        if (propertyMap == null) {
            reload();
        }
        return propertyMap;
    }

    private PropertyMap readPropertiesFilesOverrides(PropertyMap newProperties) {
        File confDir = findConfDir();
        if (confDir == null) {
            return newProperties;
        }
        PropertyMap props = new PropertyMap(newProperties);
        for (String name : PROPERTIES_FILES_NAME) {
            props.readProperties(new File(confDir, name));
        }
        return props;
    }

    private PropertyMap readClasspathDefaults() {
        PropertyMap newProperties = new PropertyMap();
        newProperties.readAllPropertiesFileFromClassPath(DEFAULT_PROPERTIES_FILENAME);
        if (newProperties.getSize() != 0) {
            newProperties = new PropertyMap(newProperties);
        }
        for (String name : PROPERTIES_FILES_NAME) {
            newProperties.readAllPropertiesFileFromClassPath(name);
        }
        return newProperties;
    }

    /**
     * Copia as propriedades do arquivo para as properties internas. As propriedades previamente existentes serão
     * sobrepostas. Esse método é utilizado para testes unitários com difererentes contextos.
     */
    @VisibleForTesting
    public synchronized void reloadAndOverrideWith(URL propertiesURL) {
        reload();
        PropertyMap p = new PropertyMap(getProperties());
        p.readProperties(propertiesURL);
        propertyMap = p;
    }

    /**
     * Prints the content of map of properties to the system output identifying the source of each property.
     */
    @Override
    public void debugContent() {
        getProperties().debugContent();
    }

    /**
     * Prints the content of map of properties to the specific output identifying the source of each property.
     */
    @Override
    public void debugContent(@Nonnull Appendable out) { getProperties().debugContent(out); }

    /**
     * Helper class to execute tests with different configuration of properties without leaving the runtime context of
     * properties dirty.
     *
     * @see #runInSandbox(IConsumerEx)
     */
    @VisibleForTesting
    public static final class Tester {

        private Tester() {}

        /**
         * Runs the code ensuring that the properties context (in {@link SingularProperties#get()}) will be restore to
         * the previous state before the execution, also provides access to current properties to be changed within the
         * code.
         * <p>In shot, saves the properties, run the code (that may change the properties) and restore the properties
         * context to the previous state.</p>
         */
        public static <EX extends Exception> void runInSandbox(
                @Nonnull IConsumerEx<SingularPropertiesImpl, EX> callable) throws EX {
            Object state = saveState();
            try {
                callable.accept(SingularPropertiesImpl.get());
            } finally {
                restoreState(state);
            }
        }

        protected static void restoreState(Object stateObject) {
            State  state      = (State) stateObject;
            SingularPropertiesImpl impl = SingularPropertiesImpl.get();
            impl.setSingularServerHome(state.systemBackup.get(SYSTEM_PROPERTY_SINGULAR_SERVER_HOME));
            impl.propertyMap = state.propertiesBackup;
        }

        public static Object saveState() {
            SingularPropertiesImpl impl = SingularPropertiesImpl.get();
            State state = new State();
            state.propertiesBackup = impl.propertyMap;
            state.systemBackup.put(SYSTEM_PROPERTY_SINGULAR_SERVER_HOME, System.getProperty(SYSTEM_PROPERTY_SINGULAR_SERVER_HOME));

            impl.propertyMap = new PropertyMap(impl.getProperties().consolidateAndFrozen());
            return state;
        }
    }

    private static class State {
        private PropertyMap propertiesBackup;
        private final Map<String, String> systemBackup = new HashMap<>();
    }
}