package org.opensingular.form.type.core.attachment;

import org.opensingular.form.type.core.attachment.handlers.FileSystemAttachmentPersistenceHandler;

public class FileSystemAttachmentPersistenceFilesTest extends BaseAttachmentPersistenceFilesTest {

    public FileSystemAttachmentPersistenceFilesTest(byte[] content, String hash) {
        super(content, hash);
    }

    @Override
    protected IAttachmentPersistenceHandler createHandler() throws Exception {
        return new FileSystemAttachmentPersistenceHandler(tmpProvider.createTempDir());
    }


}
