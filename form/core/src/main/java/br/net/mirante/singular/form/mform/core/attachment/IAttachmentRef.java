/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.core.attachment;

import java.io.IOException;
import java.io.InputStream;

import com.google.common.base.Throwables;
import com.google.common.io.ByteStreams;

/**
 * Referência para um arquivo binário persistido, contudo não identifica nome
 * original, data ou tamanho do arquivo.
 *
 * @author Daniel C. Bordin
 */
public interface IAttachmentRef {

    /**
     * <p>
     * Retorna uma String sem espaços em brancos identificando unicamente o
     * arquivo dentro do contexto que está sendo inserido.
     * </p>
     * <p>
     * A implementação default retorna o hash SHA1 do arquivo. O ideal é que o
     * id continue sendo o hash SHA1 do arquivo. É permitindo que não seja para
     * situações de restrição de implementação.
     * </p>
     */
    public default String getId() {
        return getHashSHA1();
    }

    /**
     * Retorna String de 40 digitos com o SHA1 do conteudo do arquivo.
     */
    public String getHashSHA1();

    /**
     * Retorna o conteúdo do arquivo descompactado.
     */
    public default byte[] getContentAsByteArray() {
        try {
            return ByteStreams.toByteArray(getContent());
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    /**
     * Retorna o conteúdo do arquivo descompactado.
     */
    public InputStream getContent();

    /**
     * Retorna o tamanho do arquivo se a informação estiver disponível ou Null
     * se não for possível informar. No entanto, deve sempre retornar o tamanho
     * se a referencia for produzida por uma operação de inserção
     * (addContent()).
     */
    public Integer getSize();
}
