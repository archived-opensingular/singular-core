package org.opensingular.singular.test.mssql;

import org.junit.BeforeClass;
import org.springframework.test.context.ActiveProfiles;

import org.opensingular.singular.commons.base.SingularPropertiesImpl;
import org.opensingular.singular.test.PersistenceTest;

@ActiveProfiles("mssql")
public class PersistenceMssqlTest extends PersistenceTest {

    @BeforeClass
    public static void configProperties() {
        SingularPropertiesImpl.get().reloadAndOverrideWith(ClassLoader.getSystemClassLoader().getResource(
                "singular-mssql.properties"));
    }
}
