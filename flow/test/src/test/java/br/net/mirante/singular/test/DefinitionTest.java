package br.net.mirante.singular.test;

import br.net.mirante.singular.definicao.InstanciaDefinicaoComVariavel;
import br.net.mirante.singular.definicao.InstanciaPeticao;
import br.net.mirante.singular.flow.core.MBPM;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DefinitionTest extends TestSupport {

    @Before
    public void setup() {
        assertNotNull(mbpmBean);
        MBPM.setConf(mbpmBean);
    }

    @Test
    public void testeCriarInstanciaDefinicao() {

        InstanciaPeticao id = startInstance();
        InstanciaPeticao id2 = MBPM.findProcessInstance(id.getFullId());

        assertEquals("", id, id2);
    }

    @Test
    public void executeTransition() {
        InstanciaPeticao instanciaPeticao = startInstance();
        instanciaPeticao.executeTransition();
    }

    @Test
    public void testarUsoDeVariaveis() {
        InstanciaDefinicaoComVariavel id2 = new InstanciaDefinicaoComVariavel();
        id2.start();
        if (id2.isEnd()) {
            System.out.println("acabou");
        }
    }

    private InstanciaPeticao startInstance() {
        InstanciaPeticao id = new InstanciaPeticao();
        id.start();
        return id;
    }

}
