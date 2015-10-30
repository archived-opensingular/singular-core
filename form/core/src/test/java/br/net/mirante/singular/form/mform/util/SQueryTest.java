package br.net.mirante.singular.form.mform.util;

import static br.net.mirante.singular.form.mform.util.SQuery.*;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MIComposto;

public class SQueryTest {

    @Test
    public void test() {
        MDicionario dicionario = MDicionario.create();
        MPacoteSQuery pacote = dicionario.carregarPacote(MPacoteSQuery.class);

        MIComposto contato = pacote.contato.novaInstancia();

        $(contato)
            .find(pacote.nome).val("Fulano").end()
            .find(pacote.sobrenome).val("de Tal").end()
            .find(pacote.enderecos).addNew(end -> $(end)
                .find(pacote.enderecoLogradouro).val("QI 25").end()
                .find(pacote.enderecoComplemento).val("Bloco G").end()
                .find(pacote.enderecoNumero).val(402).end()
                .find(pacote.enderecoCidade).val("Guará II").end()
                .find(pacote.enderecoEstado).val("DF").end())
            .end()
            .find(pacote.telefones).addVal("8888-8888").addVal("9999-8888").addVal("9999-9999").end()
            .find(pacote.emails).addVal("fulano@detal.com").end();

        System.out.println($(contato).find(pacote.telefones).children().val());

        contato.debug();
    }

    @Test
    public void testList() {
        MDicionario dicionario = MDicionario.create();
        MPacoteSQuery pacote = dicionario.carregarPacote(MPacoteSQuery.class);

        MIComposto contato = pacote.contato.novaInstancia();

        $(contato).find(pacote.enderecos)
            .each(it -> it.addNovo())
            .each(it -> it.addNovo())
            .each(it -> it.addNovo())
            .each(it -> it.addNovo())
            .each(it -> $(it)
                .find(pacote.enderecoNumero)
                .each((num, idx) -> num.setValor(idx)));

        Assert.assertEquals(
            Arrays.asList(0, 1, 2, 3),
            $(contato).find(pacote.enderecoNumero).list(it -> it.getValor().intValue()));

        $(contato).find(pacote.enderecoCidade)
            .each(cid -> cid.setValor("C" + $(cid).parent().find(pacote.enderecoNumero).val(Integer.class)));

        Assert.assertEquals(
            Arrays.asList("C0", "C1", "C2", "C3"),
            $(contato).find(pacote.enderecoCidade).list(it -> it.getValor()));
    }
}
