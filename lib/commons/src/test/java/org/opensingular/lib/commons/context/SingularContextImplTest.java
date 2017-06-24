package org.opensingular.lib.commons.context;


import org.junit.Assert;
import org.junit.Test;
import org.opensingular.lib.commons.context.singleton.InstanceBoundedSingletonStrategy;

public class SingularContextImplTest {

    @Test
    public void testReset() throws Exception {
        SingularContextImpl.reset();
        Assert.assertFalse(SingularContextImpl.isConfigured());
    }

    @Test
    public void testSetup() throws Exception {
        SingularContextSetup.reset();
        SingularContextImpl.setup();
        Assert.assertTrue(SingularContextImpl.isConfigured());
    }


    @Test
    public void testSetupParameterized() throws Exception {
        SingularContextImpl.reset();
        InstanceBoundedSingletonStrategy instanceBoundedSingletonStrategy = new InstanceBoundedSingletonStrategy();
        Object                           o                                = new Object();
        instanceBoundedSingletonStrategy.put("test", o);
        SingularContextImpl.setup(instanceBoundedSingletonStrategy);
        Assert.assertEquals(o, ((SingularSingletonStrategy) SingularContextImpl.get()).get("test"));
    }

    @Test
    public void testSetupParameterized2() throws Exception {
        SingularContextImpl.reset();
        InstanceBoundedSingletonStrategy instanceBoundedSingletonStrategy = new InstanceBoundedSingletonStrategy();
        SingularContextImpl.setup(instanceBoundedSingletonStrategy);
        Object o = new Object();
        ((SingularSingletonStrategy) SingularContextImpl.get()).put("test", o);
        Assert.assertEquals(o, ((SingularSingletonStrategy) SingularContextImpl.get()).get("test"));
    }

    @Test(expected = SingularContextAlreadyConfiguredException.class)
    public void testSetupWithoutReset() throws Exception {
        SingularContextSetup.reset();
        SingularContextSetup.setup();
        SingularContextSetup.setup();
    }

    @Test
    public void testGet() throws Exception {
        SingularContextImpl.reset();
        SingularContext.get();
        Assert.assertTrue(SingularContextImpl.isConfigured());
    }
}
