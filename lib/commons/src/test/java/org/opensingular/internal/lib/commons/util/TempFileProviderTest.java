/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.internal.lib.commons.util;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.internal.lib.commons.test.SingularTestUtil;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.util.TempFileUtils;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * @author Daniel C. Bordin on 02/04/2017.
 */
public class TempFileProviderTest {

    private List<File> files = new ArrayList<>();
    private List<Closeable> toBeClosed = new ArrayList<>();
    private List<FileLock> locks = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        files.clear();
        locks.clear();
        toBeClosed.clear();
    }

    @After
    public void tearDown() throws Exception {
        for (FileLock lock : locks) {
            lock.release();
        }
        for (Closeable in : toBeClosed) {
            in.close();
        }
        for (File file : files) {
            TempFileUtils.deleteOrException(file, this);
        }
        setUp();
    }

    private void assertEveryThingIsDeleted() {
        for (File file : files) {
            if (file.exists()) {
                throw new AssertionError();
            }
        }
    }


    @Test
    public void createForUseInTryClause() throws Exception {

    }

    @Test
    public void create() throws Exception {

    }

    @Test
    public void createTempDir_withCreate() throws Exception {
        TempFileProvider.create(this, tmpProvider -> {
            createDirWithFiles(tmpProvider);
        });
        assertEveryThingIsDeleted();
    }

    @Test
    public void createTempDir_withTry() throws Exception {
        try (TempFileProvider tmpProvider = TempFileProvider.createForUseInTryClause(this)) {
            createDirWithFiles(tmpProvider);
        }
        ;
        assertEveryThingIsDeleted();
    }

    private void createDirWithFiles(TempFileProvider tmpProvider) throws IOException {
        files.add(tmpProvider.createTempDir());
        files.add(new File(files.get(0), "subDir"));
        files.get(1).mkdir();
        createFile(new File(files.get(1), "xx"));
    }

    private File createFile(File file) throws IOException {
        try (FileOutputStream out = new FileOutputStream(file)) {
            out.write(1);
        }
        files.add(file);
        return file;
    }

    @Test
    public void createTempFile() throws Exception {
        TempFileProvider.create(this, tmpProvider -> {
            createFile(tmpProvider.createTempFile());
        });
        try (TempFileProvider tmpProvider = TempFileProvider.createForUseInTryClause(this)) {
            createFile(tmpProvider.createTempFile());
        }
        assertEveryThingIsDeleted();
    }

    @Test
    public void createTempFile1() throws Exception {
        TempFileProvider.create(this, tmpProvider -> {
            files.add(tmpProvider.createTempFile(new byte[]{1, 2}));
            assertTrue(files.get(0).exists());
            Assert.assertEquals(2L, files.get(0).length());

            files.add(tmpProvider.createTempFile(new byte[]{1, 2}, "my.txt"));
            assertTrue(files.get(1).exists());
            assertTrue(files.get(1).getName().endsWith("my.txt"));
            Assert.assertEquals(2L, files.get(0).length());
        });
        assertEveryThingIsDeleted();
    }

    @Test
    public void createTempFile2() throws Exception {
        TempFileProvider.create(this, tmpProvider -> {
            files.add(tmpProvider.createTempFile("my.txt"));
            assertTrue(files.get(0).getName().contains(getClass().getSimpleName()));
            assertTrue(files.get(0).getName().endsWith("my.txt"));
            files.add(tmpProvider.createTempFile((String) null));
            assertTrue(files.get(0).getName().contains(getClass().getSimpleName()));
            assertTrue(files.get(1).getName().endsWith(".tmp"));
        });
        assertEveryThingIsDeleted();

    }

    @Test
    public void createTempFile3() throws Exception {
        TempFileProvider.create(this, tmpProvider -> {
            files.add(tmpProvider.createTempFile("testeXX", "my.txt"));
            assertTrue(files.get(0).getName().endsWith("my.txt"));
            assertTrue(!files.get(0).getName().contains(getClass().getSimpleName()));
            assertTrue(files.get(0).getName().contains("testeXX"));
            files.add(tmpProvider.createTempFile((String) null, null));
            assertTrue(files.get(1).getName().contains(getClass().getSimpleName()));
            assertTrue(files.get(1).getName().endsWith(".tmp"));
        });
        assertEveryThingIsDeleted();
    }

    @Test
    public void createTempFileByDontPutOnDeleteList() throws Exception {
        TempFileProvider.create(this, tmpProvider -> {
            files.add(tmpProvider.createTempFile());
            files.add(tmpProvider.createTempFileByDontPutOnDeleteList(".xpto"));
            assertTrue(files.get(1).getName().endsWith(".xpto"));
            files.add(tmpProvider.createTempFile());
        });
        assertTrue(files.get(1).exists());
        assertTrue(files.get(1).delete());
        assertEveryThingIsDeleted();
    }

    private void createFourFiles(TempFileProvider tmpProvider) throws IOException {
        files.add(tmpProvider.createTempFile(new byte[]{1}));
        files.add(tmpProvider.createTempFile(new byte[]{1, 2}));
        files.add(tmpProvider.createTempFile(new byte[]{1, 2, 3}));
        files.add(tmpProvider.createTempFile(new byte[]{1, 2, 3, 4}));
    }


    @Test
    public void close_withLockFile_usingCreate() throws Exception {
        SingularTestUtil.assertException(() -> TempFileProvider.create(this, tmpProvider -> {
            createFourFiles(tmpProvider);
            simulateDeleteBlock(files.get(1));
            simulateDeleteBlock(files.get(2));
        }), SingularException.class, "Nao foi possível apagar o arquivo");
        assertTrue(!files.get(0).exists());
        assertTrue(files.get(1).exists());
        assertTrue(files.get(2).exists());
        assertTrue(!files.get(3).exists());
    }

    @Test
    public void close_CodeException_AndWithLockFile_usingCreate() throws Exception {
        SingularTestUtil.assertException(() -> TempFileProvider.create(this, tmpProvider -> {
            createFourFiles(tmpProvider);
            simulateDeleteBlock(files.get(1));
            simulateDeleteBlock(files.get(2));
            throw new RuntimeException("Não pode mascarar a exception original");
        }), RuntimeException.class, "Não pode mascarar a exception original");
        assertTrue(!files.get(0).exists());
        assertTrue(files.get(1).exists());
        assertTrue(files.get(2).exists());
        assertTrue(!files.get(3).exists());
    }

    private void simulateDeleteBlock(File file) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        out.write(1);
        out.flush();
        toBeClosed.add(out);
        locks.add(out.getChannel().lock());
    }

    @Test
    public void close_withLockFile_using_CreateForUseInTryClause() throws Exception {
        SingularTestUtil.assertException(() -> {
            try (TempFileProvider tmpProvider = TempFileProvider.createForUseInTryClause(this)) {
                createFourFiles(tmpProvider);
                simulateDeleteBlock(files.get(1));
                simulateDeleteBlock(files.get(2));
            }
        }, SingularException.class, "Nao foi possível apagar o arquivo");
        assertTrue(!files.get(0).exists());
        assertTrue(files.get(1).exists());
        assertTrue(files.get(2).exists());
        assertTrue(!files.get(3).exists());
    }

    @Test
    public void close_CodeException_AndWithLockFile_using_CreateForUseInTryClause() throws Exception {
        SingularTestUtil.assertException(() -> {
            try (TempFileProvider tmpProvider = TempFileProvider.createForUseInTryClause(this)) {
                createFourFiles(tmpProvider);
                simulateDeleteBlock(files.get(1));
                simulateDeleteBlock(files.get(2));
                throw new RuntimeException("Não pode mascarar a exception original");
            }
        }, RuntimeException.class, "Não pode mascarar a exception original");
        assertTrue(!files.get(0).exists());
        assertTrue(files.get(1).exists());
        assertTrue(files.get(2).exists());
        assertTrue(!files.get(3).exists());
    }

    @Test
    public void deleteOrException() throws Exception {
        TempFileProvider tmpProvider = TempFileProvider.createForUseInTryClause(this);
        createFourFiles(tmpProvider);
        simulateDeleteBlock(files.get(1));
        simulateDeleteBlock(files.get(2));
        SingularTestUtil.assertException(() -> tmpProvider.deleteOrException(), SingularException.class,
                "Nao foi possível apagar o arquivo");
        assertTrue(!files.get(0).exists());
        assertTrue(files.get(1).exists());
        assertTrue(files.get(2).exists());
        assertTrue(!files.get(3).exists());
    }

    @Test
    public void deleteQuietly() throws Exception {
        TempFileProvider tmpProvider = TempFileProvider.createForUseInTryClause(this);
        createFourFiles(tmpProvider);
        simulateDeleteBlock(files.get(1));
        simulateDeleteBlock(files.get(2));
        tmpProvider.deleteQuietly();
        assertTrue(!files.get(0).exists());
        assertTrue(files.get(1).exists());
        assertTrue(files.get(2).exists());
        assertTrue(!files.get(3).exists());
    }

}