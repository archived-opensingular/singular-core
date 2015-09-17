package br.net.mirante.singular.flow.core.dto;

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
