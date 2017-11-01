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

import org.apache.wicket.markup.html.form.TextField;
import org.junit.Test;
import org.opensingular.form.wicket.helpers.AssertionsWTextField;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;

public class MoneyMapperTest {

    @Test
    public void testEditRendering() {
        SingularDummyFormPageTester ctx = new SingularDummyFormPageTester();
        ctx.getDummyPage().setTypeBuilder(baseType ->  baseType.addFieldMonetary("money"));
        ctx.getDummyPage().addInstancePopulator(instance -> instance.setValue("money", "10,00"));
        ctx.getDummyPage().setAsEditView();
        ctx.startDummyPage();

        ctx.getAssertionsForm().getSubComponents(TextField.class).hasSize(1).element(0);
        AssertionsWTextField textField = ctx.getAssertionsForm().getSubComponentWithId("money").asTextField();
        textField.assertValue().isEqualTo("10,00");
    }

}