package org.opensingular.form.wicket.mapper.attachment.upload.factory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class AttachmentKeyFactoryTest {

    private AttachmentKeyFactory attachmentKeyFactory;

    private static final String KEY = "ac74a152-3347-40d6-852a-ca4486650c76";

    @Parameter
    public String path;

    @Parameters
    public static Object[] paths() {
        return new Object[]{"D:" + File.separator + "Temp" + File.separator + "MyFiles" + File.separator + "" + KEY,
                File.separator + "home" + File.separator + "user" + File.separator + "temp" + File.separator + KEY};
    }

    @Before
    public void setUp() {
        attachmentKeyFactory = new AttachmentKeyFactory();
    }

    @Test
    public void testGet() throws Exception {
        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(httpServletRequest.getPathTranslated()).thenReturn(path);
        assertEquals(KEY, attachmentKeyFactory.get(httpServletRequest).asString());
    }


}