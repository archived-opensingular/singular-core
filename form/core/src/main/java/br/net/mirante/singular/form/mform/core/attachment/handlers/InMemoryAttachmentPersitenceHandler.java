package br.net.mirante.singular.form.mform.core.attachment.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.common.io.ByteStreams;
import com.google.common.io.CountingInputStream;

import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentRef;
import br.net.mirante.singular.form.mform.io.HashUtil;

/**
 * <p>
 * Implementação manipulador de anexo que guarda tudo em memória como array de
 * bytes.
 * </p>
 * <p>
 * Deve ser evitado usado dessa implementação devido ao risco de estouro de
 * memória do servidor.
 * </p>
 *
 * @author Daniel C. Bordin
 */
@SuppressWarnings("serial")
public class InMemoryAttachmentPersitenceHandler extends AbstractAttachmentPersistenceHandler {

    private Map<String, InMemoryAttachmentRef> attachments;

    @Override
    protected IAttachmentRef addAttachmentCompressed(InputStream deflateInputStream, String hashSHA16Hex, int originalLength) {
        return add(new InMemoryAttachmentRef(toByteArray(deflateInputStream), originalLength, hashSHA16Hex));
    }

    private static byte[] toByteArray(InputStream in) {
        try {
            return ByteStreams.toByteArray(in);
        } catch (IOException e) {
            throw new SingularFormException("Erro lendo origem de dados", e);
        }
    }

    private IAttachmentRef add(InMemoryAttachmentRef novo) {
        if (attachments == null) {
            attachments = new HashMap<>();
        }
        attachments.put(novo.getHashSHA1(), novo);
        return novo;
    }

    @Override
    protected IAttachmentRef addAttachmentCompressed(InputStream deflateInputStream, CountingInputStream inCounting,
            DigestInputStream hashCalculatorStream) {
        return add(new InMemoryAttachmentRef(toByteArray(deflateInputStream), (int) inCounting.getCount(),
                HashUtil.toSHA1Base16(hashCalculatorStream)));
    }

    @Override
    public Collection<? extends IAttachmentRef> getAttachments() {
        return (attachments == null) ? Collections.emptyList() : attachments.values();
    }

    @Override
    public IAttachmentRef getAttachment(String hashId) {
        return (attachments == null) ? null : attachments.get(hashId);
    }

    @Override
    public void deleteAttachment(String hashId) {
        attachments.remove(hashId);
    }
}