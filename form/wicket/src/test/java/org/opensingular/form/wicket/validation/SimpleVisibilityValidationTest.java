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

package org.opensingular.form.wicket.validation;

import org.fest.assertions.api.IterableAssert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.validation.ValidationError;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;

@Ignore
public class SimpleVisibilityValidationTest {

    private static final String FIELD_ONE = "fieldOne";
    private static final String FIELD_TWO = "fieldTwo";

    private SingularDummyFormPageTester tester;

    @Before
    public void setUp(){
        tester = new SingularDummyFormPageTester();
        tester.getDummyPage().setTypeBuilder(baseType -> {

            STypeString fieldOne = baseType.addFieldString(FIELD_ONE);
            STypeString fieldTwo = baseType.addFieldString(FIELD_TWO);

            fieldOne.asAtr().required(true);
            fieldTwo.asAtr().required(true).visible(i -> false);

        });
        tester.startDummyPage();
        tester.newFormTester().submit(tester.getDummyPage().getSingularValidationButton());
    }

    @Test
    public void testIfContaisErrorOnlyForFieldOne() {
        assertThatFieldValidationErros(FIELD_ONE).hasSize(1);
        assertThatFieldValidationErros(FIELD_TWO).isEmpty();
    }

    private IterableAssert<ValidationError> assertThatFieldValidationErros(String field) {
        return tester.getAssertionsForm()
                .getSubComponentWithType(
                        tester.findTypeBySimpleName(field).is(STypeString.class).getTarget()
                )
                .assertSInstance()
                .assertThatValidationErrors();
    }


}