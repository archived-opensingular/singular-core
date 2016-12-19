package org.opensingular.form.wicket.mapper.attachment;

import org.apache.wicket.util.collections.ConcurrentHashSet;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.type.core.attachment.IAttachmentPersistenceHandler;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.support.spring.util.ApplicationContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public class FileUploadManager implements Serializable, HttpSessionBindingListener {

    private static final Logger log         = LoggerFactory.getLogger(FileUploadManager.class);
    public static final  String SESSION_KEY = FileUploadManager.class.getName();

    private volatile Path baseDirPath;

    private final ConcurrentHashSet<UploadInfo>     registeredUploads = new ConcurrentHashSet<>();
    private final ConcurrentHashSet<FileUploadInfo> uploadedFiles     = new ConcurrentHashSet<>();

    private final ISupplier<IAttachmentPersistenceHandler> persistenceHandler;

    public FileUploadManager() {
        this.persistenceHandler = () -> ApplicationContextProvider
                .get()
                .getBean(SDocument.FILE_TEMPORARY_SERVICE, IAttachmentPersistenceHandler.class);
    }

    public FileUploadManager(ISupplier<IAttachmentPersistenceHandler> persistenceHandler) {
        this.persistenceHandler = persistenceHandler;
    }


///////////////////////////////////////////////////////////////////////////
    // 
    ///////////////////////////////////////////////////////////////////////////

    public static synchronized FileUploadManager get(HttpSession session) {
        FileUploadManager manager = (FileUploadManager) session.getAttribute(SESSION_KEY);
        if (manager == null) {
            manager = new FileUploadManager();
            session.setAttribute(SESSION_KEY, manager);
            log.debug("Manager created: SESSION_ID = " + session.getId());
        }
        return manager;
    }

    ///////////////////////////////////////////////////////////////////////////
    // UPLOADS
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Cria um contexto de upload na servlet, atribiuindo os metadados necessarios a session
     *
     * @param maxFileSize      tamanho do arquivo
     * @param maxFileCount     maximo de arquivos
     * @param allowedFileTypes os tipos permitidos
     * @return a chave para os dados de upload
     */
    public AttachmentKey createUpload(Long maxFileSize, Integer maxFileCount, Collection<String> allowedFileTypes) {

        Optional<Long>               oMaxFileSize      = Optional.ofNullable(maxFileSize);
        Optional<Integer>            oMaxFileCount     = Optional.ofNullable(maxFileCount);
        Optional<Collection<String>> oAllowedFileTypes = Optional.ofNullable(allowedFileTypes);

        log.debug("createUpload({},{},{})",
                oMaxFileSize.orElse(null),
                oMaxFileCount.orElse(null),
                oAllowedFileTypes.orElseGet(Collections::emptyList)
        );

        final AttachmentKey newkey = AttachmentKey.newKey();

        registeredUploads.add(new UploadInfo(
                newkey,
                oMaxFileSize.orElse(Long.MAX_VALUE),
                oMaxFileCount.orElse(1),
                oAllowedFileTypes.orElseGet(Collections::emptyList)
        ));

        return newkey;
    }


    public synchronized Optional<UploadInfo> findUploadInfo(AttachmentKey uploadId) {
        log.debug("findUploadInfo({})", uploadId);

        return registeredUploads.stream()
                .filter(it -> it.uploadId.equals(uploadId))
                .map(UploadInfo::touch)
                .findFirst();
    }

    ///////////////////////////////////////////////////////////////////////////
    // FILES
    ///////////////////////////////////////////////////////////////////////////

    public Optional<FileUploadInfo> findFileInfo(String fid) {
        log.debug("findFileInfo({})", fid);
        return uploadedFiles.stream()
                .filter(it -> it.getAttachmentRef().getId().equals(fid))
                .findAny();
    }

    public <R> Optional<R> consumeFile(String fid, Function<IAttachmentRef, R> callback) {
        log.debug("consumeFile({})", fid);

        final Optional<FileUploadInfo> fileInfo = findFileInfo(fid);
        if (fileInfo.isPresent()) {
            IAttachmentRef ref = fileInfo.get().getAttachmentRef();
            if (ref != null) {
                R result = callback.apply(ref);
                deleteFile(fileInfo.get());
                return Optional.ofNullable(result);
            }
        }

        return Optional.empty();
    }

    public Optional<File> findLocalFile(String fid) {
        log.debug("findLocalFile({})", fid);
        return findFileInfo(fid)
                .map(this::getLocalFilePath)
                .map(Path::toFile);
    }

    public List<FileUploadInfo> listFileInfo(String uid) {
        log.debug("listFileInfo({})", uid);
        return uploadedFiles.stream()
                .filter(it -> uid.equals(it.getAttachmentRef().getId()))
                .collect(toList());
    }

    public FileUploadInfo createFile(AttachmentKey key, String fileName, InputStream input) throws IOException {

        log.debug("createFile({},{},{})", key, fileName, input);

        final Path                          path;
        final IAttachmentPersistenceHandler handler;
        final IAttachmentRef                attachment;
        final FileUploadInfo                info;
        final File                          file;

        handler = getTemporaryAttachmentPersistenceHandler();
        path = getLocalFilePath(AttachmentKey.newKey().toString());
        file = path.toFile();

        file.deleteOnExit();

        Files.copy(input, path);

        attachment = handler.addAttachment(file, Files.size(path), fileName);
        info = new FileUploadInfo(attachment);

        uploadedFiles.add(info);

        return info;
    }

    public void writeFileTo(FileUploadInfo fileInfo, OutputStream output) throws IOException {
        log.debug("writeFileTo({})", output);
        Path filePath = getLocalFilePath(fileInfo);
        Files.copy(filePath, output);
    }

    private void deleteFile(FileUploadInfo fileInfo) {
        log.debug("deleteFile({})", fileInfo);
        try {
            Files.deleteIfExists(getLocalFilePath(fileInfo));
            uploadedFiles.remove(fileInfo);

        } catch (IOException ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE
    ///////////////////////////////////////////////////////////////////////////

    private Path getLocalFilePath(FileUploadInfo fileInfo) {
        return getLocalFilePath(fileInfo.getAttachmentRef().getId());
    }

    private Path getLocalFilePath(String id) {
        return baseDir().resolve(id);
    }

    private synchronized Path baseDir() {
        if (baseDirPath == null || !Files.exists(baseDirPath)) {
            try {
                baseDirPath = Files.createTempDirectory(FileUploadManager.class.getSimpleName() + "_");
                baseDirPath.toFile().deleteOnExit();
            } catch (IOException ex) {
                log.warn(ex.getMessage(), ex);
            }
        }
        return baseDirPath;
    }

    private void deleteLocalFiles() {
        new ArrayList<>(uploadedFiles).forEach(this::deleteFile);
    }


    ///////////////////////////////////////////////////////////////////////////
    // HttpSessionBoundObject
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        deleteLocalFiles();
    }

    public IAttachmentPersistenceHandler getTemporaryAttachmentPersistenceHandler() {
        return persistenceHandler.get();
    }

}
