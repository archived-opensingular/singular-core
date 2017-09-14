package org.opensingular.form.wicket.mapper.attachment;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.tester.FormTester;
import org.fest.assertions.core.Condition;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.helpers.AssertionsSInstance;
import org.opensingular.form.io.HashUtil;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.type.core.attachment.STypeAttachment;
import org.opensingular.form.type.core.attachment.STypeAttachmentImage;
import org.opensingular.form.view.SViewAttachmentImageTooltip;
import org.opensingular.form.wicket.helpers.AssertionsWComponent;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;
import org.opensingular.form.wicket.helpers.SingularFormBaseTest;
import org.opensingular.form.wicket.model.ISInstanceAwareModel;
import org.opensingular.internal.lib.commons.util.TempFileProvider;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.opensingular.form.wicket.helpers.TestFinders.findFirstComponentWithId;

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
