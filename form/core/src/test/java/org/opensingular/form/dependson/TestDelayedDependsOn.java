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

package org.opensingular.form.dependson;


import org.junit.Assert;
import org.junit.Test;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SFormUtil;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SType;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.sample.FormTestPackage;
import org.opensingular.form.sample.STypeFormTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestDelayedDependsOn {

    private static final Logger logger = LoggerFactory.getLogger(TestDelayedDependsOn.class);

    @Test
    public void testDependsOn() throws Exception {
        SDictionary dictionary = SDictionary.create();
        dictionary.loadPackage(FormTestPackage.class);

        STypeFormTest stype     = dictionary.getType(STypeFormTest.class);
        SIComposite   composite = stype.newInstance();

        org.junit.Assert.assertEquals(2, stype.compositeWithListField.theList.getDependentTypes().size());
        for (SType s : stype.compositeWithListField.theList.getDependentTypes()) {
            logger.info(s.getName());
        }
    }

    @Test(expected = SingularFormException.class)
    public void testNullSafe() throws Exception {
        SDictionary dictionary = SDictionary.create();
        dictionary.loadPackage(FormTestPackage.class);

        STypeFormTest stype = dictionary.getType(STypeFormTest.class);
        stype.asAtr().dependsOn((SType<?>[]) null);

    }

    @Test(expected = SingularFormException.class)
    public void testNullSafeArray() throws Exception {
        SDictionary dictionary = SDictionary.create();
        dictionary.loadPackage(FormTestPackage.class);

        STypeFormTest stype = dictionary.getType(STypeFormTest.class);
        stype.asAtr().dependsOn(null, null, null);

    }


    @Test
    public void testDependsOnByStypeClass() throws Exception {
        SDictionary dictionary = SDictionary.create();
        dictionary.loadPackage(FormTestPackage.class);


        SIComposite           instance = dictionary.newInstance(STypeDependsOnByClass.class);
        STypeDependsOnByClass type     = (STypeDependsOnByClass) instance.getType();
        type.uf1.fillDF(instance.getField(type.uf1));
        SFormUtil.evaluateUpdateListeners(instance.getField(type.uf1));
        SFormUtil.evaluateUpdateListeners(instance.getField(type.uf2));
        Assert.assertEquals(2, instance.getField(type.updateListenerCalls).getValue().intValue());
    }

}
