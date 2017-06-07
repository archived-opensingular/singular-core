package org.opensingular.lib.context.singleton;

import org.opensingular.lib.commons.context.MigrationEnabledSingularSingletonStrategy;
import org.opensingular.lib.commons.context.SingularContext;
import org.opensingular.lib.commons.context.SingularContextSetup;
import org.opensingular.lib.commons.context.SingularSingletonNotFoundException;
import org.opensingular.lib.commons.context.SingularSingletonStrategy;
import org.opensingular.lib.commons.context.singleton.InstanceBoundedSingletonStrategy;
import org.opensingular.lib.commons.context.singleton.ThreadBoundedSingletonStrategy;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.commons.util.Loggable;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 *
 */
public class SpringBoundedSingletonStrategy implements SingularSingletonStrategy, MigrationEnabledSingularSingletonStrategy, Loggable {

    /**
     * Used only when this SpringBoundedSingletonStrategy is registered as a SpringBean
     */

    private ThreadBoundedSingletonStrategy tempSingleton = new ThreadBoundedSingletonStrategy();
    private InstanceBoundedSingletonStrategy springSingleton;

    public SpringBoundedSingletonStrategy() {
        tempSingleton.put(SpringBoundedSingletonStrategy.class, this);
    }

    @PostConstruct
    public void init() {
        //Configure setSpringSingleton
        springSingleton = new InstanceBoundedSingletonStrategy();
        //Migrate data from the privious singleton strategy
        MigrationEnabledSingularSingletonStrategy strategy = (MigrationEnabledSingularSingletonStrategy) SingularContext.get();
        this.put(this.tempSingleton.getEntries());
        this.putEntries(strategy.getEntries());
        //Migrate change Singular context to use this SpringBoundedSingletonStrategy
        SingularContextSetup.reset();
        SingularContextSetup.setup(this);
    }

    @Override
    public synchronized <T> void put(T thisInstance) {
        if (springSingleton == null) {
            tempSingleton.put(thisInstance);
        } else {
            springSingleton.put(thisInstance);
        }
    }

    @Override
    public synchronized <T> void put(Class<? super T> instanceClazz, T thisInstance) {
        if (springSingleton == null) {
            tempSingleton.put(instanceClazz, thisInstance);
        } else {
            springSingleton.put(instanceClazz, thisInstance);
        }
    }

    @Override
    public synchronized <T> void put(String nameKey, T thisInstance) {
        if (springSingleton == null) {
            tempSingleton.put(nameKey, thisInstance);
        } else {
            springSingleton.put(nameKey, thisInstance);
        }
    }

    @Override
    public synchronized <T> boolean exists(Class<T> classKey) {
        if (springSingleton == null) {
            return tempSingleton.exists(classKey);
        } else {
            return springSingleton.exists(classKey);
        }
    }

    @Override
    public synchronized boolean exists(String nameKey) {
        if (springSingleton == null) {
            return tempSingleton.exists(nameKey);
        } else {
            return springSingleton.exists(nameKey);
        }
    }

    @Override
    public synchronized <T> T get(Class<T> singletonClass) throws SingularSingletonNotFoundException {
        if (springSingleton == null) {
            return tempSingleton.get(singletonClass);
        } else {
            return springSingleton.get(singletonClass);
        }
    }

    @Override
    public synchronized <T> T get(String name) throws SingularSingletonNotFoundException {
        if (springSingleton == null) {
            return tempSingleton.get(name);
        } else {
            return springSingleton.get(name);
        }
    }

    @Override
    public synchronized <T> T singletonize(String nameKey, ISupplier<T> singletonFactory) {
        return SingularSingletonStrategy.super.singletonize(nameKey, singletonFactory);
    }

    @Override
    public synchronized <T> T singletonize(Class<? super T> classKey, ISupplier<T> singletonFactory) {
        return SingularSingletonStrategy.super.singletonize(classKey, singletonFactory);
    }

    @Override
    public synchronized Map<Object, Object> getEntries() {
        if (springSingleton == null) {
            return tempSingleton.getEntries();
        } else {
            return springSingleton.getEntries();
        }
    }

    @Override
    public synchronized void putEntries(Map<Object, Object> entries) {
        if (springSingleton == null) {
            tempSingleton.putEntries(entries);
        } else {
            springSingleton.putEntries(entries);
        }
    }
}
