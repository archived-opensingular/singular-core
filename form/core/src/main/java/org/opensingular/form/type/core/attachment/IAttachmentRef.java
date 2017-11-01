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

import org.apache.tika.Tika;
import org.opensingular.form.SingularFormException;

import javax.activation.DataSource;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Objects;

/**
 * Referência para um arquivo binário persistido, contudo não identifica a data ou tamanho do arquivo.
 *
 * @author Daniel C. Bordin
 */
public interface IAttachmentRef extends Serializable, DataSource{

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
     * Retorna o tamanho do arquivo se a informação estiver disponível ou -1
     * se não for possível informar. No entanto, deve sempre retornar o tamanho
     * se a referencia for produzida por uma operação de inserção
     * (addContent()).
     */
    long getSize();

    @Override
    default String getContentType() {
        try (InputStream is = getContentAsInputStream()){
            return new Tika().detect(is);
        } catch (IOException e) {
            throw addInfo(new SingularFormException("Não foi possivel detectar o content type.", e), this);
        }
    }
    
    default OutputStream getOutputStream() throws java.io.IOException {
        throw new UnsupportedOperationException();
    }

    /** Retorna o conteúdo desta referência. Mesmo que {@link #getInputStream()}. */
    @Nonnull
    default InputStream getContentAsInputStream() {
        try {
            return Objects.requireNonNull(getInputStream());
        } catch (IOException e) {
            //Faz em mais de um passo para o sonar não dar falso positivo
            SingularFormException e2 = new SingularFormException("Erro obtendo referencia", e);
            addInfo(e2, this);
            throw e2;
        }
    }


    static SingularFormException addInfo(SingularFormException e, IAttachmentRef ref) {
        e.add("name", () -> ref.getName())
                .add("id", () -> ref.getId())
                .add("size", () -> ref.getSize())
                .add("sha1", () -> ref.getHashSHA1());
        return e;
    }
}
