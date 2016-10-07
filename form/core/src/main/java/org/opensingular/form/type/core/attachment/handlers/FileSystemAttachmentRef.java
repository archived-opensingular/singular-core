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

package org.opensingular.form.type.core.attachment.handlers;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.opensingular.form.type.core.attachment.IAttachmentRef;

@SuppressWarnings("serial")
public class FileSystemAttachmentRef implements IAttachmentRef, Serializable {

    private final String id, hashSHA1, path, name;
    private long size;

    public FileSystemAttachmentRef(String id, String hashSHA1, String path, long size, String name) {
        this.id = id;
        this.hashSHA1 = hashSHA1;
        this.path = path;
        this.size = size;
        this.name = name;
    }

    public String getId() {
        return id;
    }
    
    public String getHashSHA1() {
        return hashSHA1;
    }

    public String getPath() {
        return path;
    }

    public long getSize() {
        return size;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(path);
    }
    
    @Override
    public String getName() {
        return name;
    }
}
