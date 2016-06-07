package br.net.mirante.singular.form.type.core.attachment;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SIList;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.STypeList;
import br.net.mirante.singular.form.TestCaseForm;
import br.net.mirante.singular.form.io.HashUtil;
import br.net.mirante.singular.form.io.TesteFormSerializationUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class TesteMPacoteAttachment extends TestCaseForm {

    public TesteMPacoteAttachment(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test
    public void testSimpleAttachment() {
        SIAttachment arquivo = createEmptyAttachment();
        assertNoReference(arquivo, 0);

        final byte[] conteudo = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };

        arquivo.setContent(conteudo);

        assertConteudo(conteudo, arquivo, 1);
        assertNull(arquivo.getFileName());

        arquivo.setFileName("teste.bin");
        assertEquals("teste.bin", arquivo.getFileName());

        arquivo.deleteReference();
        assertNoReference(arquivo, 0);
    }

    private static void assertConteudo(byte[] conteudoEsperado, SIAttachment arquivo, int expectedDistintictFiles) {
        String hash = HashUtil.toSHA1Base16(conteudoEsperado);

        assertTrue(Arrays.equals(conteudoEsperado, arquivo.getContentAsByteArray()));
        assertEquals((Integer) conteudoEsperado.length, arquivo.getFileSize());
        assertEquals(hash, arquivo.getFileId());
        assertEquals(hash, arquivo.getFileHashSHA1());
        assertNotNull(arquivo.getAttachmentRef());

        assertBinariosAssociadosDocument(arquivo, expectedDistintictFiles);
    }

    private SIAttachment createEmptyAttachment() {
        PackageBuilder  pb         = createTestDictionary().createNewPackage("teste");
        STypeAttachment tipo       = pb.createType("arquivo", STypeAttachment.class);
        return tipo.newInstance();
    }

    @Test
    public void testUpdateContent() {
        SIAttachment arquivo = createEmptyAttachment();

        byte[] conteudo1 = new byte[] { 1, 2, 3 };
        byte[] conteudo2 = new byte[] { 4, 5, 6, 7, 8, 9, 10 };

        arquivo.setContent(conteudo1);
        assertConteudo(conteudo1, arquivo, 1);

        arquivo.setContent(conteudo2);
        assertConteudo(conteudo2, arquivo, 1);
    }

    @Test
    public void testSetContentToNull() {
        SIAttachment arquivo = createEmptyAttachment();
        byte[] conteudo = new byte[] { 1, 2 };

        assertException(() -> arquivo.setContent((byte[]) null), "não pode ser null");
        assertNoReference(arquivo, 0);
        assertException(() -> arquivo.setContent((InputStream) null), "não pode ser null");
        assertNoReference(arquivo, 0);

        arquivo.setContent(conteudo);

        assertException(() -> arquivo.setContent((byte[]) null), "não pode ser null");
        assertConteudo(conteudo, arquivo, 1);
        assertException(() -> arquivo.setContent((InputStream) null), "não pode ser null");
        assertConteudo(conteudo, arquivo, 1);
    }

    @Test
    public void testSetContentSizeZero() {
        SIAttachment arquivo = createEmptyAttachment();
        byte[] conteudo = new byte[0];
        arquivo.setContent(conteudo);
        assertConteudo(conteudo, arquivo, 1);
    }

    @Test
    public void testSetContentWithIOException() {
        SIAttachment arquivo = createEmptyAttachment();
        assertException(() -> arquivo.setContent(new InputStreamComErro()), "Erro lendo origem de dados");

        assertNoReference(arquivo, 0);

        byte[] conteudo = new byte[] { 9, 10, 11, 12 };
        arquivo.setContent(conteudo);
        assertConteudo(conteudo, arquivo, 1);

        assertException(() -> arquivo.setContent(new InputStreamComErro()), "Erro lendo origem de dados");
        assertConteudo(conteudo, arquivo, 1);
    }

    @Test
    public void testRepeatedAttachment() {
        PackageBuilder pb = createTestDictionary().createNewPackage("teste");
        STypeList<STypeAttachment, SIAttachment> tipoLista  = pb.createListTypeOf("anexos", STypeAttachment.class);
        SIList<SIAttachment>                     lista      = tipoLista.newInstance(SIAttachment.class);

        SIAttachment arquivo1 = lista.addNew();
        SIAttachment arquivo2 = lista.addNew();
        SIAttachment arquivo3 = lista.addNew();

        final byte[] conteudo1 = new byte[] { 1, 2, 3, 4, 5 };
        final byte[] conteudo2 = new byte[] { 6, 7, 8, 9, 10 };

        assertBinariosAssociadosDocument(lista, 0);
        arquivo1.setContent(conteudo1);
        assertBinariosAssociadosDocument(lista, 1);
        arquivo2.setContent(conteudo1);
        assertBinariosAssociadosDocument(lista, 1);
        arquivo3.setContent(conteudo2);
        assertBinariosAssociadosDocument(lista, 2);

        arquivo1.deleteReference();
        assertBinariosAssociadosDocument(lista, 2);
        arquivo2.deleteReference();
        assertBinariosAssociadosDocument(lista, 1);
        arquivo3.deleteReference();
        assertBinariosAssociadosDocument(lista, 0);
    }

    @Test
    public void testRemoveAttachmentWheDeletingInstanceOrParentInstance() {
        PackageBuilder pb = createTestDictionary().createNewPackage("teste");

        STypeComposite<? extends SIComposite> tipoBloco = pb.createCompositeType("bloco");
        tipoBloco.addFieldListOf("anexos", STypeAttachment.class);
        STypeComposite<? extends SIComposite> tipoSubBloco = tipoBloco.addFieldComposite("subBloco");
        tipoSubBloco.addField("subArquivo1", STypeAttachment.class);
        tipoSubBloco.addField("subArquivo2", STypeAttachment.class);
        tipoSubBloco.addField("subArquivo3", STypeAttachment.class);

        final byte[] conteudo1 = new byte[] { 1, 2, 3 };
        final byte[] conteudo2 = new byte[] { 4, 5, 6 };
        final byte[] conteudo3 = new byte[] { 7, 8, 9 };

        // Testa apenas com lista
        SIComposite bloco = tipoBloco.newInstance();
        SIList<SIAttachment> anexos = bloco.getFieldList("anexos", SIAttachment.class);

        anexos.addNew().setContent(conteudo1); // 0
        anexos.addNew().setContent(conteudo2); // 1
        anexos.addNew().setContent(conteudo2); // 2
        anexos.addNew().setContent(conteudo3); // 3
        anexos.addNew().setContent(conteudo3); // 4
        anexos.addNew(); // 5
        assertBinariosAssociadosDocument(anexos, 3);

        anexos.remove(4);
        assertBinariosAssociadosDocument(anexos, 3);
        anexos.remove(3);
        assertBinariosAssociadosDocument(anexos, 2);

        bloco.setValue("anexos", null);
        assertBinariosAssociadosDocument(anexos, 0);

        // Testa apenas com subBloco
        bloco = tipoBloco.newInstance();
        SIComposite subBloco = bloco.getFieldComposite("subBloco");
        subBloco.getField("subArquivo1", SIAttachment.class).setContent(conteudo1);
        subBloco.getField("subArquivo2", SIAttachment.class).setContent(conteudo2);

        assertBinariosAssociadosDocument(bloco, 2);

        bloco.setValue("subBloco.subArquivo2", null);
        assertBinariosAssociadosDocument(bloco, 1);

        bloco.setValue("subBloco", null);
        assertBinariosAssociadosDocument(bloco, 0);

        // Testa apenas com lista e subBloco interferido um no outro
        bloco = tipoBloco.newInstance();
        anexos = bloco.getFieldList("anexos", SIAttachment.class);
        anexos.addNew().setContent(conteudo3); // 0
        anexos.addNew().setContent(conteudo2); // 1
        anexos.addNew().setContent(conteudo1); // 2
        subBloco = bloco.getFieldComposite("subBloco");
        subBloco.getField("subArquivo1", SIAttachment.class).setContent(conteudo1);
        subBloco.getField("subArquivo2", SIAttachment.class).setContent(conteudo2);
        subBloco.getField("subArquivo3", SIAttachment.class).setContent(conteudo3);

        assertBinariosAssociadosDocument(anexos, 3);

        bloco.setValue("subBloco.subArquivo1", null); // conteudo1
        assertBinariosAssociadosDocument(anexos, 3);
        anexos.remove(2); // conteudo1
        assertBinariosAssociadosDocument(anexos, 2);
        anexos.remove(1); // conteudo2
        assertBinariosAssociadosDocument(anexos, 2);

        bloco.setValue("anexos", null); // conteudo2, conteudo3
        assertBinariosAssociadosDocument(anexos, 2);

    }

    @Test
    public void testSerializacaoDeserializacaoComAnexo() {
        SIAttachment arq = (SIAttachment) createSerializableTestInstance("teste.arq", pacote -> {
            pacote.createType("arq", STypeAttachment.class);
        });
        final byte[] conteudo1 = new byte[] { 1, 2, 3 };
        arq.setContent(conteudo1);
        assertConteudo(conteudo1, arq, 1);
        SIAttachment arq2 = (SIAttachment) TesteFormSerializationUtil.testSerializacao(arq);
        assertConteudo(conteudo1, arq2, 1);
    }

    @Test
    public void testSerializacaoDeserializacaoCompositeComAnexo() {
        SIComposite bloco = (SIComposite) createSerializableTestInstance("teste.bloco", pacote -> {
            STypeComposite<? extends SIComposite> tipoBloco = pacote.createCompositeType("bloco");
            tipoBloco.addField("arquivo1", STypeAttachment.class);
            tipoBloco.addField("arquivo2", STypeAttachment.class);
        });
        final byte[] conteudo1 = new byte[] { 1, 2, 3 };
        final byte[] conteudo2 = new byte[] { 4, 5, 6 };


        final SIAttachment arquivo1 = bloco.getField("arquivo1", SIAttachment.class);
        final SIAttachment arquivo2 = bloco.getField("arquivo2", SIAttachment.class);

        arquivo1.setContent(conteudo1);
        arquivo2.setContent(conteudo2);

        assertConteudo(conteudo1, arquivo1, 2);
        assertConteudo(conteudo2, arquivo2, 2);

        SIComposite bloco2 = (SIComposite) TesteFormSerializationUtil.testSerializacao(bloco);

        assertConteudo(conteudo1, bloco2.getField("arquivo1", SIAttachment.class), 2);
        assertConteudo(conteudo2, bloco2.getField("arquivo2", SIAttachment.class), 2);
    }

    private static void assertBinariosAssociadosDocument(SInstance ref, int expectedDistinctFiles) {
        AttachmentDocumentService aService = AttachmentDocumentService.lookup(ref);
        assertEquals(expectedDistinctFiles, aService.countDistinctFiles());
    }

    private static void assertNoReference(SIAttachment arquivo, int expectedDistintictFiles) {
        assertNull(arquivo.getContentAsByteArray());
        assertNull(arquivo.getFileName());
        assertNull(arquivo.getFileId());
        assertNull(arquivo.getAttachmentRef());
        assertNull(arquivo.getFileHashSHA1());
        assertNull(arquivo.getFileSize());

        assertBinariosAssociadosDocument(arquivo, expectedDistintictFiles);
    }

    public static final InputStream createInputStreamGeradoraException() {
        return new InputStreamComErro();
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