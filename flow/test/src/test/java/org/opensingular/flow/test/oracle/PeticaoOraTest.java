package org.opensingular.flow.test.oracle;

import org.opensingular.singular.commons.base.SingularPropertiesImpl;
import org.opensingular.flow.test.PeticaoTest;
import org.junit.BeforeClass;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("oracle")
public class PeticaoOraTest extends PeticaoTest {

    @BeforeClass
    public static void configProperties() {
        SingularPropertiesImpl.get().reloadAndOverrideWith(ClassLoader.getSystemClassLoader().getResource(
                "singular-ora.properties"));
    }
}
