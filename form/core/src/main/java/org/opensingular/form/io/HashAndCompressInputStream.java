package org.opensingular.form.io;

import java.io.InputStream;
import java.security.DigestInputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterInputStream;

/**
 * Input stream para comprimir e calcular o hash ao mesmo tempo
 * O hash, após a leitura do input stream fica disponível através do método
 * {@link HashAndCompressInputStream#getHashSHA1()}
 *
 * O algoritmo de hash utilizado é o SHA1 e o nível de compressão é o máximo.
 */
public class HashAndCompressInputStream extends DeflaterInputStream {




    public HashAndCompressInputStream(InputStream in) {
        super(HashUtil.toSHA1InputStream(IOUtil.newBuffredInputStream(in)), new Deflater(Deflater.BEST_COMPRESSION));
    }


    public String getHashSHA1() {
        return HashUtil.bytesToBase16(((DigestInputStream) this.in).getMessageDigest().digest());
    }

}
