/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package org.opensingular.form.type.country.brazil;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.validation.InstanceValidationContext;
import org.opensingular.form.validation.ValidationErrorLevel;

/**
 * @author Daniel C. Bordin on 26/03/2017.
 */
@RunWith(Parameterized.class)
public class TestSTypeCNPJ extends TestCaseForm {

    public TestSTypeCNPJ(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test
    public void diferenteValue() {
        testCNPJ("88888888888888", true);
        testCNPJ("02.306.220/0001-73", false);
        testCNPJ("02306220000173", false);
        testCNPJ("02306220000174", true);
        testCNPJ("023", true);
        testCNPJ("XX", true);
    }

    private void testCNPJ(String value, boolean expectedToBeWrongValue) {
        SIString cnpj = createTestDictionary().newInstance(STypeCNPJ.class);
        cnpj.setValue(value);
        InstanceValidationContext ctx = new InstanceValidationContext();
        ctx.validateSingle(cnpj);

        if (ctx.hasErrorsAboveLevel(ValidationErrorLevel.ERROR)) {
            if (! expectedToBeWrongValue) {
                fail("Expected to be a correct value: '" + value + '\'');
            } else {
                String msg = ctx.getErrorsByInstanceId().get(cnpj.getId()).iterator().next().getMessage();
                assertEquals("CNPJ inválido", msg);
            }
        } else if (expectedToBeWrongValue) {
            fail("Expected to be a incorrect value: '" + value + '\'');
        }
    }

}
