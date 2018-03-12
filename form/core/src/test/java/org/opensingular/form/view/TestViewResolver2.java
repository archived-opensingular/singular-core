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

package org.opensingular.form.view;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestViewResolver2 extends SPackage {

    private STypeComposite<SIComposite> something;

    @SInfoType(spackage = TestViewResolver2.class)
    public static class StypeSomething extends STypeComposite<SIComposite> {

        @Override
        protected void onLoadType(TypeBuilder tb) {
            this.withView(SViewAutoComplete::new);
        }
    }

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        super.onLoadPackage(pb);
        pb.createType(StypeSomething.class);
        STypeComposite<SIComposite> baseType = pb.createCompositeType("baseType");
        something = baseType.addField("something", StypeSomething.class);
    }

    @Test
    public void returnBaseTypeView() throws Exception {
        SDictionary                 dict      = SDictionary.create();
        PackageBuilder              pkg       = dict.createNewPackage("test");
        STypeComposite<SIComposite> baseType  = pkg.createCompositeType("baseType");
        STypeComposite<SIComposite> something = baseType.addFieldComposite("something");

        something.autocomplete();

        assertEquals(SViewAutoComplete.class,
                ViewResolver.resolve(something.newInstance()).getClass());
    }

    @Test
    public void returnTypeViewWhenItsFromSuperType() throws Exception {
        SDictionary dict = SDictionary.create();
        TestViewResolver2 pkg = dict.loadPackage(TestViewResolver2.class);
        assertEquals(SViewAutoComplete.class,
                ViewResolver.resolve(pkg.something.newInstance()).getClass());
    }

}
