package org.opensingular.form.exemplos.notificacaosimplificada.domain.enums;

import org.opensingular.singular.support.persistence.util.EnumId;

public enum TipoClassificacao implements EnumId<TipoClassificacao, Character> {

    PRESCRICAO('P', "Prescrição"),

    DESTINACAO('D', "Destinação"),

    USO('U', "Uso");

    public static final String ENUM_CLASS_NAME = "org.opensingular.singular.exemplos.notificacaosimplificada.domain.enums.TipoClassificacao";

    private TipoClassificacao(Character codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    private Character codigo;

    private String descricao;

    @Override
    public Character getCodigo() {
        return codigo;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    @Override
    public TipoClassificacao valueOfEnum(Character id) {
        for (TipoClassificacao tipo : values()) {
            if (tipo.getCodigo().equals(id)) {
                return tipo;
            }
        }
        return null;
    }
}