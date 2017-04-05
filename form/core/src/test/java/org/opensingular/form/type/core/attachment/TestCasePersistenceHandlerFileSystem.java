package org.opensingular.form.type.core.attachment;

import org.opensingular.form.type.core.attachment.handlers.FileSystemAttachmentPersistenceHandler;

public class TestCasePersistenceHandlerFileSystem extends TestCasePersistenceHandlerBase {

    @Override
    protected IAttachmentPersistenceHandler setupHandler() {
        return new FileSystemAttachmentPersistenceHandler(tmpProvider.createTempDir());
    }

}
