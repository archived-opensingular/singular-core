package org.opensingular.singular.exemplos.notificacaosimplificada.domain.enums;

public enum TipoAcao {

    INCLUIR('I', "Incluir", "Incluído"),
    ALTERAR('A', "Alterar", "Alterado"),
    EXCLUIR('E', "Excluir", "Excluído");

    public static final String ENUM_CLASS_NAME = "org.opensingular.singular.exemplos.notificacaosimplificada.domain.enums.TipoAcao";

    private Character codigo;
    private String    descricao;
    private String    descricaoGerundio;

    private TipoAcao(Character codigo, String descricao, String descricaoGerundio) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.descricaoGerundio = descricaoGerundio;
    }

    public Character getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getDescricaoGerundio() {
        return descricaoGerundio;
    }

    public static TipoAcao valueOfEnum(Character codigo) {

        TipoAcao tipos[] = TipoAcao.values();

        for (TipoAcao tipo : tipos) {
            if (codigo != null && tipo.getCodigo().equals(codigo)) {
                return tipo;
            }
        }
        return null;
    }

}
