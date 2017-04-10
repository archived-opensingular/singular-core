package org.opensingular.flow.core.defaults;

import org.junit.Assert;
import org.junit.Test;
import org.opensingular.flow.core.ProcessInstance;
import org.opensingular.flow.core.TaskInstance;

public class NullViewLocatorTest {

    @Test
    public void nullViewLocatorTest(){
        NullViewLocator nullViewLocator = new NullViewLocator();
        // nao interessa o valor passado, ele sempre retorna nulo.
        Assert.assertNull(nullViewLocator.getDefaultHrefFor((ProcessInstance) null));
        Assert.assertNull(nullViewLocator.getDefaultHrefFor((TaskInstance) null));
    }
}
