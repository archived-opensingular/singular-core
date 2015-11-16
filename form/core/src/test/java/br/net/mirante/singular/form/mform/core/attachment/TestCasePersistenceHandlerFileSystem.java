package br.net.mirante.singular.form.mform.core.attachment;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

public class TestCasePersistenceHandlerFileSystem extends TestCasePersistenceHandlerBase {

    
    @Rule public TemporaryFolder rootTmp = new TemporaryFolder();
    private File tmpFolder;
    
    @Before public void createFolders() throws Exception{
	tmpFolder = rootTmp.newFolder("tempSingular"+Math.random());
    }
    
    
    @Override
    protected IAttachmentPersistenceHandler setupHandler() {
	return new FileSystemAttachmentHandler(tmpFolder);
    }

}
