/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.type.core.attachment.handlers;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import br.net.mirante.singular.form.type.core.attachment.IAttachmentRef;

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
