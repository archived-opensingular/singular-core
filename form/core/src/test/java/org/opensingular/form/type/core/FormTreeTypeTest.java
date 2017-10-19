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
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TestCaseForm;

@RunWith(Parameterized.class)
public class FormTreeTypeTest extends TestCaseForm {

    public FormTreeTypeTest(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test public void shouldNotLoop(){
//        FormTreeTypeTest pkg = (FormTreeTypeTest) dict.onLoadPackage((Class)FormTreeTypeTest.class);
//        STypeComposite<? extends SIComposite> node = pkg.createTipoComposto("node");

        PackageBuilder                        pkg  = createTestPackage();
        STypeComposite<? extends SIComposite> node = pkg.createCompositeType("node");

        node.addFieldString("nome");
        node.addFieldString("type");
        node.addFieldListOf("child",node);


        //FIXME: It seems the isse reside on the setRoot
        SIComposite siComposite = (SIComposite) node.newInstance();
        siComposite.getField("nome").setValue("Me");
        siComposite.getField("type").setValue("the type");
    }

//    @MInfoTipo(nome = "NodeType", pacote = FormTreeTypeTest.class)
//    public static class NodeType extends STypeComposite<SIComposite> {
//
//        @Override
//        protected void onLoadType(TypeBuilder tb) {
//            super.onLoadType(tb);
//            addCampoString("nome");
//            addCampoString("type");
//            addCampoListaOf("child",NodeType.class);
//        }
//    }

}
