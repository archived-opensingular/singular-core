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

package org.opensingular.form.type.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.lib.commons.context.RefService;

import java.io.Serializable;

@RunWith(Parameterized.class)
public class STypeIntegerTest extends  TestCaseForm  {

    public STypeIntegerTest(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test(expected = RuntimeException.class)
    public void valorMuitoGrande() {
        STypeComposite<SIComposite> base = createTestPackage().createCompositeType("base");
        STypeInteger field1 = base.addFieldInteger("numero");

        field1.withInitListener(x -> x.setValue(Long.MAX_VALUE));
        
        field1.asAtr().maxLength(20);
        assertInstance(newInstance(field1)).isValueEquals(Long.MAX_VALUE);
    }
    
    private SInstance newInstance(SType t) {
        return SDocumentFactory.empty()
                .extendAddingSetupStep(document -> document.bindLocalService("test", P.class, RefService.of(new P())))
                .createInstance(RefType.of(() -> t));
    }

    private static class P implements Serializable {

    }

   
}
