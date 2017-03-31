package org.opensingular.lib.commons.lambda;

import org.junit.Assert;
import org.junit.Test;

public class IBiConsumerTest {

    @Test
    public void testNoopIfNull(){
        Assert.assertNotNull(IBiConsumer.noopIfNull(null));
        Assert.assertNotNull(IBiConsumer.noopIfNull((a,b)->{String val = "val";}));
    }
}
