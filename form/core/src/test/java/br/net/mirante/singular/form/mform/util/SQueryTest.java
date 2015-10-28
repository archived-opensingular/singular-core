package br.net.mirante.singular.form.mform.util;

import org.junit.Test;

import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MILista;

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
        ((MIComposto) enderecos.get(0)).getCampo("numero").setValor(10);
        ((MIComposto) enderecos.get(1)).getCampo("numero").setValor(11);
        ((MIComposto) enderecos.get(2)).getCampo("numero").setValor(12);
        ((MIComposto) enderecos.get(3)).getCampo("numero").setValor(13);

        SQuery.$i(contato)
            .find(pacote.enderecoNumero)
            .each(it -> System.out.println(it.getValor()));
    }
}
