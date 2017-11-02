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

package org.opensingular.form.wicket.mapper.selection;

import org.apache.wicket.markup.html.form.TextField;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.type.core.STypeDecimal;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.wicket.helpers.SingularFormDummyPageTester;

public class STypeCompositeSelectItemAutoCompleteTest {
    private SingularFormDummyPageTester tester;

    private static STypeComposite<SIComposite> base;
    private static STypeString                 name;

    private static void buildBaseType(STypeComposite<?> baseType) {
        base = baseType.addFieldComposite("myPlanets");

        name = base.addFieldString("name");
        STypeInteger position = base.addFieldInteger("position");
        STypeDecimal diameter = base.addFieldDecimal("diameterInKm");

        base.autocomplete()
                .id(name)
                .display("Planeta: ${name}, Posição: ${position}, Diametro(Km): ${diameterInKm}")
                .simpleProvider(builder -> {
                    builder.add().set(name, "Mercury").set(position, 1).set(diameter, 4879);
                    builder.add().set(name, "Venus").set(position, 2).set(diameter, 12104);
                    builder.add().set(name, "Earth").set(position, 3).set(diameter, 12756);
                });
    }

    @Before
    public void setUp(){
        tester = new SingularFormDummyPageTester();
        tester.getDummyPage().setTypeBuilder(STypeCompositeSelectItemAutoCompleteTest::buildBaseType);
        tester.startDummyPage();
    }

    @Test
    public void renderOnlyLabels(){
        tester.assertContains("Mercury");
        tester.assertContains("Venus");
        tester.assertContains("Earth");
    }

    @Test
    public void submitsSelectedCompositeValue(){
        tester.newFormTester()
                .setValue(tester.getAssertionsForm().getSubComponents(TextField.class).element(1).getTarget(), "Venus")
                .submit();

        tester.getAssertionsForm().getSubComponentWithType(base)
                .assertSInstance().isComposite().field(name.getNameSimple()).isValueEquals("Venus");
    }
}
