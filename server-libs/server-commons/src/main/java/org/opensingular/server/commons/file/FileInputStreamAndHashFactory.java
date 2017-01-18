package org.opensingular.server.commons.file;


import org.opensingular.form.io.HashUtil;
import org.opensingular.form.io.IOUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.DigestInputStream;

public class FileInputStreamAndHashFactory {

    public FileInputStreamAndHash get(File file) throws FileNotFoundException {
        DigestInputStream din  = HashUtil.toSHA1InputStream(IOUtil.newBuffredInputStream(file));
        String            hash = HashUtil.bytesToBase16(din.getMessageDigest().digest());
        return new FileInputStreamAndHash(din, hash);
    }

}
