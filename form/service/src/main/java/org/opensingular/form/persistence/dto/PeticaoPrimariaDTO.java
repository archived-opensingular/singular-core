package org.opensingular.form.persistence.dto;

public class PeticaoPrimariaDTO {

    private Long co_versao_formulario;
    private String nomeRequerente;
    private String cnpjRequerente;
    private String emailRequerente;

    public Long getCo_versao_formulario() {
        return co_versao_formulario;
    }

    public void setCo_versao_formulario(Long co_versao_formulario) {
        this.co_versao_formulario = co_versao_formulario;
    }

    public String getNomeRequerente() {
        return nomeRequerente;
    }

    public void setNomeRequerente(String nomeRequerente) {
        this.nomeRequerente = nomeRequerente;
    }

    public String getCnpjRequerente() {
        return cnpjRequerente;
    }

    public void setCnpjRequerente(String cnpjRequerente) {
        this.cnpjRequerente = cnpjRequerente;
    }

    public String getEmailRequerente() {
        return emailRequerente;
    }

    public void setEmailRequerente(String emailRequerente) {
        this.emailRequerente = emailRequerente;
    }
}
