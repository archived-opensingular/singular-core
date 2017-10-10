package org.opensingular.lib.commons.io;

import org.opensingular.lib.commons.util.Loggable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Keeps a reference of a temp file and delete it as soon as the stream
 * is closed.
 */
public class TempFileInputStream extends FileInputStream implements Loggable{

    private File tempFile;

    public TempFileInputStream(File file) throws FileNotFoundException {
        super(file);
        this.tempFile = file;
    }


    @Override
    public void close() throws IOException {
        super.close();
        String  name               = tempFile.getName();
        boolean deletedWithSuccess = tempFile.delete();
        if(!deletedWithSuccess){
            getLogger().warn("Não foi possivel deletar o arquivo {} corretamente", name);
        }
    }

}