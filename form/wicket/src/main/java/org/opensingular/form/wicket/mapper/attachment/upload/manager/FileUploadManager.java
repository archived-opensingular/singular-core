package org.opensingular.form.wicket.mapper.attachment.upload.manager;

import org.apache.commons.lang3.ObjectUtils;
import org.opensingular.form.type.core.attachment.IAttachmentPersistenceHandler;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.form.wicket.mapper.attachment.upload.AttachmentKey;
import org.opensingular.form.wicket.mapper.attachment.upload.UploadPathHandler;
import org.opensingular.form.wicket.mapper.attachment.upload.factory.AttachmentKeyFactory;
import org.opensingular.form.wicket.mapper.attachment.upload.info.FileUploadInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.info.FileUploadInfoRepository;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadInfoRepository;
import org.opensingular.form.wicket.mapper.attachment.upload.supplier.TemporaryAttachmentPersistenceHandlerSupplier;
import org.opensingular.lib.commons.util.Loggable;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;

public class FileUploadManager implements Serializable, HttpSessionBindingListener, Loggable {

    public static final String SESSION_KEY = FileUploadManager.class.getName();

    private AttachmentKeyFactory     attachmentKeyFactory;
    private UploadInfoRepository     uploadInfoRepository;
    private UploadPathHandler        uploadPathHandler;
    private FileUploadInfoRepository fileUploadInfoRepository;

    public FileUploadManager() {
        this(new AttachmentKeyFactory(), new UploadInfoRepository(), new UploadPathHandler(), new FileUploadInfoRepository());
    }

    public FileUploadManager(AttachmentKeyFactory attachmentKeyFactory, UploadInfoRepository uploadInfoRepository,
                             UploadPathHandler uploadPathHandler, FileUploadInfoRepository fileUploadInfoRepository) {
        this.attachmentKeyFactory = attachmentKeyFactory;
        this.uploadInfoRepository = uploadInfoRepository;
        this.uploadPathHandler = uploadPathHandler;
        this.fileUploadInfoRepository = fileUploadInfoRepository;
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

        final AttachmentKey newkey = attachmentKeyFactory.get();

        uploadInfoRepository.add(new UploadInfo(newkey,
                ObjectUtils.defaultIfNull(maxFileSize, Long.MAX_VALUE),
                ObjectUtils.defaultIfNull(maxFileCount, 1),
                ObjectUtils.defaultIfNull(allowedFileTypes, Collections.emptyList()),
                temporaryAttachmentPersistenceHandlerSupplier));

        return newkey;
    }

    public synchronized Optional<UploadInfo> findUploadInfo(AttachmentKey uploadId) {
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

        final Path                          path;
        final IAttachmentPersistenceHandler handler;
        final IAttachmentRef                attachment;
        final FileUploadInfo                info;
        final File                          file;

        handler = uploadInfo.getPersistenceHandlerSupplier().get();
        path = uploadPathHandler.getLocalFilePath(attachmentKeyFactory.get().toString());
        file = path.toFile();

        file.deleteOnExit();

        Files.copy(input, path);

        attachment = handler.addAttachment(file, Files.size(path), fileName);
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