package br.net.mirante.singular.form.mform.core.attachment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.io.ByteStreams;

import br.net.mirante.singular.form.mform.SingularFormException;

public abstract class TestCasePersistenceHandlerBase {

    private IAttachmentPersistenceHandler persistenHandler;

    //@formatter:off
    private final byte[][] conteudos = new byte[][] {
            new byte[0],
            "i".getBytes(), //Caso importante pelo hash começa com zero
            "np".getBytes(),
            "1234".getBytes(),
            "TesteTesteTeste".getBytes(),
            "MiranteMiranteMiranteMirante".getBytes(),
            "sha1 this string".getBytes(),
            "The quick brown fox jumps over the lazy dog".getBytes()};
    private final String[] hashs = new String[] {
            "da39a3ee5e6b4b0d3255bfef95601890afd80709",
            "042dc4512fa3d391c5170cf3aa61e6a638f84342",
            "003fffd5649fc27c0fc0d15a402a4fe5b0444ce7",
            "7110eda4d09e062aa5e4a390b0a572ac0d2c0220",
            "ceecae2e6034de45a8303a31e9e96adb37c2443f",
            "79244437e10faf670b335edc3e3aada33e6790f8",
            "cf23df2207d99a74fbe169e3eba035e633b65d94",
            "2fd4e1c67a2d28fced849ee1bb76e7391b93eb12"};
    //@formatter:on

    // array 10 * 1024 * 1024
    private final String hashArquivo10MegaTudoZero = "8c206a1a87599f532ce68675536f0b1546900d7a";
    // array 100 * 1024 * 1024
    private final String hashArquivo100MegaTudoUm = "9abe36b18e1871f67d581f2d1f4b6a9036dcc23f";

    protected final IAttachmentPersistenceHandler getHandler() {
        if (persistenHandler == null) {
            persistenHandler = setupHandler();
        }
        return persistenHandler;
    }

    /** Se chamado mais de uma vez, deve retornar contextos diferente. */
    protected abstract IAttachmentPersistenceHandler setupHandler();

    @After
    public void limpeza() {
        persistenHandler = null;
    }


    @Test
    public void testAddAttachmentArrayByte() throws IOException {
        for (int i = 0; i < conteudos.length; i++) {
            assertAddConteudoByte(conteudos[i], hashs[i], i + 1);
        }
        // Verifica se trata colisões corretamente
        for (int i = 0; i < conteudos.length; i++) {
            assertAddConteudoByte(conteudos[i], hashs[i], conteudos.length);
        }
    }

    @Test
    public void testAddAttachmentInputStream() throws IOException {
        for (int i = 0; i < conteudos.length; i++) {
            assertAddConteudoInputStream(conteudos[i], hashs[i], i + 1);
        }
        // Verifica se trata colisões corretamente
        for (int i = 0; i < conteudos.length; i++) {
            assertAddConteudoInputStream(conteudos[i], hashs[i], conteudos.length);
        }
    }

    private void assertAddConteudoByte(byte[] conteudoEsperado, String hashEsperado, int sizeEsperado) throws IOException {
        IAttachmentRef ref = getHandler().addAttachment(conteudoEsperado);
        assertConteudo(ref, conteudoEsperado, hashEsperado, sizeEsperado);

        IAttachmentRef ref2 = getHandler().getAttachment(hashEsperado);
        assertConteudo(ref2, conteudoEsperado, hashEsperado, sizeEsperado);
    }

    private void assertAddConteudoInputStream(byte[] conteudoEsperado, String hashEsperado, int sizeEsperado) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(conteudoEsperado);
        IAttachmentRef ref = getHandler().addAttachment(in);
        assertConteudo(ref, conteudoEsperado, hashEsperado, sizeEsperado);

