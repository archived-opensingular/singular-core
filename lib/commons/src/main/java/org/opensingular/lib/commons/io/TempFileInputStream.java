package org.opensingular.lib.commons.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Keeps a reference of a temp file and delete it as soon as the stream
 * is closed.
 */
public class TempFileInputStream extends FileInputStream {

    private File tempFile;

    public TempFileInputStream(File file) throws FileNotFoundException {
        super(file);
        this.tempFile = file;
    }


    @Override
    public void close() throws IOException {
        super.close();
        tempFile.delete();
    }
}
