package br.net.mirante.singular.form.mform.core.attachment;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.zip.InflaterInputStream;

import com.google.common.base.Throwables;
import com.google.common.io.ByteStreams;
import com.google.common.io.CountingInputStream;

import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.mform.io.HashUtil;

@SuppressWarnings("serial")
public class FileSystemAttachmentHandler extends AbstractAttachmentPersistenceHandler {

    private File folder;

    public FileSystemAttachmentHandler(File folder) {
	this.folder = folder;
    }

    @Override
    public Collection<? extends IAttachmentRef> getAttachments() {
	LinkedList<IAttachmentRef> result = new LinkedList<>();
	for(File f : folder.listFiles()){
	    if(f.isFile() && f.exists()){
		try {
		    result.add(toRef(f));
		} catch (Exception e) {
		    throw Throwables.propagate(e);
		}
	    }
	}
	return result;
    }

    @Override
    public IAttachmentRef getAttachment(String hashId) {
	try {
	    return toRef(new File(folder, hashId));
	} catch (Exception e) {
	    throw Throwables.propagate(e);
	}
    }

    private FileSystemAttachmentRef toRef(File file) throws Exception {
	FileInputStream in = new FileInputStream(file);
	InflaterInputStream inflated = new InflaterInputStream(in);
	return new FileSystemAttachmentRef(toSha1HexString(inflated), 
				file.getAbsolutePath(), (int) file.length());
    }

    private String toSha1HexString(InflaterInputStream inflated) throws NoSuchAlgorithmException, IOException {
	MessageDigest md = MessageDigest.getInstance("SHA1");
	byte[] hexSha1 = md.digest(ByteStreams.toByteArray(inflated));
	return byteArray2Hex(hexSha1);
    }
    
    private static String byteArray2Hex(final byte[] hash) {
	    Formatter formatter = new Formatter();
	    try {
		for (byte b : hash) {
		    formatter.format("%02x", b);
		}
		return formatter.toString();
	    } finally {
		formatter.close();
	    }
	}

    @Override
    public void deleteAttachment(String hashId) {
	// TODO Auto-generated method stub
	
    }

    protected IAttachmentRef addAttachmentCompressed(InputStream deflateInputStream, 
	    String hashSHA16Hex, int originalLength) {
	try {
	    File dest = new File(folder, hashSHA16Hex);
	    FileOutputStream out = new FileOutputStream(dest);
	    ByteStreams.copy(deflateInputStream, out);
	    return new FileSystemAttachmentRef(hashSHA16Hex, dest.getAbsolutePath(), originalLength);
	} catch (Exception e) {
	    throw Throwables.propagate(e);
	}
    }

    protected IAttachmentRef addAttachmentCompressed(InputStream deflateInputStream, 
	    CountingInputStream inCounting, DigestInputStream hashCalculatorStream) {
	try {
	    //TODO: This does not seems right
	    ByteArrayInputStream in = new ByteArrayInputStream(
		    		ByteStreams.toByteArray(deflateInputStream));
	    return addAttachmentCompressed(in, 
                		    HashUtil.toSHA1Base16(hashCalculatorStream), 
                		    (int) inCounting.getCount());
	} catch (IOException e) {
	    throw new SingularFormException("Erro lendo origem de dados", e);
	}
    }

}

@SuppressWarnings("serial")
class FileSystemAttachmentRef implements IAttachmentRef, Serializable {

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
