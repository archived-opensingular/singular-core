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
import java.util.Date;

public interface IInstanceDTO extends Serializable {
    Integer getCod();

    void setCod(Integer cod);

    String getDescricao();

    void setDescricao(String descricao);

    Long getDelta();

    String getDeltaString();

    void setDelta(Long delta);

    Date getDataInicial();

    String getDataInicialString();

    void setDataInicial(Date dataInicial);

    Long getDeltaAtividade();

    String getDeltaAtividadeString();

    void setDeltaAtividade(Long deltaAtividade);

    Date getDataAtividade();

    String getDataAtividadeString();

    void setDataAtividade(Date dataAtividade);

    String getUsuarioAlocado();

    void setUsuarioAlocado(String usuarioAlocado);

}
