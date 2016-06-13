/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.type.core.attachment;

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
public interface IAttachmentPersistenceHandler<T extends IAttachmentRef> extends Serializable {

    /**
     * Salvo os dados informado e associa-o ao documento (formulário) atual.
     *
     * @return Referencia ao arquivo salvo, incluido id e hash do mesmo.
     */
    IAttachmentRef addAttachment(byte[] content);

    /**
     * Salvo os dados informado e associa-o ao documento (formulário) atual.
     *
     * @return Referencia ao arquivo salvo, incluido id e hash do mesmo.
     * @throws IOException
     */
    IAttachmentRef addAttachment(InputStream in);

    /**
     * Copia o anexo entre persistencias diferente. Por default, faz uma copia
     * simples (copia os bytes de um para o outro), mas pode ser sobreescrito
     * para otimizar essa copia dependendo da implementações específicas.
     */
    default IAttachmentRef copy(IAttachmentRef toBeCopied) {
        return addAttachment(toBeCopied.getContent());
    }

    /**
     * Recuperar os anexos associados ao contexto atual (provavelmente contexto
     * será um Documento).
     */
    Collection<T> getAttachments();
    /**
     * Recuperar os anexos associados ao contexto atual (provavelmente contexto
     * será um Documento).
     */
    default List<? extends IAttachmentRef> getAttachmentsAsList() {
        Collection<? extends IAttachmentRef> c = getAttachments();
        if (c instanceof List) {
            return (List<? extends IAttachmentRef>) c;
        }
        return new ArrayList<>(c);
    }

    IAttachmentRef getAttachment(String hashId);

    void deleteAttachment(String hashId);
}
