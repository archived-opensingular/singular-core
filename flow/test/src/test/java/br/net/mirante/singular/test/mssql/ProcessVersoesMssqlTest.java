package br.net.mirante.singular.test.mssql;

import org.junit.BeforeClass;
import org.springframework.test.context.ActiveProfiles;

import br.net.mirante.singular.commons.base.SingularPropertiesImpl;
import br.net.mirante.singular.test.ProcessVersoesTest;

@ActiveProfiles("mssql")
public class ProcessVersoesMssqlTest extends ProcessVersoesTest {

    @BeforeClass
    public static void configProperties() {
        SingularPropertiesImpl.INSTANCE.reloadAndOverrideWith(ClassLoader.getSystemClassLoader().getResource(
                "singular-mssql.properties"));
    }
}
