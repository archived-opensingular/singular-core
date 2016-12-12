package org.opensingular.form;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.validation.InstanceValidationContext;

@Ignore
@RunWith(Parameterized.class)
public class TestValidationWithInheritedType extends TestCaseForm {

    public TestValidationWithInheritedType(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test
    public void testOriginalType() {
        SPackageTest pckg = createTestDictionary().loadPackage(SPackageTest.class);
        try {
            new InstanceValidationContext().validateSingle(pckg.getDictionary().newInstance(A.class));
        } catch (Exception ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void testInheritedType() {
        SPackageTest pckg = createTestDictionary().loadPackage(SPackageTest.class);
        try {
            new InstanceValidationContext().validateSingle(pckg.getDictionary().newInstance(APlus.class));
        } catch (Exception ex) {
            Assert.fail(ex.getMessage());
        }
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

        @Override
        protected void onLoadType(TypeBuilder tb) {
            super.onLoadType(tb);

            fieldOne = addField("fieldOne", STypeString.class);
            fieldTwo = addField("fieldTwo", STypeString.class);

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
            super.onLoadType(tb);
            fieldThree = addField("fieldThree", STypeString.class);
        }

    }
}