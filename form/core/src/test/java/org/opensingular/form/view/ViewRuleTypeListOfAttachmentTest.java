package org.opensingular.form.view;

import org.opensingular.form.SDictionary;
import org.opensingular.form.SIList;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.type.core.attachment.STypeAttachment;
import org.junit.Test;
import org.opensingular.form.view.SViewAttachmentList;
import org.opensingular.form.view.ViewResolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ViewRuleTypeListOfAttachmentTest {

    @Test
    public void testIfResolvedViewIsSViewAttachmentList() throws Exception {
        final SIList<SIAttachment> attachments = SDictionary
                .create()
                .createNewPackage("test")
                .createListTypeOf("list", STypeAttachment.class).newInstance();
        assertEquals(SViewAttachmentList.class, ViewResolver.resolve(attachments).getClass());
    }

    @Test
    public void testIfResolvedViewIsNotSViewAttachmentList() throws Exception {
        final SIList<SIString> nomAttachments = SDictionary
                .create()
                .createNewPackage("test")
                .createListTypeOf("list", STypeString.class).newInstance();
        assertNotEquals(SViewAttachmentList.class, ViewResolver.resolve(nomAttachments).getClass());
    }
}