package br.net.mirante.singular.test.oracle;

import org.junit.BeforeClass;
import org.springframework.test.context.ActiveProfiles;

import br.net.mirante.singular.commons.base.SingularPropertiesImpl;
import br.net.mirante.singular.test.ProcessVersoesTest;

@ActiveProfiles("oracle")
public class ProcessVersoesOraTest extends ProcessVersoesTest {

    @BeforeClass
    public static void configProperties() {
        SingularPropertiesImpl.INSTANCE.reloadAndOverrideWith(ClassLoader.getSystemClassLoader().getResource(
                "singular-ora.properties"));
    }
}
