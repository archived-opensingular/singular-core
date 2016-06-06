package br.net.mirante.singular.test.mssql;

import br.net.mirante.singular.commons.base.SingularProperties;
import br.net.mirante.singular.test.SetupTest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("mssql")
public class SetupMssqlTest extends SetupTest {

    @BeforeClass
    public static void configProperties() {
        SingularProperties.INSTANCE.reloadAndOverrideWith(ClassLoader.getSystemClassLoader().getResource(
                "singular-mssql.properties"));
    }
}
