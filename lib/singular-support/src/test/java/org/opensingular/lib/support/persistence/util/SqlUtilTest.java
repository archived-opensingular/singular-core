package org.opensingular.lib.support.persistence.util;

import org.junit.Assert;
import org.junit.Test;
import org.opensingular.lib.commons.base.SingularProperties;
import org.opensingular.lib.commons.util.Loggable;

public class SqlUtilTest {


    @Test
    public void testeCustomSchemaName(){
        System.setProperty(SingularProperties.CUSTOM_SCHEMA_NAME, "ABOBRINHA");
        Assert.assertTrue(SqlUtil.isSingularSchema("ABOBRINHA"));
        Assert.assertFalse(SqlUtil.isSingularSchema(Constants.SCHEMA));
    }

    @Test
    public void testeDefaultSchemaName(){
        System.clearProperty(SingularProperties.CUSTOM_SCHEMA_NAME);
        Assert.assertTrue(SqlUtil.isSingularSchema(Constants.SCHEMA));
        Assert.assertFalse(SqlUtil.isSingularSchema("ABOBRINHA"));
    }
}
