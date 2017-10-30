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

import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.form.TextField;
import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SViewAutoComplete;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;

import static org.assertj.core.api.Assertions.assertThat;

public class STypeStringSelectItemDynamicAutoCompleteTest {

    private static final String[] DOMAINS = {"@gmail.com", "@hotmail.com", "@yahoo.com"};
    private static final String   MY_HERO = "myHero";

    private SingularDummyFormPageTester tester;

    @Before
    public void setUp() {
        tester = new SingularDummyFormPageTester();
        tester.getDummyPage().setTypeBuilder(root -> {
            STypeString myHero = root.addFieldString(MY_HERO);
            myHero.selectionOf(DOMAINS);
            myHero.withView(new SViewAutoComplete(SViewAutoComplete.Mode.DYNAMIC));
        });
        tester.startDummyPage();
    }

    @Test
    public void renderField() {
        tester.getAssertionsPage().getSubComponents(TypeaheadComponent.class).isSize(1);
        tester.getAssertionsPage().getSubComponents(TextField.class).isSize(2);
    }

    @Test
    public void haveABloodhoundBehabiour() {
        TypeaheadComponent typeaheadComponent = tester.getAssertionsPage().getSubComponents(TypeaheadComponent.class)
                .get(0)
                .getTarget(TypeaheadComponent.class);
        assertThat(typeaheadComponent.getBehaviors()).haveAtLeast(1, new Condition<Behavior>() {
            @Override
            public boolean matches(Behavior value) {
                return BloodhoundDataBehavior.class.isInstance(value);
            }
        });
    }

}