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

    /**
     * Método que faz com que seja aberto o gerenciador de BD do HSQL e pausa a execução dos testes aguardando a tecla
     * ENTER no console.
     */
    public void inspecionarDB() {
        org.hsqldb.util.DatabaseManagerSwing.main(new String[]{
                "--url", "jdbc:hsqldb:file:singulardb", "--noexit"
        });
//        aguardarEnter();
    }

    private void aguardarEnter() {
        System.out.println("Aperte ENTER para continuar...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }
}
