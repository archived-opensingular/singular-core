package org.opensingular.form.persistence.dto;

public class PeticaoPrimariaDTO extends BaseDTO {

    private Long codVersaoFormulario;
    private String nomeRequerente;
    private String cnpjRequerente;
    private String emailRequerente;

    public Long getCodVersaoFormulario() {
        return codVersaoFormulario;
    }

    public void setCodVersaoFormulario(Long codVersaoFormulario) {
        this.codVersaoFormulario = codVersaoFormulario;
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
