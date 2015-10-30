package br.net.mirante.singular.form.mform;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class TestMInstanciaDescendants {

    @Test
    public void test() {
        MDicionario dicionario = MDicionario.create();
        MPacoteTesteContatos pacote = dicionario.carregarPacote(MPacoteTesteContatos.class);

        MIComposto contato = pacote.contato.novaInstancia();

        contato.getDescendant(pacote.nome).setValor("Fulano");
        contato.getDescendant(pacote.sobrenome).setValor("de Tal");
        contato.getDescendant(pacote.enderecos).addNovo(novo -> {
            MIComposto end = pacote.endereco.castInstancia(novo);
            end.getDescendant(pacote.enderecoLogradouro).setValor("QI 25");
            end.getDescendant(pacote.enderecoComplemento).setValor("Bloco G");
            end.getDescendant(pacote.enderecoNumero).setValor(402);
            end.getDescendant(pacote.enderecoCidade).setValor("Guará II");
            end.getDescendant(pacote.enderecoEstado).setValor("DF");
        });
        contato.getDescendant(pacote.telefones).addValor("8888-8888");
        contato.getDescendant(pacote.telefones).addValor("9999-8888");
        contato.getDescendant(pacote.telefones).addValor("9999-9999");
        contato.getDescendant(pacote.emails).addValor("fulano@detal.com");

        Assert.assertEquals(
            Arrays.asList("8888-8888", "9999-8888", "9999-9999"),
            contato.listDescendantValues(pacote.telefones.getTipoElementos(), String.class));

        contato.debug();
    }

    @Test
    public void testList() {
        MDicionario dicionario = MDicionario.create();
        MPacoteTesteContatos pacote = dicionario.carregarPacote(MPacoteTesteContatos.class);

        MIComposto contato = pacote.contato.novaInstancia();

        for (int i = 0; i < 4; i++) {
            MIComposto endereco = (MIComposto) contato.getDescendant(pacote.enderecos).addNovo();
            endereco.getDescendant(pacote.enderecoNumero).setValor(Integer.valueOf(i));
        }

        Assert.assertEquals(
            Arrays.asList(0, 1, 2, 3),
            contato.listDescendantValues(pacote.enderecoNumero, Integer.class));

        for (MInstancia cid : contato.listDescendants(pacote.enderecoCidade))
            cid.setValor("C" + cid.getAncestor(pacote.endereco).getDescendant(pacote.enderecoNumero).getValor());

        Assert.assertEquals(
            Arrays.asList("C0", "C1", "C2", "C3"),
            contato.listDescendantValues(pacote.enderecoCidade, String.class));
    }
}
