package org.opensingular.lib.support.persistence.util;

import org.hibernate.dialect.H2Dialect;
import org.hibernate.id.insert.InsertGeneratedIdentifierDelegate;
import org.hibernate.type.Type;
import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

public class HybridIdentityOrSequenceGeneratorTest {

    @Test
    public void supportsBulkInsertionIdentifierGenerationTest(){
        HybridIdentityOrSequenceGenerator generator = new HybridIdentityOrSequenceGenerator();
        Assert.assertTrue(generator.supportsBulkInsertionIdentifierGeneration());
    }

    @Test
    public void determineBulkInsertionIdentifierGenerationSelectFragmentTest(){
        HybridIdentityOrSequenceGenerator generator = new HybridIdentityOrSequenceGenerator();
        Assert.assertNull(generator.determineBulkInsertionIdentifierGenerationSelectFragment(new H2Dialect()));
    }

    @Test
    public void getInsertGeneratedIdentifierDelegateTest(){
        HybridIdentityOrSequenceGenerator generator = new HybridIdentityOrSequenceGenerator();
        Assert.assertNotNull(generator.getInsertGeneratedIdentifierDelegate(null, new H2Dialect(), false));
    }
}
