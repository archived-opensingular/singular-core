package br.net.mirante.singular.test;

import org.junit.Before;
import org.junit.Test;

import br.net.mirante.singular.definicao.InstanciaDefinicaoComVariavel;
import br.net.mirante.singular.flow.core.MBPM;

import static org.junit.Assert.assertNotNull;

public class InstanciaDefinicaoComVariavelTest extends TestSupport {

    @Before
    public void setup() {
        assertNotNull(mbpmBean);
        MBPM.setConf(mbpmBean);
    }

    @Test
    public void testarUsoDeVariaveis() {
        InstanciaDefinicaoComVariavel id2 = new InstanciaDefinicaoComVariavel();
        id2.start();
        if (id2.isEnd()) {
            System.out.println("acabou");
        }
    }
}
