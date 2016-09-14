/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.type.core.attachment.handlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestInputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.IOUtils;

import br.net.mirante.singular.commons.base.SingularException;
import br.net.mirante.singular.commons.base.SingularUtil;
import br.net.mirante.singular.form.SingularFormException;
import br.net.mirante.singular.form.io.HashUtil;
import br.net.mirante.singular.form.io.IOUtil;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentRef;

/**
 * This handler persists uploaded files in the filesystem. You mus inform which
 * folder should be used to store files into. This works great not only for
 * temporary files but also for definitive files. It's worth noticing that files
 * are stored with its content SHA-1 hash as its name. Also, all files are
 * stored ZIP compressed. Usage is as follows: <code>
 * SDocument sdocument = instance.getDocument();
 * sdocument.setAttachmentPersistenceHandler(new ServiceRef<IAttachmentPersistenceHandler>() {
 * public IAttachmentPersistenceHandler get() {
 * return new FileSystemAttachmentHandler("/tmp");
 * }
 * });
 * </code>
 *
 * @author Fabricio Buzeto
 */
@SuppressWarnings("serial")
public class FileSystemAttachmentHandler implements IAttachmentPersistenceHandler<FileSystemAttachmentRef> {

    protected static final String INFO_SUFFIX = ".INFO";

    private File folder;

    public FileSystemAttachmentHandler(String folder) {
        this(new File(folder));
    }

    public FileSystemAttachmentHandler(File folder) {
        this.folder = folder;
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

    @Override
    public FileSystemAttachmentRef addAttachment(File file, long length, String name) {
        try (FileInputStream fis = new FileInputStream(file)){
            return addAttachment(fis, length, name);
        } catch (Exception e) {
            throw new SingularFormException("Erro lendo origem de dados", e);
        }
    }

    private FileSystemAttachmentRef addAttachment(InputStream origin, long originLength, String name) {
        String id = UUID.randomUUID().toString();
        File temp = findFileFromId(id);
        try (OutputStream fos = IOUtil.newBuffredOutputStream(temp);
             DigestInputStream inHash = HashUtil.toSHA1InputStream(IOUtil.newBuffredInputStream(origin));
             OutputStream infoFOS = IOUtil.newBuffredOutputStream(infoFileFromId(id))) {
            IOUtils.copy(inHash, fos);
            String sha1 = HashUtil.bytesToBase16(inHash.getMessageDigest().digest());
            IOUtil.writeLines(infoFOS, sha1, String.valueOf(originLength), name);
            return newRef(id, sha1, temp.getAbsolutePath(), originLength, name);
        } catch (Exception e) {
            throw new SingularException(e);
        }
    }

    @Override
    public FileSystemAttachmentRef copy(IAttachmentRef toBeCopied) {
        try (InputStream is = toBeCopied.newInputStream()){
            return addAttachment(is, toBeCopied.getSize(), toBeCopied.getName());
        } catch (Exception e) {
            throw new SingularException(e);
        }
    }

    @Override
    public Collection<FileSystemAttachmentRef> getAttachments() {
        LinkedList<FileSystemAttachmentRef> result = new LinkedList<>();
        File[] files = folder.listFiles();
        if (files == null) {
            return result;
        }
        for (File f : files) {
            if (f.isFile() && f.exists() && !f.getName().endsWith(INFO_SUFFIX)) {
                result.add(toRef(f));
            }
        }
        return result;
    }

    @Override
    public IAttachmentRef getAttachment(String fileId) {
        if(fileId != null){
            File file = findFileFromId(fileId);
            if (file.exists()) {
                return toRef(file);
            }
        }
        return null;
    }

    protected File findFileFromId(String fileId) {
        return new File(folder, fileId);
    }

    protected File infoFileFromId(String fileId) {
        return new File(folder, fileId + INFO_SUFFIX);
    }

    private FileSystemAttachmentRef toRef(File file) {
        try {
            List<String> lines = IOUtil.readLines(new File(file.getAbsolutePath() + INFO_SUFFIX));
            return newRef(file.getName(), lines.get(0), file.getAbsolutePath(), Long.valueOf(lines.get(1)), lines.get(2));
        } catch (Exception e) {
            throw SingularUtil.propagate(e);
        }
    }

    private FileSystemAttachmentRef newRef(String id, String hash, String filePath, long length, String name) {
        return new FileSystemAttachmentRef(id, hash, filePath, length, name);
    }

    @Override
    public void deleteAttachment(String fileId) {
        if (fileId == null) return;
        File file = findFileFromId(fileId);
        file.delete();
        File infoFile = infoFileFromId(fileId);
        infoFile.delete();
    }
}
