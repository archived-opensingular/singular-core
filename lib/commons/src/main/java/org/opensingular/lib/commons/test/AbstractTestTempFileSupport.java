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

package org.opensingular.lib.commons.test;

import com.google.common.base.Throwables;
import org.junit.After;
import org.junit.AfterClass;
import org.opensingular.internal.lib.commons.test.SingularTestUtil;
import org.opensingular.internal.lib.commons.util.TempFileProvider;
import org.opensingular.lib.commons.lambda.IConsumerEx;
import org.opensingular.lib.commons.lambda.IFunctionEx;
import org.opensingular.lib.commons.util.TempFileUtils;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * It's a support base class for creating a JUnit test that need to create temp files that are guarantied to be
 * deleted after the completions of the tests. Also, it have helps method to open generated file on the desktop of the
 * developer.
 *
 * @author Daniel C. Bordin on 2017-09-23.
 */
public abstract class AbstractTestTempFileSupport {

    private static TempFileProvider tmpProvider;

    private boolean openGeneratedFiles;

    /**
     * Indicated if the generated files should be open on the developer desktop. Default is false. It will mainly affect
     * generateFileAndShowOnDesktopForUser() methods.
     */
    protected void setOpenGeneratedFiles(boolean value) {
        openGeneratedFiles = value;
    }

    /** Indicated if the generated files should be open on the developer desktop. Default is false. */
    public boolean isOpenGeneratedFiles() {
        return openGeneratedFiles;
    }

    /** Return the current {@link TempFileProvider} or creates a ne one, if necessary. */
    @Nonnull
    protected TempFileProvider getTempFileProvider() {
        if (tmpProvider == null) {
            tmpProvider = TempFileProvider.createForUseInTryClause(this);
        }
        return tmpProvider;
    }

    /**
     * After all test method have being called, deletes all the temp files generated with {@link
     * #getTempFileProvider()}, if any. Also, if {@link #setOpenGeneratedFiles(boolean)} is set to true, wait 10 seconds
     * before deleting the file so there will be time for the operating system to open the files.
     */
    @AfterClass
    public static void cleanTmpProviderAfterAllTestMethods() {
        if (tmpProvider != null) {
            try {
                if (!tmpProvider.isEmpty()) {
                    //There is files, it means that they are been opening in the operation system
                    SingularTestUtil.waitMilli(10000);
                    tmpProvider.deleteQuietly();
                }
            } finally {
                tmpProvider = null;
            }
        }
    }

    /**
     * After each test method, deletes all the temp files generated with {@link #getTempFileProvider()}, if there is
     * any file and if {@link #setOpenGeneratedFiles(boolean)} is set to false. If {@link
     * #setOpenGeneratedFiles(boolean)} is set to true, it don't deletes and wait for the {@link #cleanTmpProvider()}
     * be be called.
     */
    @After
    public void cleanTmpProviderAfterTestMethod() {
        if (!openGeneratedFiles && tmpProvider != null) {
            tmpProvider.deleteOrException();
        }
    }

    /**
     * Calls the file generator provided and with the resulting file or opens it on the developers desktop or
     * immediately deletes the generated file (depending on {@link #setOpenGeneratedFiles(boolean)}.
     *
     * @see SingularTestUtil#showFileOnDesktopForUser(File)
     */
    protected <EX extends Exception> void generateFileAndShowOnDesktopForUser(
            @Nonnull IFunctionEx<TempFileProvider, File, EX> fileGenerator) {
        File file;
        try {
            file = fileGenerator.apply(getTempFileProvider());
        } catch (Exception e) {
            Throwables.throwIfUnchecked(e);
            throw new RuntimeException(e);
        }
        if (file != null) {
            if (isOpenGeneratedFiles()) {
                SingularTestUtil.showFileOnDesktopForUser(file);
            } else {
                TempFileUtils.deleteOrException(file, this);
            }
        }
    }

    /**
     * Creates a temp file with the provided file extension, then calls the file generator provided and with the
     * resulting file or opens it on the developers desktop or
     * immediately deletes the generated file (depending on {@link #setOpenGeneratedFiles(boolean)}.
     *
     * @param fileExtension It doesn't have a dot, it will be added (for example, "png" becomes ".png")
     * @see SingularTestUtil#showFileOnDesktopForUser(File)
     */
    protected <EX extends Exception> void generateFileAndShowOnDesktopForUser(@Nonnull String fileExtension,
            @Nonnull IConsumerEx<OutputStream, EX> fileGenerator) {
        String ext = fileExtension.indexOf('.') == -1 ? '.' + fileExtension : fileExtension;
        File file = getTempFileProvider().createTempFile(ext);
        try (FileOutputStream out = new FileOutputStream(file)) {
            fileGenerator.accept(out);
        } catch (Exception e) {
            TempFileUtils.deleteAndFailQuietily(file, this);
            Throwables.throwIfUnchecked(e);
            throw new RuntimeException(e);
        }
        if (isOpenGeneratedFiles()) {
            SingularTestUtil.showFileOnDesktopForUser(file);
        } else {
            TempFileUtils.deleteOrException(file, this);
        }
    }
}
