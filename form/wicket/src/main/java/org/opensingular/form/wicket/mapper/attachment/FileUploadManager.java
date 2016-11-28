package org.opensingular.form.wicket.mapper.attachment;

import static java.util.stream.Collectors.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.wicket.util.collections.ConcurrentHashSet;
import org.opensingular.form.io.HashUtil;
import org.opensingular.lib.commons.base.SingularProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUploadManager implements Serializable, HttpSessionBindingListener {

    public static final String                      FILE_HASH_ALGORITHM = "SHA-1";

    private static final Logger                     log                 = LoggerFactory.getLogger(FileUploadManager.class);
    private static final String                     SESSION_KEY         = FileUploadManager.class.getName();

    private volatile Path                           baseDirPath;

    private final ConcurrentHashSet<UploadInfo>     registeredUploads   = new ConcurrentHashSet<>();
    private final ConcurrentHashSet<FileUploadInfo> uploadedFiles       = new ConcurrentHashSet<>();

    ///////////////////////////////////////////////////////////////////////////
    // 
    ///////////////////////////////////////////////////////////////////////////

    public static FileUploadManager get(HttpSession session) {
        synchronized (session) {
            FileUploadManager manager = (FileUploadManager) session.getAttribute(SESSION_KEY);
            if (manager == null) {
                manager = new FileUploadManager();
                session.setAttribute(SESSION_KEY, manager);
                log.debug("Manager created: SESSION_ID = " + session.getId());
            }
            return manager;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // UPLOADS
    ///////////////////////////////////////////////////////////////////////////

    public UUID createUpload(Optional<Long> maxFileSize,
                             Optional<Integer> maxFileCount,
                             Optional<Collection<String>> allowedFileTypes) {

        log.debug("createUpload({},{},{})",
            maxFileSize.orElse(null),
            maxFileCount.orElse(null),
            allowedFileTypes.orElseGet(Collections::emptyList));

        final UUID uuid = UUID.randomUUID();

        registeredUploads.add(new UploadInfo(
            uuid,
            maxFileSize.orElse(Long.MAX_VALUE),
            maxFileCount.orElse(1),
            allowedFileTypes.orElseGet(Collections::emptyList)));
        return uuid;
    }

    public synchronized Optional<UploadInfo> findUploadInfo(UUID uploadId) {
        log.debug("findUploadInfo({})", uploadId);

        return registeredUploads.stream()
            .filter(it -> it.uploadId.equals(uploadId))
            .map(it -> it.touch())
            .findFirst();
    }

    ///////////////////////////////////////////////////////////////////////////
    // FILES
    ///////////////////////////////////////////////////////////////////////////

    public Optional<FileUploadInfo> findFileInfo(UUID fileId) {
        log.debug("findFileInfo({})", fileId);
        return uploadedFiles.stream()
            .filter(it -> it.fileId.equals(fileId))
            .findAny();
    }

    public <R> Optional<R> consumeFile(UUID fileId, Function<File, R> callback) {
        log.debug("consumeFile({})", fileId);

        final Optional<FileUploadInfo> fileInfo = findFileInfo(fileId);
        if (fileInfo.isPresent()) {
            final Optional<File> file = findLocalFile(fileInfo.get().fileId);
            if (file.isPresent()) {
                R result = callback.apply(file.get());
                deleteFile(fileInfo.get());
                return Optional.ofNullable(result);
            }
        }
        return Optional.empty();
    }

    public Optional<File> findLocalFile(UUID fileId) {
        log.debug("findLocalFile({})", fileId);
        return findFileInfo(fileId)
            .map(it -> getLocalFilePath(it))
            .map(it -> it.toFile());
    }

    public List<FileUploadInfo> listFileInfo(UUID uploadId) {
        log.debug("listFileInfo({})", uploadId);
        return uploadedFiles.stream()
            .filter(it -> it.uploadId.equals(uploadId))
            .collect(toList());
    }

    public FileUploadInfo createFile(UUID uploadId, String originalFilename, InputStream input) throws IOException {
        log.debug("createFile({},{},{})", uploadId, originalFilename, input);

        final UUID fileId = UUID.randomUUID();

        final Path file = getLocalFilePath(fileId);
        file.toFile().deleteOnExit();

        final CountingInputStream countStream = new CountingInputStream(input);
        final DigestInputStream digestStream = new DigestInputStream(countStream, getMessageDigest());

        Files.copy(digestStream, file);

        final FileUploadInfo fileInfo = new FileUploadInfo(
            uploadId,
            fileId,
            originalFilename,
            countStream.getByteCount(),
            HashUtil.bytesToBase16(digestStream.getMessageDigest().digest()),
            System.currentTimeMillis());

        uploadedFiles.add(fileInfo);
        return fileInfo;
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
        } finally {}
    }

    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE
    ///////////////////////////////////////////////////////////////////////////

    private Path getLocalFilePath(FileUploadInfo fileInfo) {
        return getLocalFilePath(fileInfo.fileId);
    }
    private Path getLocalFilePath(UUID fileId) {
        return baseDir().resolve(fileId.toString());
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

    protected MessageDigest getMessageDigest() {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(FILE_HASH_ALGORITHM);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }
        return messageDigest;
    }

    private void deleteLocalFiles() {
        new ArrayList<>(uploadedFiles).stream()
            .forEach(it -> deleteFile(it));
    }

    protected void gc() {
        final FileUploadConfig config = new FileUploadConfig(SingularProperties.get());
        final Instant timestampLimit = Instant.now().minus(Duration.ofMillis(config.globalMaxFileAge));

        final Set<FileUploadInfo> oldFiles = uploadedFiles.stream()
            .filter(it -> Instant.ofEpochMilli(it.timestamp).isBefore(timestampLimit))
            .collect(toSet());
        for (FileUploadInfo file : oldFiles)
            deleteFile(file);

        while (uploadedFiles.size() > config.globalMaxFileCount) {
            FileUploadInfo oldest = uploadedFiles.stream()
                .max((a, b) -> NumberUtils.compare(a.timestamp, b.timestamp))
                .get();
            deleteFile(oldest);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // HttpSessionBoundObject
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void valueBound(HttpSessionBindingEvent event) {}

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        deleteLocalFiles();
    }
}
