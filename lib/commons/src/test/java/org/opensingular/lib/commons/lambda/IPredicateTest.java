package org.opensingular.lib.commons.lambda;

import org.junit.Assert;
import org.junit.Test;

public class IPredicateTest {

    @Test
    public void testNoneIfFull(){
        Assert.assertNotNull(IPredicate.noneIfNull(null));
        Assert.assertNotNull(IPredicate.noneIfNull(a->{return true;}));
    }

    @Test
    public void testAllIfFull(){
        Assert.assertNotNull(IPredicate.allIfNull(null));
        Assert.assertNotNull(IPredicate.allIfNull(a->{return false;}));
    }
}
