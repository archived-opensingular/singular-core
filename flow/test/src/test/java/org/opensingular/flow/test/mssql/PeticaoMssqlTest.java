package org.opensingular.flow.test.mssql;

import org.opensingular.lib.commons.base.SingularPropertiesImpl;
import org.opensingular.flow.test.PeticaoTest;
import org.junit.BeforeClass;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("mssql")
public class PeticaoMssqlTest extends PeticaoTest {

    @BeforeClass
    public static void configProperties() {
        SingularPropertiesImpl.get().reloadAndOverrideWith(ClassLoader.getSystemClassLoader().getResource(
                "singular-mssql.properties"));
    }
}
