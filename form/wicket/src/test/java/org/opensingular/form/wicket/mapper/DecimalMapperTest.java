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

package org.opensingular.form.wicket.mapper;

import org.junit.Test;
import org.opensingular.form.wicket.helpers.SingularFormDummyPageTester;

import java.math.BigDecimal;

public class DecimalMapperTest {

    @Test
    public void testIsRendering(){
        SingularFormDummyPageTester tester = new SingularFormDummyPageTester();
        tester.getDummyPage().setTypeBuilder(tb-> tb.addFieldDecimal("decimal"));
        tester.getDummyPage().addInstancePopulator(instance -> instance.setValue("decimal", new BigDecimal(123.45)));

        tester.getDummyPage().setAsEditView();
        tester.startDummyPage();

        BigDecimal expectedValue = new BigDecimal(123.45);
        tester.getAssertionsForm().getSubComponentWithId("decimal").assertSInstance().isValueEquals(expectedValue);
    }
}
