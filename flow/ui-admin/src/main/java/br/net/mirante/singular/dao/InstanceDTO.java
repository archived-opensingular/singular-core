package br.net.mirante.singular.dao;

import java.io.Serializable;
import java.util.Date;

import br.net.mirante.singular.util.FormatUtil;

public class InstanceDTO implements Serializable {

    private Long cod;
    private String descricao;
    private Long delta;
    private Date dataInicial;
    private Long deltaAtividade;
    private Date dataAtividade;
    private String usuarioAlocado;

    public InstanceDTO(Long cod, String descricao, Long delta, Date dataInicial,
            Long deltaAtividade, Date dataAtividade, String usuarioAlocado) {
        this.cod = cod;
        this.descricao = descricao;
        this.delta = delta;
        this.dataInicial = dataInicial;
        this.deltaAtividade = deltaAtividade;
        this.dataAtividade = dataAtividade;
        this.usuarioAlocado = usuarioAlocado;
    }

    public Long getCod() {
        return cod;
    }

    public void setCod(Long cod) {
        this.cod = cod;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getDelta() {
        return delta;
    }

    public String getDeltaString() {
        StringBuilder tempo = new StringBuilder("");
        if (this.delta != null) {
            FormatUtil.appendSeconds(tempo, this.delta);
        }
        return tempo.toString();
    }

    public void setDelta(Long delta) {
        this.delta = delta;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public String getDataInicialString() {
        return FormatUtil.dateToDefaultTimestampString(dataInicial);
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Long getDeltaAtividade() {
        return deltaAtividade;
    }

    public String getDeltaAtividadeString() {
        StringBuilder tempo = new StringBuilder("");
        if (this.deltaAtividade != null) {
            FormatUtil.appendSeconds(tempo, this.deltaAtividade);
        }
        return tempo.toString();
    }

    public void setDeltaAtividade(Long deltaAtividade) {
        this.deltaAtividade = deltaAtividade;
    }

    public Date getDataAtividade() {
        return dataAtividade;
    }

    public String getDataAtividadeString() {
        return FormatUtil.dateToDefaultTimestampString(dataAtividade);
    }

    public void setDataAtividade(Date dataAtividade) {
        this.dataAtividade = dataAtividade;
    }

    public String getUsuarioAlocado() {
        return usuarioAlocado;
    }

    public void setUsuarioAlocado(String usuarioAlocado) {
        this.usuarioAlocado = usuarioAlocado;
    }
}
