package org.opensingular.server.commons.service.attachment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opensingular.form.persistence.dao.AttachmentDao;
import org.opensingular.form.persistence.entity.AttachmentEntity;
import org.opensingular.server.commons.file.FileInputStreamAndHash;
import org.opensingular.server.commons.file.FileInputStreamAndHashFactory;

import java.io.File;
import java.io.InputStream;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ServerAbstractAttachmentPersistenceServiceTest {

    @Mock
    private FileInputStreamAndHashFactory fileInputStreamAndHashFactory;

    @Mock
    private FileInputStreamAndHash fileInputStreamAndHash;

    @Mock
    private File file;

    @Mock
    private AttachmentDao attachmentDao;

    @Mock
    private InputStream inputStream;

    @InjectMocks
    private MockServerAbstractAttachmentPersistenceService serverAbstractAttachmentPersistenceService;

    @Test
    public void addAttachment() throws Exception {

        long             myFileLength     = 10L;
        String           myFileName       = "abc.pdf";
        String           myFileHash       = "123456456456456";
        AttachmentEntity attachmentEntity = new AttachmentEntity();

        attachmentEntity.setCod(1L);
        attachmentEntity.setName(myFileName);
        attachmentEntity.setHashSha1(myFileHash);

        when(fileInputStreamAndHashFactory.get(file)).thenReturn(fileInputStreamAndHash);
        when(fileInputStreamAndHash.getHash()).thenReturn(myFileHash);
        when(fileInputStreamAndHash.getInputStream()).thenReturn(inputStream);
        when(attachmentDao.insert(eq(inputStream), eq(myFileLength), eq(myFileName), eq(myFileHash))).thenReturn(attachmentEntity);

        serverAbstractAttachmentPersistenceService.addAttachment(file, myFileLength, myFileName);

        verify(fileInputStreamAndHashFactory).get(eq(file));
        verify(attachmentDao).insert(eq(inputStream), eq(myFileLength), eq(myFileName), eq(myFileHash));

    }

    static class MockServerAbstractAttachmentPersistenceService extends ServerAbstractAttachmentPersistenceService {

    }

}