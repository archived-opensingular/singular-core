package br.net.mirante.singular.form.mform.core.attachment;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import br.net.mirante.singular.form.mform.core.attachment.handlers.FileSystemAttachmentHandler;

public class FileSystemAttachmentPersistenceFilesTest extends BaseAttachmentPersistenceFilesTest {

    public FileSystemAttachmentPersistenceFilesTest(byte[] content, String hash) {
        super(content, hash);
    }

    @Rule
    public TemporaryFolder rootTmp = new TemporaryFolder();
    private File tmpFolder;

    public void createFolders() throws Exception {
        tmpFolder = rootTmp.newFolder("tempSingular" + Math.random());
    }

    @Override
    protected IAttachmentPersistenceHandler createHandler() throws Exception{
        createFolders();
        return new FileSystemAttachmentHandler(tmpFolder);
    }

}
