package org.opensingular.form.exemplos.notificacaosimplificada.domain.enums;

import org.opensingular.lib.support.persistence.util.EnumId;

public enum TipoControleValor implements EnumId<TipoControleValor, Character> {

    MAIOR('1', ">"),
    MAIOR_IGUAL('2', ">="),
    MENOR('3', "<"),
    MENOR_IGUAL('4', "<=");

    public static final String ENUM_CLASS_NAME = "org.opensingular.form.exemplos.notificacaosimplificada.domain.enums.TipoControleValor";

    private Character codigo;
    private String    descricao;

    private TipoControleValor(Character codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    @Override
    public Character getCodigo() {
        return codigo;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    @Override
    public TipoControleValor valueOfEnum(Character codigo) {
        for (TipoControleValor tipo : values()) {
            if (tipo.getCodigo().equals(codigo)) {
                return tipo;
            }
        }

        return null;
    }

    public static TipoControleValor valueOfDescricao(String descricao) {
        for (TipoControleValor tipo : values()) {
            if (tipo.getDescricao().equals(descricao)) {
                return tipo;
            }
        }

        return null;
    }

}
