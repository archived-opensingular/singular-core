package br.net.mirante.singular.form.type.core.attachment;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class AttachmentTestUtil {

    public static File writeBytesToTempFile(byte[] bytes) throws IOException {
        File f = File.createTempFile("testeteste", UUID.randomUUID().toString());
        f.deleteOnExit();
        IOUtils.copy(new ByteArrayInputStream(bytes), new FileOutputStream(f));
        return f;
    }

    public static File writeBytesToTempFile(InputStream in) throws IOException {
        File f = File.createTempFile("testeteste", UUID.randomUUID().toString());
        f.deleteOnExit();
        IOUtils.copy(in, new FileOutputStream(f));
        return f;
    }
}
