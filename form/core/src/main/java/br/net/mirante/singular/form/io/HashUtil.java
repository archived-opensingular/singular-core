/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.io;

import com.google.common.base.Throwables;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Funções utilitária para geração de hash e codificação do hash em diferentes
 * bases numéricas (ex.: base 16).
 *
 * @author Daniel C. Bordin
 */
public class HashUtil {

    /** Localiza o algorítmo de hash SHA1. */
    private static MessageDigest getMessageDigestSHA1() {
        try {
            return MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException e) {
            throw Throwables.propagate(e);
        }
    }

    /** Cálcula o hash SHA1 do array de bytes fornecido. */
    public static byte[] toSHA1(byte[] bytes) {
        MessageDigest messageDigest = getMessageDigestSHA1();
        messageDigest.update(bytes);
        return messageDigest.digest();
    }

    /** Cálcula o hash SHA1 do array de bytes fornecido. */
    public static String toSHA1Base16(InputStream input) throws IOException {
        MessageDigest messageDigest = getMessageDigestSHA1();
        byte[] buffer = new byte[10 * 1024];
        int lidos;
        while ((lidos = input.read(buffer)) != -1) {
            messageDigest.update(buffer, 0, lidos);
        }
        input.close();
        return bytesToBase16(messageDigest.digest());
    }

    /**
     * Cálcula o hash embustido na stream e reseta o calculo de hash da stream.
     */
    public static String toSHA1Base16(DigestInputStream hashCalculatorStream) {
        return bytesToBase16(hashCalculatorStream.getMessageDigest().digest());
    }

    /**
     * Retorna uma stream que calcula o hash SHA1 na medida que os dados são
     * lidos da stream original.
     */
    public static DigestInputStream toSHA1InputStream(InputStream source) {
        return new DigestInputStream(source, getMessageDigestSHA1());
    }

    /**
     * Cálcula o hash SHA1 do array de bytes fornecido devolvendo um string em
     * base 16 (Hexadecimal).
     */
    public static String toSHA1Base16(byte[] bytes) {
        return bytesToBase16(toSHA1(bytes));
    }

    final private static char[] hexArray = "0123456789abcdef".toCharArray();

    /** Gera string em base 16 (tudo minusculo) dos bytes fornecidos. */
    public static String bytesToBase16(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    // public static void main(String[] args) throws IOException {
    // File f = new File("d:\\temp\\singular\\Singular20151022.iso");
    // System.out.println(toSHA1Base16(new FileInputStream(f)));
    // }
}
