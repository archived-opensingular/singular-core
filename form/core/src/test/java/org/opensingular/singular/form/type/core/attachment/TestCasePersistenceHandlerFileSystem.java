package org.opensingular.singular.form.type.core.attachment;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import org.opensingular.form.type.core.attachment.IAttachmentPersistenceHandler;
import org.opensingular.lib.commons.base.SingularUtil;
import org.opensingular.form.type.core.attachment.handlers.FileSystemAttachmentHandler;

public class TestCasePersistenceHandlerFileSystem extends TestCasePersistenceHandlerBase {

    @Rule
    public TemporaryFolder rootTmp = new TemporaryFolder();
    private File tmpFolder;

    @Before
    public void createFolders() throws Exception {
        tmpFolder = rootTmp.newFolder("tempSingular" + Math.random());
        tmpFolder.deleteOnExit();
    }

    
    @Override
    protected IAttachmentPersistenceHandler setupHandler() {
        try {
            createFolders();
        } catch (Exception e) {
            SingularUtil.propagate(e);
        }
        return new FileSystemAttachmentHandler(tmpFolder);
    }

}
