package org.opensingular.lib.commons.scan;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.util.Loggable;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * A centralized classpath scanner used by Singular.
 * It allows reuse of previous scanning data
 */
@SuppressWarnings("unchecked")
public enum SingularClassPathScanner implements Loggable {

    INSTANCE;

    private final ScanResult scanCache;

    SingularClassPathScanner() {
        scanCache = new FastClasspathScanner().scan();
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
     * Find a set of classes annotated with the annotation represented by {@param annotationClass} class.
     * PS: the current implementation is not able to find classes that are annotated indirectly
     * from jvm classes. Ex: {@link java.security.Certificate} is annotated {@link java.lang.Deprecated}, if a hypothetical
     * non-jvm class foo.bar.Foo extends {@link java.security.Certificate}, this classpath scanner will not be able to find
     * foo.bar.Foo as a class that is annotated with  {@link java.lang.Deprecated} because this annotaton is indirectly
     * from an jvm class.
     *
     * @param annotationClass
     * @return
     */
    public Set<Class<?>> findClassesAnnotatedWith(Class<?> annotationClass) {
        if (!annotationClass.isAnnotation()) {
            throw SingularException.rethrow("Invalid Parameter: must be an annotation.");
        }
        return convertClassesNamesToTypedClassesObjects(Object.class, findAnnotated(annotationClass));
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
            return Optional.of(Class.forName(className).asSubclass(type));
        } catch (Throwable e) {
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
