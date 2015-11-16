package br.net.mirante.singular.form.mform.core.attachment.handlers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.zip.InflaterInputStream;

import com.google.common.base.Throwables;

import br.net.mirante.singular.form.mform.core.attachment.IAttachmentRef;

@SuppressWarnings("serial")
public class FileSystemAttachmentRef implements IAttachmentRef, Serializable {

    private String hashSHA1, path;
    private Integer size;
    
    public FileSystemAttachmentRef(String hashSHA1, String path, Integer size) {
	this.hashSHA1 = hashSHA1;
	this.path = path;
	this.size = size;
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
	    return new InflaterInputStream(new FileInputStream(path));
	} catch (FileNotFoundException e) {
	    throw Throwables.propagate(e);
	}
    }
}