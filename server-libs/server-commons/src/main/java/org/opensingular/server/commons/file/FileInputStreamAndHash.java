package org.opensingular.server.commons.file;


import java.io.InputStream;

public class FileInputStreamAndHash implements AutoCloseable {

    private InputStream inputStream;
    private String      hash;

    FileInputStreamAndHash(InputStream inputStream, String hash) {
        this.inputStream = inputStream;
        this.hash = hash;
    }

    @Override
    public void close() throws Exception {
        inputStream.close();
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public String getHash() {
        return hash;
    }

}