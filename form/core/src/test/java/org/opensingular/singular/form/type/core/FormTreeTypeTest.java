package org.opensingular.singular.form.type.core;

import org.opensingular.singular.form.*;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class FormTreeTypeTest extends TestCaseForm {

    public FormTreeTypeTest(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test public void shouldNotLoop(){
//        FormTreeTypeTest pkg = (FormTreeTypeTest) dict.onLoadPackage((Class)FormTreeTypeTest.class);
//        STypeComposite<? extends SIComposite> node = pkg.createTipoComposto("node");

        PackageBuilder                        pkg  = createTestDictionary().createNewPackage("pkg");
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
