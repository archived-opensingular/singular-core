/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.type.core.attachment;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;

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
 * </p>
 *
 * @author Daniel C. Bordin
 */
public interface IAttachmentPersistenceHandler<T extends IAttachmentRef> extends Serializable {

    /**
     * Salvo os dados informado e associa-o ao documento (formulário) atual.
     * O arquivo pode ser excluido em seguida
     *
     * @param file   Arquivo temporário com o conteúdo do anexo.
     * @param length Tamanho em bytes do arquivo, note que esse parâmetro é inportante uma vez que o método
     *               File.length não retorna o tamanho do arquivo de maneira confiável em qualquer sistema operacional
     * @param name Nome do arquivo original
     * @return Referencia ao arquivo salvo, incluido id e hash do mesmo.
     * @throws IOException
     */
    T addAttachment(File file, long length, String name);

    /**
     * Copia o conteúdo de um IAttachmentRef para esse persistence handler e retorna
     * o novo IAttachmentRef criado.
     */
    T copy(IAttachmentRef toBeCopied);

    /**
     * Recuperar os anexos associados ao contexto atual (provavelmente contexto
     * será um Documento).
     */
    Collection<T> getAttachments();

    IAttachmentRef getAttachment(String fileId);

    void deleteAttachment(String fileId);
}
