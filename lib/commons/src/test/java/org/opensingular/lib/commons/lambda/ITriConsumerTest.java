package org.opensingular.lib.commons.lambda;

import org.junit.Assert;
import org.junit.Test;

public class ITriConsumerTest {
    @Test
    public void testNoopIfNull(){
        Assert.assertNotNull(ITriConsumer.noopIfNull(null));
        Assert.assertNotNull(ITriConsumer.noopIfNull((a,b,c)->{String val = "val";}));
    }
}
