/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.lib.commons.scan;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.context.SingularContext;
import org.opensingular.lib.commons.context.SingularSingletonStrategy;
import org.opensingular.lib.commons.util.Loggable;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A centralized classpath scanner used by Singular.
 * It allows reuse of previous scanning data
 */
@SuppressWarnings("unchecked")
public class SingularClassPathScanner implements Loggable {


    private final ScanResult scanCache;

    protected SingularClassPathScanner() {
        long time = new Date().getTime();
        scanCache = new FastClasspathScanner().scan();
        getLogger().info("Full classpath scan in {} ms", new Date().getTime() - time);
    }

    public static SingularClassPathScanner get() {
        return ((SingularSingletonStrategy) SingularContext.get()).singletonize(SingularClassPathScanner.class, SingularClassPathScanner::new);
    }

    /**
     * Find a set of classes based on the {@param clazz} parameter so that:
     * 1. if {@param clazz} is an interface this method will return all classes that implements the given interface
     * 2. if {@param clazz} is an clazz this method will return all subclasses of the given class.
     * PS: the current implementation is not able to find classes that inherit an interface or superclass indirectly
     * from jvm classes. Ex: {@link java.util.Date} implements {@link java.io.Serializable}, if a hypothetical
     * non-jvm class foo.bar.Foo extends {@link java.util.Date}, this classpath scanner will not be able to find
     * foo.bar.Foo as a class that implements  {@link java.io.Serializable} because this inheritance is indirectly
     * from an jvm class.
     *
     * @param clazz An class or interface to filter subclasses or implementing classes from classpath.
     * @param <T>   The class type
     * @return A list of unique classes.
     */
    public <T> Set<Class<? extends T>> findSubclassesOf(Class<T> clazz) {
        return convertClassesNamesToTypedClassesObjects(clazz,
                findClassesImplementingInterface(clazz),
                findSubclasses(clazz),
                findInterfacesExtendingInterface(clazz));
    }

    /**
     * Does almost the same of {@link SingularClassPathScanner#findSubclassesOf(Class)}, but also filter results
     * by packages names
     *
     * @param clazz          An class or interface to filter subclasses or implementing classes from classpath.
     * @param filterPackages packages names to filter
     * @param <T>            The class type
     * @return A list of unique classes.
     */
    public <T> Set<Class<? extends T>> findSubclassesOf(Class<T> clazz, String... filterPackages) {
        return convertClassesNamesToTypedClassesObjects(clazz,
                filterByPackages(findClassesImplementingInterface(clazz), Arrays.asList(filterPackages)),
                filterByPackages(findSubclasses(clazz), Arrays.asList(filterPackages)),
                filterByPackages(findInterfacesExtendingInterface(clazz), Arrays.asList(filterPackages)));
    }

    /**
     * Find a set of classes annotated with the annotation represented by {@param annotationClass} class.
     * PS: the current implementation is not able to find classes that are annotated indirectly
     * from jvm classes. Ex: {@link java.security.Certificate} is annotated {@link java.lang.Deprecated}, if a hypothetical
     * non-jvm class foo.bar.Foo extends {@link java.security.Certificate}, this classpath scanner will not be able to find
     * foo.bar.Foo as a class that is annotated with  {@link java.lang.Deprecated} because this annotaton is indirectly
     * from an jvm class.
     *
     * @param annotationClass An class or representing an annotation to filter classes from classpath.
     * @return A list of unique classes.
     */
    public Set<Class<?>> findClassesAnnotatedWith(Class<?> annotationClass) {
        if (!annotationClass.isAnnotation()) {
            throw SingularException.rethrow("Invalid Parameter: must be an annotation.");
        }
        return convertClassesNamesToTypedClassesObjects(Object.class, findAnnotated(annotationClass));
    }

    /**
     * Does almost the same of {@link SingularClassPathScanner#findClassesAnnotatedWith(Class)}, but also filter results
     * by packages names
     *
     * @param annotationClass An class or representing an annotation to filter classes from classpath.
     * @param filterPackages  packages names to filter
     * @return A list of unique classes.
     */
    public Set<Class<?>> findClassesAnnotatedWith(Class<?> annotationClass, String... filterPackages) {
        if (!annotationClass.isAnnotation()) {
            throw SingularException.rethrow("Invalid Parameter: must be an annotation.");
        }
        return convertClassesNamesToTypedClassesObjects(Object.class, filterByPackages(findAnnotated(annotationClass), Arrays.asList(filterPackages)));
    }

    private List<String> filterByPackages(List<String> classes, List<String> packages) {
        return classes.stream().filter(c -> packages.stream().anyMatch(c::startsWith)).collect(Collectors.toList());
    }

    private <T> Set<Class<? extends T>> convertClassesNamesToTypedClassesObjects(Class<T> type, List<String>... lists) {
        Set<Class<? extends T>> result = new HashSet<>();
        for (List<String> list : lists) {
            list.forEach(className -> classLookup(type, className).ifPresent(result::add));
        }
        return result;
    }

    private <T> Optional<Class<? extends T>> classLookup(Class<T> type, String className) {
        try {
            Class<?> clazz = Class.forName(className, false, Thread.currentThread().getContextClassLoader());
            return Optional.of(clazz.asSubclass(type));
        } catch (Throwable e) {//NOSONAR
            getLogger().error(e.getMessage(), e);
        }
        return Optional.empty();
    }

    private List<String> findAnnotated(Class<?> clazz) {
        List<String> result = Collections.emptyList();
        if (isAnnotation(clazz)) {
            result = scanCache.getNamesOfClassesWithAnnotation(clazz);
        }
        return result;
    }

    private List<String> findSubclasses(Class<?> clazz) {
        List<String> result = Collections.emptyList();
        if (isNonFinalClass(clazz)) {
            result = scanCache.getNamesOfSubclassesOf(clazz);
        }
        return result;
    }

    private List<String> findClassesImplementingInterface(Class<?> clazz) {
        List<String> result = Collections.emptyList();
        if (isInterface(clazz)) {
            result = scanCache.getNamesOfClassesImplementing(clazz);
        }
        return result;
    }

    private List<String> findInterfacesExtendingInterface(Class<?> clazz) {
        List<String> result = Collections.emptyList();
        if (isInterface(clazz)) {
            result = scanCache.getNamesOfSubinterfacesOf(clazz);
        }
        return result;
    }

    private boolean isAnnotation(Class<?> clazz) {
        return clazz.isAnnotation();
    }

    private boolean isInterface(Class<?> clazz) {
        return clazz.isInterface();
    }

    private boolean isNonFinalClass(Class<?> clazz) {
        return !clazz.isAnnotation()
                && !clazz.isEnum()
                && !clazz.isInterface()
                && !Modifier.isFinal(clazz.getModifiers());
    }

}
