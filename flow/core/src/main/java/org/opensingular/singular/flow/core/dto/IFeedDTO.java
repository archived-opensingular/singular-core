/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.flow.core.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public interface IFeedDTO extends Serializable {
    String getDescricaoInstancia();

    void setDescricaoInstancia(String descricaoInstancia);

    String getNomeProcesso();

    void setNomeProcesso(String nomeProcesso);

    BigDecimal getTempoDecorrido();

    void setTempoAtraso(BigDecimal tempoAtraso);

    BigDecimal getMedia();

    void setMedia(BigDecimal media);
}
