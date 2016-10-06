package org.opensingular.singular.exemplos.notificacaosimplificada.domain.dto;

import java.io.Serializable;

public class VocabularioControladoDTO implements Serializable {

    private Long   id;
    private String descricao;

    public VocabularioControladoDTO(Long id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public VocabularioControladoDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
