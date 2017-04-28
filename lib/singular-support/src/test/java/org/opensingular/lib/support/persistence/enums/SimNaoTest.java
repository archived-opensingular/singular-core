package org.opensingular.lib.support.persistence.enums;

import org.junit.Assert;
import org.junit.Test;

public class SimNaoTest {

    @Test
    public void simNaoTest(){
        SimNao sim = SimNao.SIM;
        SimNao nao = SimNao.NAO;

        Assert.assertEquals("S", sim.getCodigo());
        Assert.assertEquals("N", nao.getCodigo());

        Assert.assertEquals("Sim", sim.getDescricao());
        Assert.assertEquals("Não", nao.getDescricao());

        Assert.assertNull(sim.valueOfEnum("talvez"));
        Assert.assertEquals(SimNao.SIM, sim.valueOfEnum("S"));
    }
}
