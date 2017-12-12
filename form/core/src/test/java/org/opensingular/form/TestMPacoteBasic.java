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

package org.opensingular.form;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.type.core.SIDate;
import org.opensingular.form.type.core.STypeDate;
import org.opensingular.form.type.core.STypeInteger;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class TestMPacoteBasic extends TestCaseForm {

    public TestMPacoteBasic(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test public void testCargaSimples() {
        SDictionary dictionary = createTestDictionary();
        dictionary.loadPackage(SPackageBasic.class);

        STypeInteger mtInt = dictionary.getType(STypeInteger.class);
        Assert.assertEquals(Integer.valueOf(1), mtInt.convert("1"));
        Assert.assertEquals(Integer.valueOf(-1), mtInt.convert("-1"));
        Assert.assertEquals(Integer.valueOf(10), mtInt.convert("010"));
    }

    @Test public void tipoDate(){
        SDictionary dictionary = createTestDictionary();
        dictionary.loadPackage(SPackageBasic.class);

        STypeDate mData = dictionary.getType(STypeDate.class);
        SIDate miData = mData.newInstance();
        miData.setValue("");
        assertThat(miData.getValue()).isNull();
    }
}
