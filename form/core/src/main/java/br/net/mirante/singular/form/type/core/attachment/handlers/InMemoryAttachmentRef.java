/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.type.core.attachment.handlers;

import br.net.mirante.singular.commons.base.SingularException;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentRef;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;

@SuppressWarnings("serial")
public class InMemoryAttachmentRef implements IAttachmentRef, Serializable {

    private final long size;
    private final String hashSHA1Hex;
    private final File tempFile;

    public InMemoryAttachmentRef(File tempFile, long size, String hashSHA1Hex) {
        this.tempFile = tempFile;
        this.size = size;
        this.hashSHA1Hex = hashSHA1Hex;
    }

    @Override
    public String getHashSHA1() {
        return hashSHA1Hex;
    }

    @Override
    public InputStream newInputStream() {
        try {
            return new FileInputStream(tempFile);
        } catch (FileNotFoundException e) {
            throw new SingularException(e);
        }
    }

    @Override
    public long getSize() {
        return size;
    }
}
