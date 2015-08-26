package br.net.mirante.singular.dao;

import java.io.Serializable;

public class PesquisaDTO implements Serializable {

    private Long cod;
    private String nome;
    private String sigla;
    private String categoria;
    private Long quantidade;
    private Long tempoMedio;
    private Long version;

    public PesquisaDTO(Long cod, String nome, String sigla, String categoria, Long quantidade, Long tempoMedio) {
        this.cod = cod;
        this.nome = nome;
        this.sigla = sigla;
        this.categoria = categoria;
        this.quantidade = quantidade;
        this.tempoMedio = tempoMedio;
        this.version = 1L;
    }

    public Long getCod() {
        return cod;
    }

    public void setCod(Long cod) {
        this.cod = cod;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Long quantidade) {
        this.quantidade = quantidade;
    }

    public Long getTempoMedio() {
        return tempoMedio;
    }

    public void setTempoMedio(Long tempoMedio) {
        this.tempoMedio = tempoMedio;
    }

    public String getTempoMedioString() {
        StringBuilder tempo = new StringBuilder("");
        if (this.tempoMedio != null) {
            appendSeconds(tempo, this.tempoMedio);
        }
        return tempo.toString();
    }

    private StringBuilder appendSeconds(StringBuilder time, long seconds) {
        if (seconds > 0) {
            if (seconds < 60) {
                time.append(seconds);
            } else {
                appendMinutes(time, seconds / 60);
                time.append(seconds % 60);
            }
            time.append(" s ");
        }
        return time;
    }

    private StringBuilder appendMinutes(StringBuilder time, long minutes) {
        if (minutes > 0) {
            if (minutes < 60) {
                time.append(minutes);
            } else {
                appendHours(time, minutes / 60);
                time.append(minutes % 60);
            }
            time.append(" min ");
        }
        return time;
    }

    private StringBuilder appendHours(StringBuilder time, long hours) {
        if (hours > 0) {
            if (hours < 24) {
                time.append(hours);
            } else {
                appendDays(time, hours / 24);
                time.append(hours % 24);
            }
            time.append(" h ");
        }
        return time;
    }

    private StringBuilder appendDays(StringBuilder time, long days) {
        if (days > 0) {
            time.append(days).append(" d ");
        }
        return time;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
