package org.opensingular.form.type.core.attachment;

import org.opensingular.form.type.core.attachment.handlers.InMemoryAttachmentPersistenceHandler;

public class TestCasePersistenceHandlerInMemory extends TestCasePersistenceHandlerBase {

    @Override
    protected IAttachmentPersistenceHandler setupHandler() {
        return new InMemoryAttachmentPersistenceHandler(tmpProvider.createTempDir());
    }

}
