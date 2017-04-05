package org.opensingular.lib.wicket.util.util;

import org.junit.Assert;
import org.junit.Test;

public class JavaScriptUtilsTest {
    @Test
    public void javaScriptEscape() {
        Assert.assertEquals(
            "\\\"\\'\\\\\\t\\n\\n\\n\\f\\b\\v\\u003C\\u003E\\u2028\\u2029",
            JavaScriptUtils.javaScriptEscape("\"\'\\\t\n\r\r\f\b\013<>\u2028\u2029"));

        Assert.assertNull(JavaScriptUtils.javaScriptEscape(null));
    }
}