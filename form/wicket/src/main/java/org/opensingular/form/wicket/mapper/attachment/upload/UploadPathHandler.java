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

package org.opensingular.form.wicket.mapper.attachment.upload;

import org.opensingular.form.wicket.mapper.attachment.upload.info.FileUploadInfo;
import org.opensingular.lib.commons.util.Loggable;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;


public class UploadPathHandler implements Loggable, Serializable {

    private File rootFile;

    public Path getLocalFilePath(FileUploadInfo fileInfo) {
        return getLocalFilePath(fileInfo.getAttachmentRef().getId());
    }

    public Path getLocalFilePath(String id) {
        return getOrCreateRootPath().resolve(id);
    }

    public synchronized Path getOrCreateRootPath() {
        if (rootFile == null || !rootFile.exists()) {
            try {
                Path rootPath = Files.createTempDirectory(UploadPathHandler.class.getSimpleName() + "_");
                rootFile = rootPath.toFile();
                rootFile.deleteOnExit();
            } catch (IOException ex) {
                getLogger().warn(ex.getMessage(), ex);
            }
        }
        return rootFile.toPath();
    }

}