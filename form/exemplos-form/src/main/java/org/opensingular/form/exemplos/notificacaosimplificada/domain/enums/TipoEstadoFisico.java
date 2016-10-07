package org.opensingular.form.exemplos.notificacaosimplificada.domain.enums;

import org.opensingular.lib.support.persistence.util.EnumId;

public enum TipoEstadoFisico implements EnumId<TipoEstadoFisico, Character> {

    NAO_INFORMADO('4', ""),

    SOLIDO('0', "Sólido"),

    SEMISOLIDO('1', "Semi-sólido"),

    LIQUIDO('2', "Líquido"),

    GASOSO('3', "Gasoso");

    public static final String ENUM_CLASS_NAME = "org.opensingular.form.exemplos.notificacaosimplificada.domain.enums.TipoEstadoFisico";

    private TipoEstadoFisico(Character codigo, String descricao) {
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
    public TipoEstadoFisico valueOfEnum(Character id) {
        for (TipoEstadoFisico tipo : values()) {
            if (tipo.getCodigo().equals(id)) {
                return tipo;
            }
        }
        return null;
    }
}