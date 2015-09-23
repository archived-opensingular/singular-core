package br.net.mirante.singular.test;

import org.junit.Test;

import br.net.mirante.singular.definicao.InstanciaDefinicaoComVariavel;

public class InstanciaDefinicaoComVariavelTest extends TestSupport {

    @Test
    public void testarUsoDeVariaveis() {
        InstanciaDefinicaoComVariavel id2 = new InstanciaDefinicaoComVariavel();
        id2.start();
        if (id2.isEnd()) {
            System.out.println("acabou");
        }
    }
}
