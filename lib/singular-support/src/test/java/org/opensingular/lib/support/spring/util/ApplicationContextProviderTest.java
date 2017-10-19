/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.lib.support.spring.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.context.SingularContext;
import org.opensingular.lib.commons.context.SingularContextSetup;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ConfigurationClass.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class ApplicationContextProviderTest {

    @Inject
    private ApplicationContext applicationContext;

    @Test(expected = SingularException.class)
    public void getExceptionTest(){
        SingularContextSetup.reset();
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
