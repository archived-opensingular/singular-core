package br.net.mirante.singular.form.type.core.attachment;

import org.opensingular.singular.form.type.core.attachment.IAttachmentPersistenceHandler;
import org.opensingular.singular.form.type.core.attachment.handlers.FileSystemAttachmentHandler;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.File;

public class FileSystemAttachmentPersistenceFilesTest extends BaseAttachmentPersistenceFilesTest {

    static int countGenerations = 0;
    @Rule
    public TemporaryFolder rootTmp = new TemporaryFolder();
    private File tmpFolder;

    public FileSystemAttachmentPersistenceFilesTest(byte[] content, String hash) {
        super(content, hash);
    }

    public void createFolders() throws Exception {
        tmpFolder = rootTmp.newFolder("tempSingular" + Math.random());
    }

    @Override
    @SuppressWarnings("serial")
    protected IAttachmentPersistenceHandler createHandler() throws Exception {
        createFolders();
        FileSystemAttachmentHandler handler = new FileSystemAttachmentHandler(tmpFolder);
        return handler;
    }


}
