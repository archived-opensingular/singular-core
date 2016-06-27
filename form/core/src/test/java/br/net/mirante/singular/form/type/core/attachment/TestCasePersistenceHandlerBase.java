package br.net.mirante.singular.form.type.core.attachment;

import br.net.mirante.singular.form.SingularFormException;
import com.google.common.io.ByteStreams;
import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collection;

import static br.net.mirante.singular.form.type.core.attachment.AttachmentTestUtil.writeBytesToTempFile;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public abstract class TestCasePersistenceHandlerBase {

    // @formatter:off
    private final byte[][] conteudos = new byte[][] { "i".getBytes(), "np".getBytes(), "1234".getBytes() };
    private final String[] hashs = new String[] { "042dc4512fa3d391c5170cf3aa61e6a638f84342", "003fffd5649fc27c0fc0d15a402a4fe5b0444ce7", "7110eda4d09e062aa5e4a390b0a572ac0d2c0220" };
    private IAttachmentPersistenceHandler persistenHandler;
    // @formatter:on

    private static void assertConteudo(IAttachmentPersistenceHandler handler, IAttachmentRef ref, byte[] conteudoEsperado, String hashEsperado, int sizeEsperado) throws IOException {
        assertEquals(hashEsperado, ref.getHasSHA1());
        assertEquals(hashEsperado, ref.getId());
        assertEquals(sizeEsperado, handler.getAttachments().size());
        assertTrue(Arrays.equals(conteudoEsperado, ByteStreams.toByteArray(ref.newInputStream())));
    }

    protected final IAttachmentPersistenceHandler getHandler() {
        if (persistenHandler == null) {
            persistenHandler = setupHandler();
        }
        return persistenHandler;
    }

    /**
     * Se chamado mais de uma vez, deve retornar contextos diferente.
     */
    protected abstract IAttachmentPersistenceHandler setupHandler();

    @After
    public void limpeza() {
        persistenHandler = null;
    }

    @Test
    public void testSerializacao() throws IOException, ClassNotFoundException {
        IAttachmentRef[] refs = new IAttachmentRef[conteudos.length];
        for (int i = 0; i < conteudos.length; i++) {
            refs[i] = getHandler().addAttachment(writeBytesToTempFile(conteudos[i]), conteudos[i].length);
        }
        IAttachmentPersistenceHandler handler2 = deserialize(serialize(getHandler()));

        for (int i = 0; i < conteudos.length; i++) {
            IAttachmentRef ref = handler2.getAttachment(refs[i].getId());
            assertThat(ref).isNotNull();
            assertThat(ByteStreams.toByteArray(ref.newInputStream())).isEqualTo(conteudos[i]);
            assertThat(ref.getHasSHA1()).isEqualTo(hashs[i]);
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
    @Ignore("Review this test")
    public void testIndependenciaDeleteEntreContextosDiferentes() throws IOException {
        IAttachmentPersistenceHandler handler1 = getHandler();
        IAttachmentPersistenceHandler handler2 = setupHandler();
        assertNotEquals(handler1, handler2);

        IAttachmentRef ref11 = handler1.addAttachment(writeBytesToTempFile(conteudos[1]), conteudos[1].length);
        IAttachmentRef ref12 = handler1.addAttachment(writeBytesToTempFile(conteudos[2]), conteudos[2].length);
        IAttachmentRef ref13 = handler1.addAttachment(writeBytesToTempFile(conteudos[0]), conteudos[0].length);
        assertConteudo(handler1, ref13, conteudos[0], hashs[0], 3);

        IAttachmentRef ref21 = handler2.addAttachment(writeBytesToTempFile(conteudos[1]), conteudos[1].length);
        IAttachmentRef ref22 = handler2.addAttachment(writeBytesToTempFile(conteudos[2]), conteudos[2].length);
        assertConteudo(handler2, ref22, conteudos[2], hashs[2], 2);

        handler2.deleteAttachment(ref21.getHasSHA1());
        assertNull(handler2.getAttachment(ref11.getId()));
        assertConteudo(handler2, ref22, conteudos[2], hashs[2], 1);
        assertConteudo(handler1, ref11, conteudos[1], hashs[1], 3);
        assertConteudo(handler1, handler1.getAttachment(ref11.getId()), conteudos[1], hashs[1], 3);

        handler1.deleteAttachment(ref12.getHasSHA1());
        assertNull(handler1.getAttachment(ref12.getId()));
        assertConteudo(handler2, ref22, conteudos[2], hashs[2], 1);
        assertConteudo(handler2, handler2.getAttachment(ref22.getId()), conteudos[2], hashs[2], 1);
    }

    @Test
    @Ignore("Review this test")
    public void testCopiaEntreContextosDiferentesComDeletesDepois() throws IOException {
        IAttachmentPersistenceHandler handler1 = getHandler();
        IAttachmentPersistenceHandler handler2 = setupHandler();
        assertNotEquals(handler1, handler2);

        handler1.addAttachment(writeBytesToTempFile(conteudos[1]), conteudos[1].length);
        IAttachmentRef ref12o = handler1.addAttachment(writeBytesToTempFile(conteudos[2]), conteudos[2].length);

        IAttachmentRef ref21o = handler2.addAttachment(writeBytesToTempFile(conteudos[1]), conteudos[1].length);

//         Apagando na origem
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

    @Test
    @Ignore("To be implemented")
    public void testLeituraComHashViolado() {
        fail("implementar");
    }

    @Test
    @Ignore("To be implemented")
    public void testCompactacaoConteudoInterno() {
        fail("implementar");
    }

    @Test
    public void testExceptionNaEscritaDoConteudo() throws IOException {
        try {
            getHandler().addAttachment(new File(""), 1);
            fail("Era esperada Exception");
        } catch (SingularFormException e) {
            Assert.assertTrue(e.getMessage().contains("Erro lendo origem de dados"));
        }
        assertEquals(0, getHandler().getAttachments().size());
    }

    @Test
    public void deletedFileIsNoLongerAvailable() throws IOException {
        IAttachmentRef ref = getHandler().addAttachment(writeBytesToTempFile(new byte[]{1, 2}), 2l);
        getHandler().deleteAttachment(ref.getId());
        assertThat(getHandler().getAttachment(ref.getId())).isNull();
    }

    @Test
    public void doesNothingWhenYouTryToDeleteANullFile() {
        getHandler().deleteAttachment(null);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void deleteOnlyTheDesiredFile() throws IOException {
        getHandler().addAttachment(writeBytesToTempFile(new byte[]{1, 2, 3}), 3l);
        IAttachmentRef ref = getHandler().addAttachment(writeBytesToTempFile(new byte[]{1, 2}), 2l);
        getHandler().addAttachment(writeBytesToTempFile(new byte[]{1, 2, 4, 5}), 4l);

        getHandler().deleteAttachment(ref.getId());

        assertThat((Collection<IAttachmentRef>) getHandler().getAttachments()).hasSize(2)
                .doesNotContain(ref);
    }


}
