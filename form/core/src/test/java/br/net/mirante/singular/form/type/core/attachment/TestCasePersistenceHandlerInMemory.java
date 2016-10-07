package br.net.mirante.singular.form.type.core.attachment;

import br.net.mirante.singular.form.type.core.attachment.handlers.InMemoryAttachmentPersitenceHandler;

public class TestCasePersistenceHandlerInMemory extends TestCasePersistenceHandlerBase {

    @Override
    protected IAttachmentPersistenceHandler setupHandler() {
        return new InMemoryAttachmentPersitenceHandler();
    }

}
