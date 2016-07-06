package br.net.mirante.singular.test.mssql;

import br.net.mirante.singular.commons.base.SingularPropertiesImpl;
import br.net.mirante.singular.test.PeticaoTest;
import org.junit.BeforeClass;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("mssql")
public class PeticaoMssqlTest extends PeticaoTest {

    @BeforeClass
    public static void configProperties() {
        SingularPropertiesImpl.INSTANCE.reloadAndOverrideWith(ClassLoader.getSystemClassLoader().getResource(
                "singular-mssql.properties"));
    }
}
