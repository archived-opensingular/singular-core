package org.opensingular.form.wicket.mapper.attachment.upload.info;

import org.apache.wicket.util.collections.ConcurrentHashSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.opensingular.form.type.core.attachment.IAttachmentRef;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(Parameterized.class)
public class FileUploadInfoRepositoryTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"abc", "abc1"},
                {"1", "2"},
                {"156a-adas3123", "5321321/adad"},
                {"@@!!!xxxxx", "xxx@45231//"}
        });
    }

    @Parameterized.Parameter
    public String idOne;

    @Parameterized.Parameter(value = 1)
    public String idTwo;

    @Test
    public void testFindByID() throws Exception {

        IAttachmentRef refOne = Mockito.mock(IAttachmentRef.class);
        IAttachmentRef refTwo = Mockito.mock(IAttachmentRef.class);

        FileUploadInfo upInfoOne = new FileUploadInfo(refOne);
        FileUploadInfo upInfoTwo = new FileUploadInfo(refTwo);

        Mockito.when(refOne.getId()).thenReturn(idOne);
        Mockito.when(refTwo.getId()).thenReturn(idTwo);

        FileUploadInfoRepository infoRepository = new FileUploadInfoRepository();

        infoRepository.add(upInfoOne);
        infoRepository.add(upInfoTwo);

        assertEquals(upInfoOne, infoRepository.findByID(idOne).orElse(null));
        assertEquals(upInfoTwo, infoRepository.findByID(idTwo).orElse(null));

    }

    @Test
    public void testAdd() throws Exception {

        ConcurrentHashSet<FileUploadInfo> set    = new ConcurrentHashSet<>();
        IAttachmentRef                    refOne = Mockito.mock(IAttachmentRef.class);
        IAttachmentRef                    refTwo = Mockito.mock(IAttachmentRef.class);

        FileUploadInfo upInfoOne = new FileUploadInfo(refOne);
        FileUploadInfo upInfoTwo = new FileUploadInfo(refTwo);

        Mockito.when(refOne.getId()).thenReturn(idOne);
        Mockito.when(refTwo.getId()).thenReturn(idTwo);

        FileUploadInfoRepository infoRepository = new FileUploadInfoRepository(set);

        infoRepository.add(upInfoOne);
        infoRepository.add(upInfoTwo);

        assertEquals(2, set.size());
        assertTrue(set.contains(upInfoOne));
        assertTrue(set.contains(upInfoTwo));

    }

    @Test
    public void testRemove() throws Exception {

        ConcurrentHashSet<FileUploadInfo> set    = new ConcurrentHashSet<>();
        IAttachmentRef                    refOne = Mockito.mock(IAttachmentRef.class);
        IAttachmentRef                    refTwo = Mockito.mock(IAttachmentRef.class);

        FileUploadInfo upInfoOne = new FileUploadInfo(refOne);
        FileUploadInfo upInfoTwo = new FileUploadInfo(refTwo);

        Mockito.when(refOne.getId()).thenReturn(idOne);
        Mockito.when(refTwo.getId()).thenReturn(idTwo);

        FileUploadInfoRepository infoRepository = new FileUploadInfoRepository(set);

        infoRepository.add(upInfoOne);
        infoRepository.add(upInfoTwo);

        assertEquals(2, set.size());
        assertTrue(set.contains(upInfoOne));
        assertTrue(set.contains(upInfoTwo));


        infoRepository.remove(upInfoOne);
        infoRepository.remove(upInfoTwo);

        assertTrue(set.isEmpty());

    }

}