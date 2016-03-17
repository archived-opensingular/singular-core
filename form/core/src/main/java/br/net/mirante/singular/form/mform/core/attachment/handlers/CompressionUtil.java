/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.core.attachment.handlers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterInputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import com.google.common.base.Throwables;
import com.google.common.io.ByteStreams;

/**
 * Funções de apoio a compressão e descompressão de dados para uso interno
 * apenas.
 *
 * @author Daniel C. Bordin
 *
 */
final class CompressionUtil {

    public static InputStream inflateToInputStream(byte[] compressedSource) {
        return new InflaterInputStream(new ByteArrayInputStream(compressedSource));
    }

    public static InputStream inflateToInputStream(InputStream source) {
        return new InflaterInputStream(source);
    }

    public static byte[] inflateToByteArray(byte[] compressedSource) {
        InflaterInputStream in = new InflaterInputStream(new ByteArrayInputStream(compressedSource));
        try {
            return ByteStreams.toByteArray(in);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    /**
     * Compacta usando a compressão máxima de {@link java.util.zip.Deflater}.
     */
    public static InputStream toDeflateInputStream(byte[] source) {
        return toDeflateInputStream(new ByteArrayInputStream(source));
    }

    public static InputStream toDeflateInputStream(InputStream source) {
        return new DeflaterInputStream(source, new Deflater(Deflater.BEST_COMPRESSION));
    }

    /**
     * Compacta usando a compressão máxima de {@link java.util.zip.Deflater}.
     */
    public static byte[] deflate(byte[] source) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(source.length);
        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
        DeflaterOutputStream outD = new DeflaterOutputStream(out, deflater);
        outD.write(source);
        outD.close();
        return out.toByteArray();
    }
}
