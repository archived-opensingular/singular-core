package br.net.mirante.singular.form.document;

import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.type.core.SIString;
import br.net.mirante.singular.form.type.core.STypeString;

public class TestSDocument extends TestCaseForm {

    public void testCriacaoImplicitaPacoteCore() {
        SDictionary dicionario = SDictionary.create();
        SIString    instancia1 = dicionario.newInstance(STypeString.class);
        assertFilhos(instancia1, 0);

        SIString instancia2 = dicionario.newInstance(STypeString.class);
        assertFilhos(instancia2, 0);

        assertNotSame(instancia1.getDocument(), instancia2.getDocument());
    }

    public void testCriacaoImplicitaPacoteNovo() {
        SDictionary       dicionario = SDictionary.create();
        PackageBuilder    pb         = dicionario.createNewPackage("teste");
        STypeComposite<?> tipo       = pb.createType("nome", STypeComposite.class);

        SInstance instancia1 = tipo.newInstance();
        assertFilhos(instancia1, 0);

        SInstance instancia2 = tipo.newInstance();
        assertFilhos(instancia2, 0);

        assertNotSame(instancia1.getDocument(), instancia2.getDocument());
    }

    public void testHerancaPelosSubcampos() {
        SDictionary                                         dicionario   = SDictionary.create();
        PackageBuilder                                      pb           = dicionario.createNewPackage("teste");
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
