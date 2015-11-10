package br.net.mirante.singular.form.mform.core.attachment;

import java.io.InputStream;
import java.io.Serializable;

@SuppressWarnings("serial")
public class InMemoryAttachmentRef implements IAttachmentRef, Serializable {

    private final int size;
    private final String hashSHA1Hex;
    private final byte[] conteudo;

    public InMemoryAttachmentRef(byte[] conteudo, int size, String hashSHA1Hex) {
        this.conteudo = conteudo;
        this.size = size;
        this.hashSHA1Hex = hashSHA1Hex;
    }

    @Override
    public String getHashSHA1() {
        return hashSHA1Hex;
    }

    @Override
    public InputStream getContent() {
        return CompressionUtil.inflateToInputStream(conteudo);
    }

    @Override
    public Integer getSize() {
        return size;
    }
}
