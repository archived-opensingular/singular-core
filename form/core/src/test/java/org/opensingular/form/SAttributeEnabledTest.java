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

package org.opensingular.form;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.util.STypeEMail;

import javax.annotation.Nonnull;

/**
 * @author Daniel C. Bordin on 19/09/2017.
 */
@RunWith(Parameterized.class)
public class SAttributeEnabledTest extends TestCaseForm {

    public SAttributeEnabledTest(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test
    public void test() {
        SDictionary dictionary = createTestDictionary();
        STypeEMail email = dictionary.getType(STypeEMail.class);
        STypeString string = dictionary.getType(STypeString.class);
        STypeSimple simple = dictionary.getType(STypeSimple.class);
        SType rootType = dictionary.getType(SType.class);

        SIString iString = string.newInstance();
        SIString iEmail = email.newInstance();

        Assert.assertEquals(string, email.getSuperType());
        Assert.assertEquals(simple, string.getSuperType());
        Assert.assertEquals(rootType, simple.getSuperType());

        assertHasAttribute(rootType, SPackageBasic.ATR_LABEL, true, true, false);
        assertHasAttribute(simple, SPackageBasic.ATR_LABEL, false, true, false);
        assertHasAttribute(string, SPackageBasic.ATR_LABEL, false, true, false);
        assertHasAttribute(email, SPackageBasic.ATR_LABEL, false, true, true);
        assertHasAttribute(iString, SPackageBasic.ATR_LABEL, false, true, false);
        assertHasAttribute(iEmail, SPackageBasic.ATR_LABEL, false, true, false);

        string.setAttributeValue(SPackageBasic.ATR_LABEL, "X");
        assertHasAttribute(string, SPackageBasic.ATR_LABEL, false, true, true);
        assertHasAttribute(email, SPackageBasic.ATR_LABEL, false, true, true);

        email.setAttributeValue(SPackageBasic.ATR_LABEL, null);
        assertHasAttribute(email, SPackageBasic.ATR_LABEL, false, true, true);

        assertHasAttribute(rootType, SPackageBasic.ATR_MAX_LENGTH, false, false, false);
        assertHasAttribute(simple, SPackageBasic.ATR_MAX_LENGTH, false, false, false);
        assertHasAttribute(string, SPackageBasic.ATR_MAX_LENGTH, true, true, true);
        assertHasAttribute(email, SPackageBasic.ATR_MAX_LENGTH, false, true, false);
        assertHasAttribute(iString, SPackageBasic.ATR_MAX_LENGTH, false, true, false);
        assertHasAttribute(iEmail, SPackageBasic.ATR_MAX_LENGTH, false, true, false);

        iEmail.setAttributeValue(SPackageBasic.ATR_MAX_LENGTH, 10);
        assertHasAttribute(iEmail, SPackageBasic.ATR_MAX_LENGTH, false, true, true);

    }

    private void assertHasAttribute(SAttributeEnabled target, @Nonnull AtrRef<?, ?, ?> atr,
            boolean expectedHasAttributeDefinedDirectly, boolean expectedHasAttributeDefinedInHierarchy,
            boolean expectedHashAttributeValueDirectly) {
        Assert.assertEquals(expectedHasAttributeDefinedDirectly, target.hasAttributeDefinedDirectly(atr));
        Assert.assertEquals(expectedHasAttributeDefinedInHierarchy, target.hasAttributeDefinedInHierarchy(atr));
        Assert.assertEquals(expectedHashAttributeValueDirectly, target.hasAttributeValueDirectly(atr));
    }
}