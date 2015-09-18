package br.net.mirante.singular.test;

import br.net.mirante.singular.InstanciaDefinicao;
import br.net.mirante.singular.InstanciaDefinicaoComVariavel;
import br.net.mirante.singular.flow.core.MBPM;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class DefinitionTest extends TestSupport {

    @Before
    public void setup() {
        assertNotNull(mbpmBean);
        MBPM.setConf(mbpmBean);
    }

    @Test
    public void testeCriarInstanciaDefinicao() {

        InstanciaDefinicao id = iniciarFluxo();
        InstanciaDefinicao id2 = MBPM.findProcessInstance(id.getFullId());
        assertNotNull(id2);
    }

    private InstanciaDefinicao iniciarFluxo() {
        InstanciaDefinicao id = new InstanciaDefinicao();
        id.start();
        return id;
    }

    @Test
    public void testarUsoDeVariaveis() {
        InstanciaDefinicaoComVariavel id2 = new InstanciaDefinicaoComVariavel();
        id2.start();
        if (id2.isEnd()) {
            System.out.println("acabou");
        }
    }

    @Test
    public void testarDefinicao() {
        InstanciaDefinicao instanciaDefinicao = iniciarFluxo();
        instanciaDefinicao.executeTransition();
    }

}
