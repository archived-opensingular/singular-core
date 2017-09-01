package org.opensingular.lib.commons.context;


import org.junit.Assert;
import org.junit.Test;
import org.opensingular.lib.commons.context.singleton.InstanceBoundedSingletonStrategy;

public class MigrationEnabledSingularSingletonStrategyTest {

    @Test
    public void testMigrate() throws Exception {
        SingularContextSetup.reset();

        //Default config
                ((SingularSingletonStrategy) SingularContext.get()).singletonize("nada", () -> "nada");
        Assert.assertTrue(SingularContext.get() instanceof SingularSingletonStrategy);

        //Another config
        InstanceBoundedSingletonStrategy another = new InstanceBoundedSingletonStrategy();
        another.put("nada2", "nada2");
        Assert.assertTrue(another instanceof SingularSingletonStrategy);
        another.putEntries(((SingularSingletonStrategy) SingularContext.get()));

        //reconfig
        SingularContextSetup.reset();
        SingularContextSetup.setup(another);

        Assert.assertTrue(((SingularSingletonStrategy) SingularContext.get()).exists("nada"));
        Assert.assertTrue(((SingularSingletonStrategy) SingularContext.get()).exists("nada2"));

    }
}
