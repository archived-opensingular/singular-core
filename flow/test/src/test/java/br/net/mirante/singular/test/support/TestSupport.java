package br.net.mirante.singular.test.support;

import br.net.mirante.singular.TestDAO;
import br.net.mirante.singular.flow.core.SingularFlowConfigurationBean;
import org.hibernate.SessionFactory;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Scanner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Transactional
@Rollback(value = false)
public abstract class TestSupport {

    @Inject
    protected SingularFlowConfigurationBean mbpmBean;

    @Inject
    protected TestDAO testDAO;

    @Inject
    protected SessionFactory sessionFactory;

}
