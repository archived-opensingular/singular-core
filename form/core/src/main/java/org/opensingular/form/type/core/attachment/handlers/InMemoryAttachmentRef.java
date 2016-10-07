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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.opensingular.form.type.core.attachment.IAttachmentRef;

@SuppressWarnings("serial")
public class InMemoryAttachmentRef implements IAttachmentRef, Serializable {

    private final long size;
    private final String hashSHA1Hex;
    private final File tempFile;
    private final String id;

    public InMemoryAttachmentRef(String id, File tempFile, long size, String hashSHA1Hex) {
        this.tempFile = tempFile;
        this.size = size;
        this.id = id;
        this.hashSHA1Hex = hashSHA1Hex;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getHashSHA1() {
        return hashSHA1Hex;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(tempFile);
    }
    
    @Override
    public long getSize() {
        return size;
    }
    
    @Override
    public String getName() {
        return tempFile.getName();
    }
}
