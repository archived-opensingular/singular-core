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


import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.opensingular.form.*;
import org.opensingular.form.sample.FormTestPackage;
import org.opensingular.form.sample.STypeFormTest;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.country.brazil.STypeUF;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TestDelayedDependsOn {

    private static final Logger logger = LoggerFactory.getLogger(TestDelayedDependsOn.class);

    @Test
    public void testDependsOn() throws Exception {
        SDictionary dictionary = SDictionary.create();
        dictionary.loadPackage(FormTestPackage.class);

        STypeFormTest stype     = dictionary.getType(STypeFormTest.class);
        SIComposite      composite = stype.newInstance();

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


        SIComposite instance = dictionary.newInstance(STypeDependsOnByClass.class);
        STypeDependsOnByClass type = (STypeDependsOnByClass) instance.getType();
        type.uf1.fillDF(instance.getField(type.uf1));
        SFormUtil.evaluateUpdateListeners(instance.getField(type.uf1));
        SFormUtil.evaluateUpdateListeners(instance.getField(type.uf2));
        System.out.println(instance.getField(type.updateListenerCalls).getValue());
    }

}
