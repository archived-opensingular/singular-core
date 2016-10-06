/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
