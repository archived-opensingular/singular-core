package br.net.mirante.singular.dao;

import java.io.Serializable;

public class PesquisaDTO implements Serializable {

    private Long cod;
    private String nome;
    private String categoria;
    private Long version;

    public PesquisaDTO(Long cod, String nome, String categoria) {
        this.cod = cod;
        this.nome = nome;
        this.categoria = categoria;
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
