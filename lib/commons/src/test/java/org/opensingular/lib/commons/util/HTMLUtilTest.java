/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

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