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

package org.opensingular.form.type.core.attachment;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.opensingular.internal.lib.commons.util.TempFileProvider;
import org.opensingular.form.io.HashUtil;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(value = Parameterized.class)
abstract public class BaseAttachmentPersistenceFilesTest {
    protected IAttachmentPersistenceHandler persistenHandler;

    protected byte[] content;
    protected String hash;
    protected String fileName = "teste.txt";

    public BaseAttachmentPersistenceFilesTest(byte[] content, String hash) {
        this.content = content;
        this.hash = hash;
    }

    protected TempFileProvider tmpProvider;

    @Before
    public void createTmpProvider() {
        tmpProvider = TempFileProvider.createForUseInTryClause(this);
    }

    @After
    public void cleanTmpProvider() {
        tmpProvider.deleteOrException();
    }

    @Before
    public void setHandler() throws Exception {
        persistenHandler = createHandler();
    }

    protected abstract IAttachmentPersistenceHandler createHandler() throws Exception;

    @Parameters(name = "{index}: ({1})")
    public static Iterable<Object[]> data1() {
        return Arrays.asList(new Object[][]{
                {new byte[0], "da39a3ee5e6b4b0d3255bfef95601890afd80709"},
                {"i".getBytes(Charset.forName(StandardCharsets.UTF_8.name())), "042dc4512fa3d391c5170cf3aa61e6a638f84342"}, //Caso importante pelo hash começa com zero
                {"np".getBytes(Charset.forName(StandardCharsets.UTF_8.name())), "003fffd5649fc27c0fc0d15a402a4fe5b0444ce7"},
                {"1234".getBytes(Charset.forName(StandardCharsets.UTF_8.name())), "7110eda4d09e062aa5e4a390b0a572ac0d2c0220"},
                {"TesteTesteTeste".getBytes(), "ceecae2e6034de45a8303a31e9e96adb37c2443f" },
            { "TestTesteTeste".getBytes(Charset.forName(StandardCharsets.UTF_8.name())), "965a7dcefe3755139f4943c5bcbae915f9844c64" },
            { "sha1 this string".getBytes(Charset.forName(StandardCharsets.UTF_8.name())), "cf23df2207d99a74fbe169e3eba035e633b65d94" },
            { "The quick brown fox jumps over the lazy dog".getBytes(Charset.forName(StandardCharsets.UTF_8.name())), "2fd4e1c67a2d28fced849ee1bb76e7391b93eb12" },
            { newByteArray(10 * 1024 * 1024, (byte) 0), "8c206a1a87599f532ce68675536f0b1546900d7a"},
            { newByteArray(100 * 1024 * 1024, (byte) 1), "9abe36b18e1871f67d581f2d1f4b6a9036dcc23f"}
        });
    }

    private static byte[] newByteArray(int size, byte conteudo) {
        byte[] grande = new byte[size];
        Arrays.fill(grande, conteudo);
        return grande;
    }

    @Test
    public void createReferenceWithProperDataUsingStream() throws Exception {
        assertReference(persistenHandler.addAttachment(tmpProvider.createTempFile(content), content.length, fileName, HashUtil.toSHA1Base16(content)));
    }

    private void assertReference(IAttachmentRef ref) throws IOException {
        assertEquals(hash, ref.getHashSHA1());
        assertEquals(content.length, ref.getSize());
        assertTrue(Arrays.equals(content, IOUtils.toByteArray(ref.getContentAsInputStream())));
    }

    @Test
    public void recoverReferenceWithSameDataUsingStream() throws Exception {
        IAttachmentRef original = persistenHandler.addAttachment(tmpProvider.createTempFile(content), content.length,
                fileName, HashUtil.toSHA1Base16(content));
        IAttachmentRef returned = persistenHandler.getAttachment(original.getId());
        assertReference(original, returned);
    }

    private void assertReference(IAttachmentRef original, IAttachmentRef returned) throws IOException {
        assertEquals(returned.getHashSHA1(), original.getHashSHA1());
        assertEquals(returned.getId(), original.getId());
        assertEquals(returned.getSize(), original.getSize());
        assertTrue(Arrays.equals(IOUtils.toByteArray(returned.getContentAsInputStream()), IOUtils.toByteArray(returned.getContentAsInputStream())));
        assertTrue(Arrays.equals(IOUtils.toByteArray(returned.getContentAsInputStream()), IOUtils.toByteArray(returned.getContentAsInputStream())));
    }
}
