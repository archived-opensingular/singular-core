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
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.collections4.EnumerationUtils;
import org.apache.commons.lang3.StringUtils;
import org.opensingular.lib.commons.lambda.ISupplierEx;
import org.opensingular.lib.commons.util.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * It's a equivalent of {@link Properties} with additional capabilities to save the point of origin of a property
 * ({@link PropertySource}), detect duplication of a property in different source and allow chaining of PropertyMaps.
 *
 * @author Daniel C. Bordin
 * @since 2017-11-02
 */
public final class PropertyMap {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyMap.class);

    private final LinkedHashMap<String, PropertyEntry> entries = new LinkedHashMap<>();
    private final PropertyMap parent;
    private boolean frozen;

    public PropertyMap() {this(null);}

    /**
     * Crates a {@link PropertyMap} with parent map that will be used to look for a property value if it isn't found in
     * the current one.
     */
    public PropertyMap(@Nullable PropertyMap parent) {this.parent = parent;}

    /**
     * Read a {@link Properties} from the source and loads it's content to the map detecting conflicting definition
     * os same property (throws a Exception).
     */
    public void readProperties(@Nonnull File source) {
        if (source.exists()) {
            readProperties(() -> PropertiesUtils.load(source, "UTF-8"), PropertySource.of(source));
        }
    }

    /**
     * Look for all the properties file with the specific name into the classpath and call
     * {@link #readProperties(URL)}.
     */
    public void readAllPropertiesFileFromClassPath(@Nonnull String resourceName) {
        List<URL> resources = findResources(resourceName);
        resources.forEach(url -> readProperties(url));
    }

    @Nonnull
    private List<URL> findResources(@Nonnull String resourceName) {
        try {
            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(resourceName);
            List<URL> list = EnumerationUtils.toList(resources);
            Collections.sort(list, (url1, url2) -> url1.toString().compareTo(url2.toString()));
            return list;
        } catch (Exception e) {
            throw new SingularPropertyException(
                    "Fail looking for resources with name '" + resourceName + "' in the classpath", e);
        }
    }

    /**
     * Read a {@link Properties} from the source and loads it's content to the map detecting conflicting definition
     * os same property (throws a Exception). If multiple files are found and they define the same property, then a
     * exception is launched.
     */
    public void readProperties(@Nonnull URL source) {
        readProperties(() -> PropertiesUtils.load(source, "UTF-8"), PropertySource.of(source));
    }

    private void readProperties(@Nonnull ISupplierEx<Properties, Exception> propertiesLoader,
            @Nonnull PropertySource<?> source) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("   Lendo arquivo de propriedades {}", source);
        }
        try {
            Properties properties = Objects.requireNonNull(propertiesLoader.get());
            readProperties(properties, source);
        } catch (Exception e) {
            SingularPropertyException e2 = new SingularPropertyException("Fail to read properties file", e);
            e2.add("source", source);
            throw e2;
        }
    }

    private void readProperties(@Nonnull Properties properties, @Nonnull PropertySource source) {
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            add((String) entry.getKey(), (String) entry.getValue(), source);
        }
    }
    /** Sets the value for a property registering it by a unknown source.*/
    public void add(@Nonnull String key, @Nullable String value) {
        add(key, value, PropertySource.UNKNOWN);
    }

    /** Sets the value for a property. */
    public void add(@Nonnull String key, @Nullable String value, @Nonnull PropertySource source) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(source);
        String value2 = StringUtils.trimToNull(value);
        if (frozen) {
            throw new SingularPropertyException("The properties map if frozen (locked for any further changed)");
        }
        PropertyEntry current = entries.get(key);
        if (current != null) {
            SingularPropertyException e = new SingularPropertyException("The property '" + key +
                    "' is already definied. It's not allowed to have the same property set twice.");
            e.add("key", key);
            e.add("current Source", current.getSource());
            e.add("new Source", source);
            if (Objects.equals(current.getValue(), value2)) {
                e.add("values", "both are equals");
            }
            //e.add("current Value", current.getValue());
            //e.add("new Value", value);
            throw e;
        }
        entries.put(key, new PropertyEntry(key, value2, source));
    }

    /** Looks for the value of the key in the current map and into the parent map if necessary. */
    @Nullable
    public String getValue(@Nonnull String key) {
        PropertyEntry entry = getEntry(key);
        return entry == null ? null : entry.getValue();
    }

    /** Looks for the property associated to the key in the current map and into the parent map if necessary. */
    @Nullable
    public PropertyEntry getEntry(@Nonnull String key) {
        PropertyEntry entry = entries.get(key);
        if (entry == null && parent != null) {
            return parent.getEntry(key);
        }
        return entry;
    }

    /** Verifies if there is a register of value for the key. This register may set a null value to key. */
    public boolean containsKey(@Nonnull String key) {
        return entries.containsKey(key) || (parent != null && parent.containsKey(key));
    }

    /** Looks the values of the current map and renders invalid any further attempt to change values in the map. */
    public void frozen() {
        this.frozen = true;
    }

    /** Verifies if this map is locked for change in values. */
    public boolean isFrozen() {
        return frozen;
    }

    /**
     * Generates a new map with the equivalent values of this map and it's parent (if the parent exists). The new map
     * won't have a parent map.
     */
    @Nonnull
    public PropertyMap consolidateAndFrozen() {
        PropertyMap map2 = new PropertyMap();
        copyEntries(map2.entries);
        map2.frozen();
        return map2;
    }

    private void copyEntries(LinkedHashMap<String, PropertyEntry> newMap) {
        if (parent != null) {
            parent.copyEntries(newMap);
        }
        newMap.putAll(entries);
    }

    @VisibleForTesting
    final int getSize() {
        return entries.size();
    }

    /**
     * Prints the content of map of properties to the system output identifying the source of each property.
     */
    public void debugContent() {
        debugContent(System.out);
    }

    /**
     * Prints the content of map of properties to the specific output identifying the source of each property.
     */
    public void debugContent(@Nonnull Appendable out) {
        Multimap<PropertySource<?>, PropertyEntry> entriesBySource = LinkedHashMultimap.create();
        loadEntries(entriesBySource);
        try {
            String lineSeparator = System.getProperty("line.separator");
            for (PropertySource<?> source : entriesBySource.keySet()) {
                out.append('#').append(lineSeparator);
                out.append("#source '").append(source.getDescription()).append("'");
                out.append(lineSeparator);
                debugContent(entriesBySource.get(source), out, lineSeparator);
            }
        } catch (IOException e) {
            throw new SingularPropertyException("Error writing to output", e);
        }
    }

    private void debugContent(Collection<PropertyEntry> properties, @Nonnull Appendable out, String lineSeparator)
            throws IOException {
        List<PropertyEntry> list = new ArrayList<>(properties);
        Collections.sort(list);
        for (PropertyEntry pEntry : list) {
            out.append(pEntry.getKey()).append('=');
            if (pEntry.getValue() != null) {
                out.append(pEntry.getValue());
            }
            out.append(lineSeparator);
        }
    }

    private void loadEntries(Multimap<PropertySource<?>, PropertyEntry> entriesBySource) {
        entries.entrySet().forEach(entry -> entriesBySource.put(entry.getValue().getSource(), entry.getValue()));
        if (parent != null) {
            parent.loadEntries(entriesBySource);
        }
    }

    @VisibleForTesting
    final PropertyMap getParent() {
        return parent;
    }
}
