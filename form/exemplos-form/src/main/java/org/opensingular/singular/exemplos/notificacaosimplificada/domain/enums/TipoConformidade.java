package org.opensingular.singular.exemplos.notificacaosimplificada.domain.enums;

public enum TipoConformidade {

    SIM("S", "Conforme"),
    NAO("N", "Não conforme"),
    NAO_SE_APLICA("X", "Não se aplica"),
    NAO_INFORMADO("I", "Não analisado");

    public static final String ENUM_CLASS_NAME = "org.opensingular.singular.exemplos.notificacaosimplificada.domain.enums.TipoConformidade";

    private String codigo;
    private String descricao;

    private TipoConformidade(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    /**
     * @return codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @param codigo codigo a ser atribuído
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * @return descricao
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * @param descricao descricao a ser atribuído
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public static TipoConformidade valueOfEnum(String codigo) {
        TipoConformidade status[] = TipoConformidade.values();

        for (TipoConformidade st : status) {
            if (codigo != null && codigo.equals(st.getCodigo())) {
                return st;
            }
        }
        return null;
    }

}
