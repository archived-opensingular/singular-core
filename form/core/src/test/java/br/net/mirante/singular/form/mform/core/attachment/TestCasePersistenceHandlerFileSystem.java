package br.net.mirante.singular.form.mform.core.attachment;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Throwables;

import br.net.mirante.singular.form.mform.core.attachment.handlers.FileSystemAttachmentHandler;

public class TestCasePersistenceHandlerFileSystem extends TestCasePersistenceHandlerBase {

    @Rule
    public TemporaryFolder rootTmp = new TemporaryFolder();
    private File tmpFolder;

    @Before
    public void createFolders() throws Exception {
        tmpFolder = rootTmp.newFolder("tempSingular" + Math.random());
    }

    
    @Override
    protected IAttachmentPersistenceHandler setupHandler() {
        try {
            createFolders();
        } catch (Exception e) {
            Throwables.propagate(e);
        }
        return new FileSystemAttachmentHandler(tmpFolder);
    }
    
    @Override
    protected String defineId(IAttachmentRef ref) {
        return ref.getId();
    }

}
