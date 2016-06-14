/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.type.core.attachment.handlers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.util.zip.InflaterInputStream;

import com.google.common.io.CountingInputStream;

import br.net.mirante.singular.form.SingularFormException;
import br.net.mirante.singular.form.io.HashUtil;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentRef;

@SuppressWarnings("serial")
abstract class AbstractAttachmentPersistenceHandler<T> implements IAttachmentPersistenceHandler {

    @Override
    public final IAttachmentRef addAttachment(byte[] content) {
        if (content == null) {
            throw new SingularFormException("O conteúdo do arquivo não pode ser null");
        }
        return addAttachmentCompressed(CompressionUtil.toDeflateInputStream(content), HashUtil.toSHA1Base16(content), content.length);
    }

    protected abstract IAttachmentRef addAttachmentCompressed(InputStream deflateInputStream, String hashSHA16Hex, int originalLength);

    @Override
    public final IAttachmentRef addAttachment(InputStream in) {
        if (in == null) {
            throw new SingularFormException("A InputStream não pode ser null");
        }
        CountingInputStream in2 = new CountingInputStream(in);
        DigestInputStream in3 = HashUtil.toSHA1InputStream(in2);
        return addAttachmentCompressed(CompressionUtil.toDeflateInputStream(in3), in2, in3);
    }

    protected abstract IAttachmentRef addAttachmentCompressed(InputStream deflateInputStream, CountingInputStream inCounting,
            DigestInputStream hashCalculatorStream);

    protected static InputStream inflate(byte[] compressed) {
        return new InflaterInputStream(new ByteArrayInputStream(compressed));
    }
}
