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

import org.apache.wicket.Component;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.tester.FormTester;
import org.fest.assertions.core.Condition;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.helpers.AssertionsSInstance;
import org.opensingular.form.io.HashUtil;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.type.core.attachment.STypeAttachment;
import org.opensingular.form.wicket.helpers.AssertionsWComponent;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;
import org.opensingular.form.wicket.helpers.SingularFormBaseTest;
import org.opensingular.form.wicket.model.ISInstanceAwareModel;
import org.opensingular.internal.lib.commons.util.TempFileProvider;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.opensingular.form.wicket.helpers.TestFinders.findFirstComponentWithId;

@SuppressWarnings("rawtypes")
public class AttachmentFieldTest extends SingularFormBaseTest {

    protected SDictionary     dictionary;
    public    STypeAttachment attachmentFileField;

    protected TempFileProvider tmpProvider;

    @Before
    public void createTmpProvider() {
        tmpProvider = TempFileProvider.createForUseInTryClause(this);
    }

    @After
    public void cleanTmpProvider() {
        tmpProvider.deleteOrException();
    }

    @Override
    protected void buildBaseType(STypeComposite<?> mockType) {
        dictionary = mockType.getDictionary();
        attachmentFileField = mockType.addField("fileField", STypeAttachment.class);
    }

    @Test
    public void testAddNewFileAndRemove() throws IOException {
        SingularDummyFormPageTester ctx = new SingularDummyFormPageTester();
        ctx.getDummyPage().setTypeBuilder(AttachmentFieldTest::createType);
        ctx.getDummyPage().setAsEditView();

        //Start
        ctx.startDummyPage();

        ctx.getAssertionsPage().debugComponentTree(); //TODO apagar essa linha depois de pronto o teste

        ctx.getAssertionsInstance().field("attachment").is(SIAttachment.class);

        ctx.getAssertionsPage().getSubCompomentWithId("fileUpload")
                .isNotNull()
                .assertSInstance().is(SIAttachment.class);

        //Mas exemplos de conteúdo em BaseAttachmentPersistenceFilesTest.java
        //sha1 = "7110eda4d09e062aa5e4a390b0a572ac0d2c0220"
        byte[] content = "1234".getBytes(Charset.forName(StandardCharsets.UTF_8.name()));
        java.io.File tmpFile = tmpProvider.createTempFile(content);

        FormTester formTester = ctx.newFormTester();
        formTester.submit(ctx.getAssertionsPage().getSubCompomentWithId("remove_btn").getTarget());
        //formTester.setFile(getFormRelativePath(multipleFileField), new org.apache.wicket.util.file.File(tempFile), "text/plain");

        ctx.getAssertionsPage().debugComponentTree();


        //TODO Passo a serem implementados no teste:
        // * Adicionar um arquivo
        // * Compara se o binario do arquivo adicionado bate com o armazenado na SInstance
        // * Fazer downalod do arquivo
        // * Apagar o arquivo
        // * Verificar se a SInstance está consistente

    }

    private static void createType(STypeComposite<?> baseType) {
        baseType
                .addFieldAttachment("attachment")
                .asAtr()
                .label("Attachment");
    }

    @Test
    public void testRemove() throws IOException {
        byte[] content =  new byte[]{1, 2};
        SingularDummyFormPageTester ctx = createPageWithContent(content);
        ctx.getDummyPage().setAsEditView();


        //Start
        ctx.startDummyPage();
        ctx.getAssertionsPage().debugComponentTree(); //TODO apagar essa linha depois de pronto o teste
        asssertContent(ctx.getAssertionsInstance().field("attachment"), content);
        ctx.getAssertionsPage().getSubCompomentWithId("fileUpload")
                .isNotNull()
                .assertSInstance().is(SIAttachment.class);

        //TODO Tentar baixar o arquivo
        //ctx.clickLink(ctx.getAssertionsPage().getSubCompomentWithId("downloadLink").getTarget());

        //Clica em apagar
        FormTester formTester = ctx.newFormTester();
        formTester.submit(assertDelButton(ctx.getAssertionsPage(),true).getTarget());

        asssertContent(ctx.getAssertionsInstance().field("attachment"), null);
    }

    private AssertionsWComponent assertDelButton(AssertionsWComponent componentAtt, boolean buttonRequired) {
        componentAtt.isNotNull();
        AssertionsWComponent remove = componentAtt.getSubCompomentWithId("remove_btn");
        if (buttonRequired) {
            remove.isNotNull();
            Assert.assertTrue(remove.getTarget().isEnabled() && remove.getTarget().isVisible());
        } else {
            Assert.assertTrue(!remove.getTargetOpt().isPresent() || !remove.getTarget().isEnabled() ||
                    !remove.getTarget().isVisible());
        }
        return remove;
    }

