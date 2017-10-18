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

package org.opensingular.form.type.core.attachment;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.io.HashUtil;
import org.opensingular.form.io.TestFormSerializationUtil;
import org.opensingular.form.type.core.attachment.handlers.InMemoryAttachmentPersistenceHandler;
import org.opensingular.internal.lib.commons.util.TempFileProvider;
import org.opensingular.lib.commons.context.RefService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@RunWith(Parameterized.class)
public class TesteMPacoteAttachment extends TestCaseForm {

    public TesteMPacoteAttachment(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    private TempFileProvider tmpProvider;

    @Before
    public void createTmpProvider() {
        tmpProvider = TempFileProvider.createForUseInTryClause(this);
    }

    @After
    public void cleanTmpProvider() {
        tmpProvider.deleteOrException();
    }

    private static void assertConteudo(byte[] conteudoEsperado, SIAttachment arquivo, int expectedDistintictFiles) throws IOException {
        String hash = HashUtil.toSHA1Base16(conteudoEsperado);

        assertTrue(Arrays.equals(conteudoEsperado, arquivo.getContentAsByteArray().get()));
        assertEquals(conteudoEsperado.length, arquivo.getFileSize());
        assertEquals(hash, arquivo.getFileHashSHA1());
        assertNotNull(arquivo.getAttachmentRef());

        assertBinariosAssociadosDocument(arquivo, expectedDistintictFiles);
    }

    private static void assertBinariosAssociadosDocument(SInstance ref, int expectedDistinctFiles) {
        AttachmentDocumentService aService = AttachmentDocumentService.lookup(ref);
        assertEquals(expectedDistinctFiles, aService.countDistinctFiles());
    }

    private static void assertNoReference(SIAttachment arquivo, int expectedDistintictFiles) throws IOException {
        assertNull(arquivo.getContentAsByteArray().orElse(null));
        assertNull(arquivo.getContentAsInputStream().orElse(null));
        assertNull(arquivo.getFileName());
        assertNull(arquivo.getFileId());
        assertNull(arquivo.getAttachmentRef());
        assertNull(arquivo.getFileHashSHA1());
        assertEquals(arquivo.getFileSize(), -1);

        assertBinariosAssociadosDocument(arquivo, expectedDistintictFiles);
    }

    public static final InputStream createInputStreamGeradoraException() {
        return new InputStreamComErro();
    }

    @Test
    public void testSimpleAttachment() throws IOException {
        SIAttachment arquivo = createEmptyAttachment();
        assertNoReference(arquivo, 0);

        final byte[] conteudo = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        arquivo.setContent(null, tmpProvider.createTempFile(conteudo), conteudo.length, HashUtil.toSHA1Base16(conteudo));

        assertConteudo(conteudo, arquivo, 1);
        assertNull(arquivo.getFileName());

        arquivo.setFileName("teste.bin");
        assertEquals("teste.bin", arquivo.getFileName());

        arquivo.deleteReference();
        assertNoReference(arquivo, 0);
    }

    private SIAttachment createEmptyAttachment() {
        PackageBuilder pb = createTestPackage();
        STypeAttachment tipo = pb.createType("arquivo", STypeAttachment.class);
        return configPersistence(tipo.newInstance());
    }

    private <T extends SInstance> T configPersistence(T instance) {
        InMemoryAttachmentPersistenceHandler ref2 = new InMemoryAttachmentPersistenceHandler(tmpProvider.createTempDir());
        instance.getDocument().setAttachmentPersistenceTemporaryHandler(RefService.of(ref2));
        return instance;
    }

    @Test
    public void testUpdateContent() throws IOException {
        SIAttachment arquivo = createEmptyAttachment();

        byte[] conteudo1 = new byte[]{1, 2, 3};
        byte[] conteudo2 = new byte[]{4, 5, 6, 7, 8, 9, 10};

        arquivo.setContent("", tmpProvider.createTempFile(conteudo1), conteudo1.length, HashUtil.toSHA1Base16(conteudo1));
        assertConteudo(conteudo1, arquivo, 1);

        arquivo.setContent("", tmpProvider.createTempFile(conteudo2), conteudo2.length, HashUtil.toSHA1Base16(conteudo2));
        assertConteudo(conteudo2, arquivo, 1);
    }

    @Test
    public void testSetContentToNull() throws IOException {
        SIAttachment arquivo  = createEmptyAttachment();
        byte[]       conteudo = new byte[]{1, 2};

        assertException(() -> arquivo.setContent("", null, 0, ""), "O arquivo não pode ser nulo.");
        assertNoReference(arquivo, 0);

        arquivo.setContent("", tmpProvider.createTempFile(conteudo), conteudo.length, HashUtil.toSHA1Base16(conteudo));

    }

    @Test
    public void testSetContentSizeZero() throws IOException {
        SIAttachment arquivo = createEmptyAttachment();
        byte[] conteudo = new byte[0];
        assertException(() -> arquivo.setContent("", tmpProvider.createTempFile(conteudo), conteudo.length, HashUtil.toSHA1Base16(conteudo)),
                "O tamanho (em bytes) da nova referência a deve ser preenchido.");
    }

    @Test
    public void testSetContentWithIOException() throws IOException {
        SIAttachment arquivo = createEmptyAttachment();
        assertException(() -> arquivo.setContent("", new File(""), 0, ""), "Erro lendo origem de dados");

        assertNoReference(arquivo, 0);

        byte[] conteudo = new byte[]{9, 10, 11, 12};
        arquivo.setContent("", tmpProvider.createTempFile(conteudo), conteudo.length, HashUtil.toSHA1Base16(conteudo));
        assertConteudo(conteudo, arquivo, 1);

        assertException(() -> arquivo.setContent("", new File(""), 0, ""), "Erro lendo origem de dados");
        assertConteudo(conteudo, arquivo, 1);
    }

    @Test
    public void testRepeatedAttachment() throws IOException {
        PackageBuilder pb = createTestPackage();
        STypeList<STypeAttachment, SIAttachment> tipoLista = pb.createListTypeOf("anexos", STypeAttachment.class);
        SIList<SIAttachment> lista = configPersistence(tipoLista.newInstance(SIAttachment.class));

        SIAttachment arquivo1 = lista.addNew();
        SIAttachment arquivo2 = lista.addNew();
        SIAttachment arquivo3 = lista.addNew();

        final byte[] conteudo1 = new byte[]{1, 2, 3, 4, 5};
        final byte[] conteudo2 = new byte[]{6, 7, 8, 9, 10};

        assertBinariosAssociadosDocument(lista, 0);
        arquivo1.setContent("", tmpProvider.createTempFile(conteudo1), conteudo1.length, HashUtil.toSHA1Base16(conteudo1));
        assertBinariosAssociadosDocument(lista, 1);
        arquivo2.setContent("", tmpProvider.createTempFile(conteudo1), conteudo1.length, HashUtil.toSHA1Base16(conteudo1));
        assertBinariosAssociadosDocument(lista, 2);
        arquivo3.setContent("", tmpProvider.createTempFile(conteudo2), conteudo2.length, HashUtil.toSHA1Base16(conteudo2));
        assertBinariosAssociadosDocument(lista, 3);

        arquivo1.deleteReference();
        assertBinariosAssociadosDocument(lista, 2);
        arquivo2.deleteReference();
        assertBinariosAssociadosDocument(lista, 1);
        arquivo3.deleteReference();
        assertBinariosAssociadosDocument(lista, 0);
    }

    @Test
    public void testRemoveAttachmentWheDeletingInstanceOrParentInstance() throws IOException {
        PackageBuilder pb = createTestPackage();

        STypeComposite<? extends SIComposite> tipoBloco = pb.createCompositeType("bloco");
        tipoBloco.addFieldListOf("anexos", STypeAttachment.class);
        STypeComposite<? extends SIComposite> tipoSubBloco = tipoBloco.addFieldComposite("subBloco");
        tipoSubBloco.addField("subArquivo1", STypeAttachment.class);
        tipoSubBloco.addField("subArquivo2", STypeAttachment.class);
        tipoSubBloco.addField("subArquivo3", STypeAttachment.class);

        final byte[] conteudo1 = new byte[]{1, 2, 3};
        final byte[] conteudo2 = new byte[]{4, 5, 6};
        final byte[] conteudo3 = new byte[]{7, 8, 9};

        // Testa apenas com lista
        SIComposite bloco = configPersistence(tipoBloco.newInstance());
        SIList<SIAttachment> anexos = bloco.getFieldList("anexos", SIAttachment.class);

        anexos.addNew().setContent("", tmpProvider.createTempFile(conteudo1), conteudo1.length, HashUtil.toSHA1Base16(conteudo1)); // 0
        anexos.addNew().setContent("", tmpProvider.createTempFile(conteudo2), conteudo2.length, HashUtil.toSHA1Base16(conteudo2)); // 1
        anexos.addNew().setContent("", tmpProvider.createTempFile(conteudo2), conteudo2.length, HashUtil.toSHA1Base16(conteudo2)); // 2
        anexos.addNew().setContent("", tmpProvider.createTempFile(conteudo3), conteudo3.length, HashUtil.toSHA1Base16(conteudo3)); // 3
        anexos.addNew().setContent("", tmpProvider.createTempFile(conteudo3), conteudo3.length, HashUtil.toSHA1Base16(conteudo3)); // 4
        anexos.addNew(); // 5
        assertBinariosAssociadosDocument(anexos, 5);

        anexos.remove(4);
        assertBinariosAssociadosDocument(anexos, 4);
        anexos.remove(3);
        assertBinariosAssociadosDocument(anexos, 3);

        bloco.setValue("anexos", null);
        assertBinariosAssociadosDocument(anexos, 0);

        // Testa apenas com subBloco
        bloco = configPersistence(tipoBloco.newInstance());
        SIComposite subBloco = bloco.getFieldComposite("subBloco");
        subBloco.getField("subArquivo1", SIAttachment.class).setContent("", tmpProvider.createTempFile(conteudo1), conteudo1.length, HashUtil.toSHA1Base16(conteudo1));
        subBloco.getField("subArquivo2", SIAttachment.class).setContent("", tmpProvider.createTempFile(conteudo2), conteudo2.length, HashUtil.toSHA1Base16(conteudo2));

        assertBinariosAssociadosDocument(bloco, 2);

        bloco.setValue("subBloco.subArquivo2", null);
        assertBinariosAssociadosDocument(bloco, 1);

        bloco.setValue("subBloco", null);
        assertBinariosAssociadosDocument(bloco, 0);

        // Testa apenas com lista e subBloco interferido um no outro
        bloco = configPersistence(tipoBloco.newInstance());
        anexos = bloco.getFieldList("anexos", SIAttachment.class);
        anexos.addNew().setContent("", tmpProvider.createTempFile(conteudo3), conteudo3.length, HashUtil.toSHA1Base16(conteudo3)); // 0
        anexos.addNew().setContent("", tmpProvider.createTempFile(conteudo2), conteudo2.length, HashUtil.toSHA1Base16(conteudo2)); // 1
        anexos.addNew().setContent("", tmpProvider.createTempFile(conteudo1), conteudo1.length, HashUtil.toSHA1Base16(conteudo1)); // 2
        subBloco = bloco.getFieldComposite("subBloco");
        subBloco.getField("subArquivo1", SIAttachment.class).setContent("", tmpProvider.createTempFile(conteudo1), conteudo1.length, HashUtil.toSHA1Base16(conteudo1));
        subBloco.getField("subArquivo2", SIAttachment.class).setContent("", tmpProvider.createTempFile(conteudo2), conteudo2.length, HashUtil.toSHA1Base16(conteudo2));
        subBloco.getField("subArquivo3", SIAttachment.class).setContent("", tmpProvider.createTempFile(conteudo3), conteudo3.length, HashUtil.toSHA1Base16(conteudo3));

        assertBinariosAssociadosDocument(anexos, 6);

        bloco.setValue("subBloco.subArquivo1", null); // conteudo1
        assertBinariosAssociadosDocument(anexos, 5);
        anexos.remove(2); // conteudo1
        assertBinariosAssociadosDocument(anexos, 4);
        anexos.remove(1); // conteudo2
        assertBinariosAssociadosDocument(anexos, 3);

        bloco.setValue("anexos", null); // conteudo2, conteudo3
        assertBinariosAssociadosDocument(anexos, 2);
    }

    @Test
    public void testSerializacaoDeserializacaoComAnexo() throws IOException {
        SIAttachment arq = (SIAttachment) createSerializableTestInstance("teste.arq", pacote -> {
            pacote.createType("arq", STypeAttachment.class);
        });
        configPersistence(arq);

        final byte[] conteudo1 = new byte[]{1, 2, 3};
        arq.setContent("arq1", tmpProvider.createTempFile(conteudo1), conteudo1.length, HashUtil.toSHA1Base16(conteudo1));
        assertConteudo(conteudo1, arq, 1);
        SIAttachment arq2 = (SIAttachment) TestFormSerializationUtil.testSerializacao(arq);
        assertConteudo(conteudo1, arq2, 1);
    }

    @Test
    public void testSerializacaoDeserializacaoCompositeComAnexo() throws IOException {
        SIComposite bloco = (SIComposite) createSerializableTestInstance("teste.bloco", pacote -> {
            STypeComposite<? extends SIComposite> tipoBloco = pacote.createCompositeType("bloco");
            tipoBloco.addField("arquivo1", STypeAttachment.class);
            tipoBloco.addField("arquivo2", STypeAttachment.class);
        });
        configPersistence(bloco);

        final byte[] conteudo1 = new byte[]{1, 2, 3};
        final byte[] conteudo2 = new byte[]{4, 5, 6};


        final SIAttachment arquivo1 = bloco.getField("arquivo1", SIAttachment.class);
        final SIAttachment arquivo2 = bloco.getField("arquivo2", SIAttachment.class);

        arquivo1.setContent("content", tmpProvider.createTempFile(conteudo1), conteudo1.length, HashUtil.toSHA1Base16(conteudo1));
        arquivo2.setContent("content", tmpProvider.createTempFile(conteudo2), conteudo2.length, HashUtil.toSHA1Base16(conteudo2));

        assertConteudo(conteudo1, arquivo1, 2);
        assertConteudo(conteudo2, arquivo2, 2);

        SIComposite bloco2 = (SIComposite) TestFormSerializationUtil.testSerializacao(bloco);

        assertConteudo(conteudo1, bloco2.getField("arquivo1", SIAttachment.class), 2);
        assertConteudo(conteudo2, bloco2.getField("arquivo2", SIAttachment.class), 2);
    }

    private static final class InputStreamComErro extends InputStream {

        int count = 0;

        @Override
        public int read() throws IOException {
            verificarLimite();
            count++;
            return 40;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            verificarLimite();
            count += len;
            return len;
        }

        private void verificarLimite() throws IOException {
            // Gera a exception apenas com 100 K para provavelmente dar erro
            // depois de ter lido mais de uma vez usando read(byte[]), ou seja,
            // tenta dar um erro em um tamanho maior que o buffer de leitura.
            if (count > 102400) {
                throw new IOException("Simulando IOException no meio de uma leitura (lidos " + count + ")");
            }
        }
    }
}
