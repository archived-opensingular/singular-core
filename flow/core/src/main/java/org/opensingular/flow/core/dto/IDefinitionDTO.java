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

package org.opensingular.flow.core.dto;

import java.io.Serializable;

public interface IDefinitionDTO extends Serializable {
    Integer getCod();

    void setCod(Integer cod);

    String getNome();

    void setNome(String nome);

    String getSigla();

    void setSigla(String sigla);

    String getCategoria();

    void setCategoria(String categoria);

    Long getQuantidade();

    void setQuantidade(Long quantidade);

    Long getTempoMedio();

    void setTempoMedio(Long tempoMedio);

    String getTempoMedioString();

    Long getThroughput();

    void setThroughput(Long throughput);

    Long getVersion();

    void setVersion(Long version);
}
