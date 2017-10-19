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

package org.opensingular.lib.commons;

import com.google.common.base.Joiner;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.opensingular.lib.commons.io.TempFileInputStream;
import org.opensingular.lib.commons.util.Loggable;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.Charset;

public class TempFileInputStreamTest implements Loggable {

    @Test
    public void test() throws Exception {
        File f = File.createTempFile("test", "supernada");
        try (FileWriter fw = new FileWriter(f)){
            IOUtils.write("teste 123", fw);
        }
        try (TempFileInputStream ts = new TempFileInputStream(f)){
            getLogger().info(Joiner.on("\n").join(IOUtils.readLines(ts, Charset.defaultCharset())));
        }
        Assert.assertFalse(f.exists());
    }
}
