package br.net.mirante.singular.commons.base;

import java.security.MessageDigest;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

public final class SingularUtil {

    public static String toSHA1(Object object) {
        return toSHA1(object.toString().getBytes());
    }

    public static String toSHA1(byte[] bytes) {
        MessageDigest sha1Digest = DigestUtils.getSha1Digest();
        sha1Digest.update(bytes);

        return Hex.encodeHexString(sha1Digest.digest());
    }
}
