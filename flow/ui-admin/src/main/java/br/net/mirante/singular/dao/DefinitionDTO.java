package br.net.mirante.singular.dao;

import java.io.Serializable;

import br.net.mirante.singular.util.FormatUtil;

public class DefinitionDTO implements Serializable {

    private Long cod;
    private String nome;
    private String sigla;
    private String categoria;
    private Long quantidade;
    private Long tempoMedio;
    private Long throughput;
    private Long version;

    public DefinitionDTO(Long cod, String nome, String sigla, String categoria,
            Long quantidade, Long tempoMedio, Long throughput) {
        this.cod = cod;
        this.nome = nome;
        this.sigla = sigla;
        this.categoria = categoria;
        this.quantidade = quantidade;
        this.tempoMedio = tempoMedio;
        this.throughput = throughput;
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
            FormatUtil.appendSeconds(tempo, this.tempoMedio);
        }
        return tempo.toString();
    }

    public Long getThroughput() {
        return throughput;
    }

    public void setThroughput(Long throughput) {
        this.throughput = throughput;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
