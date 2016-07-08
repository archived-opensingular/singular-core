package br.net.mirante.singular.test.oracle;

import org.junit.BeforeClass;
import org.springframework.test.context.ActiveProfiles;

import br.net.mirante.singular.commons.base.SingularPropertiesImpl;
import br.net.mirante.singular.test.PersistenceTest;


@ActiveProfiles("oracle")
public class PersistenceOraTest extends PersistenceTest {

    @BeforeClass
    public static void configProperties() {
        SingularPropertiesImpl.get().reloadAndOverrideWith(ClassLoader.getSystemClassLoader().getResource("singular-ora.properties"));
    }
}
