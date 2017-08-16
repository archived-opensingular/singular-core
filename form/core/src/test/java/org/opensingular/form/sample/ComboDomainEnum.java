package org.opensingular.form.sample;

public enum ComboDomainEnum {

    PROPRIA("Própria"),
    AFREATAMENTO_CASCO_NU("Afretamento a casco nu"),
    EM_CONSTRUCAO("Em construção");

    private String descricao;

    ComboDomainEnum(String descricao) {
        this.descricao = descricao;
    }


    @Override
    public String toString() {
        return descricao;
    }
}
