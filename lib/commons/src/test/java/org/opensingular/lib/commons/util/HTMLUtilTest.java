package org.opensingular.lib.commons.util;

import org.junit.Test;

import static org.junit.Assert.*;


public class HTMLUtilTest {

    @Test
    public void isHTML() throws Exception {
        //trues
        assertTrue(HTMLUtil.isHTML("<html><body></body></html>"));
        assertTrue(HTMLUtil.isHTML("<html>\n<body>\n\n\n</body>\n\n</html>"));
        assertTrue(HTMLUtil.isHTML("<html><body></ body></ html>"));
        assertTrue(HTMLUtil.isHTML("<body></ body>"));
        //falses
        assertFalse(HTMLUtil.isHTML("<!@#$%¨&*(>testest<!@#$%¨&*(/>"));
        assertFalse(HTMLUtil.isHTML("<br />"));
        assertFalse(HTMLUtil.isHTML("123489"));
        assertFalse(HTMLUtil.isHTML("!@#$%¨&*()´q´qáá´´a´´eé´´ií´´oóó"));
        assertFalse(HTMLUtil.isHTML("<@></@>"));
    }

}