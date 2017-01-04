/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package org.opensingular.form.util.diff;

/**
 * Representa os diferentes tipos de resultados de comparação.
 *
 * @author Daniel C. Bordin on 27/12/2016.
 */
public enum DiffType {

    /** Resultado da comparação indefinido */
    UNKNOWN_STATE,
    /** As instâncias não foram alterandas e ambas apresentem o mesmo valor não nulo atribuido. */
    UNCHANGED_WITH_VALUE,
    /** As instâncias não foram alterandas e apresentem ambas conteúdo null */
    UNCHANGED_EMPTY,
    /**
     * Foi alterado ou por inserção de um novo item em uma lista ou antes a instância era null e agora passou para não
     * null.
     */
    CHANGED_NEW,
    /** Foi alterado ou por ter sido apagado da lista ou pelo conteudo ter sido alterado tudo para null. */
    CHANGED_DELETED,
    /** O conteúdo foi alterado, sendo que tanto antes quando depois as instâncias tinham conteudo diferente de null. */
    CHANGED_CONTENT;
}
