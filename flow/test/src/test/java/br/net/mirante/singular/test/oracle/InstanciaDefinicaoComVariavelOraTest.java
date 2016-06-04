package br.net.mirante.singular.test.oracle;

import br.net.mirante.singular.commons.base.SingularProperties;
import br.net.mirante.singular.test.InstanciaDefinicaoComVariavelTest;
import org.junit.BeforeClass;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("oracle")
public class InstanciaDefinicaoComVariavelOraTest extends InstanciaDefinicaoComVariavelTest {

    @BeforeClass
    public static void configProperties() {
        SingularProperties.INSTANCE.reloadAndOverrideWith(ClassLoader.getSystemClassLoader().getResource(
                "singular-ora.properties"));
    }
}
