package br.net.mirante.singular.test.mssql;

import org.junit.BeforeClass;
import org.springframework.test.context.ActiveProfiles;

import br.net.mirante.singular.commons.base.SingularProperties;
import br.net.mirante.singular.test.PersistenceTest;

@ActiveProfiles("mssql")
public class PersistenceMssqlTest extends PersistenceTest {

    @BeforeClass
    public static void configProperties() {
        SingularProperties.get().reloadAndOverrideWith(ClassLoader.getSystemClassLoader().getResource(
                "singular-mssql.properties"));
    }
}
