package br.net.mirante.singular.test.mssql;

import org.junit.BeforeClass;
import org.springframework.test.context.ActiveProfiles;

import br.net.mirante.singular.commons.base.SingularProperties;
import br.net.mirante.singular.test.InstanciaDefinicaoComVariavelTest;

@ActiveProfiles("mssql")
public class InstanciaDefinicaoComVariavelMssqlTest extends InstanciaDefinicaoComVariavelTest {

    @BeforeClass
    public static void configProperites() {
        SingularProperties.INSTANCE.loadFrom(ClassLoader.getSystemClassLoader()
                .getResourceAsStream("singular-mssql.properties"));
    }
}
