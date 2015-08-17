package br.net.mirante.singular.dao;

public class PesquisaDTO {

    private Long cod;
    private String nome;
    private Long quantidade;

    public PesquisaDTO(Long cod, String nome, Long quantidade) {
        this.cod = cod;
        this.nome = nome;
        this.quantidade = quantidade;
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

    public Long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Long quantidade) {
        this.quantidade = quantidade;
    }
}
