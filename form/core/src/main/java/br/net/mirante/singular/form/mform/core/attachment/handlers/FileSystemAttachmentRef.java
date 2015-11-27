package br.net.mirante.singular.form.mform.core.attachment.handlers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;

import com.google.common.base.Throwables;

import br.net.mirante.singular.form.mform.core.attachment.IAttachmentRef;

@SuppressWarnings("serial")
public class FileSystemAttachmentRef implements IAttachmentRef, Serializable {

    private String id, hashSHA1, path;
    private Integer size;

    public FileSystemAttachmentRef(String id, String hashSHA1, String path, Integer size) {
        this.id = id;
        this.hashSHA1 = hashSHA1;
        this.path = path;
        this.size = size;
    }

    public String getId() {
        return id;
    }
    
    public String getHashSHA1() {
        return hashSHA1;
    }

    public String getPath() {
        return path;
    }

    public Integer getSize() {
        return size;
    }

    @Override
    public InputStream getContent() {
        try {
            return new FileInputStream(path);
        } catch (FileNotFoundException e) {
            throw Throwables.propagate(e);
        }
    }
}
