package br.net.mirante.singular.dao;

import java.io.Serializable;
import java.math.BigDecimal;

public class FeedDTO implements Serializable {

    private String descricaoInstancia;
    private String nomeProcesso;
    private BigDecimal tempoAtraso;
    private BigDecimal media;

    public FeedDTO(String nomeProcesso, String descricaoInstancia, BigDecimal tempoAtraso, BigDecimal media) {
        this.descricaoInstancia = descricaoInstancia;
        this.nomeProcesso = nomeProcesso;
        this.tempoAtraso = tempoAtraso;
        this.media = media;
    }

    public String getDescricaoInstancia() {
        return descricaoInstancia;
    }

    public void setDescricaoInstancia(String descricaoInstancia) {
        this.descricaoInstancia = descricaoInstancia;
    }

    public String getNomeProcesso() {
        return nomeProcesso;
    }

    public void setNomeProcesso(String nomeProcesso) {
        this.nomeProcesso = nomeProcesso;
    }

    public BigDecimal getTempoDecorrido() {
        return tempoAtraso;
    }

    public void setTempoAtraso(BigDecimal tempoAtraso) {
        this.tempoAtraso = tempoAtraso;
    }

    public BigDecimal getMedia() {
        return media;
    }

    public void setMedia(BigDecimal media) {
        this.media = media;
    }
}
