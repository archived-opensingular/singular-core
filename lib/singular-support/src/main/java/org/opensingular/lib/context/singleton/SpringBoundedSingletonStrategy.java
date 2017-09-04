package org.opensingular.lib.context.singleton;

import org.opensingular.lib.commons.context.DelegationSingletonStrategy;
import org.opensingular.lib.commons.context.SingularContext;
import org.opensingular.lib.commons.context.SingularContextSetup;
import org.opensingular.lib.commons.context.SingularSingletonStrategy;
import org.opensingular.lib.commons.context.singleton.InstanceBoundedSingletonStrategy;
import org.opensingular.lib.commons.context.singleton.ThreadBoundedSingletonStrategy;
import org.opensingular.lib.commons.util.Loggable;
import org.springframework.context.annotation.Lazy;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 *
 */
@Lazy(false)
public class SpringBoundedSingletonStrategy extends DelegationSingletonStrategy implements Loggable {

    /**
     * Used only when this SpringBoundedSingletonStrategy is registered as a SpringBean
     */
    private final ThreadBoundedSingletonStrategy tempSingleton = new ThreadBoundedSingletonStrategy();
    private InstanceBoundedSingletonStrategy springSingleton;

    public SpringBoundedSingletonStrategy() {
        tempSingleton.put(SpringBoundedSingletonStrategy.class, this);
    }

    /**
     * Automatically replaces the current {@link SingularSingletonStrategy} keeping all singletons already registered
     */
    @PostConstruct
    public void init() {
        //Configure setSpringSingleton
        springSingleton = new InstanceBoundedSingletonStrategy();
        SingularSingletonStrategy strategy = (SingularSingletonStrategy) SingularContext.get();
        //Migrate data from the previous singleton strategy
        this.putEntries(strategy);
        this.putEntries(this.tempSingleton);
        SingularContextSetup.reset();
        SingularContextSetup.setup(this);
    }

    /**
     * Spring destroy method
     */
    @PreDestroy
    public void destroy(){
        //cleaning up static reference to this bean since this context was shut down
        SingularContextSetup.reset();
    }

    @Nonnull
    @Override
    protected SingularSingletonStrategy getStrategyImpl() {
        return springSingleton == null ? tempSingleton : springSingleton;
    }
}
