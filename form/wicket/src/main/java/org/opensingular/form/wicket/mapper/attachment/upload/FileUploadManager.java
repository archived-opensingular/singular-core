/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package org.opensingular.form.wicket.mapper.attachment.upload;

import org.apache.commons.lang3.ObjectUtils;
import org.opensingular.form.io.HashUtil;
import org.opensingular.form.type.core.attachment.IAttachmentPersistenceHandler;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.form.wicket.mapper.attachment.upload.info.FileUploadInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.info.FileUploadInfoRepository;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadInfoRepository;
import org.opensingular.lib.commons.util.Loggable;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;

public class FileUploadManager implements Serializable, HttpSessionBindingListener, Loggable {

    public static final String SESSION_KEY = FileUploadManager.class.getName();

    private AttachmentKeyFactory attachmentKeyFactory;
    private UploadInfoRepository uploadInfoRepository;
    private UploadPathHandler uploadPathHandler;
    private FileUploadInfoRepository fileUploadInfoRepository;


    public FileUploadManager() {
        this.attachmentKeyFactory = makeAttachmentKeyFactory();
        this.uploadInfoRepository = makeUploadInfoRepository();
        this.uploadPathHandler = makeUploadPathHandler();
        this.fileUploadInfoRepository = makeFileUploadInfoRepository();
    }

    protected AttachmentKeyFactory makeAttachmentKeyFactory() {
        return new AttachmentKeyFactory();
    }

    protected UploadInfoRepository makeUploadInfoRepository() {
        return new UploadInfoRepository();
    }

    protected UploadPathHandler makeUploadPathHandler() {
        return new UploadPathHandler();
    }

    protected FileUploadInfoRepository makeFileUploadInfoRepository() {
        return new FileUploadInfoRepository();
    }

    /**
     * Cria um contexto de upload na servlet, atribiuindo os metadados necessarios a session
     *
     * @param maxFileSize      tamanho do arquivo
     * @param maxFileCount     maximo de arquivos
     * @param allowedFileTypes os tipos permitidos
     * @return a chave para os dados de upload
     */
    public AttachmentKey createUpload(Long maxFileSize, Integer maxFileCount, Collection<String> allowedFileTypes,
                                      TemporaryAttachmentPersistenceHandlerSupplier temporaryAttachmentPersistenceHandlerSupplier) {

        getLogger().debug("createUpload({},{},{})", maxFileSize, maxFileCount, allowedFileTypes);

        final AttachmentKey newkey = attachmentKeyFactory.make();

        uploadInfoRepository.add(createUploadInfo(
                maxFileSize, maxFileCount, allowedFileTypes, temporaryAttachmentPersistenceHandlerSupplier, newkey));

        return newkey;
    }

    public UploadInfo createUploadInfo(Long maxFileSize, Integer maxFileCount, Collection<String> allowedFileTypes, TemporaryAttachmentPersistenceHandlerSupplier temporaryAttachmentPersistenceHandlerSupplier, AttachmentKey newkey) {
        return new UploadInfo(newkey,
                ObjectUtils.defaultIfNull(maxFileSize, Long.MAX_VALUE),
                ObjectUtils.defaultIfNull(maxFileCount, 1),
                ObjectUtils.defaultIfNull(allowedFileTypes, Collections.emptyList()),
                temporaryAttachmentPersistenceHandlerSupplier);
    }

    public synchronized Optional<UploadInfo> findUploadInfoByAttachmentKey(AttachmentKey uploadId) {
        return uploadInfoRepository.findByAttachmentKey(uploadId);
    }

    public <R> Optional<R> consumeFile(String fid, Function<IAttachmentRef, R> callback) {
        getLogger().debug("consumeFile({})", fid);
        return fileUploadInfoRepository.findByID(fid).map(info -> {
            if (info.getAttachmentRef() != null) {
                R result = callback.apply(info.getAttachmentRef());
                deleteFile(info);
                return result;
            }
            return null;
        });
    }

    public FileUploadInfo createFile(UploadInfo uploadInfo, String fileName, InputStream input) throws IOException {

        getLogger().debug("createFile({},{},{})", uploadInfo.getUploadId(), fileName, input);

        final Path path;
        final IAttachmentPersistenceHandler handler;
        final IAttachmentRef attachment;
        final FileUploadInfo info;
        final File file;

        handler = uploadInfo.getPersistenceHandlerSupplier().get();
        path = uploadPathHandler.getLocalFilePath(attachmentKeyFactory.make().toString());
        file = path.toFile();

        file.deleteOnExit();

        DigestInputStream din = HashUtil.toSHA1InputStream(input);
        Files.copy(din, path);

        attachment = handler.addAttachment(file, Files.size(path), fileName, HashUtil.bytesToBase16(din.getMessageDigest().digest()));
        info = new FileUploadInfo(attachment);

        fileUploadInfoRepository.add(info);

        return info;
    }

    private void deleteFile(FileUploadInfo fileInfo) {
        getLogger().debug("deleteFile({})", fileInfo);
        try {
            Files.deleteIfExists(uploadPathHandler.getLocalFilePath(fileInfo));
            fileUploadInfoRepository.remove(fileInfo);
        } catch (IOException ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }
    }

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        fileUploadInfoRepository.stream().forEach(this::deleteFile);
    }

}