    @Test
    public void testReadOnly() throws IOException {
        byte[] content =  new byte[]{1, 2};
        SingularDummyFormPageTester ctx = createPageWithContent(content);
        ctx.getDummyPage().setAsVisualizationView();

        //Start
        ctx.startDummyPage();
        ctx.getAssertionsPage().debugComponentTree(); //TODO apagar essa linha depois de pronto o teste
        asssertContent(ctx.getAssertionsInstance().field("attachment"), content);
        ctx.getAssertionsPage().getSubCompomentWithId("_readOnlyAttachment").isNotNull();
        assertDelButton(ctx.getAssertionsPage(),false);

        //TODO Tentar baixar o arquivo
        //ctx.clickLink(ctx.getAssertionsPage().getSubCompomentWithId("downloadLink").getTarget());
    }

    @Nonnull
    private SingularDummyFormPageTester createPageWithContent(byte[] content) {
        java.io.File file = tmpProvider.createTempFile(content);

        SingularDummyFormPageTester ctx = new SingularDummyFormPageTester();
        ctx.getDummyPage().setTypeBuilder(AttachmentFieldTest::createType);
        ctx.getDummyPage().addInstancePopulator( instance -> {
            instance.getField("attachment", SIAttachment.class).setContent("teste.txt", file, file.length(), HashUtil.toSHA1Base16(content));
        });
        return ctx;
    }

    private void asssertContent(AssertionsSInstance attachment, byte[] expectedContent) {
        attachment.is(SIAttachment.class);
        SIAttachment att = attachment.getTarget(SIAttachment.class);
        if (expectedContent == null) {
//            Assert.assertFalse(att.getContentAsByteArray().isPresent());
        } else {
//            byte[] current = att.getContentAsByteArray().get();
//            assertArrayEquals(expectedContent, current);
        }
    }

    @Test
    @Ignore("Apagar depois de refazer os teste corretamente")
    public void verifyIfInputFieldIsHidden() {
        Component fileUpload = findFirstComponentWithId(page, "fileUpload");
        assertThat(fileUpload).isNotNull();
        assertThat(fileUpload.getMarkupAttributes().get("style").toString()).contains("display:none");
    }

    @Test
    @Ignore("Apagar depois de refazer os teste corretamente")
    public void verifyChooseAndRemoveVisibility() throws IOException {

        //Recupera os componentes
        Component removeFileButton = findFirstComponentWithId(page, "remove_btn");
        Component choose = findFirstComponentWithId(page, "upload_btn");
        FileUploadField uploadField = (FileUploadField) findFirstComponentWithId(page, "fileUpload");

        //Recupera o model do fileupload
        ISInstanceAwareModel model = (ISInstanceAwareModel) uploadField.getModel();

        // Verifica se a visibilidade está ok
        tester.assertVisible(choose.getPageRelativePath());
        tester.assertInvisible(removeFileButton.getPageRelativePath());

        // Verifica se não existe arqiovps
        assertThat(model.getSInstance().isEmptyOfData()).isTrue();

        File file = createTempFileAndSetOnField(uploadField);

        //Executa o evento que configura o model
        executeAjaxFormSubmitBehavior(uploadField);

        //Verifica se o valor do model mudou
        assertThat(model.getSInstance().isEmptyOfData()).isFalse();

        //Verifica se a visibilidade mudou
        tester.assertInvisible(choose.getPageRelativePath());
        tester.assertVisible(removeFileButton.getPageRelativePath());

        file.deleteOnExit();
    }

    @Test
    @Ignore("Apagar depois de refazer os teste corretamente")
    public void assertFileModelValues() throws IOException {

        //Recupera os componentes
        FileUploadField uploadField = (FileUploadField) findFirstComponentWithId(page, "fileUpload");
        //Recupera o model do fileupload
        ISInstanceAwareModel model = (ISInstanceAwareModel) uploadField.getModel();

        File file = createTempFileAndSetOnField(uploadField);

        //Executa o evento que configura o model
        executeAjaxFormSubmitBehavior(uploadField);

        // Verifica se é instancia de SInstance
        assertThat(model.getSInstance()).is(new Condition<SInstance>() {
            @Override
            public boolean matches(SInstance value) {
                return value instanceof SIAttachment;
            }
        });

        SIAttachment attachment = (SIAttachment) model.getSInstance();
        assertThat(attachment.getFileName()).isEqualTo(file.getName());

        file.deleteOnExit();
    }

    private void executeAjaxFormSubmitBehavior(Component c){
        AjaxFormSubmitBehavior behavior = (AjaxFormSubmitBehavior) c
                .getBehaviors()
                .stream()
                .filter(f -> AjaxFormSubmitBehavior.class.isAssignableFrom(f.getClass()))
                .findFirst().get();
        tester.executeBehavior(behavior);
    }

    private File createTempFileAndSetOnField(FileUploadField uploadField) throws IOException {
        //Cria um arquivo de teste e adiciona ao componente

        java.io.File temp = tmpProvider.createTempFile("Teste".getBytes(), ".txt");
        final File file = new File(temp);
        String path = uploadField.getPath();
        form.setFile(path.substring(path.lastIndexOf("form:") + "form:".length(), path.length()), file, "text/plain");

        return file;
    }

}
