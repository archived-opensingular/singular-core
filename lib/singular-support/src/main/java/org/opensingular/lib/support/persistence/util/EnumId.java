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

package org.opensingular.lib.support.persistence.util;

/**
 * Interface que deve ser utilizada pelas enumerações {@link Enum} que
 * necessitam trabalhar com um ID diferente do valor ORDINAL.
 *
 * @param <E>
 * @param <ID> Tipo do identificador da enumeração.
 * @author alessandro.leite
 * @since 23/10/2009
 */
public interface EnumId<E extends Enum<E>, ID> {

    /**
     * Retorna o identificador da enumeração.
     */
    ID getCodigo();

    /**
     * @return Retorna o valor da enumeração.
     */
    String getDescricao();

    Enum<E> valueOfEnum(ID codigo);
}