package br.net.mirante.singular.form.io;

import br.net.mirante.singular.form.io.HashUtil;

import java.io.InputStream;
import java.security.DigestInputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterInputStream;

public class HashAndCompressInputStream extends DeflaterInputStream {


    public HashAndCompressInputStream(InputStream in) {
        super(HashUtil.toSHA1InputStream(in), new Deflater(Deflater.BEST_COMPRESSION));
    }


    public String getHashSHA1() {
        return HashUtil.bytesToBase16(((DigestInputStream) this.in).getMessageDigest().digest());
    }

}
