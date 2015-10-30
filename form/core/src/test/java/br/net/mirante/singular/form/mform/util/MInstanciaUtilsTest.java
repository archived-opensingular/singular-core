package br.net.mirante.singular.form.mform.util;

import org.junit.Test;

import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MIComposto;

public class MInstanciaUtilsTest {

    @Test
    public void test() {
        MDicionario dicionario = MDicionario.create();
        MPacoteSQuery pacote = dicionario.carregarPacote(MPacoteSQuery.class);

        MIComposto contato = pacote.contato.novaInstancia();

        contato.getDescendant(pacote.nome).getValor();
    }

}
