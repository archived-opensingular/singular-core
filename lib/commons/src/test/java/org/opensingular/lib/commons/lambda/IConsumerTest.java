package org.opensingular.lib.commons.lambda;

import org.junit.Assert;
import org.junit.Test;

public class IConsumerTest {
    @Test
    public void testNoop(){
        Assert.assertNotNull(IConsumer.noopIfNull(null));
        Assert.assertNotNull(IConsumer.noopIfNull(a->{String val = "val";}));
    }

    @Test
    public void testAndThen(){
        IConsumer<String> consumer = (IConsumer<String>) s -> {};
        consumer.andThen(a -> {});
    }
}
