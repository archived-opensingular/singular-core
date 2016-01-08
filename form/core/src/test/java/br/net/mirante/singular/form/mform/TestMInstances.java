package br.net.mirante.singular.form.mform;

import org.junit.Assert;
import org.junit.Test;

public class TestMInstances {

    @Test
    public void test() {
        MDicionario dicionario = MDicionario.create();
        MPacoteTesteContatos pacote = dicionario.carregarPacote(MPacoteTesteContatos.class);

        MIComposto contato = pacote.contato.novaInstancia();

        MInstances.getDescendant(contato, pacote.nome).getValor();
        MInstances.listDescendants(contato, pacote.enderecoEstado).stream();

        Assert.assertTrue(MInstances.findCommonAncestor(contato, pacote.contato)
            .filter(it -> it.getMTipo() == pacote.contato)
            .isPresent());
    }
}
