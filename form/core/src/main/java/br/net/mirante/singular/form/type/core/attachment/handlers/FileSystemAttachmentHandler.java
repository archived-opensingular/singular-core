/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.type.core.attachment.handlers;

import br.net.mirante.singular.form.SingularFormException;
import br.net.mirante.singular.form.io.HashUtil;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentRef;
import com.google.common.base.Throwables;
import com.google.common.io.ByteStreams;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Formatter;
import java.util.LinkedList;

/**
 * This handler persists uploaded files in the filesystem. You mus inform which
 * folder should be used to store files into. This works great not only for
 * temporary files but also for definitive files. It's worth noticing that files
 * are stored with its content SHA-1 hash as its name. Also, all files are
 * stored ZIP compressed. Usage is as follows: <code>
 *     SDocument sdocument = instance.getDocument();
 *     sdocument.setAttachmentPersistenceHandler(new ServiceRef<IAttachmentPersistenceHandler>() {
 *        public IAttachmentPersistenceHandler get() {
 *             return new FileSystemAttachmentHandler("/tmp");
 *          } 
 *      });
 * </code>
 * 
 * @author Fabricio Buzeto
 */
@SuppressWarnings("serial")
public class FileSystemAttachmentHandler implements IAttachmentPersistenceHandler {

    private File folder;
    private IdGenerator generator = new IdGenerator();

    public FileSystemAttachmentHandler(String folder) {
        this(new File(folder));
    }

    public FileSystemAttachmentHandler(File folder) {
        this.folder = folder;
    }

    public void setGenerator(IdGenerator generator) {
        this.generator = generator;
    }
    
    @Override
    public Collection<? extends IAttachmentRef> getAttachments() {
        LinkedList<IAttachmentRef> result = new LinkedList<>();
        File[] files = folder.listFiles();
        if (files == null) {
            return result;
        }
        for (File f : files) {
            if (f.isFile() && f.exists()) {
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
            File file = fileFromId(hashId);
            if(file.exists()){
                return toRef(file);
            }
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
        return null;
    }

    private File fileFromId(String hashId) {
        return new File(folder, hashId);
    }

    private FileSystemAttachmentRef toRef(File file) throws Exception {
        FileInputStream in = new FileInputStream(file);
        return new FileSystemAttachmentRef(file.getName(), toSha1HexString(in), 
            file.getAbsolutePath(), (int) file.length());
    }

    private String toSha1HexString(InputStream inflated) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        byte[] hexSha1 = md.digest(ByteStreams.toByteArray(inflated));
        return byteArray2Hex(hexSha1);
    }

    private static String byteArray2Hex(final byte[] hash) {
        try (Formatter formatter = new Formatter()) {
            for (byte b : hash) {
                formatter.format("%02x", b);
            }
            return formatter.toString();
        }
    }

    @Override
    public void deleteAttachment(String hashId) {
        if(hashId == null) return ; 
        File file = fileFromId(hashId);
        file.delete();
    }

    @Override
    public IAttachmentRef addAttachment(byte[] content) {
        try {
            String sha1 = HashUtil.toSHA1Base16(content);
            String id = generator.generate(content);
            File dest = fileFromId(id);
            FileOutputStream out = new FileOutputStream(dest);
            out.write(content);
            out.close();
            return new FileSystemAttachmentRef(id, sha1, dest.getAbsolutePath(), 
                content.length);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public IAttachmentRef addAttachment(InputStream in) {
        try {
            return addAttachment(ByteStreams.toByteArray(in));
        } catch (IOException e) {
            throw new SingularFormException("Erro lendo origem de dados", e);
        }
    }

    /**
     * @return Creates a temporary handler for temporary files.
     * @throws IOException
     */
    public static FileSystemAttachmentHandler newTemporaryHandler() throws IOException {
        return new FileSystemAttachmentHandler(createTemporaryFolder());
    }

    public static File createTemporaryFolder() throws IOException {
        File tmpDir = File.createTempFile("singular", "showcase");
        tmpDir.delete();
        tmpDir.mkdir();
        tmpDir.deleteOnExit();
        return tmpDir;
    }
}
