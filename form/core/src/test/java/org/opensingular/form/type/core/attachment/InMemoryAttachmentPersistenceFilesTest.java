package org.opensingular.form.type.core.attachment;

import org.opensingular.form.type.core.attachment.handlers.InMemoryAttachmentPersistenceHandler;

public class InMemoryAttachmentPersistenceFilesTest extends BaseAttachmentPersistenceFilesTest {

    public InMemoryAttachmentPersistenceFilesTest(byte[] content, String hash) {
        super(content, hash);
    }

    @Override
    protected IAttachmentPersistenceHandler createHandler() {
        return new InMemoryAttachmentPersistenceHandler(tmpProvider.createTempDir());
    }


}
