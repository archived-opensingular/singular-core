/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.wicket.mapper.attachment.upload.info;

import org.apache.wicket.util.collections.ConcurrentHashSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.opensingular.form.wicket.mapper.attachment.upload.AttachmentKey;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class UploadInfoRepositoryTest {

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
    public void testFindByAttachmentKey() throws Exception {

        UploadInfo upInfoOne = Mockito.mock(UploadInfo.class);
        UploadInfo upInfoTwo = Mockito.mock(UploadInfo.class);

        AttachmentKey keyOne = new AttachmentKey(idOne);
        AttachmentKey keyTwo = new AttachmentKey(idTwo);

        Mockito.when(upInfoOne.getUploadId()).thenReturn(keyOne);
        Mockito.when(upInfoTwo.getUploadId()).thenReturn(keyTwo);

        UploadInfoRepository infoRepository = new UploadInfoRepository();

        infoRepository.add(upInfoOne);
        infoRepository.add(upInfoTwo);

        assertEquals(upInfoOne, infoRepository.findByAttachmentKey(keyOne).orElse(null));
        assertEquals(upInfoTwo, infoRepository.findByAttachmentKey(keyTwo).orElse(null));

    }

    @Test
    public void testAdd() throws Exception {

        ConcurrentHashSet<UploadInfo> set = new ConcurrentHashSet<>();

        UploadInfo upInfoOne = Mockito.mock(UploadInfo.class);
        UploadInfo upInfoTwo = Mockito.mock(UploadInfo.class);

        AttachmentKey keyOne = new AttachmentKey(idOne);
        AttachmentKey keyTwo = new AttachmentKey(idTwo);

        Mockito.when(upInfoOne.getUploadId()).thenReturn(keyOne);
        Mockito.when(upInfoTwo.getUploadId()).thenReturn(keyTwo);

        UploadInfoRepository infoRepository = new UploadInfoRepository(set);

        infoRepository.add(upInfoOne);
        infoRepository.add(upInfoTwo);

        assertEquals(2, set.size());
        assertTrue(set.contains(upInfoOne));
        assertTrue(set.contains(upInfoTwo));

    }

    @Test
    public void testRemove() throws Exception {

        ConcurrentHashSet<UploadInfo> set = new ConcurrentHashSet<>();

        UploadInfo upInfoOne = Mockito.mock(UploadInfo.class);
        UploadInfo upInfoTwo = Mockito.mock(UploadInfo.class);

        AttachmentKey keyOne = new AttachmentKey(idOne);
        AttachmentKey keyTwo = new AttachmentKey(idTwo);

        Mockito.when(upInfoOne.getUploadId()).thenReturn(keyOne);
        Mockito.when(upInfoTwo.getUploadId()).thenReturn(keyTwo);

        UploadInfoRepository infoRepository = new UploadInfoRepository(set);

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