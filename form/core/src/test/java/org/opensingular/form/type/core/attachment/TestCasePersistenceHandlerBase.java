package org.opensingular.form.type.core.attachment;

import com.google.common.io.ByteStreams;
import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.opensingular.form.SingularFormException;
import org.opensingular.internal.lib.commons.util.SingularIOUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.opensingular.form.type.core.attachment.AttachmentTestUtil.writeBytesToTempFile;

public abstract class TestCasePersistenceHandlerBase {

    // @formatter:off
    private final byte[][] conteudos = new byte[][]{"i".getBytes(), "np".getBytes(), "1234".getBytes()};
    private final String[] fileNames = new String[]{"i.txt", "np.txt", "1234.txt"};
    private final String[] hashs     = new String[]{"042dc4512fa3d391c5170cf3aa61e6a638f84342", "003fffd5649fc27c0fc0d15a402a4fe5b0444ce7", "7110eda4d09e062aa5e4a390b0a572ac0d2c0220"};
    private IAttachmentPersistenceHandler persistenHandler;
    // @formatter:on

    private static void assertConteudo(IAttachmentPersistenceHandler handler, IAttachmentRef ref, byte[] conteudoEsperado, String hashEsperado, int sizeEsperado) throws IOException {
        assertEquals(hashEsperado, ref.getHashSHA1());
        assertEquals(hashEsperado, ref.getId());
        assertEquals(sizeEsperado, handler.getAttachments().size());
        assertTrue(Arrays.equals(conteudoEsperado, ByteStreams.toByteArray(ref.getInputStream())));
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
            refs[i] = getHandler().addAttachment(writeBytesToTempFile(conteudos[i]), conteudos[i].length, fileNames[i]);
        }

        for (int i = 0; i < conteudos.length; i++) {
            IAttachmentRef ref = refs[i];
            IAttachmentRef ref2 = SingularIOUtils.serializeAndDeserialize(ref);
            IAttachmentRef ref3 = getHandler().getAttachment(ref.getId());

            assertThat(ref2).isNotNull();
            assertThat(ByteStreams.toByteArray(ref2.getInputStream())).isEqualTo(conteudos[i]);
            assertThat(ref2.getHashSHA1()).isEqualTo(hashs[i]);

            assertThat(ref3).isNotNull();
            assertThat(ByteStreams.toByteArray(ref3.getInputStream())).isEqualTo(conteudos[i]);
            assertThat(ref3.getHashSHA1()).isEqualTo(hashs[i]);
        }
    }

    @Test
    @Ignore("Review this test")
    public void testIndependenciaDeleteEntreContextosDiferentes() throws IOException {
        IAttachmentPersistenceHandler handler1 = getHandler();
        IAttachmentPersistenceHandler handler2 = setupHandler();
        assertNotEquals(handler1, handler2);

        IAttachmentRef ref11 = handler1.addAttachment(writeBytesToTempFile(conteudos[1]), conteudos[1].length, fileNames[1]);
        IAttachmentRef ref12 = handler1.addAttachment(writeBytesToTempFile(conteudos[2]), conteudos[2].length, fileNames[2]);
        IAttachmentRef ref13 = handler1.addAttachment(writeBytesToTempFile(conteudos[0]), conteudos[0].length, fileNames[0]);
        assertConteudo(handler1, ref13, conteudos[0], hashs[0], 3);

        IAttachmentRef ref21 = handler2.addAttachment(writeBytesToTempFile(conteudos[1]), conteudos[1].length, fileNames[1]);
        IAttachmentRef ref22 = handler2.addAttachment(writeBytesToTempFile(conteudos[2]), conteudos[2].length, fileNames[2]);
        assertConteudo(handler2, ref22, conteudos[2], hashs[2], 2);

        handler2.deleteAttachment(ref21.getHashSHA1(), null);
        assertNull(handler2.getAttachment(ref11.getId()));
        assertConteudo(handler2, ref22, conteudos[2], hashs[2], 1);
        assertConteudo(handler1, ref11, conteudos[1], hashs[1], 3);
        assertConteudo(handler1, handler1.getAttachment(ref11.getId()), conteudos[1], hashs[1], 3);

        handler1.deleteAttachment(ref12.getHashSHA1(), null);
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

        handler1.addAttachment(writeBytesToTempFile(conteudos[1]), conteudos[1].length, fileNames[1]);
        IAttachmentRef ref12o = handler1.addAttachment(writeBytesToTempFile(conteudos[2]), conteudos[2].length, fileNames[2]);

        IAttachmentRef ref21o = handler2.addAttachment(writeBytesToTempFile(conteudos[1]), conteudos[1].length, fileNames[1]);

//         Apagando na origem
        IAttachmentRef ref22c = handler2.copy(ref12o, null).getNewAttachmentRef();
        assertConteudo(handler2, ref22c, conteudos[2], hashs[2], 2);
        assertConteudo(handler2, handler2.getAttachment(hashs[2]), conteudos[2], hashs[2], 2);

        handler1.deleteAttachment(hashs[2], null);
        assertNull(handler1.getAttachment(hashs[2]));
        assertConteudo(handler2, ref22c, conteudos[2], hashs[2], 2);
        assertConteudo(handler2, handler2.getAttachment(hashs[2]), conteudos[2], hashs[2], 2);

        // apagando no destino
        IAttachmentRef ref11c = handler1.copy(ref21o, null).getNewAttachmentRef();
        assertConteudo(handler1, ref11c, conteudos[1], hashs[1], 1);
        assertConteudo(handler1, handler1.getAttachment(hashs[1]), conteudos[1], hashs[1], 1);

        handler1.deleteAttachment(hashs[1], null);
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
            getHandler().addAttachment(new File(""), 1, "teste.txt");
            fail("Era esperada Exception");
        } catch (SingularFormException e) {
            Assert.assertTrue(e.getMessage().contains("Erro lendo origem de dados"));
        }
        assertEquals(0, getHandler().getAttachments().size());
    }

    @Test
    public void deletedFileIsNoLongerAvailable() throws IOException {
        IAttachmentRef ref = getHandler().addAttachment(writeBytesToTempFile(new byte[]{1, 2}), 2l, "testando.txt");
        getHandler().deleteAttachment(ref.getId(), null);
        assertThat(getHandler().getAttachment(ref.getId())).isNull();
    }

    @Test
    public void doesNothingWhenYouTryToDeleteANullFile() {
        getHandler().deleteAttachment(null, null);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void deleteOnlyTheDesiredFile() throws IOException {
        getHandler().addAttachment(writeBytesToTempFile(new byte[]{1, 2, 3}), 3l, "testando1.txt");
        IAttachmentRef ref = getHandler().addAttachment(writeBytesToTempFile(new byte[]{1, 2}), 2l, "testando2.txt");
        getHandler().addAttachment(writeBytesToTempFile(new byte[]{1, 2, 4, 5}), 4l, "testando3.txt");

        getHandler().deleteAttachment(ref.getId(), null);

        assertThat((Collection<IAttachmentRef>) getHandler().getAttachments()).hasSize(2)
                .doesNotContain(ref);
    }


}
