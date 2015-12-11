package br.net.mirante.singular.form.mform.document;

import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoLista;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.TestCaseForm;
import br.net.mirante.singular.form.mform.core.MIString;
import br.net.mirante.singular.form.mform.core.MTipoString;

public class TestSDocument extends TestCaseForm {

    public void testCriacaoImplicitaPacoteCore() {
        MDicionario dicionario = MDicionario.create();
        MIString instancia1 = dicionario.novaInstancia(MTipoString.class);
        assertFilhos(instancia1, 0);

        MIString instancia2 = dicionario.novaInstancia(MTipoString.class);
        assertFilhos(instancia2, 0);

        assertNotSame(instancia1.getDocument(), instancia2.getDocument());
    }

    public void testCriacaoImplicitaPacoteNovo() {
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");
        MTipoComposto<?> tipo = pb.createTipo("nome", MTipoComposto.class);

        MInstancia instancia1 = tipo.novaInstancia();
        assertFilhos(instancia1, 0);

        MInstancia instancia2 = tipo.novaInstancia();
        assertFilhos(instancia2, 0);

        assertNotSame(instancia1.getDocument(), instancia2.getDocument());
    }

    public void testHerancaPelosSubcampos() {
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");
        MTipoLista<MTipoComposto<MIComposto>, MIComposto> tipoLista = pb.createTipoListaOfNovoTipoComposto("pessoas", "pessoa");
        MTipoComposto<?> tipoComposto = tipoLista.getTipoElementos();
        tipoComposto.addCampoString("nome");
        tipoComposto.addCampoListaOf("dependentes", MTipoString.class);

        MILista<MIComposto> pessoas = tipoLista.novaInstancia(MIComposto.class);
        assertFilhos(pessoas, 0);

        MIComposto pessoa = pessoas.addNovo();
        assertFilhos(pessoa, 0);

        pessoa.setValor("nome", "Daniel");
        assertFilhos(pessoa.getCampo("nome"), 0);

        MIString campo = pessoa.getFieldList("dependentes", MIString.class).addValor("Lara");
        assertFilhos(campo, 0);
        assertFilhos(pessoas, 4);
    }
    
}
