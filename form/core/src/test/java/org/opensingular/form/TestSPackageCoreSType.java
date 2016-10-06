package org.opensingular.form;

import org.opensingular.form.type.core.STypeString;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Testa funcionaldiades básicas de SType.
 *
 * @author Daniel C. Bordin
 */
@RunWith(Parameterized.class)
public class TestSPackageCoreSType extends TestCaseForm {

    public TestSPackageCoreSType(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test
    public void testGetDependenteTypeIsMuttable() {
        PackageBuilder              pb = createTestDictionary().createNewPackage("teste");
        STypeComposite<SIComposite> typeRec1 = pb.createCompositeType("Rec1");
        STypeString field11 = typeRec1.addFieldString("field1");
        STypeString field12 = typeRec1.addFieldString("field2");
        field11.addDependentType(field12);

        assertException(() -> field11.getDependentTypes().add(field12), UnsupportedOperationException.class);
        assertException(() -> field12.getDependentTypes().add(field12), UnsupportedOperationException.class);
    }

    @Test
    public void testDependsOn() {
        PackageBuilder pb = createTestDictionary().createNewPackage("teste");

        STypeComposite<SIComposite> typeRec1 = pb.createCompositeType("Rec1");
        STypeString field11 = typeRec1.addFieldString("field1");
        STypeString field12 = typeRec1.addFieldString("field2");
        STypeString field13 = typeRec1.addFieldString("field3");

        field11.addDependentType(field12);
        field11.addDependentType(field13);
        field12.addDependentType(field13);

        assertType(field11).dependentsTypesAre(field12, field13);
        assertType(field12).dependentsTypesAre(field13);
        assertType(field13).dependentsTypesAre();
    }

    @Test
    public void testDependsOnWithExtension() {
        PackageBuilder pb = createTestDictionary().createNewPackage("teste");

        STypeComposite<SIComposite> typeRec1 = pb.createCompositeType("Rec1");
        STypeString field11 = typeRec1.addFieldString("field1");
        STypeString field12 = typeRec1.addFieldString("field2");
        STypeString field13 = typeRec1.addFieldString("field3");

        field11.addDependentType(field12);
        field11.addDependentType(field13);
        field12.addDependentType(field13);

        STypeComposite<SIComposite> typeRec2 = pb.createType("Rec2", typeRec1);
        STypeString field21 = (STypeString) typeRec2.getField("field1");
        STypeString field22 = (STypeString) typeRec2.getField("field2");
        STypeString field23 = (STypeString) typeRec2.getField("field3");
        STypeString field24 = typeRec2.addFieldString("field4");
        STypeString field25 = typeRec2.addFieldString("field5");

        assertType(field11).dependentsTypesAre(field12, field13);
        assertType(field12).dependentsTypesAre(field13);
        assertType(field13).dependentsTypesAre();

        assertType(field21).dependentsTypesAre(field12, field13).isDependentType(field22, field23);
        assertType(field22).dependentsTypesAre(field13).isDependentType(field23);
        assertType(field23).dependentsTypesAre();
        assertType(field24).dependentsTypesAre();
        assertType(field25).dependentsTypesAre();

        //3 rodada
        field21.addDependentType(field22);
        field21.addDependentType(field24);
        field11.addDependentType(field22);
        field11.addDependentType(field25);
        field24.addDependentType(field25);

        assertType(field11).dependentsTypesAre(field12, field13, field25);
        assertType(field12).dependentsTypesAre(field13);
        assertType(field13).dependentsTypesAre();

        assertType(field21).dependentsTypesAre(field12, field13, field24, field25).isDependentType(field22, field23);
        assertType(field22).dependentsTypesAre(field13).isDependentType(field23);
        assertType(field23).dependentsTypesAre();
        assertType(field24).dependentsTypesAre(field25);
        assertType(field25).dependentsTypesAre();
    }

    @Test
    public void testDependsOnReferenciaCircular() {
        PackageBuilder pb = createTestDictionary().createNewPackage("teste");

        STypeComposite<SIComposite> typeRec1 = pb.createCompositeType("Rec1");
        STypeString field11 = typeRec1.addFieldString("field1");
        STypeString field12 = typeRec1.addFieldString("field2");
        STypeString field13 = typeRec1.addFieldString("field3");

        field11.addDependentType(field12);
        assertException(() -> field12.addDependentType(field11), SingularFormException.class, "Referência circular");
        field12.addDependentType(field13);
        assertException(() -> field13.addDependentType(field11), SingularFormException.class, "Referência circular");

        assertType(field11).dependentsTypesAre(field12);
        assertType(field12).dependentsTypesAre(field13);
        assertType(field13).dependentsTypesAre();

        STypeComposite<SIComposite> typeRec2 = pb.createType("Rec2", typeRec1);
        STypeString field21 = (STypeString) typeRec2.getField("field1");
        STypeString field22 = (STypeString) typeRec2.getField("field2");
        STypeString field23 = (STypeString) typeRec2.getField("field3");
        STypeString field24 = typeRec2.addFieldString("field4");
        STypeString field25 = typeRec2.addFieldString("field5");

        assertException(() -> field22.addDependentType(field21), SingularFormException.class, "Referência circular");
        assertException(() -> field23.addDependentType(field21), SingularFormException.class, "Referência circular");

        field23.addDependentType(field24);
        assertException(() -> field24.addDependentType(field23), SingularFormException.class, "Referência circular");

        //Os dois casos abaixo deveriam ser detectados, mas a lógica atual não consegue
        //Descomentar se no futuro for resolvido
        //assertException(() -> field24.addDependentType(field21), SingularFormException.class, "Referência circular");
        //assertException(() -> field24.addDependentType(field11), SingularFormException.class, "Referência circular");

        field21.addDependentType(field25);

        assertType(field11).dependentsTypesAre(field12).isNotDependentType(field25);
        assertType(field21).dependentsTypesAre(field12, field25).isDependentType(field22);
        assertType(field22).dependentsTypesAre(field13).isDependentType(field23);
        assertType(field23).dependentsTypesAre(field24);
        assertType(field24).dependentsTypesAre();
        assertType(field25).dependentsTypesAre();
    }
}
