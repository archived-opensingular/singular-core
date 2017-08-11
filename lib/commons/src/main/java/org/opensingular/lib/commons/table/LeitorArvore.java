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

package org.opensingular.lib.commons.table;

/**
 * Model apra obter a estrutura da �rvore bem como os valores de cada coluna.
 */
public interface LeitorArvore extends LeitorLinha<Object> {

    /**
     * Retorna os elementos do primeiro n�vel da �rvore.
     */
    public Object getRaizes();

    /**
     * Recupera os filhos a serem exibidos.
     *
     * @param item para o qual se deseja obter os filhos.
     * @return Pode retornar qualquer objeto iter�vel, ou um Array ou mesmo um
     * objeto simples se houver apenas um filho. Pode retornar null para
     * indicar a aus�ncia de filhos ou uma lista de tamanho zero.
     */
    public Object getFilhos(Object item);

}
