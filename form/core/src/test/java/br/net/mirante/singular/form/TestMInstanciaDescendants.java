package br.net.mirante.singular.form;

import br.net.mirante.singular.form.type.core.SIString;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TestMInstanciaDescendants {

    @Test
    public void test() {
        SDictionary dicionario = SDictionary.create();
        SPackageTesteContatos pacote = dicionario.loadPackage(SPackageTesteContatos.class);

        SIComposite contato = pacote.contato.newInstance();

        contato.getDescendant(pacote.nome).setValue("Fulano");
        contato.getDescendant(pacote.sobrenome).setValue("de Tal");

        SIComposite endereco = contato.getDescendant(pacote.enderecos).addNew();
        endereco.getDescendant(pacote.enderecoLogradouro).setValue("QI 25");
        endereco.getDescendant(pacote.enderecoComplemento).setValue("Bloco G");
        endereco.getDescendant(pacote.enderecoNumero).setValue(402);
        endereco.getDescendant(pacote.enderecoCidade).setValue("Guará II");
        endereco.getDescendant(pacote.enderecoEstado).setValue("DF");

        SIList<SIString> telefones = contato.getDescendant(pacote.telefones);
        telefones.addValue("8888-8888");
        telefones.addValue("9999-8888");
        telefones.addValue("9999-9999");

        SIList<SIString> emails = contato.getDescendant(pacote.emails);
        emails.addValue("fulano@detal.com");

        Assert.assertEquals(
            Arrays.asList("8888-8888", "9999-8888", "9999-9999"),
            contato.listDescendantValues(pacote.telefones.getElementsType(), String.class));

//        contato.debug();
    }

    @Test
    public void testList() {
        SDictionary dicionario = SDictionary.create();
        SPackageTesteContatos pacote = dicionario.loadPackage(SPackageTesteContatos.class);

        SIComposite contato = pacote.contato.newInstance();

        for (int i = 0; i < 4; i++) {
            SIComposite endereco = (SIComposite) contato.getDescendant(pacote.enderecos).addNew();
            endereco.getDescendant(pacote.enderecoNumero).setValue(Integer.valueOf(i));
        }

        Assert.assertEquals(
            Arrays.asList(0, 1, 2, 3),
            contato.listDescendantValues(pacote.enderecoNumero, Integer.class));

        for (SInstance cid : contato.listDescendants(pacote.enderecoCidade))
            cid.setValue("C" + cid.getAncestor(pacote.endereco).getDescendant(pacote.enderecoNumero).getValue());

        Assert.assertEquals(
            Arrays.asList("C0", "C1", "C2", "C3"),
            contato.listDescendantValues(pacote.enderecoCidade, String.class));
    }

    @Test
    public void testIncorrectAncestor() {
        SDictionary dicionario = SDictionary.create();
        SPackageTesteContatos pacote = dicionario.loadPackage(SPackageTesteContatos.class);
        SIComposite contato = pacote.contato.newInstance();

        Assert.assertFalse(contato.getDescendant(pacote.telefones).findAncestor(pacote.enderecos).isPresent());
        Assert.assertFalse(contato.getDescendant(pacote.enderecos).findAncestor(pacote.endereco).isPresent());
    }

    @Test
    public void testIncorrectDescendant() {
        SDictionary dic = SDictionary.create();
        SPackageTesteContatos pac = dic.loadPackage(SPackageTesteContatos.class);
        SIComposite contato = pac.contato.newInstance();

        Assert.assertFalse(contato.getDescendant(pac.telefones).findDescendant(pac.endereco).isPresent());
        Assert.assertFalse(contato.getDescendant(pac.enderecos).findDescendant(pac.telefones).isPresent());

        contato.getDescendant(pac.enderecos).addNew();
        Assert.assertFalse(contato.getDescendant(pac.endereco).findDescendant(pac.emails).isPresent());
    }

    @Test
    public void testStream() {
        SDictionary dicionario = SDictionary.create();
        SPackageTesteContatos pacote = dicionario.loadPackage(SPackageTesteContatos.class);

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

        SIComposite contato = pacote.contato.newInstance();
        contato.getDescendant(pacote.enderecos).addNew();
        contato.getDescendant(pacote.telefones).addNew();
        contato.getDescendant(pacote.emails).addNew();

        contato.streamDescendants(true)
            .forEachOrdered(instancia -> Assert.assertTrue(
                "Tipo não encontrado: " + instancia.getType(),
                tipos.remove(instancia.getType())));

        Assert.assertTrue("Não percorreu o(s) tipo(s) " + tipos, tipos.isEmpty());
    }
}
