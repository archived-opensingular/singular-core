package br.net.mirante.singular.test.mssql;

import br.net.mirante.singular.commons.base.SingularProperties;
import br.net.mirante.singular.test.PersistenceTest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("mssql")
public class PersistenceMssqlTest extends PersistenceTest {

    @BeforeClass
    public static void configProperites() {
        SingularProperties.INSTANCE.loadFrom(ClassLoader.getSystemClassLoader().getResourceAsStream("singular-mssql.properties"));
    }
}