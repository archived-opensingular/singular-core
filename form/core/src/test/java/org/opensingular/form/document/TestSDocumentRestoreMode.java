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

package org.opensingular.form.document;


import org.junit.Assert;
import org.junit.Test;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.sample.FormTestPackage;
import org.opensingular.form.sample.STypeFormTest;
import org.opensingular.form.util.transformer.Value;

public class TestSDocumentRestoreMode {

    @Test
    public void testRestoreMode() throws Exception {
        SDictionary dictionary = SDictionary.create();
        dictionary.loadPackage(FormTestPackage.class);

        STypeFormTest stype     = dictionary.getType(STypeFormTest.class);

        SIComposite          composite1 = stype.newInstance();
        Assert.assertEquals(1, composite1.findNearest(stype.compositeWithListField.theList.getElementsType().theNestedTroublesomeList).get().size());

        SIComposite          composite2 = stype.newInstance();

        composite2.getDocument().initRestoreMode();
        Value.copyValues(composite1, composite2);
        composite2.getDocument().finishRestoreMode();

        Assert.assertEquals(1, composite2.findNearest(stype.compositeWithListField.theList.getElementsType().theNestedTroublesomeList).get().size());
    }
}
