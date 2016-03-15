package br.net.mirante.singular.form.mform;

import org.junit.Assert;
import org.junit.Test;

public class TestMInstances {

    @Test
    public void test() {
        SDictionary dicionario = SDictionary.create();
        SPackageTesteContatos pacote = dicionario.loadPackage(SPackageTesteContatos.class);

        SIComposite contato = pacote.contato.newInstance();

        SInstances.getDescendant(contato, pacote.nome).getValue();
        SInstances.listDescendants(contato, pacote.enderecoEstado).stream();

        Assert.assertTrue(SInstances.findCommonAncestor(contato, pacote.contato)
            .filter(it -> it.getType() == pacote.contato)
            .isPresent());
    }
}
