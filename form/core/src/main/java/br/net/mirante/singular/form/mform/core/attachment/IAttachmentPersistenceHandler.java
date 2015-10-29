package br.net.mirante.singular.form.mform.core.attachment;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * Representa um repositório para os anexos de um instância de documento. Esse
 * repositório pode ser tanto de caráter temporário para a inclusões iniciais ou
 * permanente para formulário persistidos.
 * </p>
 * <p>
 * As referências ao arquivos é controlada por um string de 40 digitos que
 * consiste no hash SHA1 do conteudo do arquivo em questão.
 * </p>
 * <p>
 * O serviço de persistência não mantém nenhuma informação sobre o tipo do
 * arquivo, nome original ou outras informações. Se necessário algo nesse
 * sentido, a estrutura de dados que tem o id do arquivo deverá ter o cuidado de
 * guardar.
 * </p>
 * <p>
 * Apesar de não ser obrigatório, é altamente recomendado que os arquivo sejam
 * guardados compactados como gzip em máxima compressão (ver
 * {@link java.util.zip.DeflaterOutputStream} e
 * {@link java.util.zip.InflaterInputStream}).
 * </p>
 *
 * @author Daniel C. Bordin
 */
public interface IAttachmentPersistenceHandler extends Serializable {

    /**
     * Salvo os dados informado e associa-o ao documento (formulário) atual.
     *
     * @return Referencia ao arquivo salvo, incluido id e hash do mesmo.
     */
    public IAttachmentRef addAttachment(byte[] content);

    /**
     * Salvo os dados informado e associa-o ao documento (formulário) atual.
     *
     * @return Referencia ao arquivo salvo, incluido id e hash do mesmo.
     * @throws IOException
     */
    public IAttachmentRef addAttachment(InputStream in);

    /**
     * Copia o anexo entre persistencias diferente. Por default, faz uma copia
     * simples (copia os bytes de um para o outro), mas pode ser sobreescrito
     * para otimizar essa copia dependendo da implementações específicas.
     */
    public default IAttachmentRef copy(IAttachmentRef toBeCopied) {
        return addAttachment(toBeCopied.getContent());
    }

    /**
     * Recuperar os anexos associados ao contexto atual (provavelmente contexto
     * será um Documento).
     */
    public Collection<? extends IAttachmentRef> getAttachments();
    /**
     * Recuperar os anexos associados ao contexto atual (provavelmente contexto
     * será um Documento).
     */
    public default List<? extends IAttachmentRef> getAttachmentsAsList() {
        Collection<? extends IAttachmentRef> c = getAttachments();
        if (c instanceof List) {
            return (List<? extends IAttachmentRef>) c;
        }
        return new ArrayList<>(c);
    }

    public IAttachmentRef getAttachment(String hashId);

    public void deleteAttachment(String hashId);
}
