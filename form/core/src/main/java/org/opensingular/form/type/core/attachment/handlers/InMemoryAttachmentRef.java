/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
