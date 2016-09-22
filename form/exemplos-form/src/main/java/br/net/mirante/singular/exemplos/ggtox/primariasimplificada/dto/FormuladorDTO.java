package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.dto;

public class FormuladorDTO {

    private String razaoSocial;
    private String cnpj;
    private String endereco;

    public String getCnpj() {
        return cnpj;
    }

    public FormuladorDTO setCnpj(String cnpj) {
        this.cnpj = cnpj;
        return this;
    }

    public String getEndereco() {
        return endereco;
    }

    public FormuladorDTO setEndereco(String endereco) {
        this.endereco = endereco;
        return this;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public FormuladorDTO setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
        return this;
    }
}
