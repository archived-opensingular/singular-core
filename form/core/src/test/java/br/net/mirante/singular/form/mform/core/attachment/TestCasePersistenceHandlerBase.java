package br.net.mirante.singular.form.mform.core.attachment;

import static org.fest.assertions.api.Assertions.assertThat;
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
            "i".getBytes(), 
            "np".getBytes(),
            "1234".getBytes()};
    private final String[] hashs = new String[] {
            "042dc4512fa3d391c5170cf3aa61e6a638f84342",
            "003fffd5649fc27c0fc0d15a402a4fe5b0444ce7",
            "7110eda4d09e062aa5e4a390b0a572ac0d2c0220"};
    //@formatter:on

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

    @Test
    public void testSerializacao() throws IOException, ClassNotFoundException {
        for (int i = 0; i < conteudos.length; i++) {
            getHandler().addAttachment(conteudos[i]);
        }
        IAttachmentPersistenceHandler handler2 = deserialize(serialize(getHandler()));

        for (int i = 0; i < conteudos.length; i++) {
            IAttachmentRef ref = handler2.getAttachment(hashs[i]);
            assertThat(ref).isNotNull();
            assertThat(ref.getContentAsByteArray()).isEqualTo(conteudos[i]);
            assertThat(ref.getHashSHA1()).isEqualTo(hashs[i]);
        }
    }

    private byte[] serialize(IAttachmentPersistenceHandler handler) throws IOException {
        ByteArrayOutputStream outB = new ByteArrayOutputStream();
        ObjectOutputStream outO = new ObjectOutputStream(outB);
        outO.writeObject(handler);
        outO.close();

        return outB.toByteArray();
    }
    
    private IAttachmentPersistenceHandler deserialize(byte[] serialized) throws IOException, ClassNotFoundException {
        ObjectInputStream inO = new ObjectInputStream(new ByteArrayInputStream(serialized));
        return (IAttachmentPersistenceHandler) inO.readObject();
    }

    @Test
    public void testIndependenciaDeleteEntreContextosDiferentes() throws IOException {
        IAttachmentPersistenceHandler handler1 = getHandler();
        IAttachmentPersistenceHandler handler2 = setupHandler();
        assertNotEquals(handler1, handler2);

        IAttachmentRef ref11 = handler1.addAttachment(conteudos[1]);
        IAttachmentRef ref12 = handler1.addAttachment(conteudos[2]);
        IAttachmentRef ref13 = handler1.addAttachment(conteudos[0]);
        assertConteudo(handler1, ref13, conteudos[0], hashs[0], 3);

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
