package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.dto;

public class CulturaDTO {

    private String nome;
    private String doseMaxima;
    private String numeroMaximoAplicacoes;
    private String intervaloSegurança;

    public String getNome() {
        return nome;
    }

    public CulturaDTO setNome(String nome) {
        this.nome = nome;
        return this;
    }

    public String getDoseMaxima() {
        return doseMaxima;
    }

    public CulturaDTO setDoseMaxima(String doseMaxima) {
        this.doseMaxima = doseMaxima;
        return this;
    }

    public String getNumeroMaximoAplicacoes() {
        return numeroMaximoAplicacoes;
    }

    public CulturaDTO setNumeroMaximoAplicacoes(String numeroMaximoAplicacoes) {
        this.numeroMaximoAplicacoes = numeroMaximoAplicacoes;
        return this;
    }

    public String getIntervaloSegurança() {
        return intervaloSegurança;
    }

    public CulturaDTO setIntervaloSegurança(String intervaloSegurança) {
        this.intervaloSegurança = intervaloSegurança;
        return this;
    }

}