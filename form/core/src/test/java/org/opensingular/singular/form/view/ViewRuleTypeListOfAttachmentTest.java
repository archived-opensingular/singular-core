package org.opensingular.singular.form.view;

import org.opensingular.singular.form.SDictionary;
import org.opensingular.singular.form.SIList;
import org.opensingular.singular.form.type.core.SIString;
import org.opensingular.singular.form.type.core.STypeString;
import org.opensingular.singular.form.type.core.attachment.SIAttachment;
import org.opensingular.singular.form.type.core.attachment.STypeAttachment;
import org.junit.Test;

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