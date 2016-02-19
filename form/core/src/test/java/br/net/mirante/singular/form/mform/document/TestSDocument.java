package br.net.mirante.singular.form.mform.document;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeString;

public class TestSDocument extends TestCaseForm {

    public void testCriacaoImplicitaPacoteCore() {
        SDictionary dicionario = SDictionary.create();
        SIString instancia1 = dicionario.newInstance(STypeString.class);
        assertFilhos(instancia1, 0);

        SIString instancia2 = dicionario.newInstance(STypeString.class);
        assertFilhos(instancia2, 0);

        assertNotSame(instancia1.getDocument(), instancia2.getDocument());
    }

    public void testCriacaoImplicitaPacoteNovo() {
        SDictionary dicionario = SDictionary.create();
        PackageBuilder pb = dicionario.createNewPackage("teste");
        STypeComposite<?> tipo = pb.createTipo("nome", STypeComposite.class);

        SInstance instancia1 = tipo.novaInstancia();
        assertFilhos(instancia1, 0);

        SInstance instancia2 = tipo.novaInstancia();
        assertFilhos(instancia2, 0);

        assertNotSame(instancia1.getDocument(), instancia2.getDocument());
    }

    public void testHerancaPelosSubcampos() {
        SDictionary dicionario = SDictionary.create();
        PackageBuilder pb = dicionario.createNewPackage("teste");
        STypeLista<STypeComposite<SIComposite>, SIComposite> tipoLista = pb.createTipoListaOfNovoTipoComposto("pessoas", "pessoa");
        STypeComposite<?> tipoComposto = tipoLista.getTipoElementos();
        tipoComposto.addCampoString("nome");
        tipoComposto.addCampoListaOf("dependentes", STypeString.class);

        SList<SIComposite> pessoas = tipoLista.novaInstancia(SIComposite.class);
        assertFilhos(pessoas, 0);

        SIComposite pessoa = pessoas.addNovo();
        assertFilhos(pessoa, 0);

        pessoa.setValor("nome", "Daniel");
        assertFilhos(pessoa.getCampo("nome"), 0);

        SIString campo = pessoa.getFieldList("dependentes", SIString.class).addValor("Lara");
        assertFilhos(campo, 0);
        assertFilhos(pessoas, 4);
    }
    
}
