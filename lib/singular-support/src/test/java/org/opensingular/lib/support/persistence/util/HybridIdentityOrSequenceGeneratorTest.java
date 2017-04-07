package org.opensingular.lib.support.persistence.util;

import org.hibernate.dialect.H2Dialect;
import org.hibernate.type.Type;
import org.junit.Assert;
import org.junit.Test;

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
}
