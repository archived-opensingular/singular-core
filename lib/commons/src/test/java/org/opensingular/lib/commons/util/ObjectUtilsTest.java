package org.opensingular.lib.commons.util;

import org.junit.Assert;
import org.junit.Test;

public class ObjectUtilsTest {

    @Test
    public void IsAllNullTest(){
        String test = null;
        Assert.assertTrue(ObjectUtils.isAllNull(null, null, null, test));

        test = "not null anymore";
        Assert.assertFalse(ObjectUtils.isAllNull(null, null, null, test));
    }
}
