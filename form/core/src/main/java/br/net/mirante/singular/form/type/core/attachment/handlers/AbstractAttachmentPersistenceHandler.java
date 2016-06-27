/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.type.core.attachment.handlers;

import br.net.mirante.singular.commons.base.SingularUtil;
import br.net.mirante.singular.form.io.HashUtil;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentPersistenceHandler;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestInputStream;

@SuppressWarnings("serial")
abstract class AbstractAttachmentPersistenceHandler implements IAttachmentPersistenceHandler {


    /**
     * Calcular o hash do input stream e o comprime ao mesmo tempo
     * que o copia para o output stream out
     * @param in
     *  Entrada do dado
     * @param out
     *  Destino da cópia comprimida
     * @return
     */
    protected String hashAndCopyCompressed(InputStream in, OutputStream out) {
        try {
            try (DigestInputStream shaCalculator = HashUtil.toSHA1InputStream(in);
                 InputStream deflator = CompressionUtil.toDeflateInputStream(shaCalculator);
                 OutputStream os = out) {
                IOUtils.copy(deflator, os);
                return HashUtil.bytesToBase16(shaCalculator.getMessageDigest().digest());
            }
        } catch (IOException e) {
            throw SingularUtil.propagate(e);
        }
    }

    protected InputStream decompressStream(InputStream in) {
        return CompressionUtil.inflateToInputStream(in);
    }


}
