package org.opensingular.singular.form.type.core.attachment;

import org.opensingular.form.type.core.attachment.IAttachmentPersistenceHandler;
import org.opensingular.form.type.core.attachment.handlers.InMemoryAttachmentPersitenceHandler;

public class TestCasePersistenceHandlerInMemory extends TestCasePersistenceHandlerBase {

    @Override
    protected IAttachmentPersistenceHandler setupHandler() {
        return new InMemoryAttachmentPersitenceHandler();
    }

}
