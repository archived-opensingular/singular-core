package br.net.mirante.singular.form.mform;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import br.net.mirante.singular.form.mform.core.SIString;

public class TestMInstanciaDescendants {

    @Test
    public void test() {
        SDictionary dicionario = SDictionary.create();
        SPackageTesteContatos pacote = dicionario.carregarPacote(SPackageTesteContatos.class);

        SIComposite contato = pacote.contato.novaInstancia();

        contato.getDescendant(pacote.nome).setValor("Fulano");
        contato.getDescendant(pacote.sobrenome).setValor("de Tal");

        SIComposite endereco = contato.getDescendant(pacote.enderecos).addNovo();
        endereco.getDescendant(pacote.enderecoLogradouro).setValor("QI 25");
        endereco.getDescendant(pacote.enderecoComplemento).setValor("Bloco G");
        endereco.getDescendant(pacote.enderecoNumero).setValor(402);
        endereco.getDescendant(pacote.enderecoCidade).setValor("Guará II");
        endereco.getDescendant(pacote.enderecoEstado).setValor("DF");

        SList<SIString> telefones = contato.getDescendant(pacote.telefones);
        telefones.addValor("8888-8888");
        telefones.addValor("9999-8888");
        telefones.addValor("9999-9999");

        SList<SIString> emails = contato.getDescendant(pacote.emails);
        emails.addValor("fulano@detal.com");

        Assert.assertEquals(
            Arrays.asList("8888-8888", "9999-8888", "9999-9999"),
            contato.listDescendantValues(pacote.telefones.getTipoElementos(), String.class));

//        contato.debug();
    }

    @Test
    public void testList() {
        SDictionary dicionario = SDictionary.create();
        SPackageTesteContatos pacote = dicionario.carregarPacote(SPackageTesteContatos.class);

        SIComposite contato = pacote.contato.novaInstancia();

        for (int i = 0; i < 4; i++) {
            SIComposite endereco = (SIComposite) contato.getDescendant(pacote.enderecos).addNovo();
            endereco.getDescendant(pacote.enderecoNumero).setValor(Integer.valueOf(i));
        }

        Assert.assertEquals(
            Arrays.asList(0, 1, 2, 3),
            contato.listDescendantValues(pacote.enderecoNumero, Integer.class));

        for (SInstance cid : contato.listDescendants(pacote.enderecoCidade))
            cid.setValor("C" + cid.getAncestor(pacote.endereco).getDescendant(pacote.enderecoNumero).getValor());

        Assert.assertEquals(
            Arrays.asList("C0", "C1", "C2", "C3"),
            contato.listDescendantValues(pacote.enderecoCidade, String.class));
    }

    @Test
    public void testIncorrectAncestor() {
        SDictionary dicionario = SDictionary.create();
        SPackageTesteContatos pacote = dicionario.carregarPacote(SPackageTesteContatos.class);
        SIComposite contato = pacote.contato.novaInstancia();

        Assert.assertFalse(contato.getDescendant(pacote.telefones).findAncestor(pacote.enderecos).isPresent());
        Assert.assertFalse(contato.getDescendant(pacote.enderecos).findAncestor(pacote.endereco).isPresent());
    }

    @Test
    public void testIncorrectDescendant() {
        SDictionary dic = SDictionary.create();
        SPackageTesteContatos pac = dic.carregarPacote(SPackageTesteContatos.class);
        SIComposite contato = pac.contato.novaInstancia();

        Assert.assertFalse(contato.getDescendant(pac.telefones).findDescendant(pac.endereco).isPresent());
        Assert.assertFalse(contato.getDescendant(pac.enderecos).findDescendant(pac.telefones).isPresent());

        contato.getDescendant(pac.enderecos).addNovo();
        Assert.assertFalse(contato.getDescendant(pac.endereco).findDescendant(pac.emails).isPresent());
    }

    @Test
    public void testStream() {
        SDictionary dicionario = SDictionary.create();
        SPackageTesteContatos pacote = dicionario.carregarPacote(SPackageTesteContatos.class);

        Set<SType<?>> tipos = new HashSet<>(Arrays.asList(
            pacote.contato,
            pacote.identificacao,
            pacote.nome,
            pacote.sobrenome,
            pacote.enderecos,
            pacote.endereco,
            pacote.enderecoLogradouro,
            pacote.enderecoNumero,
            pacote.enderecoComplemento,
            pacote.enderecoCidade,
            pacote.enderecoEstado,
            pacote.telefones,
            pacote.telefone,
            pacote.emails,
            pacote.email));

        SIComposite contato = pacote.contato.novaInstancia();
        contato.getDescendant(pacote.enderecos).addNovo();
        contato.getDescendant(pacote.telefones).addNovo();
        contato.getDescendant(pacote.emails).addNovo();

        contato.streamDescendants(true)
            .forEachOrdered(instancia -> Assert.assertTrue(
                "Tipo não encontrado: " + instancia.getMTipo(),
                tipos.remove(instancia.getMTipo())));

        Assert.assertTrue("Não percorreu o(s) tipo(s) " + tipos, tipos.isEmpty());
    }
}
