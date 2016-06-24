package br.net.mirante.singular.form.type.core.attachment;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SIList;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.STypeList;
import br.net.mirante.singular.form.TestCaseForm;
import br.net.mirante.singular.form.io.HashUtil;
import br.net.mirante.singular.form.io.TesteFormSerializationUtil;
import com.google.common.base.Throwables;
import com.google.common.io.ByteStreams;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static br.net.mirante.singular.form.type.core.attachment.AttachmentTestUtil.*;

@RunWith(Parameterized.class)
public class TesteMPacoteAttachment extends TestCaseForm {

    public TesteMPacoteAttachment(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    private static void assertConteudo(byte[] conteudoEsperado, SIAttachment arquivo, int expectedDistintictFiles) throws IOException {
        String hash = HashUtil.toSHA1Base16(conteudoEsperado);

        assertTrue(Arrays.equals(conteudoEsperado, ByteStreams.toByteArray(arquivo.newInputStream())));
        assertEquals(conteudoEsperado.length, arquivo.getFileSize());
        assertEquals(hash, arquivo.getFileId());
        assertEquals(hash, arquivo.getFileHashSHA1());
        assertNotNull(arquivo.getAttachmentRef());

        assertBinariosAssociadosDocument(arquivo, expectedDistintictFiles);
    }

    private static void assertBinariosAssociadosDocument(SInstance ref, int expectedDistinctFiles) {
        AttachmentDocumentService aService = AttachmentDocumentService.lookup(ref);
        assertEquals(expectedDistinctFiles, aService.countDistinctFiles());
    }

    private static void assertNoReference(SIAttachment arquivo, int expectedDistintictFiles) throws IOException {
        assertNull(ByteStreams.toByteArray(arquivo.newInputStream()));
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

    @Test
    public void testSimpleAttachment() throws IOException {
        SIAttachment arquivo = createEmptyAttachment();
        assertNoReference(arquivo, 0);

        final byte[] conteudo = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        arquivo.setContent("asasd", writeBytesToTempFile(conteudo), conteudo.length);

        assertConteudo(conteudo, arquivo, 1);
        assertNull(arquivo.getFileName());

        arquivo.setFileName("teste.bin");
        assertEquals("teste.bin", arquivo.getFileName());

        arquivo.deleteReference();
        assertNoReference(arquivo, 0);
    }

    private SIAttachment createEmptyAttachment() {
        PackageBuilder pb = createTestDictionary().createNewPackage("teste");
        STypeAttachment tipo = pb.createType("arquivo", STypeAttachment.class);
        return tipo.newInstance();
    }

    @Test
    public void testUpdateContent() throws IOException {
        SIAttachment arquivo = createEmptyAttachment();

        byte[] conteudo1 = new byte[]{1, 2, 3};
        byte[] conteudo2 = new byte[]{4, 5, 6, 7, 8, 9, 10};

        arquivo.setContent("", writeBytesToTempFile(conteudo1), conteudo1.length);
        assertConteudo(conteudo1, arquivo, 1);

        arquivo.setContent("", writeBytesToTempFile(conteudo2), conteudo2.length);
        assertConteudo(conteudo2, arquivo, 1);
    }

    @Test
    public void testSetContentToNull() throws IOException {
        SIAttachment arquivo = createEmptyAttachment();
        byte[] conteudo = new byte[]{1, 2};

        assertException(() -> arquivo.setContent("", null, 0), "não pode ser null");
        assertNoReference(arquivo, 0);

        arquivo.setContent("", writeBytesToTempFile(conteudo), conteudo.length);

    }

    @Test
    public void testSetContentSizeZero() throws IOException {
        SIAttachment arquivo = createEmptyAttachment();
        byte[] conteudo = new byte[0];
        arquivo.setContent("", writeBytesToTempFile(conteudo), conteudo.length);
        assertConteudo(conteudo, arquivo, 1);
    }

    @Test
    public void testSetContentWithIOException() throws IOException {
        SIAttachment arquivo = createEmptyAttachment();
        assertException(() -> {
            try {
                arquivo.setContent("", writeBytesToTempFile(new InputStreamComErro()), 0);
            } catch (IOException e) {
                throw  Throwables.propagate(e);
            }
        }, "Erro lendo origem de dados");

        assertNoReference(arquivo, 0);

        byte[] conteudo = new byte[]{9, 10, 11, 12};
        arquivo.setContent("", writeBytesToTempFile(conteudo), conteudo.length);
        assertConteudo(conteudo, arquivo, 1);

        assertException(() -> {
            try {
                arquivo.setContent("", writeBytesToTempFile(new InputStreamComErro()), 0);
            } catch (IOException e) {
                throw  Throwables.propagate(e);
            }
        }, "Erro lendo origem de dados");
        assertConteudo(conteudo, arquivo, 1);
    }

    @Test
    public void testRepeatedAttachment() throws IOException {
        PackageBuilder pb = createTestDictionary().createNewPackage("teste");
        STypeList<STypeAttachment, SIAttachment> tipoLista = pb.createListTypeOf("anexos", STypeAttachment.class);
        SIList<SIAttachment> lista = tipoLista.newInstance(SIAttachment.class);

        SIAttachment arquivo1 = lista.addNew();
        SIAttachment arquivo2 = lista.addNew();
        SIAttachment arquivo3 = lista.addNew();

        final byte[] conteudo1 = new byte[]{1, 2, 3, 4, 5};
        final byte[] conteudo2 = new byte[]{6, 7, 8, 9, 10};

        assertBinariosAssociadosDocument(lista, 0);
        arquivo1.setContent("", writeBytesToTempFile(conteudo1), conteudo1.length);
        assertBinariosAssociadosDocument(lista, 1);
        arquivo2.setContent("", writeBytesToTempFile(conteudo1), conteudo1.length);
        assertBinariosAssociadosDocument(lista, 1);
        arquivo3.setContent("", writeBytesToTempFile(conteudo2), conteudo2.length);
        assertBinariosAssociadosDocument(lista, 2);

        arquivo1.deleteReference();
        assertBinariosAssociadosDocument(lista, 2);
        arquivo2.deleteReference();
        assertBinariosAssociadosDocument(lista, 1);
        arquivo3.deleteReference();
        assertBinariosAssociadosDocument(lista, 0);
    }

    @Test
    public void testRemoveAttachmentWheDeletingInstanceOrParentInstance() throws IOException {
        PackageBuilder pb = createTestDictionary().createNewPackage("teste");

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
        SIComposite bloco = tipoBloco.newInstance();
        SIList<SIAttachment> anexos = bloco.getFieldList("anexos", SIAttachment.class);

        anexos.addNew().setContent("", writeBytesToTempFile(conteudo1), conteudo1.length); // 0
        anexos.addNew().setContent("", writeBytesToTempFile(conteudo2), conteudo2.length); // 1
        anexos.addNew().setContent("", writeBytesToTempFile(conteudo2), conteudo2.length); // 2
        anexos.addNew().setContent("", writeBytesToTempFile(conteudo3), conteudo3.length); // 3
        anexos.addNew().setContent("", writeBytesToTempFile(conteudo3), conteudo3.length); // 4
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
        subBloco.getField("subArquivo1", SIAttachment.class).setContent("", writeBytesToTempFile(conteudo1), conteudo1.length);
        subBloco.getField("subArquivo2", SIAttachment.class).setContent("", writeBytesToTempFile(conteudo2), conteudo2.length);

        assertBinariosAssociadosDocument(bloco, 2);

        bloco.setValue("subBloco.subArquivo2", null);
        assertBinariosAssociadosDocument(bloco, 1);

        bloco.setValue("subBloco", null);
        assertBinariosAssociadosDocument(bloco, 0);

        // Testa apenas com lista e subBloco interferido um no outro
        bloco = tipoBloco.newInstance();
        anexos = bloco.getFieldList("anexos", SIAttachment.class);
        anexos.addNew().setContent("", writeBytesToTempFile(conteudo3), conteudo3.length); // 0
        anexos.addNew().setContent("", writeBytesToTempFile(conteudo2), conteudo2.length); // 1
        anexos.addNew().setContent("", writeBytesToTempFile(conteudo1), conteudo1.length); // 2
        subBloco = bloco.getFieldComposite("subBloco");
        subBloco.getField("subArquivo1", SIAttachment.class).setContent("", writeBytesToTempFile(conteudo1), conteudo1.length);
        subBloco.getField("subArquivo2", SIAttachment.class).setContent("", writeBytesToTempFile(conteudo2), conteudo2.length);
        subBloco.getField("subArquivo3", SIAttachment.class).setContent("", writeBytesToTempFile(conteudo3), conteudo3.length);

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
    public void testSerializacaoDeserializacaoComAnexo() throws IOException {
        SIAttachment arq = (SIAttachment) createSerializableTestInstance("teste.arq", pacote -> {
            pacote.createType("arq", STypeAttachment.class);
        });
        final byte[] conteudo1 = new byte[]{1, 2, 3};
        arq.setContent("arq1", writeBytesToTempFile(conteudo1), conteudo1.length);
        assertConteudo(conteudo1, arq, 1);
        SIAttachment arq2 = (SIAttachment) TesteFormSerializationUtil.testSerializacao(arq);
        assertConteudo(conteudo1, arq2, 1);
    }

    @Test
    public void testSerializacaoDeserializacaoCompositeComAnexo() throws IOException {
        SIComposite bloco = (SIComposite) createSerializableTestInstance("teste.bloco", pacote -> {
            STypeComposite<? extends SIComposite> tipoBloco = pacote.createCompositeType("bloco");
            tipoBloco.addField("arquivo1", STypeAttachment.class);
            tipoBloco.addField("arquivo2", STypeAttachment.class);
        });
        final byte[] conteudo1 = new byte[]{1, 2, 3};
        final byte[] conteudo2 = new byte[]{4, 5, 6};


        final SIAttachment arquivo1 = bloco.getField("arquivo1", SIAttachment.class);
        final SIAttachment arquivo2 = bloco.getField("arquivo2", SIAttachment.class);

        arquivo1.setContent("content",writeBytesToTempFile(conteudo1), conteudo1.length);
        arquivo2.setContent("content", writeBytesToTempFile(conteudo2), conteudo2.length);

        assertConteudo(conteudo1, arquivo1, 2);
        assertConteudo(conteudo2, arquivo2, 2);

        SIComposite bloco2 = (SIComposite) TesteFormSerializationUtil.testSerializacao(bloco);

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
