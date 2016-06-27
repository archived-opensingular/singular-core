/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.type.core.attachment.handlers;

import br.net.mirante.singular.commons.base.SingularException;
import br.net.mirante.singular.form.io.HashUtil;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentRef;
import com.google.common.base.Throwables;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

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
public class FileSystemAttachmentHandler extends AbstractAttachmentPersistenceHandler {

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
    public IAttachmentRef addAttachment(File file, long length) {
        try {
            return addAttachment(new FileInputStream(file), length);
        } catch (Exception e) {
            throw new SingularException(e);
        }
    }

    private IAttachmentRef addAttachment(InputStream origin, long originLength) throws IOException {
        String id = UUID.randomUUID().toString();
        File temp = fileFromId(id);
        String sha1 = hashAndCopyCompressed(origin, new FileOutputStream(temp));
        File destination = fileFromId(id);
        temp.renameTo(destination);
        return newRef(id, sha1, destination.getAbsolutePath(), originLength);
    }

    @Override
    public IAttachmentRef copy(IAttachmentRef toBeCopied) {
        try {
            return addAttachment(toBeCopied.newInputStream(), toBeCopied.getSize());
        } catch (Exception e) {
            throw new SingularException(e);
        }
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
    public IAttachmentRef getAttachment(String fileId) {
        try {
            File file = fileFromId(fileId);
            if (file.exists()) {
                return toRef(file);
            }
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
        return null;
    }

    protected File fileFromId(String fileId) {
        return new File(folder, fileId);
    }

    private FileSystemAttachmentRef toRef(File file) throws Exception {
        FileInputStream in = new FileInputStream(file);
        return newRef(file.getName(), HashUtil.toSHA1Base16(in), file.getAbsolutePath(), -1);
    }

    private FileSystemAttachmentRef newRef(String name, String hash, String filePath, long length){
        return new FileSystemAttachmentRef(name, hash, filePath, length) {
            @Override
            public InputStream newInputStream() {
                return decompressStream(super.newInputStream());
            }
        };
    }

    @Override
    public void deleteAttachment(String fileId) {
        if (fileId == null) return;
        File file = fileFromId(fileId);
        file.delete();
    }
}
