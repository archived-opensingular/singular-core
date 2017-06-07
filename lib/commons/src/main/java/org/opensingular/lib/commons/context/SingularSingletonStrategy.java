package org.opensingular.lib.commons.context;

import org.opensingular.lib.commons.lambda.ISupplier;

public interface SingularSingletonStrategy {


    /**
     * Keeps a single instance and use the the given instance class as key
     * The key to recover the singleton is the object class
     * Only one instance per class can be registered
     * Null values must be ignored silently
     * @param thisInstance
     * @param <T>
     * @return
     */
    <T> void put(T thisInstance);


    /**
     * Keeps a single instance for the given class and use the informed class as key
     * The key to recover the singleton is the object class
     * Only one instance per class can be registered
     * Null values must be ignored silently
     * @param thisInstance
     * @param <T>
     * @return
     */
    <T> void put(Class<? super T> instanceClazz, T thisInstance);


    /**
     * Keep a single instance for the given name identifier
     * Note that, different from Spring singleton, in this case
     * Only one instance per name can be registered
     * Null values must be ignored silently
     * @param nameKey
     * @param thisInstance
     * @param <T>
     * @return
     */
    <T> void put(String nameKey, T thisInstance);


    /**
     * Checks if exists a singleton for the given class
     * This method is intended to find singletons registered by
     * thie {SingularSingletonStrategy#put(T thisInstance)}
     *
     * @param classKey
     * @param <T>
     * @return true if exists, false otherwise
     */
    <T> boolean exists(Class<T> classKey);

    /**
     * Checks if exists a singleton for the given name
     * This method is intended to find singletons registered by
     * thie {SingularSingletonStrategy#put(String name, T thisInstance)}
     *
     * @param nameKey
     * @return true if exists, false otherwise
     */
    boolean exists(String nameKey);

    /**
     * @param singletonClass
     * @param <T>
     * @return
     */
    <T> T get(Class<T> singletonClass) throws SingularSingletonNotFoundException;

    <T> T get(String name) throws SingularSingletonNotFoundException;

    /**
     * Get an exsisting singleton entry or create it if is necessary using @param singletonFactory
     * lambda
     * @param nameKey
     * @param singletonFactory
     * @param <T>
     * @return
     */
    default <T> T singletonize(String nameKey, ISupplier<T> singletonFactory) {
        if (!exists(nameKey)) {
            put(nameKey, singletonFactory.get());
        }
        return get(nameKey);
    }

    /**
     * 
     * Get an exsisting singleton entry or create it if is necessary using @param singletonFactory
     * lambda
     * @param classKey
     * @param singletonFactory
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    default <T> T singletonize(Class<? super T> classKey, ISupplier<T> singletonFactory) {
        if (!exists(classKey)) {
            put(classKey, singletonFactory.get());
        }
        return (T)get(classKey);
    }

}