        IAttachmentRef ref2 = getHandler().getAttachment(hashEsperado);
        assertConteudo(ref2, conteudoEsperado, hashEsperado, sizeEsperado);
    }

    private void assertConteudo(IAttachmentRef ref, byte[] conteudoEsperado, String hashEsperado, int sizeEsperado) throws IOException {
        assertConteudo(getHandler(), ref, conteudoEsperado, hashEsperado, sizeEsperado);
    }

    private static void assertConteudo(IAttachmentPersistenceHandler handler, IAttachmentRef ref, byte[] conteudoEsperado,
            String hashEsperado, int sizeEsperado) throws IOException {
        assertEquals(hashEsperado, ref.getHashSHA1());
        assertEquals(hashEsperado, ref.getId());
        assertEquals(sizeEsperado, handler.getAttachments().size());
        assertTrue(Arrays.equals(conteudoEsperado, ref.getContentAsByteArray()));
        assertTrue(Arrays.equals(conteudoEsperado, ByteStreams.toByteArray(ref.getContent())));
    }

    private static byte[] newByteArray(int size, byte conteudo) {
        byte[] grande = new byte[size];
        Arrays.fill(grande, conteudo);
        return grande;
    }

    @Test
    public void testAddAttachmentArrayByteGrande1() throws IOException {
        assertAddConteudoByte(newByteArray(10 * 1024 * 1024, (byte) 0), hashArquivo10MegaTudoZero, 1);
    }

    @Ignore("Demora a execução")
    public void testAddAttachmentArrayByteGrande2() throws IOException {
        assertAddConteudoByte(newByteArray(100 * 1024 * 1024, (byte) 1), hashArquivo100MegaTudoUm, 1);
    }

    @Test
    public void testAddAttachmentInpuStreamGrande1() throws IOException {
        assertAddConteudoInputStream(newByteArray(10 * 1024 * 1024, (byte) 0), hashArquivo10MegaTudoZero, 1);
    }

    @Ignore("Demora a execução")
    public void testAddAttachmentInpuStreamGrande2() throws IOException {
        assertAddConteudoInputStream(newByteArray(100 * 1024 * 1024, (byte) 1), hashArquivo100MegaTudoUm, 1);
    }

    @Test
    public void testSerializacao() throws IOException, ClassNotFoundException {
        for (int i = 0; i < conteudos.length; i++) {
            getHandler().addAttachment(conteudos[i]);
        }
        ByteArrayOutputStream outB = new ByteArrayOutputStream();
        ObjectOutputStream outO = new ObjectOutputStream(outB);
        outO.writeObject(getHandler());
        outO.close();

        ObjectInputStream inO = new ObjectInputStream(new ByteArrayInputStream(outB.toByteArray()));
        IAttachmentPersistenceHandler handler2 = (IAttachmentPersistenceHandler) inO.readObject();

        for (int i = 0; i < conteudos.length; i++) {
            IAttachmentRef ref = handler2.getAttachment(hashs[i]);
            assertNotNull(ref);
            assertConteudo(ref, conteudos[i], hashs[i], conteudos.length);
        }
    }

    @Test
    public void testIndependenciaDeleteEntreContextosDiferentes() throws IOException {
        IAttachmentPersistenceHandler handler1 = getHandler();
        IAttachmentPersistenceHandler handler2 = setupHandler();
        assertNotEquals(handler1, handler2);

        IAttachmentRef ref11 = handler1.addAttachment(conteudos[1]);
        IAttachmentRef ref12 = handler1.addAttachment(conteudos[2]);
        IAttachmentRef ref13 = handler1.addAttachment(conteudos[3]);
        assertConteudo(handler1, ref13, conteudos[3], hashs[3], 3);

        IAttachmentRef ref21 = handler2.addAttachment(conteudos[1]);
        IAttachmentRef ref22 = handler2.addAttachment(conteudos[2]);
        assertConteudo(handler2, ref22, conteudos[2], hashs[2], 2);

        handler2.deleteAttachment(ref21.getHashSHA1());
        assertNull(handler2.getAttachment(hashs[1]));
        assertConteudo(handler2, ref22, conteudos[2], hashs[2], 1);
        assertConteudo(handler1, ref11, conteudos[1], hashs[1], 3);
        assertConteudo(handler1, handler1.getAttachment(ref11.getHashSHA1()), conteudos[1], hashs[1], 3);

        handler1.deleteAttachment(ref12.getHashSHA1());
        assertNull(handler1.getAttachment(hashs[2]));
        assertConteudo(handler2, ref22, conteudos[2], hashs[2], 1);
        assertConteudo(handler2, handler2.getAttachment(ref22.getHashSHA1()), conteudos[2], hashs[2], 1);
    }

    @Test
    public void testCopiaEntreContextosDiferentesComDeletesDepois() throws IOException {
        IAttachmentPersistenceHandler handler1 = getHandler();
        IAttachmentPersistenceHandler handler2 = setupHandler();
        assertNotEquals(handler1, handler2);

        IAttachmentRef ref11 = handler1.addAttachment(conteudos[1]);
        IAttachmentRef ref12o = handler1.addAttachment(conteudos[2]);

        IAttachmentRef ref21o = handler2.addAttachment(conteudos[1]);

        // Apagando na origem
        IAttachmentRef ref22c = handler2.copy(ref12o);
        assertConteudo(handler2, ref22c, conteudos[2], hashs[2], 2);
        assertConteudo(handler2, handler2.getAttachment(hashs[2]), conteudos[2], hashs[2], 2);

        handler1.deleteAttachment(hashs[2]);
        assertNull(handler1.getAttachment(hashs[2]));
        assertConteudo(handler2, ref22c, conteudos[2], hashs[2], 2);
        assertConteudo(handler2, handler2.getAttachment(hashs[2]), conteudos[2], hashs[2], 2);

        // apagando no destino
        IAttachmentRef ref11c = handler1.copy(ref21o);
        assertConteudo(handler1, ref11c, conteudos[1], hashs[1], 1);
        assertConteudo(handler1, handler1.getAttachment(hashs[1]), conteudos[1], hashs[1], 1);

        handler1.deleteAttachment(hashs[1]);
        assertNull(handler1.getAttachment(hashs[1]));
        assertConteudo(handler2, ref21o, conteudos[1], hashs[1], 2);
        assertConteudo(handler2, handler2.getAttachment(hashs[1]), conteudos[1], hashs[1], 2);
    }

    @Test @Ignore("To be implemented")
    public void testLeituraComHashViolado() {
        fail("implementar");
    }

    @Test  @Ignore("To be implemented")
    public void testCompactacaoConteudoInterno() {
        fail("implementar");
    }

    @Test
    public void testExceptionNaEscritaDoConteudo() {
        try {
            getHandler().addAttachment(TesteMPacoteAttachment.createInputStreamGeradoraException());
            fail("Era esperada Exception");
        } catch (SingularFormException e) {
            Assert.assertTrue(e.getMessage().contains("Erro lendo origem de dados"));
        }
        assertEquals(0, getHandler().getAttachments().size());
    }
}
