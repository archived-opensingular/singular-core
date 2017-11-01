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

package org.opensingular.form.wicket.mapper.attachment;

import org.apache.commons.io.IOUtils;
import org.apache.wicket.util.tester.FormTester;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.SIList;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.io.HashUtil;
import org.opensingular.form.io.IOUtil;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.wicket.helpers.AssertionsWComponent;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;
import org.opensingular.internal.lib.commons.util.TempFileProvider;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;

public class AttachmentListMapperTest {

    protected TempFileProvider tmpProvider;

    @Before
    public void createTmpProvider() {
        tmpProvider = TempFileProvider.createForUseInTryClause(this);
    }

    @After
    public void cleanTmpProvider() {
        tmpProvider.deleteOrException();
    }


    @Test
    public void testAddNewFileAndRemove() throws IOException {
        SingularDummyFormPageTester ctx = new SingularDummyFormPageTester();
        ctx.getDummyPage().setTypeBuilder(AttachmentListMapperTest::createType);
        ctx.getDummyPage().setAsEditView();
        ctx.startDummyPage();

        ctx.getAssertionsPage().debugComponentTree(); //TODO apagar essa linha depois de pronto o teste
        SIList<SIAttachment> atts = (SIList<SIAttachment>) ctx.getAssertionsInstance().field("attachments").isList(0)
                .getTarget();

        ctx.getAssertionsPage().getSubCompomentForSInstance(atts).isNotNull().assertSInstance().isList(0);

        byte[] content = new byte[]{3, 4};
        File tmpFile = tmpProvider.createTempFile(content);

        FormTester formTester = ctx.newFormTester();
        //formTester.setFile(getFormRelativePath(multipleFileField), new org.apache.wicket.util.file.File(tempFile),
        // "text/plain");


        //TODO Passo a serem implementados no teste:
        // * Adicionar um arquivo
        // * Compara se o binario do arquivo adicionado bate com o armazenado na SInstance
        // * Fazer downalod do arquivo
        // * Verificar se a SInstance está consistente
    }

    private static void createType(STypeComposite<?> baseType) {
        baseType.addFieldListOfAttachment("attachments", "attachment").asAtr().label("Attachments");
    }

    @Test
    public void testRemove() throws IOException {
        byte[] content1 = new byte[]{1, 2};
        byte[] content2 = new byte[]{3, 4, 5};

        SingularDummyFormPageTester ctx = createTestPageWithTwoAttachments(content1, content2);
        ctx.getDummyPage().setAsEditView();
        ctx.startDummyPage();

        SIList<SIAttachment> atts = (SIList<SIAttachment>) ctx.getAssertionsInstance().field("attachments").isList(2)
                .getTarget();

        AssertionsWComponent assertAttachs = ctx.getAssertionsPage().getSubCompomentForSInstance(atts).isNotNull();

        FormTester formTester = ctx.newFormTester();
        AssertionsWComponent button = assertDelButton(assertAttachs.getSubCompomentForSInstance(atts.get(0)), true);
        formTester.submit(button.getTarget());

        assertAttachs = ctx.getAssertionsPage().getSubCompomentForSInstance(atts).isNotNull();
        SIAttachment att = assertAttachs.assertSInstance().isList(1).field("[0]").getTarget(SIAttachment.class);
        Assert.assertArrayEquals(content2, IOUtils.toByteArray(att.getContentAsInputStream().get()));
    }

    @Test
    public void testReadOnly() throws IOException {
        byte[] content1 = new byte[]{1, 2};
        byte[] content2 = new byte[]{3, 4, 5};

        SingularDummyFormPageTester ctx = createTestPageWithTwoAttachments(content1, content2);
        ctx.getDummyPage().setAsVisualizationView();
        ctx.startDummyPage();

        ctx.getAssertionsPage().debugComponentTree(); //TODO apagar essa linha depois de pronto o teste
        SIList<SIAttachment> atts = (SIList<SIAttachment>) ctx.getAssertionsInstance().field("attachments").isList(2)
                .getTarget();

        AssertionsWComponent assertAttachs = ctx.getAssertionsPage().getSubCompomentForSInstance(atts).isNotNull();

        assertDelButton(assertAttachs.getSubCompomentForSInstance(atts.get(0)), false);
        assertDelButton(assertAttachs.getSubCompomentForSInstance(atts.get(1)), false);
    }

    private AssertionsWComponent assertDelButton(AssertionsWComponent componentAtt, boolean buttonRequired) {
        componentAtt.isNotNull();
        AssertionsWComponent remove = componentAtt.getSubCompomentWithId("remove_btn");
        if (buttonRequired) {
            remove.isNotNull();
            Assert.assertTrue(remove.getTarget().isEnabled() && remove.getTarget().isVisible());
        } else {
            Assert.assertTrue(
                    ! remove.getTargetOpt().isPresent() || !remove.getTarget().isEnabled() || !remove.getTarget().isVisible());
        }
        return remove;
    }

    @Nonnull
    private SingularDummyFormPageTester createTestPageWithTwoAttachments(byte[] content1, byte[] content2) {
        File file1 = tmpProvider.createTempFile(content1);
        File file2 = tmpProvider.createTempFile(content2);

        SingularDummyFormPageTester ctx = new SingularDummyFormPageTester();
        ctx.getDummyPage().setTypeBuilder(AttachmentListMapperTest::createType);
        ctx.getDummyPage().addInstancePopulator(instance -> {
            SIList<SIAttachment> attachments = (SIList<SIAttachment>) instance.getField("attachments");
            attachments.addNew().setContent("teste1.txt", file1, file1.length(), HashUtil.toSHA1Base16(content1));
            attachments.addNew().setContent("teste2.txt", file2, file2.length(), HashUtil.toSHA1Base16(content2));
        });
        return ctx;
    }
}