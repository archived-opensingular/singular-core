package br.net.mirante.singular.form.mform.basic.view;

import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.core.attachment.SIAttachment;
import br.net.mirante.singular.form.mform.core.attachment.STypeAttachment;
import org.junit.Test;

import static org.junit.Assert.*;

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