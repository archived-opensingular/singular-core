package org.opensingular.singular.form.type.core.attachment;

import org.opensingular.form.type.core.attachment.IAttachmentPersistenceHandler;
import org.opensingular.form.type.core.attachment.handlers.InMemoryAttachmentPersitenceHandler;

public class InMemoryAttachmentPersistenceFilesTest extends BaseAttachmentPersistenceFilesTest {

    public InMemoryAttachmentPersistenceFilesTest(byte[] content, String hash) {
        super(content, hash);
    }

    @Override
    protected IAttachmentPersistenceHandler createHandler() {
        return new InMemoryAttachmentPersitenceHandler();
    }


}
