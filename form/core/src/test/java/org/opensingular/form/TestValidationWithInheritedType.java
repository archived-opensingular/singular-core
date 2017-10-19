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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.validation.InstanceValidationContext;

@RunWith(Parameterized.class)
public class TestValidationWithInheritedType extends TestCaseForm {

    public TestValidationWithInheritedType(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test
    public void testOriginalType() {
        SPackageTest pckg = createTestDictionary().loadPackage(SPackageTest.class);
        new InstanceValidationContext().validateSingle(pckg.getDictionary().newInstance(A.class));
    }

    @Test
    public void testInheritedType() {
        SPackageTest pckg = createTestDictionary().loadPackage(SPackageTest.class);
        new InstanceValidationContext().validateSingle(pckg.getDictionary().newInstance(APlus.class));
    }

    @SInfoPackage(name = "br.com.spackagetest")
    public static class SPackageTest extends SPackage {

        @Override
        protected void onLoadPackage(PackageBuilder pb) {
            super.onLoadPackage(pb);
            pb.createType(A.class);
            pb.createType(APlus.class);
        }
    }


    @SInfoType(name = "A", spackage = SPackageTest.class, newable = true)
    public static class A extends STypeComposite<SIComposite> {

        public STypeString fieldOne;
        public STypeString fieldTwo;
        public STypeComposite<SIComposite> compositeOne;
        public STypeString compositeOneFieldOne;

        @Override
        protected void onLoadType(TypeBuilder tb) {
            fieldOne = addField("fieldOne", STypeString.class);
            fieldTwo = addField("fieldTwo", STypeString.class);
            compositeOne = addFieldComposite("compositeOne");
            compositeOneFieldOne = compositeOne.addField("compositeOneFieldOne", STypeString.class);

            this.addInstanceValidator(validatable -> {
//                if ("x".equals(validatable.getInstance().getField(fieldOne.getNameSimple()).getValue())) {  //funciona utilizando o nome
                if ("x".equals(validatable.getInstance().getField(fieldOne).getValue())) { //não funciona utilizando o tipo
                    validatable.error("valor igual a x");
                }
            });
        }

    }

    @SInfoType(name = "APlus", spackage = SPackageTest.class, newable = true)
    public static class APlus extends A {

        public STypeString fieldThree;

        @Override
        protected void onLoadType(TypeBuilder tb) {
            fieldOne.asAtr().visible(true);
            fieldThree = addField("fieldThree", STypeString.class);
        }

    }
}