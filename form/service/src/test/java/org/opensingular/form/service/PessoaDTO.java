package org.opensingular.form.service;

import org.opensingular.form.persistence.dto.BaseDTO;

public class PessoaDTO extends BaseDTO {

    private Long codVersaoFormulario;
    private String nome;
    private String idade;

    public Long getCodVersaoFormulario() {
        return codVersaoFormulario;
    }

    public void setCodVersaoFormulario(Long codVersaoFormulario) {
        this.codVersaoFormulario = codVersaoFormulario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIdade() {
        return idade;
    }

    public void setIdade(String idade) {
        this.idade = idade;
    }
}
