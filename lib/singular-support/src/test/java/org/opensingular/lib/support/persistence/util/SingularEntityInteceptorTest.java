package org.opensingular.lib.support.persistence.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.opensingular.lib.support.persistence.DatabaseObjectNameReplacement;
import org.opensingular.lib.support.persistence.SingularEntityInterceptor;

public class SingularEntityInteceptorTest {

    private final static String SCHEMA_VELHO = "SCHEMA_VELHO";
    private final static String SCHEMA_NOVO = "SCHEMA_NOVO";
    private final static String SQUENCE_VELHA = "SQUENCE_VELHA";
    private final static String SQUENCE_NOVA = "SQUENCE_NOVA";

    private DatabaseObjectNameReplacement schemaName = new DatabaseObjectNameReplacement(SCHEMA_VELHO, SCHEMA_NOVO);
    private DatabaseObjectNameReplacement sequenceName = new DatabaseObjectNameReplacement(SQUENCE_VELHA, SQUENCE_NOVA);

    @Test
    public void replaceObjectNameInSqlScript(){
        List<DatabaseObjectNameReplacement> schemaReplacements = new ArrayList<>();
        schemaReplacements.add(schemaName);
        schemaReplacements.add(sequenceName);

        SingularEntityInterceptor singularEntityInterceptor = new SingularEntityInterceptor(schemaReplacements);

        Assert.assertEquals(SCHEMA_NOVO + "TABELA",  singularEntityInterceptor.onPrepareStatement(SCHEMA_VELHO + "TABELA"));
        Assert.assertEquals(SCHEMA_NOVO + SQUENCE_NOVA,  singularEntityInterceptor.onPrepareStatement(SCHEMA_VELHO + SQUENCE_VELHA));
    }
}
