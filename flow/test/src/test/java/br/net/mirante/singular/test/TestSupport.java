package br.net.mirante.singular.test;

import br.net.mirante.singular.TestDAO;
import br.net.mirante.singular.TestMBPMBean;
import org.hibernate.SessionFactory;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Scanner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
@Transactional
@Rollback(false)
public abstract class TestSupport {

    @Inject
    protected TestMBPMBean mbpmBean;

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
