/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.type.core.attachment;

import java.io.InputStream;

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
    String getId();


    /**
     * Retorna String de 40 digitos com o SHA1 do conteudo do arquivo.
     */
    String getHashSHA1();

    /**
     * Retorna o conteúdo do arquivo em um novo inputStream.
     * Cada chamada a esse método deve retornar um novo inputStream
     *
     */
    InputStream newInputStream();

    /**
     * Retorna o tamanho do arquivo se a informação estiver disponível ou -1
     * se não for possível informar. No entanto, deve sempre retornar o tamanho
     * se a referencia for produzida por uma operação de inserção
     * (addContent()).
     */
    long getSize();
    
    /**
     * Retorna o nome do arquivo.
     */
    String getName();
}
