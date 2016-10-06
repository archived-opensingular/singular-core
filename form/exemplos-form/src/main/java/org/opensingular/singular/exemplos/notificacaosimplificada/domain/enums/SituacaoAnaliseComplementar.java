package org.opensingular.singular.exemplos.notificacaosimplificada.domain.enums;

import org.opensingular.singular.support.persistence.util.EnumId;

public enum SituacaoAnaliseComplementar implements EnumId<SituacaoAnaliseComplementar, Character> {
    NAO_INICIADA('N', "Não iniciada"),
    INICIADA('I', "Iniciada"),
    ESTORNADA('E', "Estornada"),
    CONCLUIDA('C', "Concluída");

    public static final String ENUM_CLASS_NAME = "org.opensingular.singular.exemplos.notificacaosimplificada.domain.enums.SituacaoAnaliseComplementar";

    private Character codigo;
    private String    descricao;

    private SituacaoAnaliseComplementar(Character codigo, String descricao) {
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
    public SituacaoAnaliseComplementar valueOfEnum(Character codigo) {
        for (SituacaoAnaliseComplementar tipo : values()) {
            if (tipo.getCodigo().equals(codigo)) {
                return tipo;
            }
        }

        return null;
    }

}
