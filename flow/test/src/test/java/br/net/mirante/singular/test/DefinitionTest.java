package br.net.mirante.singular.test;

import br.net.mirante.singular.Definicao;
import br.net.mirante.singular.flow.core.MBPM;
import br.net.mirante.singular.service.MBPMBean;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class DefinitionTest {

    @Inject
    private MBPMBean mbpmBean;

    @Before
    public void setup() {
        assertNotNull(mbpmBean);
        MBPM.setConf(mbpmBean);
    }

    @Test
    public void teste() {



        System.out.println("legal");
    }

}
