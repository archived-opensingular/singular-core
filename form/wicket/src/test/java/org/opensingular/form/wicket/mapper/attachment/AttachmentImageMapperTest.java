package org.opensingular.form.wicket.mapper.attachment;

import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.SType;
import org.opensingular.form.type.core.attachment.STypeAttachmentImage;
import org.opensingular.form.view.SViewAttachmentImageTooltip;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;
import org.opensingular.internal.lib.commons.util.TempFileProvider;

import java.io.IOException;

@SuppressWarnings("rawtypes")
public class AttachmentImageMapperTest {

    protected TempFileProvider tmpProvider;

    @Before
    public void createTmpProvider() {
        tmpProvider = TempFileProvider.createForUseInTryClause(this);
    }

    @Test
    public void testRenderComponent() throws IOException {
        SingularDummyFormPageTester tester = new SingularDummyFormPageTester();
        tester.getDummyPage().setTypeBuilder(tb->tb.addField("imgFile", STypeAttachmentImage.class));
        tester.getDummyPage().setAsEditView();

        tester.startDummyPage();

        tester.getAssertionsForm().getSubCompomentWithId("fileUpload").isNotNull();
    }

    @Test
    public void testRenderTooltipMapper() throws IOException {
        SingularDummyFormPageTester tester = new SingularDummyFormPageTester();
        tester.getDummyPage().setTypeBuilder(tb->{
            SType imgFile = tb.addField("imgFile", STypeAttachmentImage.class);
            imgFile.setView(SViewAttachmentImageTooltip::new);
        });
        tester.getDummyPage().setAsEditView();

        tester.startDummyPage();

        tester.getAssertionsForm().getSubCompomentWithId("fileUpload").assertSInstance().isNotNull();
    }
}
