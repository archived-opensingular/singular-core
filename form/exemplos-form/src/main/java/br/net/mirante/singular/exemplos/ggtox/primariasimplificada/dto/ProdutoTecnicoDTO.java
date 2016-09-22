package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.dto;

public class ProdutoTecnicoDTO {

    private String nome;
    private String processo;

    public String getNome() {
        return nome;
    }

    public ProdutoTecnicoDTO setNome(String nome) {
        this.nome = nome;
        return this;
    }

    public String getProcesso() {
        return processo;
    }

    public ProdutoTecnicoDTO setProcesso(String processo) {
        this.processo = processo;
        return this;
    }

}