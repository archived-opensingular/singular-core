package br.net.mirante.singular.form.mform.core.attachment;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoLista;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.TestCaseForm;
import br.net.mirante.singular.form.mform.io.HashUtil;

public class TesteMPacoteAttachment extends TestCaseForm {

    public void testSimpleAttachment() {
        MIAttachment arquivo = createEmptyAttachment();
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

    private static void assertConteudo(byte[] conteudoEsperado, MIAttachment arquivo, int expectedDistintictFiles) {
        String hash = HashUtil.toSHA1Base16(conteudoEsperado);

        assertTrue(Arrays.equals(conteudoEsperado, arquivo.getContentAsByteArray()));
        assertEquals((Integer) conteudoEsperado.length, arquivo.getFileSize());
        assertEquals(hash, arquivo.getFileId());
        assertEquals(hash, arquivo.getFileHashSHA1());
        assertNotNull(arquivo.getAttachmentRef());

        assertBinariosAssociadosDocument(arquivo, expectedDistintictFiles);
    }

    private static MIAttachment createEmptyAttachment() {
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");
        MTipoAttachment tipo = pb.createTipo("arquivo", MTipoAttachment.class);
        return tipo.novaInstancia();
    }

    public void testUpdateContent() {
        MIAttachment arquivo = createEmptyAttachment();

        byte[] conteudo1 = new byte[] { 1, 2, 3 };
        byte[] conteudo2 = new byte[] { 4, 5, 6, 7, 8, 9, 10 };

        arquivo.setContent(conteudo1);
        assertConteudo(conteudo1, arquivo, 1);

        arquivo.setContent(conteudo2);
        assertConteudo(conteudo2, arquivo, 1);
    }

    public void testSetContentToNull() {
        MIAttachment arquivo = createEmptyAttachment();
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

    public void testSetContentSizeZero() {
        MIAttachment arquivo = createEmptyAttachment();
        byte[] conteudo = new byte[0];
        arquivo.setContent(conteudo);
        assertConteudo(conteudo, arquivo, 1);
    }

    public void testSetContentWithIOException() {
        MIAttachment arquivo = createEmptyAttachment();
        assertException(() -> arquivo.setContent(new InputStreamComErro()), "Erro lendo origem de dados");

        assertNoReference(arquivo, 0);

        byte[] conteudo = new byte[] { 9, 10, 11, 12 };
        arquivo.setContent(conteudo);
        assertConteudo(conteudo, arquivo, 1);

        assertException(() -> arquivo.setContent(new InputStreamComErro()), "Erro lendo origem de dados");
        assertConteudo(conteudo, arquivo, 1);
    }

    public void testRepeatedAttachment() {
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");
        MTipoLista<MTipoAttachment> tipoLista = pb.createTipoListaOf("anexos", MTipoAttachment.class);
        MILista<MIAttachment> lista = tipoLista.novaInstancia(MIAttachment.class);

        MIAttachment arquivo1 = lista.addNovo();
        MIAttachment arquivo2 = lista.addNovo();
        MIAttachment arquivo3 = lista.addNovo();

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

    public void testRemoveAttachmentWheDeletingInstanceOrParentInstance() {
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");

        MTipoComposto<? extends MIComposto> tipoBloco = pb.createTipoComposto("bloco");
        tipoBloco.addCampoListaOf("anexos", MTipoAttachment.class);
        MTipoComposto<? extends MIComposto> tipoSubBloco = tipoBloco.addCampoComposto("subBloco");
        tipoSubBloco.addCampo("subArquivo1", MTipoAttachment.class);
        tipoSubBloco.addCampo("subArquivo2", MTipoAttachment.class);
        tipoSubBloco.addCampo("subArquivo3", MTipoAttachment.class);

        final byte[] conteudo1 = new byte[] { 1, 2, 3 };
        final byte[] conteudo2 = new byte[] { 4, 5, 6 };
        final byte[] conteudo3 = new byte[] { 7, 8, 9 };

        // Testa apenas com lista
        MIComposto bloco = tipoBloco.novaInstancia();
        MILista<MIAttachment> anexos = bloco.getFieldList("anexos", MIAttachment.class);

        anexos.addNovo().setContent(conteudo1); // 0
        anexos.addNovo().setContent(conteudo2); // 1
        anexos.addNovo().setContent(conteudo2); // 2
        anexos.addNovo().setContent(conteudo3); // 3
        anexos.addNovo().setContent(conteudo3); // 4
        anexos.addNovo(); // 5
        assertBinariosAssociadosDocument(anexos, 3);

        anexos.remove(4);
        assertBinariosAssociadosDocument(anexos, 3);
        anexos.remove(3);
        assertBinariosAssociadosDocument(anexos, 2);

        bloco.setValor("anexos", null);
        assertBinariosAssociadosDocument(anexos, 0);

        // Testa apenas com subBloco
        bloco = tipoBloco.novaInstancia();
        MIComposto subBloco = bloco.getFieldRecord("subBloco");
        subBloco.getField("subArquivo1", MIAttachment.class).setContent(conteudo1);
        subBloco.getField("subArquivo2", MIAttachment.class).setContent(conteudo2);

        assertBinariosAssociadosDocument(anexos, 2);

        bloco.setValor("subBloco.subArquivo2", null);
        assertBinariosAssociadosDocument(anexos, 1);

        bloco.setValor("subBloco", null);
        assertBinariosAssociadosDocument(anexos, 0);

        // Testa apenas com lista e subBloco interferido um no outro
        bloco = tipoBloco.novaInstancia();
        anexos = bloco.getFieldList("anexos", MIAttachment.class);
        anexos.addNovo().setContent(conteudo3); // 0
        anexos.addNovo().setContent(conteudo2); // 1
        anexos.addNovo().setContent(conteudo1); // 2
        subBloco = bloco.getFieldRecord("subBloco");
        subBloco.getField("subArquivo1", MIAttachment.class).setContent(conteudo1);
        subBloco.getField("subArquivo2", MIAttachment.class).setContent(conteudo2);
        subBloco.getField("subArquivo3", MIAttachment.class).setContent(conteudo3);

        assertBinariosAssociadosDocument(anexos, 3);

        bloco.setValor("subBloco.subArquivo1", null); // conteudo1
        assertBinariosAssociadosDocument(anexos, 3);
        anexos.remove(2); // conteudo1
        assertBinariosAssociadosDocument(anexos, 2);
        anexos.remove(1); // conteudo2
        assertBinariosAssociadosDocument(anexos, 2);

        bloco.setValor("anexos", null); // conteudo2, conteudo3
        assertBinariosAssociadosDocument(anexos, 1);

    }

    public void testSerializacaoDeserializacaoComAnexo() {
        fail("Implementar essa verificação");
    }

    private static void assertBinariosAssociadosDocument(MInstancia ref, int expectedDistinctFiles) {
        AttachmentDocumentService aService = AttachmentDocumentService.lookup(ref);
        assertEquals(expectedDistinctFiles, aService.countDistinctFiles());
    }

    private static void assertNoReference(MIAttachment arquivo, int expectedDistintictFiles) {
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
