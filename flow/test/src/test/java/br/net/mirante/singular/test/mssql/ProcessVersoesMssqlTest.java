package br.net.mirante.singular.test.mssql;

import br.net.mirante.singular.commons.base.SingularProperties;
import br.net.mirante.singular.definicao.ProcessVersoes;
import br.net.mirante.singular.test.ProcessVersoesTest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("mssql")
public class ProcessVersoesMssqlTest extends ProcessVersoesTest {

    @BeforeClass
    public static void configProperites() {
        SingularProperties.INSTANCE.loadFrom(ClassLoader.getSystemClassLoader().getResourceAsStream("singular-mssql.properties"));
    }
}
