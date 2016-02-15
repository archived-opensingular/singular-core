package br.net.mirante.singular.form.mform.core.attachment;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import br.net.mirante.singular.form.mform.MDicionarioResolver;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SList;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeLista;
import br.net.mirante.singular.form.mform.TestCaseForm;
import br.net.mirante.singular.form.mform.io.HashUtil;
import br.net.mirante.singular.form.mform.io.TesteFormSerializationUtil;
import br.net.mirante.singular.lambda.IConsumer;

public class TesteMPacoteAttachment extends TestCaseForm {

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

    private static SIAttachment createEmptyAttachment() {
        SDictionary dicionario = SDictionary.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");
        STypeAttachment tipo = pb.createTipo("arquivo", STypeAttachment.class);
        return tipo.novaInstancia().setTemporary();
    }

    public void testUpdateContent() {
        SIAttachment arquivo = createEmptyAttachment();

        byte[] conteudo1 = new byte[] { 1, 2, 3 };
        byte[] conteudo2 = new byte[] { 4, 5, 6, 7, 8, 9, 10 };

        arquivo.setContent(conteudo1);
        assertConteudo(conteudo1, arquivo, 1);

        arquivo.setContent(conteudo2);
        assertConteudo(conteudo2, arquivo, 1);
    }

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

    public void testSetContentSizeZero() {
        SIAttachment arquivo = createEmptyAttachment();
        byte[] conteudo = new byte[0];
        arquivo.setContent(conteudo);
        assertConteudo(conteudo, arquivo, 1);
    }

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

    public void testRepeatedAttachment() {
        SDictionary dicionario = SDictionary.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");
        STypeLista<STypeAttachment, SIAttachment> tipoLista = pb.createTipoListaOf("anexos", STypeAttachment.class);
        SList<SIAttachment> lista = tipoLista.novaInstancia(SIAttachment.class);

        SIAttachment arquivo1 = lista.addNovo();
        SIAttachment arquivo2 = lista.addNovo();
        SIAttachment arquivo3 = lista.addNovo();

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
        SDictionary dicionario = SDictionary.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");

        STypeComposite<? extends SIComposite> tipoBloco = pb.createTipoComposto("bloco");
        tipoBloco.addCampoListaOf("anexos", STypeAttachment.class);
        STypeComposite<? extends SIComposite> tipoSubBloco = tipoBloco.addCampoComposto("subBloco");
        tipoSubBloco.addCampo("subArquivo1", STypeAttachment.class);
        tipoSubBloco.addCampo("subArquivo2", STypeAttachment.class);
        tipoSubBloco.addCampo("subArquivo3", STypeAttachment.class);

        final byte[] conteudo1 = new byte[] { 1, 2, 3 };
        final byte[] conteudo2 = new byte[] { 4, 5, 6 };
        final byte[] conteudo3 = new byte[] { 7, 8, 9 };

        // Testa apenas com lista
        SIComposite bloco = tipoBloco.novaInstancia();
        SList<SIAttachment> anexos = bloco.getFieldList("anexos", SIAttachment.class);

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
        SIComposite subBloco = bloco.getFieldRecord("subBloco");
        subBloco.getField("subArquivo1", SIAttachment.class).setContent(conteudo1);
        subBloco.getField("subArquivo2", SIAttachment.class).setContent(conteudo2);

        assertBinariosAssociadosDocument(bloco, 2);

        bloco.setValor("subBloco.subArquivo2", null);
        assertBinariosAssociadosDocument(bloco, 1);

        bloco.setValor("subBloco", null);
        assertBinariosAssociadosDocument(bloco, 0);

        // Testa apenas com lista e subBloco interferido um no outro
        bloco = tipoBloco.novaInstancia();
        anexos = bloco.getFieldList("anexos", SIAttachment.class);
        anexos.addNovo().setContent(conteudo3); // 0
        anexos.addNovo().setContent(conteudo2); // 1
        anexos.addNovo().setContent(conteudo1); // 2
        subBloco = bloco.getFieldRecord("subBloco");
        subBloco.getField("subArquivo1", SIAttachment.class).setContent(conteudo1);
        subBloco.getField("subArquivo2", SIAttachment.class).setContent(conteudo2);
        subBloco.getField("subArquivo3", SIAttachment.class).setContent(conteudo3);

        assertBinariosAssociadosDocument(anexos, 3);

        bloco.setValor("subBloco.subArquivo1", null); // conteudo1
        assertBinariosAssociadosDocument(anexos, 3);
        anexos.remove(2); // conteudo1
        assertBinariosAssociadosDocument(anexos, 2);
        anexos.remove(1); // conteudo2
        assertBinariosAssociadosDocument(anexos, 2);

        bloco.setValor("anexos", null); // conteudo2, conteudo3
        assertBinariosAssociadosDocument(anexos, 2);

    }

    public void testSerializacaoDeserializacaoComAnexo() {
        MDicionarioResolver resolver = TesteFormSerializationUtil.createLoaderPacoteTeste((IConsumer<PacoteBuilder>) pacote -> {
            STypeComposite<? extends SIComposite> tipoBloco = pacote.createTipoComposto("bloco");
            tipoBloco.addCampo("arquivo1", STypeAttachment.class);
            tipoBloco.addCampo("arquivo2", STypeAttachment.class);
        });
        final byte[] conteudo1 = new byte[] { 1, 2, 3 };
        final byte[] conteudo2 = new byte[] { 4, 5, 6 };

        SIComposite bloco = (SIComposite) resolver.loadType("teste.bloco").novaInstancia();

        final SIAttachment arquivo1 = bloco.getField("arquivo1", SIAttachment.class);
        final SIAttachment arquivo2 = bloco.getField("arquivo2", SIAttachment.class);

        arquivo1.setTemporary();
        arquivo2.setTemporary();

        arquivo1.setContent(conteudo1);
        arquivo2.setContent(conteudo2);

        assertConteudo(conteudo1, arquivo1, 2);
        assertConteudo(conteudo2, arquivo2, 2);

        SIComposite bloco2 = (SIComposite) TesteFormSerializationUtil.testSerializacao(bloco, resolver);

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
