package br.net.mirante.singular.test.oracle;

import br.net.mirante.singular.commons.base.SingularProperties;
import br.net.mirante.singular.test.PeticaoTest;
import org.junit.BeforeClass;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("oracle")
public class PeticaoOraTest extends PeticaoTest {

    @BeforeClass
    public static void configProperites() {
        SingularProperties.INSTANCE.loadFrom(ClassLoader.getSystemClassLoader().getResourceAsStream("singular-ora.properties"));
    }
}
