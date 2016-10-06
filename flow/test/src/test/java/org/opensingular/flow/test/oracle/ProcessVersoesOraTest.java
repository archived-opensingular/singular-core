package org.opensingular.flow.test.oracle;

import org.junit.BeforeClass;
import org.springframework.test.context.ActiveProfiles;

import org.opensingular.singular.commons.base.SingularPropertiesImpl;
import org.opensingular.flow.test.ProcessVersoesTest;

@ActiveProfiles("oracle")
public class ProcessVersoesOraTest extends ProcessVersoesTest {

    @BeforeClass
    public static void configProperties() {
        SingularPropertiesImpl.get().reloadAndOverrideWith(ClassLoader.getSystemClassLoader().getResource(
                "singular-ora.properties"));
    }
}
