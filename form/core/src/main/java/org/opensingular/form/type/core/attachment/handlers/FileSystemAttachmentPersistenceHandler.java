/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.type.core.attachment.handlers;

import org.apache.commons.io.IOUtils;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.io.HashUtil;
import org.opensingular.form.io.IOUtil;
import org.opensingular.form.type.core.attachment.AttachmentCopyContext;
import org.opensingular.form.type.core.attachment.IAttachmentPersistenceHandler;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.base.SingularUtil;
import org.opensingular.lib.commons.util.TempFileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.security.DigestInputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * This handler persists uploaded files in the filesystem. You mus inform which
 * folder should be used to store files into. This works great not only for
 * temporary files but also for definitive files. It's worth noticing that files
 * are stored with its content SHA-1 hash as its name. Also, all files are
 * stored ZIP compressed. Usage is as follows: <code>
 * SDocument sdocument = instance.getDocument();
 * sdocument.setAttachmentPersistenceHandler(new ServiceRef<IAttachmentPersistenceHandler>() {
 * public IAttachmentPersistenceHandler get() {
 * return new FileSystemAttachmentPersistenceHandler("/tmp");
 * }
 * });
 * </code>
 *
 * @author Fabricio Buzeto
 */
@SuppressWarnings("serial")
public class FileSystemAttachmentPersistenceHandler
        implements IAttachmentPersistenceHandler<FileSystemAttachmentRef>, Serializable {

    protected static final String INFO_SUFFIX = ".INFO";

    private final File folder;

    private static final Logger LOGGER = Logger.getLogger(FileSystemAttachmentPersistenceHandler.class.getName());

    public FileSystemAttachmentPersistenceHandler(String folder) {
        this(new File(folder));
    }

    public FileSystemAttachmentPersistenceHandler(File folder) {
        this.folder = folder;
    }

    /**
     * @return Creates a temporary handler for temporary files.
     * @throws IOException
     */
    public static FileSystemAttachmentPersistenceHandler newTemporaryHandler() throws IOException {
        return new FileSystemAttachmentPersistenceHandler(createTemporaryFolder());
    }

    private static File createTemporaryFolder() throws IOException {
        File tmpDir = Files.createTempDirectory("singular").toFile();
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
        try (FileOutputStream f1 = new FileOutputStream(temp);
             OutputStream fos = IOUtil.newBufferedOutputStream(f1);
             DigestInputStream inHash = HashUtil.toSHA1InputStream(IOUtil.newBuffredInputStream(origin));
             OutputStream infoFOS = IOUtil.newBufferedOutputStream(infoFileFromId(id))) {
            IOUtils.copy(inHash, fos);
            String sha1 = HashUtil.bytesToBase16(inHash.getMessageDigest().digest());
            IOUtil.writeLines(infoFOS, sha1, String.valueOf(originLength), name);
            return newRef(id, sha1, temp.getAbsolutePath(), originLength, name);
        } catch (Exception e) {
            throw new SingularFormException("Erro adicionando anexo", e);
        }
    }

    @Override
    public AttachmentCopyContext<FileSystemAttachmentRef> copy(IAttachmentRef attachmentRef, SDocument document) {
        try (InputStream is = attachmentRef.getInputStream()){
            return new AttachmentCopyContext<>(addAttachment(is, attachmentRef.getSize(), attachmentRef.getName()));
        } catch (Exception e) {
            throw SingularException.rethrow(e);
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
    public FileSystemAttachmentRef getAttachment(String fileId) {
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
            return newRef(file.getName(), lines.get(0), file.getAbsolutePath(), Long.parseLong(lines.get(1)), lines.get(2));
        } catch (Exception e) {
            throw SingularUtil.propagate(e);
        }
    }

    private FileSystemAttachmentRef newRef(String id, String hash, String filePath, long length, String name) {
        return new FileSystemAttachmentRef(id, hash, filePath, length, name);
    }

    @Override
    public void deleteAttachment(String key, SDocument document) {
        if (key != null) {
            TempFileUtils.deleteOrException(findFileFromId(key), getClass());
            TempFileUtils.deleteOrException(infoFileFromId(key), getClass());
        }
    }
}
