package br.net.mirante.singular.form.type.core.attachment;

import br.net.mirante.singular.form.type.core.attachment.handlers.InMemoryAttachmentPersitenceHandler;

public class InMemoryAttachmentPersistenceFilesTest extends BaseAttachmentPersistenceFilesTest {

    public InMemoryAttachmentPersistenceFilesTest(byte[] content, String hash) {
        super(content, hash);
    }

    @Override
    protected IAttachmentPersistenceHandler createHandler() {
        return new InMemoryAttachmentPersitenceHandler();
    }

    @Override
    protected String defineId(IAttachmentRef ref) {
        return hash;
    }

}
