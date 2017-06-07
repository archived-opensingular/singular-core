package org.opensingular.lib.support.spring.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.context.SingularContext;
import org.opensingular.lib.commons.context.SingularContextSetup;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ConfigurationClass.class})
public class ApplicationContextProviderTest {

    @Inject
    private ApplicationContext applicationContext;

    @Test(expected = SingularException.class)
    public void getExceptionTest(){
        new ApplicationContextProvider().setApplicationContext(null);
        ApplicationContextProvider.get();
    }

    @Test
    public void setApplicationContextTest(){
        ApplicationContextProvider provider = new ApplicationContextProvider();
        provider.setApplicationContext(applicationContext);

        Assert.assertNotNull(ApplicationContextProvider.get());
    }

    @Test
    public void supplierOfTest(){
        new ApplicationContextProvider().setApplicationContext(applicationContext);
        ISupplier<SimpleBeanClass> supplier = ApplicationContextProvider.supplierOf(SimpleBeanClass.class);
        Assert.assertEquals("value string",supplier.get().getString());
    }
}
