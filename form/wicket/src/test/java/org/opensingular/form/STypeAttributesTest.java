/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form;

import org.apache.wicket.markup.html.form.RadioChoice;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.country.brazil.STypeAddress;
import org.opensingular.form.view.SViewSelectionByRadio;
import org.opensingular.form.wicket.helpers.AssertionsWComponentList;
import org.opensingular.form.wicket.helpers.SingularFormDummyPageTester;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

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

public class STypeAttributesTest {

    private static STypeString cep;

    private SingularFormDummyPageTester tester;

    private static void buildBaseType(STypeComposite<?> baseCompositeField) {
        cep = baseCompositeField.addFieldString("cep");
        cep.asAtr().exists(false);
    }

    @Before
    public void setUp(){
        tester = new SingularFormDummyPageTester();
        tester.getDummyPage().setTypeBuilder(STypeAttributesTest::buildBaseType);
        tester.getDummyPage().enableAnnotation();
    }

    @Test
    public void testExists() {
        tester.startDummyPage();

        Assert.assertFalse(tester.getAssertionsPage().getSubComponentWithType(cep).getTarget().isVisibleInHierarchy());


    }

}

