package br.net.mirante.singular.form.mform.util;

import static br.net.mirante.singular.form.mform.util.SQuery.*;

import org.junit.Test;

import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.core.MIString;

public class SQueryTest {

    @Test
    public void testRaiz() {
        MDicionario dicionario = MDicionario.create();
        MPacoteSQuery pacote = dicionario.carregarPacote(MPacoteSQuery.class);

        MIComposto contato = pacote.contato.novaInstancia();
        MILista<?> enderecos = contato.getFieldList(pacote.enderecos.getNomeSimples());
        enderecos.addNovo();
        enderecos.addNovo();
        enderecos.addNovo();
        enderecos.addNovo();
        ((MIComposto) enderecos.get(0)).getCampo("numero").setValor(0);
        ((MIComposto) enderecos.get(1)).getCampo("numero").setValor(1);
        ((MIComposto) enderecos.get(2)).getCampo("numero").setValor(2);
        ((MIComposto) enderecos.get(3)).getCampo("numero").setValor(3);

        $i(contato)
            .find(pacote.endereco)
            .each(it -> it.getCampo("cidade").setValor("C" + it.getCampo("numero").getValor()))
            .end()
            .find(pacote.enderecoNumero)
            .each(it -> System.out.println(it.getValor()))
            .end()
            .find(pacote.enderecoCidade)
            .each(it -> System.out.println(it.getValor()))
            .end()
            .find(MIString.class)
            .each(it -> System.out.println(it.getNome() + " = " + it.getValor()));
    }
}
