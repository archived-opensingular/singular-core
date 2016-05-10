package br.net.mirante.singular.form.type.core.attachment;

import br.net.mirante.singular.form.type.core.attachment.handlers.FileSystemAttachmentHandler;
import br.net.mirante.singular.form.type.core.attachment.handlers.IdGenerator;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.File;

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

    static int countGenerations = 0;
    
    
    @Override @SuppressWarnings("serial")
    protected IAttachmentPersistenceHandler createHandler() throws Exception{
        createFolders();
        FileSystemAttachmentHandler handler = new FileSystemAttachmentHandler(tmpFolder);
        handler.setGenerator(new IdGenerator(){
            @Override
            public String generate(byte[] content) {
                countGenerations ++;
                return "generate_"+countGenerations;
            }
        });
        return handler;
    }

    @Override
    protected String defineId(IAttachmentRef ref) {
        return "generate_"+countGenerations;
    }

    
    
}
