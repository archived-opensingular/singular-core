package br.net.mirante.singular.form.document;

import br.net.mirante.singular.form.*;
import org.opensingular.singular.form.PackageBuilder;
import org.opensingular.singular.form.SDictionary;
import org.opensingular.singular.form.SIComposite;
import org.opensingular.singular.form.SIList;
import org.opensingular.singular.form.SInstance;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.STypeList;
import org.opensingular.singular.form.type.core.SIString;
import org.opensingular.singular.form.type.core.STypeString;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class TestSDocument extends TestCaseForm {

    public TestSDocument(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test
    public void testCriacaoImplicitaPacoteCore() {
        SDictionary dicionario = createTestDictionary();
        SIString    instancia1 = dicionario.newInstance(STypeString.class);
        assertFilhos(instancia1, 0);

        SIString instancia2 = dicionario.newInstance(STypeString.class);
        assertFilhos(instancia2, 0);

        assertNotSame(instancia1.getDocument(), instancia2.getDocument());
    }

    @Test
    public void testCriacaoImplicitaPacoteNovo() {
        PackageBuilder    pb         = createTestDictionary().createNewPackage("teste");
        STypeComposite<?> tipo       = pb.createType("nome", STypeComposite.class);

        SInstance instancia1 = tipo.newInstance();
        assertFilhos(instancia1, 0);

        SInstance instancia2 = tipo.newInstance();
        assertFilhos(instancia2, 0);

        assertNotSame(instancia1.getDocument(), instancia2.getDocument());
    }

    @Test
    public void testHerancaPelosSubcampos() {
        PackageBuilder pb = createTestDictionary().createNewPackage("teste");
        STypeList<STypeComposite<SIComposite>, SIComposite> tipoLista    = pb.createListOfNewCompositeType("pessoas", "pessoa");
        STypeComposite<?>                                   tipoComposto = tipoLista.getElementsType();
        tipoComposto.addFieldString("nome");
        tipoComposto.addFieldListOf("dependentes", STypeString.class);

        SIList<SIComposite> pessoas = tipoLista.newInstance(SIComposite.class);
        assertFilhos(pessoas, 0);

        SIComposite pessoa = pessoas.addNew();
        assertFilhos(pessoa, 0);

        pessoa.setValue("nome", "Daniel");
        assertFilhos(pessoa.getField("nome"), 0);

        SIString campo = pessoa.getFieldList("dependentes", SIString.class).addValue("Lara");
        assertFilhos(campo, 0);
        assertFilhos(pessoas, 4);
    }
    
}
