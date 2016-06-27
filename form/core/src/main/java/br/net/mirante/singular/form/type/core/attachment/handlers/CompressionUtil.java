/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.type.core.attachment.handlers;

import java.io.InputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterInputStream;
import java.util.zip.InflaterInputStream;

/**
 * Funções de apoio a compressão e descompressão de dados para uso interno
 * apenas.
 *
 * @author Daniel C. Bordin
 */
final class CompressionUtil {

    public static InputStream inflateToInputStream(InputStream source) {
        return new InflaterInputStream(source);
    }


    public static InputStream toDeflateInputStream(InputStream source) {
        return new DeflaterInputStream(source, new Deflater(Deflater.BEST_COMPRESSION));
    }

}
