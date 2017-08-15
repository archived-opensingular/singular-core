package org.opensingular.form.sample;

public enum TipoPropriedadeEmbarcacao {

    PROPRIA("Própria"),
    AFREATAMENTO_CASCO_NU("Afretamento a casco nu"),
    EM_CONSTRUCAO("Em construção");

    private String descricao;

    TipoPropriedadeEmbarcacao(String descricao) {
        this.descricao = descricao;
    }


    @Override
    public String toString() {
        return descricao;
    }
}
