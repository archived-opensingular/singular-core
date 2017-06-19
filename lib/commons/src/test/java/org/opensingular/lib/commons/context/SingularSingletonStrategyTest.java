package org.opensingular.lib.commons.context;


import org.junit.Assert;
import org.junit.Test;
import org.opensingular.lib.commons.context.singleton.InstanceBoundedSingletonStrategy;

public class SingularSingletonStrategyTest {

    @Test
    public void testPutString() throws Exception {
        InstanceBoundedSingletonStrategy instanceBoundedSingletonStrategy = new InstanceBoundedSingletonStrategy();

        instanceBoundedSingletonStrategy.singletonize("test", () -> "nada");
        Assert.assertTrue(instanceBoundedSingletonStrategy.exists("test"));
        Assert.assertTrue(instanceBoundedSingletonStrategy.get("test").equals("nada"));

    }

    @Test
    public void testPutClass() throws Exception {
        InstanceBoundedSingletonStrategy instanceBoundedSingletonStrategy = new InstanceBoundedSingletonStrategy();

        instanceBoundedSingletonStrategy.singletonize(String.class, () -> "nada");
        Assert.assertTrue(instanceBoundedSingletonStrategy.exists(String.class));
        Assert.assertTrue(instanceBoundedSingletonStrategy.get(String.class).equals("nada"));

    }


    @Test(expected = SingularSingletonNotFoundException.class)
    public void testGetNotExisting() {
        InstanceBoundedSingletonStrategy instanceBoundedSingletonStrategy = new InstanceBoundedSingletonStrategy();
        instanceBoundedSingletonStrategy.get(String.class);

    }
}
