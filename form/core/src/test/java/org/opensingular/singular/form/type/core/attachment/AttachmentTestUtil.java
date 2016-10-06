package org.opensingular.singular.form.type.core.attachment;

import org.apache.commons.io.IOUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class AttachmentTestUtil {

    public static File writeBytesToTempFile(byte[] bytes) throws IOException {
        File f = File.createTempFile("testeteste", UUID.randomUUID().toString());
        f.deleteOnExit();
        try (InputStream in = new ByteArrayInputStream(bytes);
             OutputStream out = new BufferedOutputStream(new FileOutputStream(f))
        ) {
            IOUtils.copy(in, out);
        }
        return f;
    }

    public static File writeBytesToTempFile(InputStream inParam) throws IOException {
        File f = File.createTempFile("testeteste", UUID.randomUUID().toString());
        f.deleteOnExit();
        try (InputStream in = inParam;
             OutputStream out = new BufferedOutputStream(new FileOutputStream(f));
        ) {
            IOUtils.copy(in, out);
        }
        return f;
    }
}
