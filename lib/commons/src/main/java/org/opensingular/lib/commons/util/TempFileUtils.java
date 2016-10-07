/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package org.opensingular.lib.commons.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;

import com.google.common.base.Preconditions;
import com.google.common.io.Files;

import org.opensingular.lib.commons.lambda.IBiConsumerEx;
import org.opensingular.lib.commons.lambda.IConsumerEx;
import org.opensingular.lib.commons.lambda.ISupplierEx;

public abstract class TempFileUtils {

    private static final String DEFAULT_FILE_SUFFIX = ".tmp";
    private static final String DEFAULT_FILE_PREFIX = "TempFileUtils_";

    private TempFileUtils() {}

    public static void withTempDir(IConsumerEx<File, IOException> callback) throws IOException {
        internalWithTempFile(() -> Files.createTempDir(), callback);
    }

    public static void withTempFile(String prefix, String suffix, IConsumerEx<File, IOException> callback) throws IOException {
        internalWithTempFile(() -> File.createTempFile(prefix, suffix), callback);
    }
    public static void withTempFile(IConsumerEx<File, IOException> callback) throws IOException {
        withTempFile(DEFAULT_FILE_PREFIX, DEFAULT_FILE_SUFFIX, callback);
    }

    private static void internalWithTempFile(ISupplierEx<File, IOException> fileSupplier, IConsumerEx<File, IOException> callback) throws IOException {
        File file = null;
        try {
            file = fileSupplier.get();
            callback.accept(file);
        } finally {
            if (exists(file)) {
                FileUtils.deleteQuietly(file);
            }
        }
    }

    /**
     * 
     * @param relativePath caminho 
     * @param callback biconsumer<baseDir, file>
     * @throws IOException
     */
    public static void withFileInTempDir(Path relativePath, IBiConsumerEx<File, File, IOException> callback) throws IOException {
        Preconditions.checkArgument(!relativePath.isAbsolute());
        Preconditions.checkArgument(relativePath.getNameCount() > 0);
        withTempDir(dir -> {
            Path dirPath = dir.toPath();
            Path filePath = dirPath.resolve(relativePath);
            filePath.getParent().toFile().mkdirs();
            internalWithTempFile(() -> filePath.toFile(), file -> callback.accept(dir, file));
        });
    }

    public static boolean exists(File f) {
        return (f != null) && f.exists();
    }
}
