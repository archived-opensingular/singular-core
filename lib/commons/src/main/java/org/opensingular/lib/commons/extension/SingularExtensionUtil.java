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

package org.opensingular.lib.commons.extension;

import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.context.SingularContext;
import org.opensingular.lib.commons.context.SingularSingletonStrategy;
import org.opensingular.lib.commons.util.Loggable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

/**
 * Lookup in the classpath for registered implementation of a class or interface. This is a standard way to create
 * point of extension to plug additional code that will be added by specific projects or by adding new JARs to a
 * project.
 * <p>The implementation of the look up methods use Java's {@link ServiceLoader} (see its JavaDoc). How to register a
 * extension point
 * implementation is also detailed in the article
 * <a href="http://www.oracle.com/technetwork/articles/javase/extensible-137159.html">Creating Extensible Applications
 * With the Java Platform</a>.</>
 * <p>For example, if the extension point class is <b>my.MyExtension</b>, the basic steps can be resumed as:</p>
 * <ol>
 * <li>Create the implementation class extending MyExtension, e.g., <b>example.MyExtensionPointImplementation</b></li>
 * <li>Register the implementation by creating in the class path a file with the name:<br>
 * <code>META-INF/services/<b>my.MyExtension</b></code></li>
 * <li>In the file add the line (may be multiple lines with different implementations):<br>
 * <code><b>example.MyExtensionPointImplementation</b>    # Standard implementation</code></li>
 * </ol>
 * <p>
 * <p>Optionally, there are the following possibilities of customization:</p>
 * <ul>
 * <li>Use {@link SingularExtension#getExtensionPriority()} to indicated the most relevant implementation when there
 * may be more then one registered implementation.</li>
 * <li>Annotate the implementation using {@link ExtensionQualifier} annotation, so it may be possible to lookup for a
 * named point of extension by calling for example {@link #findExtensions(Class, String)}. This feature is useful when
 * behavior is the same (e.g., add a button) but in different places.<br> The same implementation may
 * have multiple qualifiers (meaning that it me be used in multiple places). See a example in {@link
 * ExtensionQualifier}.</li>
 * </ul>
 *
 * @see SingularExtension
 * @see ServiceLoader
 */
public final class SingularExtensionUtil implements Loggable {

    private SingularExtensionUtil() {
    }

    public static SingularExtensionUtil get() {
        return ((SingularSingletonStrategy) SingularContext.get()).singletonize(SingularExtensionUtil.class,
                SingularExtensionUtil::new);
    }

    /**
     * Finds all the registered implementations for the class.
     *
     * @return The extensions will be ordered from the highest priority to the lowest (see {@link
     * SingularExtension#getExtensionPriority()}). It may return a empty list.
     */
    @Nonnull
    public <T extends SingularExtension> List<T> findExtensions(@Nonnull Class<T> extensionTarget) {
        return findExtensions(extensionTarget, null);
    }

    /**
     * Finds all the registered implementations for the class.
     *
     * @param qualifier If not null, will return only the implementation annotated with the same value using the {@link
     *                  ExtensionQualifier}. If null, will result all implementations.
     * @return The extensions will be ordered from the highest priority to the lowest (see {@link
     * SingularExtension#getExtensionPriority()}). It may return a empty list.
     */
    @Nonnull
    public <T extends SingularExtension> List<T> findExtensions(@Nonnull Class<T> extensionTarget,
                                                                @Nullable String qualifier) {
        Objects.requireNonNull(extensionTarget);
        checkIfValidExtensionClass(extensionTarget);
        List<T> list = new ArrayList<>();
        for (T extension : loadServices(extensionTarget)) {
            if (qualifier == null || hasQualifier(extension, qualifier)) {
                list.add(extension);
            }
        }
        if (list.size() > 1) {
            list.sort((t1, t2) -> t2.getExtensionPriority() - t1.getExtensionPriority());
        }
        return list;
    }

    private <T extends SingularExtension> Iterable<T> loadServices(@Nonnull Class<T> extensionTarget) {
        try {
            return ServiceLoader.load(extensionTarget);
        } catch (ServiceConfigurationError e) {
            getLogger().debug(e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    private <T extends SingularExtension> boolean hasQualifier(@Nonnull T extension, @Nonnull String targetQualifier) {
        for (ExtensionQualifier q : extension.getClass().getAnnotationsByType(ExtensionQualifier.class)) {
            if (Objects.equals(q.value(), targetQualifier)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Lookup for the implementation of the extension point.
     * <p>If multiple implementations are found, they will sorted by {@link SingularExtension#getExtensionPriority()}
     * and the one with the highest priority will be returned.</p>
     */
    @Nonnull
    public <T extends SingularExtension> Optional<T> findExtension(@Nonnull Class<T> extensionTarget) {
        return findExtension(extensionTarget, null);
    }

    /**
     * Lookup for the implementation of the extension point.
     * <p>If multiple implementations are found, they will sorted by {@link SingularExtension#getExtensionPriority()}
     * and the one with the highest priority will be returned.</p>
     *
     * @param qualifier If not null, return only a extension point that matches de qualifier.
     */
    @Nonnull
    public <T extends SingularExtension> Optional<T> findExtension(@Nonnull Class<T> extensionTarget,
                                                                   @Nullable String qualifier) {
        List<T> list = findExtensions(extensionTarget, qualifier);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    /**
     * Lookup for the implementation of the extension point and throws a exception if no one is found.
     * <p>If multiple implementations are found, they will sorted by {@link SingularExtension#getExtensionPriority()}
     * and the one with the highest priority will be returned.</p>
     */
    @Nonnull
    public <T extends SingularExtension> T findExtensionOrException(@Nonnull Class<T> extensionTarget) {
        return findExtensionOrException(extensionTarget, null);
    }

    /**
     * Lookup for the implementation of the extension point and throws a exception if no one is found.
     * <p>If multiple implementations are found, they will sorted by {@link SingularExtension#getExtensionPriority()}
     * and the one with the highest priority will be returned.</p>
     *
     * @param qualifier If not null, return only a extension point that matches de qualifier.
     */
    @Nonnull
    public <T extends SingularExtension> T findExtensionOrException(@Nonnull Class<T> extensionTarget,
                                                                    @Nullable String qualifier) {
        List<T> list = findExtensions(extensionTarget, qualifier);
        if (list.isEmpty()) {
            throw new SingularException("No registered implementation for " + extensionTarget.getName() + " was found");
        }
        return list.get(0);
    }

    private <T extends SingularExtension> void checkIfValidExtensionClass(@Nonnull Class<T> extensionClass) {
        if (!extensionClass.getName().endsWith("Extension")) {
            throw new SingularException("The name of the class " + extensionClass.getName() +
                    " is no valid for a extension point. It must ends with the sufix 'Extension'");
        }
    }

}