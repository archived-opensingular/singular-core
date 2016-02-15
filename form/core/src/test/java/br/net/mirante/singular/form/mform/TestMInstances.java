package br.net.mirante.singular.form.mform;

import org.junit.Assert;
import org.junit.Test;

public class TestMInstances {

    @Test
    public void test() {
        SDictionary dicionario = SDictionary.create();
        SPackageTesteContatos pacote = dicionario.loadPackage(SPackageTesteContatos.class);

        SIComposite contato = pacote.contato.novaInstancia();

        MInstances.getDescendant(contato, pacote.nome).getValue();
        MInstances.listDescendants(contato, pacote.enderecoEstado).stream();

        Assert.assertTrue(MInstances.findCommonAncestor(contato, pacote.contato)
            .filter(it -> it.getMTipo() == pacote.contato)
            .isPresent());
    }
}
