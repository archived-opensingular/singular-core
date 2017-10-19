package org.opensingular.form.document;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeString;

@RunWith(Parameterized.class)
public class TestSDocument extends TestCaseForm {

    public TestSDocument(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test
    public void testCriacaoImplicitaPacoteCore() {
        SDictionary dictionary = createTestDictionary();
        SIString    instance1 = dictionary.newInstance(STypeString.class);
        assertChildren(instance1, 0);

        SIString instance2 = dictionary.newInstance(STypeString.class);
        assertChildren(instance2, 0);

        assertNotSame(instance1.getDocument(), instance2.getDocument());
    }

    @Test
    public void testCriacaoImplicitaPacoteNovo() {
        PackageBuilder    pb         = createTestPackage();
        STypeComposite<?> tipo       = pb.createType("nome", STypeComposite.class);

        SInstance instance1 = tipo.newInstance();
        assertChildren(instance1, 0);

        SInstance instance2 = tipo.newInstance();
        assertChildren(instance2, 0);

        assertNotSame(instance1.getDocument(), instance2.getDocument());
    }

    @Test
    public void testHerancaPelosSubcampos() {
        PackageBuilder pb = createTestPackage();
        STypeList<STypeComposite<SIComposite>, SIComposite> tipoLista    = pb.createListOfNewCompositeType("pessoas", "pessoa");
        STypeComposite<?>                                   tipoComposto = tipoLista.getElementsType();
        tipoComposto.addFieldString("nome");
        tipoComposto.addFieldListOf("dependentes", STypeString.class);

        SIList<SIComposite> pessoas = tipoLista.newInstance(SIComposite.class);
        assertChildren(pessoas, 0);

        SIComposite pessoa = pessoas.addNew();
        assertChildren(pessoa, 0);

        pessoa.setValue("nome", "Daniel");
        assertChildren(pessoa.getField("nome"), 0);

        SIString campo = pessoa.getFieldList("dependentes", SIString.class).addValue("Lara");
        assertChildren(campo, 0);
        assertChildren(pessoas, 4);
    }
    
}
